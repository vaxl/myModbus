package portWork;

import base.LogWork;
import base.MessageStatus;
import base.PortWork;
import factory.FactorySetup;
import message.Message;
import settings.Setup;
import settings.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer implements PortWork{
    private Text text;
    private Setup setup;
    private ServerSocket serverSocket;
    private LogWork messageWork;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private int i=0;

    public TcpServer() {
        text = (Text) FactorySetup.factory.get("text.xml");
        setup = (Setup) FactorySetup.factory.get("setup.xml");
        messageWork = (LogWork) FactorySetup.factory.get("messageWork");
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
    public boolean start() {
        try{
            serverSocket = new ServerSocket(setup.port);
            messageWork.print(text.PORTOPEN);
            socket = serverSocket.accept();
            messageWork.print(text.CONNECTED);
            in = socket.getInputStream();
            out = socket.getOutputStream();
            return true;
        }catch (Exception e) {return false;}
    }

    @Override
    public void stop() {
        try {
            serverSocket.close();
            socket.close();
            in.close();
            out.close();
            messageWork.print(text.PORTCLOSE);
        } catch (IOException e) {e.printStackTrace();}
    }
    @Override
    public boolean isAlive(){
        return socket.isConnected() & !socket.isClosed();
    }

}
