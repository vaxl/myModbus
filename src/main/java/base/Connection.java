package base;

import database.Registr;
import message.Message;

public interface Connection extends Runnable{
    Message read();
    void write(Message message);
    void write(byte[] message);
    boolean init();
    void stop();
    boolean isAlive();
    void event (Registr reg);
}
