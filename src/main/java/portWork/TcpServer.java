package portWork;

import base.*;
import database.Entity.Registrs;
import message.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import static helpers.LogicHelper.*;

public class TcpServer extends AbstractServer {
    private ServerSocket serverSocket;
    private Socket socket;
    private InputStream in;
    private OutputStream out;

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
                    if (event.get()){
                        Message message = new Message();
                        message.setRegs(regEvent);
                        message.setStatus(MessageStatus.SEND);
                        return message;
                    }
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
    public boolean isAlive(){
        return socket.isConnected() & !socket.isClosed();
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

    @Override
    public void close() {
        try {
            if (serverSocket!=null) serverSocket.close();
            if (socket!=null ) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {e.printStackTrace();}
    }
}
