package controller;

import base.View;
import factory.FactorySetup;
import model.Model;
import settings.Text;
import view.GuiView;

public class GuiController {
    private Model model = new Model();
    private GuiView view = new GuiView(this);
    private Text text;

    private GuiController() {
        FactorySetup.addToFactory("View",view);
    }

    public static void main(String[] args) {
        GuiController controller = new GuiController();
        controller.model.init();
        controller.text = (Text) FactorySetup.getClazz("text.xml");
    }

    public void cmd(String cmd){
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
        //view.setLogView(View.logView.valueOf(view.readText()));
    }
}
