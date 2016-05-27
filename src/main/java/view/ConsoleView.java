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
                case ONLYERRORS:
                    if (message.getStatus() == MessageStatus.ERR) {
                        System.out.println(message.getLogTx(logView));
                        System.out.println(message.getLogRx(logView));
                    }
                case OFF:
                    break;
                default:
                    if (message.getStatus() != MessageStatus.NOANSWER) System.out.println(message.getLogTx(logView));
                    if (message.getStatus() != MessageStatus.SEND) System.out.println(message.getLogRx(logView));
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

    @Override
    public String getDbType() {
        return null;
    }

    @Override
    public void createTable() {

    }
}
