package controller;


import factory.FactorySetup;
import helpers.LogicHelper;
import model.Model;
import settings.Setup;
import settings.Text;
import view.ConsoleView;

public class ConsoleController {
    private Model model = new Model();
    private ConsoleView view = new ConsoleView();
    private Text text;
    private Setup setup;

    private ConsoleController() {
        FactorySetup.addToFactory("View",view);
    }

    public static void main(String[] args) {
        ConsoleController controller = new ConsoleController();
        controller.model.init();
        controller.text = Text.getInstance();
        controller.setup = Setup.getInstance();

        while(true) {
            controller.cmd(controller.view.readText());
        }

    }

    private void cmd(String msg){
        String [] cmd = msg.split(" ");
        switch (cmd[0]) {
            case "start": {
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

            case "protocol":{
                if (cmd[1]==null) view.print(text.ENTERPROTOCOL);
                else setup.protocol= cmd[1];
                break;
            }
            case "connection": {
                if (cmd[1] == null) view.print(text.ENTERPROTOCOL);
                else setup.connection = cmd[1];
                break;
            }
            case "tx": {
                if (setup.protocol.equals("Text"))
                    model.write(LogicHelper.textToByte(cmd[1]));
                else model.write(LogicHelper.strByteToByte(cmd[1]));
                break;
            }
            default:view.print(text.NOCMD);
        }
    }

}
