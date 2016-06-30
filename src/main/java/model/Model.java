package model;

import base.*;
import database.Registr;
import database.RegistrsHashMap;
import factory.FactorySetup;
import helpers.LogicHelper;
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
        initDatabase();
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
        Database db = (Database) FactorySetup.getClazz("Database");
        if(db==null){
            db = new RegistrsHashMap();
            FactorySetup.addToFactory("Database", db);
        }
    }

    public void addDb(Registr reg, int num){
        Database db = (Database) FactorySetup.getClazz("Database");
        for (int i = reg.getReg(); i <reg.getReg()+num ; i++) {
            db.add(new Registr(reg.getId(),i,reg.getType()));
        }
    }

    public void stop(){
        if (connection!=null) connection.stop();
    }

    public void clearCach() {
        Database db = (Database) FactorySetup.getClazz("Database");
        db.getCach().clearCach();
    }

    public void event(Registr reg) {
        if(connection==null) return;
        connection.event(reg);
    }
}
