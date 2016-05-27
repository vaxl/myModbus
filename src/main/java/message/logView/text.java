package message.logView;


import base.Logger;

public class text implements Logger {
    @Override
    public String execute(byte[] message) {
        StringBuilder str = new StringBuilder();
        for (byte i: message )
            str.append(Character.valueOf((char)i)).append(" ");
        return str.toString();
    }
}