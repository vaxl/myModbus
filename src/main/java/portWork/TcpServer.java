package portWork;

import base.View;
import base.MessageStatus;
import base.Connection;
import base.Protocol;
import factory.FactorySetup;
import message.Message;
import message.MessageParseExec;
import settings.Setup;
import settings.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer implements Connection {
    private Text text;
    private Setup setup;
    private ServerSocket serverSocket;
    private View view;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private int i=0;
    private boolean run;

    public TcpServer() {
        text = (Text) FactorySetup.getClazz("text.xml");
        setup = (Setup) FactorySetup.getClazz("setup.xml");
        view = (View) FactorySetup.getClazz("View");
    }

    @Override
    public Message read() {
        try {
            while (true) {
                if (in.available() > 0) {
                    i = 0;
                    byte[] arr = new byte[in.available()];
                    in.read(arr);
                    Message message = new Message(arr);
                    message.setStatus(MessageStatus.OK);
                    return message;
                } else {
                    Thread.sleep(10);
                    i++;
                    if (i > 1000 | !isAlive()) {
                        stop();
                        Message message = new Message(null);
                        message.setStatus(MessageStatus.NOCONNECT);
                        return message;
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); stop();}
        return null;
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
            run=false;
            serverSocket.close();
            socket.close();
            in.close();
            out.close();
            view.print(text.PORTCLOSE);
        } catch (IOException e) {e.printStackTrace();}
    }
    @Override
    public boolean isAlive(){
        return socket.isConnected() & !socket.isClosed();
    }
    @Override
    public void start() {
        run=true;
            while (run) {
                Message message = read();
                if(message.getStatus() == MessageStatus.NOCONNECT) break;
                MessageParseExec.execute(Protocol.valueOf(setup.protocol),message);
                if (message.getStatus()!= MessageStatus.NOANSWER){
                    write(message);
                }
                view.print(message);
            }
    }
}
