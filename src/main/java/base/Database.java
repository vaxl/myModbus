package base;

import exeptions.NoSuchRegistrs;
import message.Message;

import java.util.Map;

public interface Database {
    void create(String name);
    byte [] read(int reg,int num,RegistrsTypes type) throws NoSuchRegistrs;
    void update(int reg, int num, RegistrsTypes type, int value) throws NoSuchRegistrs;
    Map getMap( RegistrsTypes type);
    void clearCach();
    void putToCach(String key, Message message);
    Message getFromCach(String key);
    String getName(int reg, RegistrsTypes type);
    void setName(int reg, RegistrsTypes type, String value);
    void clearDb();
    void add(RegistrsTypes type,int reg,int num);
}
