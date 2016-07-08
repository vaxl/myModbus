package database;

import message.Message;
import java.util.HashMap;
import java.util.Map;

public  class CachMap {
    private  Map<String, Message> hash = new HashMap<>();
    private static CachMap cachMap = new CachMap();

    private CachMap() {
    }

    public static CachMap getInstance(){
        return cachMap;
    }

    public void putToCach(String key, Message message) {
        hash.put(key,message);
    }
    public Message getFromCach(String key) {
        if (hash.containsKey(key)) return hash.get(key);
        return null;
    }
    public void clearCach() {
        hash.clear();
    }
}
