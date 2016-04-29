package database;


import base.Database;
import base.ParseMessage;
import base.RegistrsTypes;
import base.View;
import com.sun.org.apache.xpath.internal.operations.Bool;
import exeptions.NoSuchRegistrs;
import factory.FactorySetup;
import message.Message;
import view.GuiModbusTableView;

import static  helpers.LogicHelper.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RegistrsHashMap implements Database{
    private  Map<RegistrsTypes,Map<Integer,Boolean>> databaseBool = new HashMap<>();
    private  Map<RegistrsTypes,Map<Integer,Integer>> databaseInt = new HashMap<>();
    private Map<String, Message> hash = new HashMap<>();
    private View view = (View) FactorySetup.getClazz("View");

    public RegistrsHashMap() {
        databaseBool.put(RegistrsTypes.COILS, new HashMap<>());
        databaseBool.put(RegistrsTypes.DINPUT, new HashMap<>());
        databaseInt.put(RegistrsTypes.HOLDING, new HashMap<>());
        databaseInt.put(RegistrsTypes.INPUTREG, new HashMap<>());
        create();
    }

    @Override
    public void create() {
        for (int i = 0; i <100; i++)     databaseInt.get(RegistrsTypes.HOLDING).put(300+i,i);
        for (int i = 0; i <18; i++)      databaseInt.get(RegistrsTypes.INPUTREG).put(400+i,18-i);
        for (int i = 1; i <18; i++)      databaseBool.get(RegistrsTypes.COILS).put(99+i,(i%2==0));
        for (int i = 1; i <18; i++)      databaseBool.get(RegistrsTypes.DINPUT).put(199+i,i%2!=0);
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
    public Map getMap(RegistrsTypes type) {
        switch (type){
            case DINPUT:
            case COILS:{
                return databaseBool.get(type);
            }
            case INPUTREG:
            case HOLDING:return databaseInt.get(type);
        }
        return null;
    }

    @Override
    public void clearCach() {
        hash.clear();
    }

    @Override
    public Map getCach() {
        return hash;
    }
}
