package database.Entity;

import base.RegTypes;

public class TableRegs {
    private final RegTypes type;
    private final int id;

    public TableRegs(int id, RegTypes type) {
        this.id = id;
        this.type = type;
    }

    public RegTypes getType() {
        return type;
    }

    public int getId() {
        return id;
    }

}
