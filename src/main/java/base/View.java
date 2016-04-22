package base;

import message.Message;

public interface View {
    enum logView{ORIGINAL,HEX,TEXT}
    static String logsTypes(){
        StringBuilder str = new StringBuilder();
        for (logView l: View.logView.values())
            str.append(l).append(" ");
        return str.toString();
    }
    void print(String text);
    void print(Message text);
    String readText();
    void setLogView(logView logView);
}
