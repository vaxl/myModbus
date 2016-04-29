package controller;

import base.View;
import factory.FactorySetup;
import helpers.LogicHelper;
import model.Model;
import settings.Setup;
import settings.Text;
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

    public void cmd(String msg){
        String [] cmd = msg.split(" ",2);
        switch (cmd[0].trim()) {
            case "run": {
                model.start();
                break;
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
                if (cmd[1]==null) view.print(text.ENTERLOG);
                else {
                    setLog(cmd[1]);
                }
                break;
            }
            case "setProtocol":{
                if (cmd[1]==null) view.print(text.ENTERPROTOCOL);
                else setup.protocol= cmd[1].trim();
                break;
            }
            case "setConnection":{
                if (cmd[1]==null) view.print(text.ENTERPROTOCOL);
                else setup.connection= cmd[1].trim();
                break;
            }
            case "tx": {
                if (cmd[1]==null | cmd[1].equals("")) break;
                if (setup.protocol.equals("Text"))
                    model.write(LogicHelper.textToByte(cmd[1]));
                else model.write(LogicHelper.strByteToByte(cmd[1]));
                view.print(text.TX + cmd[1]);
                break;
            }
            case "addDb": {
                model.initDatabase();
                view.createTable();
                break;
            }
            case "clearCach":{
                model.clearCach();
                break;
            }
            default:view.print(text.NOCMD);
        }
    }

    private void setLog(String log){
        view.setLogView(View.logView.valueOf(log));
    }


}
