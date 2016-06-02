package database;

import base.*;
import exeptions.NoSuchRegistrs;
import factory.FactorySetup;
import helpers.ExcelHelper;
import static  helpers.LogicHelper.*;
import java.util.*;

public class RegistrsHashMap implements Database{
    private static final String RESOURCES = "xls\\";
    private Map<Integer,Map<RegTypes,TreeMap<Integer,Registr>>> databases = new HashMap<>();
    private View view = (View) FactorySetup.getClazz("View");
    private CachMap cach = new CachMap();

    private void init(int id) {
        databases.putIfAbsent(id, new HashMap<>());
        for (RegTypes r : RegTypes.values())
            databases.get(id).putIfAbsent(r,new TreeMap<>());
    }

    @Override
    public CachMap getCach() {
        return cach;
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
        for (int i = 0; i <100; i++)     getMap(RegTypes.HOLDING,id).put(300+i,new Registr("reg" + i , i));
        for (int i = 0; i <18; i++)      getMap(RegTypes.INPUTREG,id).put(400+i,new Registr("reg" + i ,18-i));
        for (int i = 1; i <18; i++)      getMap(RegTypes.COILS,id).put(99+i,new Registr("reg" + i , i%2));
        for (int i = 1; i <18; i++)      getMap(RegTypes.DINPUT,id).put(199+i,new Registr("reg" + i , i%2));

        for (int i = 1; i <5; i++)      getMap(RegTypes.SINGLEBIT,id).put(199+i,new Registr("reg" + i , i%2));
        for (int i = 1; i <5; i++)      getMap(RegTypes.SCALEDMESURE,id).put(100+i,new Registr("reg" + i , i));
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
                    getMap(type,id).put(reg, new Registr(line[NAME], val));
                }
        }
    }

    @Override
    public void add(RegTypes type, int reg, int num,int id) {
        if (databases.get(id)==null) init(id);
        for (int i = reg; i < reg + num; i++) {
            getMap(type,id).put(i, new Registr());
        }
    }

    @Override
    public byte[] read(int reg, int num, RegTypes type,int id) throws NoSuchRegistrs {
        byte[] res;
        int temp=0;
        Map<Integer,Registr> map = getMap(type,id);
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
    public byte[] readAll(RegTypes type,int id){
        byte[] res=null;
        int i=0;
        Map<Integer,Registr> map = getMap(type,id);
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
    public void setValue(int reg, RegTypes type, int value, int id) throws NoSuchRegistrs {
        Map<Integer,Registr> map = getMap(type,id);
        if (!map.containsKey(reg)) throw new NoSuchRegistrs();
        map.get(reg).setValue(value);
        view.dbChanged(id,type);
    }

    @Override
    public void setName(int reg, RegTypes type, String value,int id) {
        getMap(type,id).get(reg).setName(value);
    }

    private Map<Integer,Registr> getMap(RegTypes type,int id){
        try {
            return databases.get(id).get(type);
        }catch (Exception e) {return null;}
    }

    @Override
    public int sizeTable(RegTypes type,int id) {
        return getMap(type,id).size();
    }

    @Override
    public int read(int reg, RegTypes type,int id) throws NoSuchRegistrs {
        return getMap(type,id).get(reg).getValue();
    }

    @Override
    public int readValue(RegTypes type, int row,int id) {
        Registr  value = (Registr) getMap(type,id).values().toArray()[row];
        return value.getValue();
    }

    @Override
    public String readName(RegTypes type, int row,int id) {
        Registr  value = (Registr) getMap(type,id).values().toArray()[row];
        return value.getName();
    }

    @Override
    public int readReg(RegTypes type, int row,int id) {
        return (int) getMap(type,id).keySet().toArray()[row];
    }
}
