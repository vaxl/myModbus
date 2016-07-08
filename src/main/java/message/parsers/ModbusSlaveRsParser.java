package message.parsers;

import base.*;
import database.CachMap;
import database.Db;
import database.Entity.BaseReg;
import database.Entity.Registr;
import factory.FactorySetup;
import message.Message;
import settings.*;
import java.util.Arrays;

import static base.View.logView.*;
import static helpers.LogicHelper.*;
import static message.parsConst.ModbusRs.*;
import static message.parsConst.ModbusTcp.FUNC;

public class ModbusSlaveRsParser implements ParseMessage {
    private StringBuilder strRx;
    private StringBuilder strTx;
    private MessageStatus status;
    private Database db = Db.getInstance();
    private View view = (View) FactorySetup.getClazz("View");
    private Text text = (Text) FactorySetup.getClazz("text.xml");
    private Setup setup = (Setup) FactorySetup.getClazz("setup.xml");
    private CachMap cach = CachMap.getInstance();

    @Override
    public void execute(Message message) {
        byte[] rx = message.getRx();
        if (isRightPack(rx)) {
            if (!fromHash(message)) {
                message.setTx(parsePack(rx));
                message.setRxDecode(strRx.toString());
                message.setTxDecode(strTx.toString());
                message.setStatus(status);
                cach.putToCach(message.getLogRx(ORIGINAL),message);
            }
        }else message.setStatus(MessageStatus.NOANSWER);
    }

    private byte[] parsePack(byte[] rx) {
        int reg = twoByte2Int(rx[REGHI],rx[REGLO]);
        int num = twoByte2Int(rx[NUMHI],rx[NUMLO]);
        int id = rx[ID];
        BaseReg baseReg = new BaseReg(id, RegTypes.values()[rx[FUNC]]);

        strRx = new StringBuilder()
                      .append(text.ADDRES)
                      .append(rx[ID])
                      .append(text.FUNCTION)
                      .append(rx[FUNC])
                      .append(text.REGISTR)
                      .append(reg);

        if (rx[FUNC]<5)  strRx.append(text.NUMBER).append(num);
            else strRx.append(text.DATA).append(num);
        if(!check(rx)) {
            status= MessageStatus.NOANSWER;
            return null;
        }

        byte[] data;
        if (rx[FUNC]<5){
            data =ParserHelper.regsToByteData(reg,num,db.readAll(baseReg));
            if (data==null) return errorMsg(rx);
        }
        else{
            Registr regCurrent = db.readReg(reg,baseReg);
            if(regCurrent!=null) {
                regCurrent.setValue(num);
                db.update(regCurrent);
                cach.clearCach();
                strTx = new StringBuilder(text.CMDACKNOL);
                return rx;
            }else return errorMsg(rx);
        }

        int size = data.length + 5;
        byte tx[] = new byte[size];
        tx[ID] = rx[ID];
        tx[FUNC] = rx[FUNC];
        tx[2] =(byte) data.length;
        strTx = new StringBuilder(text.ADDRES)
                          .append(tx[ID])
                          .append(text.FUNCTION)
                          .append(tx[FUNC])
                          .append(text.NUMBER)
                          .append(tx[2])
                          .append(text.DATA);

        for (int i = 3; i <data.length+3 ; i++) {
            tx[i]=data[i-3];
            strTx.append(tx[i]).append(" ");
        }
        int crcTx= crc16(Arrays.copyOf(tx,tx.length-2));
        tx[tx.length-2] = int2ByteLo(crcTx);
        tx[tx.length-1] = int2ByteHi(crcTx);
        return tx;
    }

    private byte[] errorMsg(byte[] rx) {
        strTx = new StringBuilder(text.ERRREG);
        byte[] errtx = new byte[5];
        errtx[ID]= rx[ID];
        errtx[FUNC] =(byte) (rx[FUNC] | 128);
        errtx[2] = 2;
        int crcTxErr= crc16(Arrays.copyOf(errtx,3));
        errtx[3] = int2ByteLo(crcTxErr);
        errtx[4] = int2ByteHi(crcTxErr);
        return errtx;
    }

    private boolean check(byte rx[]){
        final int CRCLO=6;
        final int CRCHI=7;

        int crcRx = twoByte2Int(rx[CRCHI],rx[CRCLO]);
        int crc = crc16(Arrays.copyOf(rx,6));
        if(crcRx!=crc) {
            view.print(text.ERRCRC);
            return false;
        }
        if (rx[ID]!=setup.id) {
            view.print(text.ERRID);
            return false;
        }
        return true;
    }

    private boolean fromHash(Message message) {
        if (message.getRx()[FUNC]>4) return false;
        Message mesHash = cach.getFromCach(message.getLogRx(ORIGINAL));
        if (mesHash!=null) {
            message.setRxDecode(mesHash.getLogRx(DECODE));
            message.setTxDecode(mesHash.getLogRx(DECODE));
            message.setTx(mesHash.getTx());
            message.setStatus(mesHash.getStatus());
            return true;
        }
        return false;
    }

    private boolean isRightPack(byte[] rx) {
        if (rx.length!=8) {
            view.print(text.ERRPACK);
            return false;
        }
        if (rx[FUNC]>6 | rx[FUNC]<1) {
            view.print(text.ERRFUNC);
            return false;
        }
        return true;
    }
}
