package view;

import base.RegistrsTypes;
import base.View;
import base.MessageStatus;
import message.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class ConsoleView implements View {
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private logView logView;

    @Override
    public void print(String text) {
        System.out.println(text);
    }

    @Override
    public String readText() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void print(Message message) {
        if (message!=null) {
            switch (logView) {
                case ORIGINAL:
                    System.out.println(message.getRxString());
                    if (message.getStatus() != MessageStatus.NOANSWER) System.out.println(message.getTxString());
                    break;
                case HEX:
                    System.out.println(message.getRxHexString());
                    if (message.getStatus() != MessageStatus.NOANSWER) System.out.println(message.getTxHexString());
                    break;
                case TEXT:
                    System.out.println(message.getRxText());
                    if (message.getStatus() != MessageStatus.NOANSWER) System.out.println(message.getTxText());
                    break;
                case DECODE:
                    System.out.println(message.getRxDecode());
                    if (message.getStatus() != MessageStatus.NOANSWER) System.out.println(message.getTxDecode());
                    break;
                case ONLYERRORS:
                    if (message.getStatus() == MessageStatus.ERR) {
                        System.out.println(message.getRxDecode());
                        System.out.println(message.getTxDecode());
                    }
                case OFF:
                    break;
                default:
                    System.out.println("err");
            }
        }
    }

    @Override
    public void dbChanged() {

    }

    @Override
    public void setLogView(View.logView logView) {
        this.logView = logView;
    }

}
