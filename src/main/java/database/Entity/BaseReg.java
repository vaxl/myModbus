package database.Entity;

import base.RegTypes;

public class BaseReg {
    private RegTypes type;
    private int id;

    public BaseReg(int id, RegTypes type) {
        this.id = id;
        this.type = type;
    }

    public RegTypes getType() {
        return type;
    }

    public void setType(RegTypes type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
