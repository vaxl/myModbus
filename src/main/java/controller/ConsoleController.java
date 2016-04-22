package controller;


import base.View;
import factory.FactorySetup;
import model.Model;
import settings.Text;
import view.ConsoleView;

public class ConsoleController {
    private Model model = new Model();
    private ConsoleView view = new ConsoleView();
    private Text text;

    private ConsoleController() {
        FactorySetup.addToFactory("View",view);
    }

    public static void main(String[] args) {
        ConsoleController controller = new ConsoleController();
        controller.model.init();
        controller.text = (Text) FactorySetup.getClazz("text.xml");

        while(true) {
            controller.cmd(controller.view.readText());
        }

    }

    private void cmd(String cmd){
            switch (cmd) {
                case "start": {
                    model.start();
                    break;
                }
                case "exit": {
                    System.exit(0);
                    break;
                }
                case "setlog": {
                    setLog();
                    break;
                }
                default:view.print(text.NOCMD);
            }
    }

    private void setLog(){
        view.print(text.ENTERLOG + View.logsTypes());
        view.setLogView(View.logView.valueOf(view.readText()));
    }
}
