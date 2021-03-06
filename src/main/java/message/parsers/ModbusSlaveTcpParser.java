package message.parsers;

import base.*;
import database.CachMap;
import database.Db;
import database.Entity.Registr;
import database.Entity.Registrs;
import database.HashMapDB;
import factory.FactorySetup;
import message.Message;
import settings.*;
import java.util.Arrays;
import static base.View.logView.*;
import static message.parsConst.ModbusTcp.*;
import static helpers.LogicHelper.*;

public class ModbusSlaveTcpParser implements ParseMessage {
    private View messageWork = (View) FactorySetup.getClazz("View");
    private Text text = Text.getInstance();
    private Database db = Db.getInstance();
    private CachMap cach = CachMap.getInstance();
    private StringBuilder strTx;
    private Registrs baseReg;
    private MessageStatus status;

    @Override
    public void execute(Message message) {
        if (db==null) db = new HashMapDB();
        byte[] rx = message.getRx();
        if (isRightPack(rx)) {
            if (!fromHash(message)) {
                message.setTx(parsePack(rx));
                message.setTxDecode(strTx.toString());
                message.setRegs(baseReg);
                message.setStatus(status);
                toHash(message);
            }
        }else message.setStatus(MessageStatus.NOANSWER);
    }


    private boolean isRightPack(byte[] rx){
        if (!(rx.length==12 )){
            messageWork.print(text.ERRPACK);
            return false;
        }
        if (rx[7]>6 | rx[7]<1){
            messageWork.print(text.ERRFUNC);
            return false;
        }
        return true;
    }

    private byte[] parsePack(byte[] rx){
        int dataSize;
        byte [] data;
        int id = rx[ADR];
        int startAdr = twoByte2Int(rx[REGHI],rx[REGLO]);
        int num = twoByte2Int(rx[NUMHI],rx[NUMLO]);
        if (rx[FUNC]==3 | rx[FUNC]==4) dataSize = num* 2;
        else if (rx[FUNC]==5 | rx[FUNC]==6) dataSize=rx.length-9;
            else  dataSize = bitInByte(num);
        byte [] tx = new byte[dataSize + 9];
        baseReg = new Registrs(id,startAdr,RegTypes.values()[rx[FUNC]],num);

        tx[IDHI] = rx[IDHI];
        tx[IDLO] = rx[IDLO];
        tx[HEADHI] = rx[HEADHI];
        tx[HEADLO] = rx[HEADLO];
        tx[ADR] = rx[ADR];

        if (rx[FUNC]==5 | rx[FUNC]==6) {
            Registr regist = db.readReg(baseReg);
            if(regist!=null) {
                regist.setValue(num);
                db.update(regist);
                cach.clearCach();
                strTx = new StringBuilder(text.CMDACKNOL);
                 return rx;
            }else return errorMsg(tx,rx);
        }else {
            data =ParserHelper.regsToByteData(startAdr,num,db.readAll(baseReg));
            if (data == null) return errorMsg(tx, rx);
        }
        tx[LENHI] = int2ByteHi(3+dataSize);
        tx[LENLO] = int2ByteLo(3+dataSize);
        tx[FUNC] = rx[FUNC];
        tx[8] =(byte) dataSize;
        status = MessageStatus.OK;
        strTx = new StringBuilder(text.ADDRES)
                .append(tx[ADR])
                .append(text.FUNCTION)
                .append(tx[FUNC])
                .append(text.DATA);
        for (int i = 9,j=0; i <tx.length ; i++,j++) {
            tx[i] = data[j];
            strTx.append(Byte.toUnsignedInt(data[j])).append(" ");
        }
        return tx;
    }

    private boolean fromHash(Message message){
        if (message.getRx()[7]>4) return false;
        String hashKey = eatStringSpace(message.getLogRx(ORIGINAL),4);
        Message mesHash = cach.getFromCach(hashKey);
        if (mesHash!=null) {
            byte[] tx = mesHash.getTx();
            tx[0] = message.getRx()[0];
            tx[1] = message.getRx()[1];
            message.setRegs(mesHash.getRegs());
            message.setTxDecode(mesHash.getLogTx(DECODE));
            message.setTx(tx);
            message.setStatus(mesHash.getStatus());
            return true;
        }
        return false;
    }

    private void toHash(Message message){
        cach.putToCach(eatStringSpace(message.getLogRx(ORIGINAL),4),message);
    }

    public String eatStringSpace(String text,int number){
        StringBuilder str = new StringBuilder(text.trim());
        for (int i = 0; i < number; i++)
            str.delete(0,str.indexOf(" ")+1);
        return str.toString();
    }

    private byte[] errorMsg(byte[] tx,byte[] rx){
        strTx = new StringBuilder(text.ERRREG);
        tx[LENHI] = 0;
        tx[LENLO] = 3;
        tx[FUNC] = (byte) (rx[FUNC] | 128);
        tx[8] = 2 ;
        status = MessageStatus.ERR;
        return Arrays.copyOf(tx,9);
    }
}
