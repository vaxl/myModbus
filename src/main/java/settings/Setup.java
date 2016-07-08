package settings;

import factory.FactorySetup;

public class Setup {
    private static Setup setup;

    public int port;
    public int id;
    public String connection;
    public String messageWork;
    public String protocol;
    public String logView;
    public String database;

    public String rsPort;
    public int baud;
    public int bits;
    public int stop;
    public int parity;

    public Setup(){}

    public static Setup getInstance(){
        if (setup == null) {
            setup = (Setup) FactorySetup.getClazz("setup.xml");
        }
        return setup;
    }
}
