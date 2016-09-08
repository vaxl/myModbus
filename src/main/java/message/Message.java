package message;

import base.MessageStatus;
import base.View;
import database.Entity.Registr;
import database.Entity.Registrs;

import static base.View.logView.*;
import java.util.List;

public class Message  {
    private byte[] rx;
    private byte[] tx;
    private MessageStatus status;
    private String textTx;
    private Registrs regs;

    public void setTxDecode(String textTx) {
        this.textTx = textTx;
    }

    public Message(byte[] data) {
        this.rx = data;
    }

    public Message() {
    }

    public byte[] getTx() {
        return tx;
    }
    public byte[] getRx() {
        return rx;
    }

    public void setTx(byte[] tx) {
        this.tx = tx;
    }
    public void setTx(List<Byte> txx) {
        byte[] tx = new byte[txx.size()];
        int i = 0;
        for (Byte b : txx) {
            tx[i] = b;
            i++;
        }
        this.tx = tx;
    }

    public MessageStatus getStatus() {
        return status;
    }
    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public String getLogTx(View.logView logView){
        if (logView == DECODE | logView == ONLYERRORS) return textTx;
        return  MessageLogExec.execute(logView,tx);
    }
    public String getLogRx(View.logView logView){
        if (logView == DECODE | logView == ONLYERRORS) return regs.toString();
        return  MessageLogExec.execute(logView,rx);
    }

    public Registrs getRegs() {
        return regs;
    }

    public void setRegs(Registrs regs) {
        this.regs = regs;
    }
}
