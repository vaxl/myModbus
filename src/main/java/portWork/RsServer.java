package portWork;

import base.MessageStatus;
import jssc.SerialPortException;
import message.Message;
import jssc.SerialPort;

public class RsServer extends AbstractServer  {
    private SerialPort serialPort;

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
    public boolean isAlive() {
        return serialPort.isOpened();
    }

    @Override
    public void close() {
        try {
            if(serialPort!=null) serialPort.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }
}
