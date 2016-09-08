package database.Entity;

import base.RegTypes;

public class Registrs extends Registr{
    private int num;


    public Registrs(int id, int reg, RegTypes type, int num) {
        super(id, reg, type);
        this.num = num;
    }


    public Registrs(Registr registr, int num) {
        this(registr.getId(), registr.getReg(),registr.getType(),num);
    }

    public int getNum() {
        return num;
    }

    @Override
    public String toString() {
            return " ID = " + super.getId() +
                " Func = " + super.getType().name() +
                 " Reg = " + super.getReg() +
                  " Num = " + num;
    }
}
