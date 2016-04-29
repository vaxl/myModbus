package portWork;

import base.*;
import factory.FactorySetup;
import helpers.LogicHelper;
import message.Message;
import message.MessageParseExec;
import settings.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class TcpServer implements Connection {
    private Text text;
    private Setup setup;
    private ServerSocket serverSocket;
    private View view;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private AtomicBoolean run=new AtomicBoolean();

    public TcpServer() {
        text = (Text) FactorySetup.getClazz("text.xml");
        setup = (Setup) FactorySetup.getClazz("setup.xml");
        view = (View) FactorySetup.getClazz("View");
    }

    @Override
    public Message read() {
        try {
            while (true) {
                if(!isAlive()) break;
                if (in.available() > 0) {
                    byte[] arr = new byte[in.available()];
                    in.read(arr);
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
                out.write(message.getTx());
        } catch (IOException e) {
            e.printStackTrace();
            stop();
        }
    }
    @Override
    public boolean init() {
        try{
            serverSocket = new ServerSocket(setup.port);
            view.print(text.PORTOPEN);
            socket = serverSocket.accept();
            view.print(text.CONNECTED);
            in = socket.getInputStream();
            out = socket.getOutputStream();
            return true;
        }catch (Exception e) {return false;}
    }
    @Override
    public void stop() {
        try {
            run.set(false);
            if (serverSocket!=null) serverSocket.close();
            if (socket!=null ) {
                socket.close();
                in.close();
                out.close();
            }
            view.print(text.PORTCLOSE);
        } catch (IOException e) {e.printStackTrace();}
    }
    @Override
    public boolean isAlive(){
        return socket.isConnected() & !socket.isClosed();
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
    public void write(byte[] message) {
        try {
            if(message!=null)
                out.write(message);
        } catch (IOException e) {
            e.printStackTrace();
            stop();
        }
    }
}
