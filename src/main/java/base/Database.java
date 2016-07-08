package base;

import database.Entity.BaseReg;
import database.Entity.Registr;
import java.util.Collection;

public interface Database {
    void create(String name,int id);
    void clearDb();
    int sizeTable(BaseReg baseReg);

    boolean update(Registr reg);
    void add(Registr reg);
    Registr readReg(int reg, BaseReg baseReg);
    Collection<Registr> readAll(BaseReg baseReg);
}
