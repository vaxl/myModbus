package message;

import base.PortMessage;

public class PortMessageImpl implements PortMessage {
    private String data;

    public PortMessageImpl(String data) {
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
        if (data == null) return "no data";
        return data;
    }

    @Override
    public String toString() {
        return "PortMessageImpl{" +
                "data='" + data + '\'' +
                '}';
    }
}
