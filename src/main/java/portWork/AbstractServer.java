package portWork;

import base.*;
import database.Entity.Registr;
import database.Entity.Registrs;
import factory.FactorySetup;
import message.Message;
import message.MessageParseExec;
import settings.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractServer implements Runnable {
    private AtomicBoolean run   = new AtomicBoolean();
    View view = (View) FactorySetup.getClazz("View");
    Text text = Text.getInstance();
    Setup setup = Setup.getInstance();
    AtomicBoolean event = new AtomicBoolean();
    Registrs regEvent;

    public abstract Message read();
    public abstract void write(Message message);
    public abstract void write(byte[] message);
    public abstract boolean init();
    public abstract boolean isAlive() ;
    public abstract void close();

    public void stop() {
        run.set(false);
        close();
        view.print(text.PORTCLOSE);
    }

    public  void event(Registrs reg){
        regEvent = reg;
        event.set(true);
    }

    @Override
    public void run() {
        run.set(true);
        if (init()){
            while (run.get()) {
                Message message = read();
                if (message.getStatus() == MessageStatus.NOCONNECT) {
                    if (run.get()) {
                        view.print(text.CONNECTIONLOST);
                        stop();
                    }
                    break;
                }
                else {
                    MessageParseExec.execute(Protocol.valueOf(setup.protocol), message);
                    if (message.getStatus() != MessageStatus.NOANSWER)
                        write(message);
                    view.print(message);
                }
                if (event.get()) event.set(false);
            }
        }
    }
}
