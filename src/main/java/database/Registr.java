package database;

import base.RegTypes;

public class Registr {
    private String name;
    private int value;
    private int reg;
    private RegTypes type;
    private int id;

    public Registr(int id,int reg, RegTypes type) {
        this.reg = reg;
        this.type = type;
        this.value = 0;
        this.name = type.name()+ " " +reg;
        this.id =id;
    }

    public Registr(int id,int reg, RegTypes type, int value,String name) {
        this.name = name;
        this.value = value;
        this.reg = reg;
        this.type = type;
        this.id =id;
    }

    public Registr(int id,int reg, RegTypes type,int value) {
        this.value = value;
        this.reg = reg;
        this.type = type;
        this.id=id;
        this.name = type.name()+ " " +reg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getReg() {
        return reg;
    }

    public void setReg(int reg) {
        this.reg = reg;
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
