package base;

import database.CachMap;
import exeptions.NoSuchRegistrs;


public interface Database {
    void create(String name);
    byte [] read(int reg, int num, RegTypes type) throws NoSuchRegistrs;
    int read(int reg, RegTypes type) throws NoSuchRegistrs;
    void setValue(int reg, RegTypes type, int value) throws NoSuchRegistrs;
    void setName(int reg, RegTypes type, String value);
    void clearDb();
    void add(RegTypes type, int reg, int num);
    byte[] readAll(RegTypes type);
    int readValue(RegTypes type, int row);
    String readName(RegTypes type, int row);
    int readReg(RegTypes type, int row);
    int sizeTable(RegTypes type);
    CachMap getCach();
}
