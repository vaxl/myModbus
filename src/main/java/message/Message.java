package message;

import base.MessageStatus;
import base.View;
import static base.View.logView.*;
import java.util.List;

public class Message  {
    private byte[] rx;
    private byte[] tx;
    private MessageStatus status;
    private String textRx;
    private String textTx;

    public void setRxDecode(String textRx) {
        this.textRx = textRx;
    }
    public void setTxDecode(String textTx) {
        this.textTx = textTx;
    }

    public Message(byte[] data) {
        this.rx = data;
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
        if (logView == DECODE | logView == ONLYERRORS) return textRx;
        return  MessageLogExec.execute(logView,rx);
    }
}
