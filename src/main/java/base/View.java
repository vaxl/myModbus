package base;

import message.Message;
import view.GuiModbusTableView;

import java.util.Map;

public interface View {
    enum logView{ORIGINAL,HEX,TEXT,OFF,ONLYERRORS,DECODE}
    static String logsTypes(){
        StringBuilder str = new StringBuilder();
        for (logView l: View.logView.values())
            str.append(l).append(" ");
        return str.toString();
    }
    void print(String text);
    void print(Message text);
    String readText();
    void dbChanged(int id,RegTypes regTypes);
    void createIdTab();
}
