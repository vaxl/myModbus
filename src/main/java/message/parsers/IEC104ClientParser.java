package message.parsers;

import base.*;
import exeptions.NoSuchRegistrs;
import factory.FactorySetup;
import message.Message;
import settings.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static base.View.logView.*;
import static message.parsConst.Iec104.*;
import static helpers.LogicHelper.*;

public class IEC104ClientParser implements ParseMessage {
    private byte rx[];
    private Message message;
    private int countNs=0;
    private int countNr=0;
    private Text text = (Text) FactorySetup.getClazz("text.xml");
    private Setup setup = (Setup) FactorySetup.getClazz("setup.xml");
    private Database db = (Database) FactorySetup.getClazz("Database");
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
        int key = twoByte2Int(rx[1],rx[2]);
        RegTypes type = RegTypes.values()[rx[0]];
        if (getFunction(type,false)==0) {
            message.setStatus(MessageStatus.NOANSWER);
            message.setRxDecode(text.ERRFUNC);
            return;
        }
        List<Byte> arr = new ArrayList<>(32);
        try {
            byte [] val = db.read(key,1,type);
            headerGen(arr);
            arr.add(TYPE, getFunction(type,true));
            arr.add(NUM,SINGLE);
            arr.add(COT,COTSPONTANEOUS);
            arr.add(ADR,SINGLE);
            arr.add(ASDUL,int2ByteLo(setup.id));
            arr.add(ASDUH,int2ByteHi(setup.id));
            arr.add(REGLO,rx[2]);
            arr.add(REGHI,rx[1]);
            arr.add(REG0, ZERO);
            for (int i = val.length-1,j=VAL; i >=0 ; i--,j++)
                arr.add(j,val[i]);
            if (type!= RegTypes.SINGLEBIT)  arr.add(ZERO);  // признак качества заглушка
            time(arr);
            arr.set(LEN, (byte) (arr.size()-2));
            message.setTx(arr);
            message.setTxDecode(text.REGISTR + key + text.FUNCTION + type.name());
            countNs+=2;
        } catch (NoSuchRegistrs ignored) {
        }
    }
    private void parseServiceM(){
        Message service = cash.get(message.getLogRx(ORIGINAL));
        if (service!=null){
            if (service.getStatus() ==  MessageStatus.START) restart();
            message.setTx(service.getTx());
            message.setStatus(service.getStatus());
            message.setTxDecode(service.getLogRx(DECODE));
            message.setRxDecode(service.getLogRx(DECODE));
        }
        else if (message.getRx()[2]==1) {
            message.setStatus(MessageStatus.NOANSWER);
            message.setRxDecode(text.CMDACKNOL);
        }
        else {
            message.setStatus(MessageStatus.NOANSWER);
            message.setRxDecode(text.ERRFUNC);
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

                for(RegTypes type : RegTypes.values()) {
                    byte[] data = getData(type);
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
                message.setRxDecode(text.GI);
                message.setTxDecode(text.CMDACKNOL);
                break;
            }
            default:message.setStatus(MessageStatus.NOANSWER);
        }
    }
    private byte[] getData(RegTypes type) {
        if (getFunction(type,false)==0) return null;
        byte [] data = db.readAll(type);
        if (data==null) return null;
        int k=1;
        if (type == RegTypes.SINGLEBIT) k=4;
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
        m1.setRxDecode(text.STARTACT);
        m1.setTxDecode(text.CMDACKNOL);
        cash.put(m1.getLogRx(ORIGINAL),m1);

        byte[] answ2 = {104,4,int2ByteLo(131),0,0,0};
        byte[] req2 =  {104,4,67,0,0,0};
        Message m2 = new Message(req2);
        m2.setTx(answ2);
        m2.setStatus(MessageStatus.OK);
        m2.setRxDecode(text.TESTACT);
        m2.setTxDecode(text.CMDACKNOL);
        cash.put(m2.getLogRx(ORIGINAL),m2);
    }
}
