package factory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;


public class FactorySetup {
    private static  HashMap<String,Object> factory = new HashMap<>();
    private static final String RESOURCES = "xml/";

    public static void readXml(String file) {
        factory.put(file,ReadXMLFileSAX.readXML(RESOURCES + file));
    }

    public static void readXml() {
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

    public static void init(){
        readXml();
    }

    public static void addToFactory(String clazz,Object object){
        factory.put(clazz,object);
    }

    public static Object getClazz(String name){
        return factory.get(name);
    }
}
