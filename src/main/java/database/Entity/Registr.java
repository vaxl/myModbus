package database.Entity;

import base.RegTypes;

public class Registr extends TableRegs implements Comparable<Registr>{
    private String name;
    private int value;
    final private int reg;

    public Registr(int id,int reg, RegTypes type) {
        super(id,type);
        this.reg = reg;
        this.value = 0;
        this.name = type.name()+ " " +reg;
    }

    public Registr(int id,int reg, RegTypes type, int value,String name) {
        super(id,type);
        this.name = name;
        this.value = value;
        this.reg = reg;
    }

    public Registr(int id,int reg, RegTypes type,int value) {
        super(id,type);
        this.value = value;
        this.reg = reg;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Registr registr = (Registr) o;

        return reg == registr.reg;

    }

    @Override
    public int hashCode() {
        return reg;
    }

    @Override
    public int compareTo(Registr o) {
        return reg-o.getReg();
    }
}
