package view;

import base.View;
import base.MessageStatus;
import message.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
                    System.out.println(message.getRxToString());
                    if (message.getStatus() != MessageStatus.NOANSWER) System.out.println(message.getTxToString());
                    break;
                case HEX:
                    System.out.println(message.getRxToHexString());
                    if (message.getStatus() != MessageStatus.NOANSWER) System.out.println(message.getTxToHexString());
                    break;
                case TEXT:
                    System.out.println(message.getTextRx());
                    if (message.getStatus() != MessageStatus.NOANSWER) System.out.println(message.getTextTx());
                    break;
                default:
                    System.out.println("err");
            }
        }
    }

    @Override
    public void setLogView(View.logView logView) {
        this.logView = logView;
    }

}
