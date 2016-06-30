package database;

import base.*;
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
            getMap(reg.getType(),reg.getId()).put(reg.getReg(), reg);
    }

   /* @Override
    public byte[] read(int reg, int num, RegTypes type,int id){
        byte[] res;
        int temp=0;
        Map<Integer,Registr> map = getMap(type,id);
        if (map==null) return null;
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
                    }else return null;
                }
                break;
            }
            case SINGLEBIT:{
                if (map.containsKey(reg)) {
                    return new byte[]{int2ByteLo(map.get(reg).getValue())};
                }else return null;
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
                    }else return null;
                }
                break;
            }
            default:return null;
        }
        return res;
    }
*/
    @Override
    public Registr readReg(int reg, RegTypes type,int id){
        return getMap(type,id).containsKey(reg)? getMap(type,id).get(reg) : null;
    }

    @Override
    public Collection<Registr> readAll(RegTypes type, int id){
        return  getMap(type,id).values();
    }

    @Override
    public boolean update(Registr reg){
        Map<Integer,Registr> map = getMap(reg);
        if (!map.containsKey(reg.getReg())) return false;
        add(reg);
        view.dbChanged(reg.getId(),reg.getType());
        return true;
    }

    @Override
    public Map<Integer,Registr> getMap(RegTypes type, int id){
            return databases.get(id).get(type);
    }

    private Map<Integer,Registr> getMap(Registr reg){
        return databases.get(reg.getId()).get(reg.getType());
    }

    @Override
    public int sizeTable(RegTypes type,int id) {
        return getMap(type,id).size();
    }

}
