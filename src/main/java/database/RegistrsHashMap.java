package database;


import base.Database;
import base.RegistrsTypes;

import java.util.HashMap;

public class RegistrsHashMap implements Database{
    private HashMap<Short,Boolean> map1x = new HashMap<>();
    private HashMap<Short,Boolean> map2x = new HashMap<>();
    private HashMap<Short,Short> map3x = new HashMap<>();
    private HashMap<Short,Short> map4x = new HashMap<>();

    @Override
    public void create() {
        for (short i = 0; i <18; i++)      map3x.put(((short) (500+i)),i);
        for (short i = 0; i <18; i++)      map4x.put((short)(400+i),(short)(18-i));
        for (short i = 1; i <18; i++)      map1x.put((short)(199+i),(i%2==0));
        for (short i = 1; i <18; i++)      map2x.put((short)(299+i),i%2!=0);
    }

    @Override
    public byte[] read(int reg, int num, RegistrsTypes type) {
        return new byte[0];
    }

    @Override
    public void update(int reg, int num, RegistrsTypes type) {

    }
}
