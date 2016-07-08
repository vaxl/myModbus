package database;

import base.*;
import database.Entity.BaseReg;
import database.Entity.Registr;
import factory.FactorySetup;
import helpers.ExcelHelper;
import java.util.*;

public class HashMapDB implements Database{
    private static final String RESOURCES = "xls\\";
    private Map<Integer,Map<RegTypes,TreeMap<Integer,Registr>>> databases = new HashMap<>();
    private View view = (View) FactorySetup.getClazz("View");

    private void init(int id) {
        databases.putIfAbsent(id, new HashMap<>());
        for (RegTypes r : RegTypes.values())
            databases.get(id).putIfAbsent(r,new TreeMap<>());
    }

    @Override
    public void clearDb() {
        databases.clear();
    }

    @Override
    public void create(String name, int id) {
        final int NAME=0;
        final int REG=1;
        final int VALUE=3;
        final int FUNC=2;

        init(id);
        if (name.equals("test")) {
        for (int i = 0; i <100; i++)     add(new Registr(id,300+i,RegTypes.HOLDING,i));
        for (int i = 0; i <18; i++)      add(new Registr(id,400+i,RegTypes.INPUTREG,18-i));
        for (int i = 1; i <18; i++)      add(new Registr(id,99+i,RegTypes.COILS,i%2));
        for (int i = 1; i <18; i++)      add(new Registr(id,199+i,RegTypes.DINPUT,i%2));
        for (int i = 1; i <5; i++)       add(new Registr(id,199+i,RegTypes.SINGLEBIT,i%2));
        for (int i = 1; i <5; i++)       add(new Registr(id,100+i,RegTypes.SCALEDMESURE,i));
        return;
        }
        if (name.contains(".")) {
            List<String[]> list = ExcelHelper.readLines(RESOURCES + name);
            if (list != null)
                for (String[] line : list) {
                    RegTypes type = RegTypes.values()[Integer.valueOf(line[FUNC])];
                    int val;
                    int reg;
                    try {
                        val = Integer.valueOf(line[VALUE]);
                        reg = Integer.valueOf(line[REG]);
                    } catch (Exception e) {continue;}
                    add(new Registr(id,reg,type,val,line[NAME]));
                }
        }
    }

    @Override
    public void add(Registr reg) {
        databases.putIfAbsent(reg.getId(),new HashMap<>()).putIfAbsent(reg.getType(),new TreeMap<>());
            getMap(reg).put(reg.getReg(), reg);
    }

    @Override
    public Registr readReg(int reg, BaseReg baseReg){
        return getMap(baseReg).containsKey(reg)? getMap(baseReg).get(reg) : null;
    }

    @Override
    public Collection<Registr> readAll(BaseReg baseReg){
        return  getMap(baseReg).values();
    }

    @Override
    public boolean update(Registr reg){
        if (!getMap(reg).containsKey(reg.getReg())) return false;
        add(reg);
        view.dbChanged(reg.getId(),reg.getType());
        return true;
    }

    private Map<Integer,Registr> getMap(BaseReg baseReg){
            return databases.get(baseReg.getId()).get(baseReg.getType());
    }

    @Override
    public int sizeTable(BaseReg baseReg) {
        return getMap(baseReg).size();
    }
}
