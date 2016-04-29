package model;

import base.Connection;
import base.Database;
import base.View;
import database.RegistrsHashMap;
import factory.FactorySetup;
import helpers.ReflectionHelper;
import settings.Setup;
import settings.Text;

public class Model {
    private Setup setup;
    private View view;
    private Text text;
    private Connection connection;
    private final String DIRCON = "portWork.";

    public Model() {
        FactorySetup.readXml();
    }

    public void start(){
        connection = (Connection) ReflectionHelper.createInstance(DIRCON + setup.connection);
        if (connection!=null) {
                new Thread(connection,"Connection").start();
        }else view.print(text.NOCONNECT);
    }

    public void init(){
        setup = (Setup) FactorySetup.getClazz("setup.xml");
        view = (View) FactorySetup.getClazz("View");
        text = (Text) FactorySetup.getClazz("text.xml");
        view.setLogView(View.logView.valueOf(setup.logView));
    }
    public void refreshConf(){
        FactorySetup.readXml("setup.xml");
        setup = (Setup) FactorySetup.getClazz("setup.xml");
    }

    public void write(byte[] message) {
        if (connection.isAlive())
            connection.write(message);
    }

    public void initDatabase(){
        FactorySetup.addToFactory("Database",new RegistrsHashMap());
    }



    public void stop(){
        if (connection!=null) connection.stop();
    }

    public void clearCach() {
        Database db = (Database) FactorySetup.getClazz("Database");
        db.clearCach();
    }
}
