package message;

import base.MessageStatus;

public class Message  {
    private byte[] rx;
    private byte[] tx;
    private MessageStatus status;
    private String textRx;
    private String textTx;

    void setTextRx(String textRx) {
        this.textRx = textRx;
    }

    void setTextTx(String textTx) {
        this.textTx = textTx;
    }

    public Message(byte[] data) {
        this.rx = data;
    }

    public byte[] getTx() {
        return tx;
    }

    void setTx(byte[] tx) {
        this.tx = tx;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    byte[] getRx() {
        return rx;
    }

    public String getRxToString() {
        StringBuilder str = new StringBuilder();
        for (byte i: rx )
            str.append(Byte.toUnsignedInt(i)).append(" ");
        return str.toString();
    }

    public String getTxToString() {
        StringBuilder str = new StringBuilder();
        for (byte i: tx )
            str.append(Byte.toUnsignedInt(i)).append(" ");
        return str.toString();
    }

    public String getTxToHexString() {
        StringBuilder str = new StringBuilder();
        for (byte i: tx )
            str.append(Integer.toHexString(Byte.toUnsignedInt(i))).append(" ");
        return str.toString();
    }

    public String getRxToHexString() {
        StringBuilder str = new StringBuilder();
        for (byte i: rx )
            str.append(Integer.toHexString(Byte.toUnsignedInt(i))).append(" ");
        return str.toString();
    }

    public String getTextRx() {
        return textRx;
    }

    public String getTextTx() {
        return textTx;
    }
}
