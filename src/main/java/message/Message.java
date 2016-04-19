package message;

import base.PortMessage;

public class Message implements PortMessage {
    private String data;

    public Message(String data) {
        this.data = data;
    }

    @Override
    public void answer() {
        //TODO
    }

    @Override
    public String toHex() {
        return null;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "data='" + data + '\'' +
                '}';
    }


}
