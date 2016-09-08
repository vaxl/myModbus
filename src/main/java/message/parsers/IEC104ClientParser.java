package message.parsers;

import base.*;
import database.Db;
import database.Entity.DiagRegistrs;
import database.Entity.Registrs;
import database.Entity.TableRegs;
import database.Entity.Registr;
import message.Message;
import settings.*;
import java.time.LocalDateTime;
import java.util.*;

import static base.RegTypes.*;
import static base.View.logView.*;
import static message.parsConst.Iec104.*;
import static helpers.LogicHelper.*;

public class IEC104ClientParser implements ParseMessage {
    private byte rx[];
    private Message message;
    private int countNs=0;
    private int countNr=0;
    private Text text = Text.getInstance();
    private Setup setup = Setup.getInstance();
    private Database db = Db.getInstance();
    private HashMap<String,Message> cash = new HashMap<>();

    public IEC104ClientParser() {
        init();
    }

    @Override
    public void execute(Message message) {
        rx = message.getRx();
        this.message = message;
        if (message.getStatus()!=MessageStatus.SEND) {
            if (rx.length == SERVSIZE) parseServiceM();
            else parseInformM();
        }
        else if (message.getStatus()==MessageStatus.SEND){
            sendMesage();
        }
    }

    private void sendMesage() {
        Registrs regs = message.getRegs();
        int key = regs.getReg();
        RegTypes type =regs.getType();
        int id = regs.getId();
        if (getFunction(type,false)==0) {
            message.setStatus(MessageStatus.NOANSWER);
            message.setRegs(new DiagRegistrs(text.ERRFUNC));
            return;
        }
        List<Byte> arr = new ArrayList<>(32);
            byte [] val = ParserHelper.regsToByteData(key,1,db.readAll(regs));
            if (val==null) return;
            headerGen(arr);
            arr.add(TYPE, getFunction(type,true));
            arr.add(NUM,SINGLE);
            arr.add(COT,COTSPONTANEOUS);
            arr.add(ADR,SINGLE);
            arr.add(ASDUL,int2ByteLo(setup.id));
            arr.add(ASDUH,int2ByteHi(setup.id));
            arr.add(REGLO,int2ByteLo(key));
            arr.add(REGHI,int2ByteHi(key));
            arr.add(REG0, ZERO);
            for (int i = val.length-1,j=VAL; i >=0 ; i--,j++)
                arr.add(j,val[i]);
            if (type!= SINGLEBIT)  arr.add(ZERO);  // признак качества заглушка
            time(arr);
            arr.set(LEN, (byte) (arr.size()-2));
            message.setTx(arr);
            message.setTxDecode(text.REGISTR + key + text.FUNCTION + type.name());
            countNs+=2;
    }

    private void parseServiceM(){
        Message service = cash.get(message.getLogRx(ORIGINAL));
        if (service!=null){
            if (service.getStatus() ==  MessageStatus.START) restart();
            message.setTx(service.getTx());
            message.setStatus(service.getStatus());
            message.setTxDecode(service.getLogRx(DECODE));
            message.setRegs(service.getRegs());
        }
        else if (message.getRx()[2]==1) {
            message.setStatus(MessageStatus.NOANSWER);
            message.setRegs(new DiagRegistrs(text.CMDACKNOL));
        }
        else {
            message.setStatus(MessageStatus.NOANSWER);
            message.setRegs(service.getRegs());
        }
    }
    private void parseInformM(){
        switch (rx[TYPE]){
            case GI :{
                int curArr = 0;
                int rxLen = rx.length;
                int size = rxLen*2;
                countNr+=2;
                byte tx[]=new byte[size];
                byte [] actConf = actConf();
                curArr+=rx.length;
                int id = twoByte2Int(rx[ASDUH],rx[ASDUL]);

                for(RegTypes type : RegTypes.values()) {
                    byte[] data = getData(new TableRegs(id,type));
                    if (data != null) {
                        tx = Arrays.copyOf(tx,tx.length+data.length);
                        System.arraycopy(data, 0, tx, curArr, data.length);
                        curArr += data.length;
                        countNs+=2;
                    }
                }
                System.arraycopy(actConf,0,tx,0,rxLen);
                System.arraycopy(actTerm(),0,tx,curArr,rxLen);

                message.setTx(tx);
                message.setStatus(MessageStatus.OK);
                message.setRegs(new DiagRegistrs(text.GI));
                message.setTxDecode(text.CMDACKNOL);
                break;
            }
            default:message.setStatus(MessageStatus.NOANSWER);
        }
    }
    private byte[] getData(TableRegs tableRegs) {
        RegTypes type = tableRegs.getType();
        if (getFunction(type,false)==0) return null;
        byte [] data = convertToBytes(db.readAll(tableRegs));
        if (data==null) return null;
        int k=1;
        if (type == SINGLEBIT) k=4;
        if (type == RegTypes.SCALEDMESURE) k=6;
        if (type == RegTypes.SHORTFLOAT) k=8;
        int size = data.length;
        int txsize = 12 + size;
        int len = txsize-2;
        byte[] tx = new byte[txsize];
        System.arraycopy(headerGen(len),0,tx,0,6);
        tx[TYPE] = getFunction(type,false);
        tx[NUM] =int2ByteLo(size/k);
        tx[COT] = COTGI;
        tx[ADR] = rx[ADR];
        tx[ASDUH] = rx[ASDUH];
        tx[ASDUL] = rx[ASDUL];
        System.arraycopy(data,0,tx,REGLO,size);
        return tx;
    }
    private byte[] headerGen(int len){
        return new byte[]{104, int2ByteLo(len),int2ByteLo(countNs),int2ByteHi(countNs),int2ByteLo(countNr),int2ByteHi(countNr)};
    }
    private void headerGen(List<Byte> arr){
        arr.add(START,STARTWORD);
        arr.add(LEN,  SERVSIZE);
        arr.add(NSLO, int2ByteLo(countNs));
        arr.add(NSHI, int2ByteHi(countNs));
        arr.add(NRLO, int2ByteLo(countNr));
        arr.add(NRHI, int2ByteHi(countNr));
    }
    private byte[] actConf(){
        byte[] tx = Arrays.copyOf(rx,rx.length);
        System.arraycopy(headerGen(rx[LEN]),0,tx,0,6);
        tx[COT] = COTACTCONF;
        countNs+=2;
        return tx;
    }
    private byte[] actTerm(){
        byte[] tx = Arrays.copyOf(rx,rx.length);
        System.arraycopy(headerGen(rx[LEN]),0,tx,0,6);
        tx[COT] = COTACTTERM;
        countNs+=2;
        return tx;
    }
    private byte getFunction(RegTypes type, boolean time) {
        if (!time) {
            switch (type) {
                case SINGLEBIT:
                    return 1;
                case SCALEDMESURE:
                    return 11;
                case SHORTFLOAT:
                    return 13;
                default:
                    return 0;
            }
        } else {
            switch (type) {
                case SINGLEBIT:
                    return 30;
                case SCALEDMESURE:
                    return 35;
                case SHORTFLOAT:
                    return 36;
                default:
                    return 0;
            }
        }
    }
    private static void time(List<Byte> arr){
        LocalDateTime date = LocalDateTime.now();
        date.plusHours(1);
        int secMs =  date.getSecond()*1000 + date.getNano()/1000000;
        arr.add(int2ByteLo(secMs));
        arr.add(int2ByteHi(secMs));
        arr.add((byte) date.getMinute());
        arr.add((byte) date.getHour());
        arr.add((byte) date.getDayOfMonth());
        arr.add((byte) date.getMonthValue());
        arr.add((byte) (date.getYear()-2000));
    }
    private void restart(){
        countNs=0;
        countNr=0;
    }
    private void init(){
        byte[] req =  {104,4,7,0,0,0};
        byte[] answ = {104,4,11,0,0,0};
        Message m1 = new Message(req);
        m1.setTx(answ);
        m1.setStatus(MessageStatus.START);
        m1.setRegs(new DiagRegistrs(text.STARTACT));
        m1.setTxDecode(text.CMDACKNOL);
        cash.put(m1.getLogRx(ORIGINAL),m1);

        byte[] answ2 = {104,4,int2ByteLo(131),0,0,0};
        byte[] req2 =  {104,4,67,0,0,0};
        Message m2 = new Message(req2);
        m2.setTx(answ2);
        m2.setStatus(MessageStatus.OK);
        m2.setRegs(new DiagRegistrs(text.TESTACT));
        m2.setTxDecode(text.CMDACKNOL);
        cash.put(m2.getLogRx(ORIGINAL),m2);
    }

    private byte[] convertToBytes(Collection<Registr> regs){
        byte[] res=null;
        int i=0;
        if(regs.isEmpty()) return null;
        switch (((Registr) regs.toArray()[0]).getType()) {
            case SHORTFLOAT: {
                break;
            }
            case SCALEDMESURE:  {
                res = new byte[regs.size()*6];
                for (Registr r : regs){
                    int reg = r.getReg();
                    int val= r.getValue();
                    res[ i ]=int2ByteLo(reg);
                    res[i+1]=int2ByteHi(reg);
                    res[i+2]=0;
                    res[i+3]=int2ByteLo(val);
                    res[i+4]=int2ByteHi(val);
                    res[i+5] = 0;
                    i+=6;
                }
                break;
            }
            case SINGLEBIT: {
                res = new byte[regs.size()*4];
                for (Registr r : regs){
                    int reg = r.getReg();
                    res[i]=int2ByteLo(reg);
                    res[i+1]=int2ByteHi(reg);
                    res[i+2]=0;
                    if (r.getValue()==1) res[i+3]=1;
                    res[i+3]=0;
                    i+=4;
                }
                break;
            }
        }
        return res;
    }
}
