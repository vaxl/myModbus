package factory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;


public class FactorySetup {
    static public HashMap<String,Object> factory = new HashMap<>();
    private final String RESOURCES = "xml/";

    public void readXml(String clazz) {
        factory.put(clazz,ReadXMLFileSAX.readXML(RESOURCES + clazz));
    }

    public void readXml() {
        File file = new File(RESOURCES);
        File [] files = file.listFiles();
        if (files!=null) {
            try {
                for (File f : files) {
                    factory.put(f.getName(), ReadXMLFileSAX.readXML(f.getCanonicalPath()));
                    System.out.println("factory generate filename =  " +  f.getName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void init (){

    }

}
