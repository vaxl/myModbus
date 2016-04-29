package message;

import base.MessageStatus;
import factory.FactorySetup;
import settings.Text;

public class Message  {
    private Text text = (Text) FactorySetup.getClazz("text.xml");
    private byte[] rx;
    private byte[] tx;
    private MessageStatus status;
    private String textRx;
    private String textTx;

    void setRxDecode(String textRx) {
        this.textRx = textRx;
    }

    void setTxDecode(String textTx) {
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

    public String getRxString() {
        StringBuilder str = new StringBuilder(text.RX);
        for (byte i: rx )
            str.append(Byte.toUnsignedInt(i)).append(" ");
        return str.toString();
    }

    public String getTxString() {
        StringBuilder str = new StringBuilder(text.TX);
        for (byte i: tx )
            str.append(Byte.toUnsignedInt(i)).append(" ");
        return str.toString();
    }

    public String getTxHexString() {
        StringBuilder str = new StringBuilder(text.TX);
        for (byte i: tx )
            str.append(Integer.toHexString(Byte.toUnsignedInt(i))).append(" ");
        return str.toString();
    }

    public String getRxHexString() {
        StringBuilder str = new StringBuilder(text.RX);
        for (byte i: rx )
            str.append(Integer.toHexString(Byte.toUnsignedInt(i))).append(" ");
        return str.toString();
    }

    public String getRxDecode() {
        return textRx;
    }

    public String getTxDecode() {
        return textTx;
    }

    public String getRxText() {
        StringBuilder str = new StringBuilder(text.RX);
        for (byte i: rx )
            str.append(Character.valueOf((char)i)).append(" ");
        return str.toString();
    }

    public String getTxText() {
        StringBuilder str = new StringBuilder(text.TX);
        if(tx!=null)
            for (byte i : tx)
                str.append(Character.valueOf((char) i)).append(" ");
        return str.toString();
    }
}
