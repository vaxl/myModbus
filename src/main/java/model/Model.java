package model;

import base.*;
import database.CachMap;
import database.Db;
import database.Entity.Registr;
import database.Entity.Registrs;
import factory.FactorySetup;
import helpers.ReflectionHelper;
import portWork.AbstractServer;
import settings.Setup;
import settings.Text;

public class Model {
    private Setup setup;
    private View view;
    private Text text;
    private AbstractServer connection;
    private final String DIRCON = "portWork.";

    public Model() {
        FactorySetup.readXml();
    }

    public void start(){
        connection = (AbstractServer) ReflectionHelper.createInstance(DIRCON + setup.connection);
        if (connection!=null) {
                new Thread(connection,"Connection").start();
        }else view.print(text.NOCONNECT);
    }

    public void init(){
        setup = Setup.getInstance();
        view = (View) FactorySetup.getClazz("View");
        text = Text.getInstance();
    }

    public void write(byte[] message) {
        if (connection.isAlive())
            connection.write(message);
    }

    public void addDb(Registr reg, int num){
        for (int i = reg.getReg(); i <reg.getReg()+num ; i++)
            Db.getInstance().add(new Registr(reg.getId(),i,reg.getType()));
    }

    public void stop(){
        if (connection!=null) connection.stop();
    }

    public void clearCach() {
        CachMap.getInstance().clearCach();
    }

    public void event(Registrs reg) {
        if(connection==null) return;
        connection.event(reg);
    }
}
