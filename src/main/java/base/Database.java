package base;

import exeptions.NoSuchRegistrs;

public interface Database {
    void create();
    byte [] read(int reg,int num,RegistrsTypes type) throws NoSuchRegistrs;
    void update (int reg,int num,RegistrsTypes type) ;
}
