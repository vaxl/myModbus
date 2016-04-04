package base;

public interface PortWork {
    Message read();
    void write(Message message);
}
