package message.logView;


import base.Logger;

public class off implements Logger {
    @Override
    public String execute(byte[] message) {
        return "";
    }
}