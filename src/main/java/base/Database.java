package base;

import database.CachMap;
import database.Registr;

import java.util.Collection;
import java.util.Map;

public interface Database {
    void create(String name,int id);
    void clearDb();

    boolean update(Registr reg);
    void add(Registr reg);
    Registr readReg(int reg, RegTypes type,int id);
    Collection<Registr> readAll(RegTypes type, int id);
    /*byte [] read(int reg, int num, RegTypes type,int id);*/

    Map<Integer,Registr> getMap(RegTypes type, int id);

    int sizeTable(RegTypes type, int id);
    CachMap getCach();
}
