package base;

import message.Message;

public interface Connection {
    Message read();
    void write(Message message);
    boolean init();
    void stop();
    boolean isAlive();
    void start();
}
