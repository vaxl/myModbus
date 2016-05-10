package message;

import base.*;
import database.RegistrsHashMap;
import exeptions.NoSuchRegistrs;
import factory.FactorySetup;
import settings.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static helpers.LogicHelper.*;
import static helpers.LogicHelper.twoByte2Int;

public class ModbusSlaveTcpParser implements ParseMessage {
    private View messageWork = (View) FactorySetup.getClazz("View");
    private Text text = (Text) FactorySetup.getClazz("text.xml");
    private Database db = (Database) FactorySetup.getClazz("Database");
    private StringBuilder strRx;
    private StringBuilder strTx;
    private MessageStatus status;
    @Override
    public void execute(Message message) {
        if (db==null) db = new RegistrsHashMap();
        byte[] rx = message.getRx();
        if (isRightPack(rx)) {
            if (!fromHash(message)) {
                message.setTx(parsePack(rx));
                message.setRxDecode(strRx.toString());
                message.setTxDecode(strTx.toString());
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
        final byte IDHI = 0;
        final byte IDLO = 1;
        final byte HEADHI=2;
        final byte HEADLO=3;
        final byte LENHI =4;
        final byte LENLO =5;
        final byte ADR  = 6;
        final byte FUNC = 7;
        final byte REGHI =8;
        final byte REGLO =9;
        final byte NUMHI=10;
        final byte NUMLO=11;
        int dataSize;
        byte [] data;

        int startAdr = twoByte2Int(rx[REGHI],rx[REGLO]);
        int num = twoByte2Int(rx[NUMHI],rx[NUMLO]);
        if (rx[FUNC]==3 | rx[FUNC]==4) dataSize = num* 2;
        else if (rx[FUNC]==5 | rx[FUNC]==6) dataSize=rx.length-9;
            else  dataSize = bitInByte(num);
        byte [] tx = new byte[dataSize + 9];

        strRx = new StringBuilder(text.RX)
                          .append(text.ADDRES)
                          .append(rx[ADR])
                          .append(text.FUNCTION)
                          .append(rx[FUNC])
                          .append(text.REGISTR)
                          .append(startAdr)
                          .append(text.NUMBER)
                          .append(num);
        tx[IDHI] = rx[IDHI];
        tx[IDLO] = rx[IDLO];
        tx[HEADHI] = rx[HEADHI];
        tx[HEADLO] = rx[HEADLO];
        tx[ADR] = rx[ADR];

        try{
            if (rx[FUNC]==5 | rx[FUNC]==6) {
                db.update(startAdr,1,RegistrsTypes.values()[rx[FUNC]],num);
                db.clearCach();
                strTx = new StringBuilder(text.TX)
                        .append(text.CMDACKNOL);
                return rx;
            }else
            data = db.read(startAdr,num,RegistrsTypes.values()[rx[FUNC]]);
        }catch (NoSuchRegistrs e) {
            strTx = new StringBuilder(text.TX)
                              .append(text.ERRREG);
            tx[LENHI] = 0;
            tx[LENLO] = 3;
            tx[FUNC] = (byte) (rx[FUNC] | 128);
            tx[8] = 2 ;
            status = MessageStatus.ERR;
            return Arrays.copyOf(tx,9);
        }

        tx[LENHI] = int2ByteHi(3+dataSize);
        tx[LENLO] = int2ByteLo(3+dataSize);
        tx[FUNC] = rx[FUNC];
        tx[8] =(byte) dataSize;
        status = MessageStatus.OK;
        strTx = new StringBuilder(text.TX)
                .append(text.ADDRES)
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
        String hashKey = eatStringSpace(message.getRxString(),4);
        Message mesHash = db.getFromCach(hashKey);
        if (mesHash!=null) {
            byte[] tx = mesHash.getTx();
            tx[0] = message.getRx()[0];
            tx[1] = message.getRx()[1];
            message.setRxDecode(mesHash.getRxDecode());
            message.setTxDecode(mesHash.getTxDecode());
            message.setTx(tx);
            message.setStatus(mesHash.getStatus());
            return true;
        }
        return false;
    }

    private void toHash(Message message){
        db.putToCach(eatStringSpace(message.getRxString(),4),message);
    }

    public String eatStringSpace(String text,int number){
        StringBuilder str = new StringBuilder(text.trim());
        for (int i = 0; i < number; i++)
            str.delete(0,str.indexOf(" ")+1);
        return str.toString();
    }
}
