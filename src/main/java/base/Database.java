package base;

import database.CachMap;
import exeptions.NoSuchRegistrs;


public interface Database {
    void create(String name,int id);
    byte [] read(int reg, int num, RegTypes type,int id) throws NoSuchRegistrs;
    int read(int reg, RegTypes type,int id) throws NoSuchRegistrs;
    void setValue(int reg, RegTypes type, int value,int id) throws NoSuchRegistrs;
    void setName(int reg, RegTypes type, String value,int id);
    void clearDb();
    void add(RegTypes type, int reg, int num,int id);
    byte[] readAll(RegTypes type,int id);
    int readValue(RegTypes type, int row,int id);
    String readName(RegTypes type, int row,int id);
    int readReg(RegTypes type, int row,int id);
    int sizeTable(RegTypes type,int id);
    CachMap getCach();
}
