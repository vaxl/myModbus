package settings;

import factory.FactorySetup;

public class Text {
     private static Text text;

     public Text(){}

     public static Text getInstance(){
          if (text == null) {
               text = (Text) FactorySetup.getClazz("text.xml");
          }
          return text;
     }

     public String PORTOPEN = "PORTOPEN";
     public String PORTCLOSE = "PORTCLOSE";
     public String CONNECTED = "CONNECTED";
     public String ERRPACK = "ERRPACK";
     public String ERRFUNC = "ERRFUNC";
     public String RX = "RX";
     public String TX = "tx";
     public String REGISTR = "registr";
     public String ADDRES = "addres";
     public String FUNCTION = "function";
     public String NUMBER = "number";
     public String DATA = "data";
     public String ERRREG = "ERRREG";
     public String NOCONNECT = "NOCONNECT";
     public String NOCMD = "NOCMD";
     public String ENTERLOG = "ENTERLOG";
     public String CONNECTIONLOST = "CONNECTIONLOST";
     public String ENTERPROTOCOL = "ENTERPROTOCOL";
     public String ENTERCONNECTION = "ENTERCONNECTION";
     public String GUINAME = "GUINAME";
     public String GUICONNECT = "GUICONNECT";
     public String GUISTART = "GUISTART";
     public String GUISTOP = "GUISTOP";
     public String GUILOGGER = "GUILOGGER";
     public String GUIPROTOCOL = "GUIPROTOCOL";
     public String GUICONNECTION = "GUICONNECTION";
     public String GUICLEAR = "GUICLEAR";
     public String GUISEND = "GUISEND";
     public String CMDACKNOL = "CMDACKNOL";
     public String ERRDB = "ERRDB";
     public String GUIDB = "GUIDB";
     public String ERRCRC = "ERRCRC";
     public String CRC = "CRC";
     public String ERRID = "ERRID";
     public String GI = "GI";
     public String STARTACT = "STARTACT";
     public String TESTACT = "TESTACT";
     public String OK = "OK";
     public String ENTERREGS = "ENTERREGS";
     public String ERRREGS = "ERRREGS";
     public String ADDREGS = "ADDREGS";
     public String ADDDB = "ADDDB";
     public String DB = "DB";

}
