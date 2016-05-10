package database;


import base.Database;
import base.RegistrsTypes;
import base.View;
import exeptions.NoSuchRegistrs;
import factory.FactorySetup;
import helpers.ExcelHelper;
import message.Message;

import static  helpers.LogicHelper.*;

import java.util.*;

public class RegistrsHashMap implements Database{
    private static final String RESOURCES = "xls\\";
    private  Map<RegistrsTypes,Map<Integer,Boolean>> databaseBool = new HashMap<>();
    private  Map<RegistrsTypes,Map<Integer,Integer>> databaseInt = new HashMap<>();
    private  Map<RegistrsTypes,Map<Integer,String>> databaseString = new HashMap<>();
    private Map<String, Message> hash = new HashMap<>();
    private View view = (View) FactorySetup.getClazz("View");

    public RegistrsHashMap() {
    }

    private void init() {
        databaseBool.put(RegistrsTypes.COILS, new TreeMap<>());
        databaseString.put(RegistrsTypes.COILS, new TreeMap<>());
        databaseBool.put(RegistrsTypes.DINPUT, new TreeMap<>());
        databaseString.put(RegistrsTypes.DINPUT, new TreeMap<>());
        databaseInt.put(RegistrsTypes.HOLDING, new TreeMap<>());
        databaseString.put(RegistrsTypes.HOLDING, new TreeMap<>());
        databaseInt.put(RegistrsTypes.INPUTREG, new TreeMap<>());
        databaseString.put(RegistrsTypes.INPUTREG, new TreeMap<>());
    }

    @Override
    public void clearDb() {
        databaseBool.clear();
        databaseInt.clear();
        databaseString.clear();
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
        for (int i = 0; i <100; i++)     databaseInt.get(RegistrsTypes.HOLDING).put(300+i,i);
        for (int i = 0; i <100; i++)     databaseString.get(RegistrsTypes.HOLDING).put(300+i,"hold " + i);
        for (int i = 0; i <18; i++)      databaseInt.get(RegistrsTypes.INPUTREG).put(400+i,18-i);
        for (int i = 1; i <18; i++)      databaseBool.get(RegistrsTypes.COILS).put(99+i,(i%2==0));
        for (int i = 1; i <18; i++)      databaseBool.get(RegistrsTypes.DINPUT).put(199+i,i%2!=0);
        return;
        }
        if (name.equals("none")) {
            return;
        }

        List<String[]> list =  ExcelHelper.readLines(RESOURCES + name);
        if (list!=null)
            for (String[] line : list) {
                RegistrsTypes type = RegistrsTypes.values()[Integer.valueOf(line[FUNC])];
                int val;
                int reg;
                try {
                    val = Integer.valueOf(line[VALUE]);
                }catch (Exception e) {val =0;}
                try {
                    reg = Integer.valueOf(line[REG]);
                } catch (NumberFormatException e) {
                    continue;
                }
                switch (type){
                    case COILS:
                    case DINPUT:{
                        Map<Integer,Boolean> map = databaseBool.get(type);
                        map.put(reg,val==1);
                        databaseString.get(type).put(Integer.valueOf(line[REG]),line[NAME]);
                        break;
                    }
                    case HOLDING:
                    case INPUTREG:{
                        Map<Integer,Integer> map = databaseInt.get(type);
                        map.put(reg,val);
                        databaseString.get(type).put(Integer.valueOf(line[REG]),line[NAME]);
                        break;
                    }
                }
            }
    }

    @Override
    public void add(RegistrsTypes type, int reg, int num) {
        switch (type) {
            case DINPUT:
            case COILS: {
                Map<Integer, Boolean> map = databaseBool.get(type);
                for (int i = reg; i < reg + num; i++) {
                    map.put(i, false);
                    setName(reg, type, "");
                }
                break;
            }
            case HOLDING:
            case INPUTREG: {
                Map<Integer, Integer> map = databaseInt.get(type);
                for (int i = reg; i < reg + num; i++) {
                    map.put(i, 0);
                    setName(reg, type, "");
                }
                break;
            }
        }
        view.createTable();
    }

    @Override
    public byte[] read(int reg, int num, RegistrsTypes type) throws NoSuchRegistrs {
        byte[] res;
        int temp=0;

        switch (type) {
            case DINPUT:
            case COILS: {
                Map<Integer,Boolean> map = databaseBool.get(type);
                res = new byte[bitInByte(num)];
                for (int i = 0; i <  num ; i++) {
                    if (map.containsKey(i+reg)) {
                        if (i%8==0)   temp = 0;
                        if (map.get(i+reg)){
                            temp += (int)(Math.pow(2,(i%8)));
                            res[i/8] =int2ByteLo(temp);
                        }
                    }else throw new NoSuchRegistrs();
                }
                break;
            }
            case HOLDING:
            case INPUTREG:{
                Map<Integer,Integer> map = databaseInt.get(type);
                res = new byte[num*2];
                for (int i = 0; i <  num ; i++) {
                    if (map.containsKey(i+reg)) {
                        int d = map.get(reg+i);
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
    public void update(int reg, int num, RegistrsTypes type, int value) throws NoSuchRegistrs {
        switch (type){
            case WRITEBIT:{
               Map<Integer,Boolean> map = databaseBool.get(RegistrsTypes.COILS);
                if (!map.containsKey(reg)) throw new NoSuchRegistrs();
                if (value==0) map.put(reg,false);
                else map.put(reg,true);
                view.dbChanged();
                break;
            }
            case WRITEINT:{
                Map<Integer,Integer> map = databaseInt.get(RegistrsTypes.HOLDING);
                if (!map.containsKey(reg)) throw new NoSuchRegistrs();
                map.put(reg,value);
                view.dbChanged();
                break;
            }
        }
    }

    @Override
    public String getName(int reg, RegistrsTypes type){
        Map<Integer,String> map = databaseString.get(type);
        return map.get(reg);
    }

    @Override
    public void setName(int reg, RegistrsTypes type, String value) {
        Map<Integer,String> map = databaseString.get(type);
        map.put(reg,value);
    }

    @Override
    public Map getMap(RegistrsTypes type) {
        switch (type){
            case DINPUT:
            case COILS:{
                return databaseBool.get(type);
            }
            case INPUTREG:
            case HOLDING:
                return databaseInt.get(type);
        }
        return null;
    }

    @Override
    public void clearCach() {
        hash.clear();
    }

    @Override
    public void putToCach(String key, Message message) {
         hash.put(key,message);
    }

    @Override
    public Message getFromCach(String key) {
        if (hash.containsKey(key)) return hash.get(key);
        return null;
    }
}
