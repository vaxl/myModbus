package base;

import database.Entity.TableRegs;
import database.Entity.Registr;
import java.util.Collection;

public interface Database {
    void create(String name,int id);
    void clearDb();
    int sizeTable(TableRegs tableRegs);
    boolean update(Registr reg);
    void add(Registr reg);
    Registr readReg(Registr baseReg);
    Collection<Registr> readAll(TableRegs tableRegs);
}
