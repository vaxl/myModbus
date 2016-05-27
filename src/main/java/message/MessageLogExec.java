package message;

import base.Logger;
import base.View;

import java.util.HashMap;
import java.util.Map;

class MessageLogExec {
    private static Map<View.logView, Logger> commandMap;
    private static final String DIR = "message.logView.";
    static {
        commandMap = new HashMap<>();
        for (View.logView p : View.logView.values())
            commandMap.put(p, (Logger) helpers.ReflectionHelper.createInstance(DIR + p.name().toLowerCase()));
    }

    public static  String execute(View.logView logView, byte[] message) {
        return commandMap.get(logView).execute(message);
    }
}
