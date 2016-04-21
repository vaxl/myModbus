package base;

import message.Message;

public interface PortWork {
    Message read();
    void write(Message message);
    boolean start();
    void stop();
    boolean isAlive();
}
