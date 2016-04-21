package message;

import base.LogWork;
import base.MessageStatus;
import base.ParseMessage;
import exeptions.NoSuchRegistrs;
import factory.FactorySetup;
import settings.*;

import java.util.Arrays;

import static helpers.LogicHelper.*;

public class ModbusSlaveTcpParser implements ParseMessage {
    private LogWork messageWork = (LogWork) FactorySetup.factory.get("messageWork");
    private Text text = (Text) FactorySetup.factory.get("text.xml");
    private StringBuilder strRx;
    private StringBuilder strTx;
    @Override
    public void execute(Message message) {

        if (isRightPack(message.getRx())) {
            message.setTx(parsePack(message.getRx()));
            message.setTextRx(strRx.toString());
            message.setTextTx(strTx.toString());
            message.setStatus(MessageStatus.OK);
        }else message.setStatus(MessageStatus.NOANSWER);
    }

    private boolean isRightPack(byte[] rx){
        if (rx.length!=12){
            messageWork.print(text.ERRPACK);
            return false;
        }
        if (rx[7]>4 | rx[7]<1){
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

        int startAdr = twoByte2Int(rx[REGHI],rx[REGLO]);
        int num = twoByte2Int(rx[NUMHI],rx[NUMLO]);
        if (rx[FUNC]==3 | rx[FUNC]==4) dataSize = num* 2;
            else  dataSize = bitInByte(num);
        byte [] data = new byte[dataSize];
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
            data = getData(data);
        }catch (NoSuchRegistrs e) {
            strTx = new StringBuilder(text.TX)
                              .append(text.ERRREG);
            tx[LENHI] = 0;
            tx[LENLO] = 3;
            tx[FUNC] = (byte) (rx[FUNC] | 128);
            tx[8] = 2 ;
            return Arrays.copyOf(tx,9);
        }

        tx[LENHI] = int2ByteHi(3+dataSize);
        tx[LENLO] = int2ByteLo(3+dataSize);
        tx[FUNC] = rx[FUNC];
        tx[8] =(byte) dataSize;

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

    private byte[] getData (byte [] data) throws NoSuchRegistrs{
        // Arrays.fill(data,(byte)1);
        throw new NoSuchRegistrs();
       // return data;
    }
}
