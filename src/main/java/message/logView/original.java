package message.logView;

import base.Logger;

public class original implements Logger {
    @Override
    public String execute(byte[] message) {
        StringBuilder str = new StringBuilder();
        for (byte i: message )
            str.append(Byte.toUnsignedInt(i)).append(" ");
        return str.toString();
    }
}