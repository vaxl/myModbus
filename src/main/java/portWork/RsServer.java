package portWork;

import base.Connection;
import base.MessageStatus;
import base.Protocol;
import base.View;
import database.Registr;
import factory.FactorySetup;
import jssc.SerialPortException;
import message.Message;
import jssc.SerialPort;
import message.MessageParseExec;
import settings.Setup;
import settings.Text;
import java.util.concurrent.atomic.AtomicBoolean;

public class RsServer implements Connection {

    private View view;
    private Text text;
    private Setup setup;
    private SerialPort serialPort;
    private AtomicBoolean run=new AtomicBoolean();

    public RsServer() {
        text = (Text) FactorySetup.getClazz("text.xml");
        setup = (Setup) FactorySetup.getClazz("setup.xml");
        view = (View) FactorySetup.getClazz("View");
    }

    @Override
    public Message read() {
        try {
            while (true) {
                if(!isAlive()) break;
                if (serialPort.getInputBufferBytesCount() > 0) {
                    byte[] arr = serialPort.readBytes();
                    Message message = new Message(arr);
                    message.setStatus(MessageStatus.OK);
                    return message;
                } else {
                    Thread.sleep(10);
                }
            }
        } catch (Exception e) { e.printStackTrace();}
        Message message = new Message(null);
        message.setStatus(MessageStatus.NOCONNECT);
        return message;
    }

    @Override
    public void write(Message message) {
        try {
            if(message.getTx()!=null)
                serialPort.writeBytes(message.getTx());
        } catch (SerialPortException e) {
            e.printStackTrace();
            stop();
        }
    }

    @Override
    public void write(byte[] message) {
        try {
            if(message!=null)
                serialPort.writeBytes(message);
        } catch (SerialPortException e) {
            e.printStackTrace();
            stop();
        }
    }

    @Override
    public boolean init() {
        serialPort = new SerialPort(setup.rsPort);
        try{
            serialPort.openPort();
            serialPort.setParams(setup.baud,setup.bits,setup.stop,setup.parity);
            view.print(text.PORTOPEN + " " + setup.rsPort);
            return true;
        }catch (SerialPortException e) {return false;}
    }

    @Override
    public void stop() {
        try {
            run.set(false);
            if(serialPort!=null) serialPort.closePort();
            view.print(text.PORTCLOSE);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isAlive() {
        return serialPort.isOpened();
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
            }
        }
    }

    @Override
    public void event(Registr reg) {
        //TODO
    }
}
