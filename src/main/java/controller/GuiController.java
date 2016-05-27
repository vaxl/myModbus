package controller;

import base.RegTypes;
import base.View;
import factory.FactorySetup;
import helpers.LogicHelper;
import model.Model;
import settings.*;
import view.GuiView;

public class GuiController {
    private Model model = new Model();
    private GuiView view = new GuiView(this);
    private Text text;
    private Setup setup;

    private GuiController() {
        FactorySetup.addToFactory("View",view);
    }

    public static void main(String[] args) {
        GuiController controller = new GuiController();
        controller.model.init();
        controller.text = (Text) FactorySetup.getClazz("text.xml");
        controller.setup = (Setup) FactorySetup.getClazz("setup.xml");
        controller.view.init();
    }

    public void start(){
        model.start();
    }
    public void stop(){
        model.stop();
    }
    public void setProtocol(String protocol){
        setup.protocol = protocol;
    }

    public void setConnection(String con){
        setup.connection = con;
    }

    public void addDb(){
        model.initDatabase();
        view.createTable();
    }

    public void addRegs(String reg, String num, String type){
        model.addDb(Integer.valueOf(reg),Integer.valueOf(num),RegTypes.values()[Integer.valueOf(type)]);
    }

    public void writeToPort(String  data){
        if (setup.protocol.equals(View.logView.TEXT.toString()))
            model.write(LogicHelper.textToByte(data));
        else model.write(LogicHelper.strByteToByte(data));
        view.print(text.TX + data);
    }

    public void clearCach(){
        model.clearCach();
    }

    public void cmdConsole(String msg){
        String [] cmd = msg.split(" ",2);
        switch (cmd[0].trim()) {
            case "start": {
                start();break;
            }
            case "exit": {
                System.exit(0);
                break;
            }
            case "stop": {
                model.stop();
                break;
            }
            case "setlog": {
                setLog(cmd[1]);
                break;
            }
            case "setProtocol":{
                setProtocol(cmd[1]);
                break;
            }
            case "setConnection":{
                setConnection(cmd[1]);
                break;
            }
            case "addDb": {
                addDb();
                break;
            }
            case "clearCach":{
                clearCach();
                break;
            }
            default:view.print(text.NOCMD);
        }
    }

    public void setLog(String log){
        view.setLogView(View.logView.valueOf(log));
    }

    public void event(RegTypes type, int key) {
         model.event(type,key);
    }
}
