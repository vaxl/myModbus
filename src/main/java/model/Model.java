package model;

import base.Connection;
import base.View;
import factory.FactorySetup;
import helpers.ReflectionHelper;
import settings.Setup;
import settings.Text;

public class Model {
    private Setup setup;
    private View view;
    private Text text;

    public void start(){
        Connection connection = (Connection) ReflectionHelper.createInstance(setup.connection);
        if (connection!=null) {
            if (connection.init()) connection.start();
        }else view.print(text.NOCONNECT);
    }

    public void init(){
        FactorySetup.readXml();
        setup = (Setup) FactorySetup.getClazz("setup.xml");
        view = (View) FactorySetup.getClazz("View");
        text = (Text) FactorySetup.getClazz("text.xml");
        view.setLogView(View.logView.valueOf(setup.logView));
    }
    public void refreshConf(){
        FactorySetup.readXml("setup.xml");
        setup = (Setup) FactorySetup.getClazz("setup.xml");
    }

}
