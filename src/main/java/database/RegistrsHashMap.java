package database;

import base.*;
import exeptions.NoSuchRegistrs;
import factory.FactorySetup;
import helpers.ExcelHelper;
import message.Message;
import static  helpers.LogicHelper.*;
import java.util.*;

public class RegistrsHashMap implements Database{
    private static final String RESOURCES = "xls\\";
    private Map<Integer,Map<RegTypes,TreeMap<Integer,Registr>>> databases = new HashMap<>();
    private  Map<RegTypes,TreeMap<Integer,Registr>> database = new HashMap<>();
    private View view = (View) FactorySetup.getClazz("View");
    private CachMap cach = new CachMap();

    private void init() {
        for (RegTypes r : RegTypes.values())
            database.put(r,new TreeMap<>());
    }

    @Override
    public CachMap getCach() {
        return cach;
    }

    @Override
    public void clearDb() {
        database.clear();
    }

    @Override
    public void create(String name) {
        final int NAME=0;
        final int REG=1;
        final int VALUE=3;
        final int FUNC=2;

        clearDb();
        init();

        if (name.equals("test")) {
        for (int i = 0; i <100; i++)     database.get(RegTypes.HOLDING).put(300+i,new Registr("reg" + i , i));
        for (int i = 0; i <18; i++)      database.get(RegTypes.INPUTREG).put(400+i,new Registr("reg" + i ,18-i));
        for (int i = 1; i <18; i++)      database.get(RegTypes.COILS).put(99+i,new Registr("reg" + i , i%2));
        for (int i = 1; i <18; i++)      database.get(RegTypes.DINPUT).put(199+i,new Registr("reg" + i , i%2));

        for (int i = 1; i <5; i++)      database.get(RegTypes.SINGLEBIT).put(199+i,new Registr("reg" + i , i%2));
        for (int i = 1; i <5; i++)      database.get(RegTypes.SCALEDMESURE).put(100+i,new Registr("reg" + i , i));
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
                    getMap(type).put(reg, new Registr(line[NAME], val));
                }
        }
    }

    @Override
    public void add(RegTypes type, int reg, int num) {
        Map<Integer,Registr> map = database.get(type);
        for (int i = reg; i < reg + num; i++) {
             map.put(i, new Registr());
        }
        view.createTable();
    }

    @Override
    public byte[] read(int reg, int num, RegTypes type) throws NoSuchRegistrs {
        byte[] res;
        int temp=0;
        Map<Integer,Registr> map = database.get(type);
        if (map==null) throw new NoSuchRegistrs();
        switch (type) {
            case DINPUT:
            case COILS: {
                res = new byte[bitInByte(num)];
                for (int i = 0; i <  num ; i++) {
                    if (map.containsKey(i+reg)) {
                        if (i%8==0)   temp = 0;
                        if (map.get(i+reg).getValue()==1){
                            temp += (int)(Math.pow(2,(i%8)));
                            res[i/8] =int2ByteLo(temp);
                        }
                    }else throw new NoSuchRegistrs();
                }
                break;
            }
            case SINGLEBIT:{
                if (map.containsKey(reg)) {
                    return new byte[]{int2ByteLo(map.get(reg).getValue())};
                }else throw new NoSuchRegistrs();
            }
            case SCALEDMESURE:
            case HOLDING:
            case INPUTREG:{
                res = new byte[num*2];
                for (int i = 0; i <  num ; i++) {
                    if (map.containsKey(i+reg)) {
                        int d = map.get(reg+i).getValue();
                        res[i*2] = int2ByteHi(d);
                        res[i*2+1] = int2ByteLo(d);
                    }else throw new NoSuchRegistrs();
                }
                break;
            }
            default:throw new NoSuchRegistrs();
        }
        return res;
    }

    @Override
    public byte[] readAll(RegTypes type){
        byte[] res=null;
        int i=0;
        Map<Integer,Registr> map = database.get(type);
        if(map.isEmpty()) return null;
        switch (type) {
            case SHORTFLOAT: {
                break;
            }
            case SCALEDMESURE:  {
                res = new byte[map.size()*6];
                for (Map.Entry<Integer,Registr> m : map.entrySet()){
                    int reg = m.getKey();
                    int val= m.getValue().getValue();
                    res[ i ]=int2ByteLo(reg);
                    res[i+1]=int2ByteHi(reg);
                    res[i+2]=0;
                    res[i+3]=int2ByteLo(val);
                    res[i+4]=int2ByteHi(val);
                    res[i+5] = 0;
                    i+=6;
                }
                break;
            }
            case SINGLEBIT: {
                res = new byte[map.size()*4];
                for (Map.Entry<Integer,Registr> m : map.entrySet()){
                    int reg = m.getKey();
                    res[i]=int2ByteLo(reg);
                    res[i+1]=int2ByteHi(reg);
                    res[i+2]=0;
                    if (m.getValue().getValue()==1) res[i+3]=1;
                    res[i+3]=0;
                    i+=4;
                }
                break;
            }
        }
        return res;
    }

    @Override
    public void setValue(int reg, RegTypes type, int value) throws NoSuchRegistrs {
        Map<Integer,Registr> map = database.get(type);
        if (!map.containsKey(reg)) throw new NoSuchRegistrs();
        map.get(reg).setValue(value);
        view.dbChanged();
    }

    @Override
    public void setName(int reg, RegTypes type, String value) {
        Map<Integer,Registr> map = database.get(type);
        map.get(reg).setName(value);
    }

    private Map<Integer,Registr> getMap(RegTypes type) {
        return database.get(type);
    }

    @Override
    public int sizeTable(RegTypes type) {
        return getMap(type).size();
    }

    @Override
    public int read(int reg, RegTypes type) throws NoSuchRegistrs {
        return getMap(type).get(reg).getValue();
    }

    @Override
    public int readValue(RegTypes type, int row) {
        TreeMap<Integer,Registr> map = database.get(type);
        Registr  value = (Registr) map.values().toArray()[row];
        return value.getValue();
    }

    @Override
    public String readName(RegTypes type, int row) {
        TreeMap<Integer,Registr> map = database.get(type);
        Registr  value = (Registr) map.values().toArray()[row];
        return value.getName();
    }

    @Override
    public int readReg(RegTypes type, int row) {
        return (int) getMap(type).keySet().toArray()[row];
    }
}
