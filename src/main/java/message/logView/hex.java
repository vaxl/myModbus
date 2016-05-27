package message.logView;


import base.Logger;

public class hex implements Logger {
    @Override
    public String execute(byte[] message) {
        StringBuilder str = new StringBuilder();
        for (byte i: message )
            str.append(Integer.toHexString(Byte.toUnsignedInt(i))).append(" ");
        return str.toString();
    }
}