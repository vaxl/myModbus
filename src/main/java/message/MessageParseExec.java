package message;

import base.ParseMessage;
import base.Protocol;
import java.util.HashMap;
import java.util.Map;

public class MessageParseExec {
    private static Map<Protocol, ParseMessage> commandMap;
    private final static String DIR = "message.parsers.";

    static {
        commandMap = new HashMap<>();
        for (Protocol p : Protocol.values())
                commandMap.put(p, (ParseMessage) helpers.ReflectionHelper.createInstance(DIR + p.name() + "Parser"));
    }

    private MessageParseExec() {}


    public static  void execute(Protocol operation, Message message) {
        commandMap.get(operation).execute(message);
    }
}
