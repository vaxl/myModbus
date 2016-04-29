package base;

import exeptions.NoSuchRegistrs;

import java.util.Map;

public interface Database {
    void create();
    byte [] read(int reg,int num,RegistrsTypes type) throws NoSuchRegistrs;
    void update(int reg, int num, RegistrsTypes type, int value) throws NoSuchRegistrs;
    Map getMap( RegistrsTypes type);
    void clearCach();
    Map getCach();
}
