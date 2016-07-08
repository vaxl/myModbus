package controller;

import base.View;
import database.Entity.Registr;
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
        view.createIdTab();
    }

    public void addRegs(Registr reg, int num){
        model.addDb(reg, num);
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

    public void event(Registr reg) {
         model.event(reg);
    }
}
