package message;

import base.ParseMessage;
import base.Protocol;
import java.util.HashMap;
import java.util.Map;

public class MessageParseExec {
    private static Map<Protocol, ParseMessage> commandMap;

    static {
        commandMap = new HashMap<>();
        commandMap.put(Protocol.NONE, new NoneParser());
        commandMap.put(Protocol.MODBUSLAVETCP, new ModbusSlaveTcpParser());
    }

    private MessageParseExec() {}

    public static  void execute(Protocol operation, Message message) {
        commandMap.get(operation).execute(message);
    }
}
