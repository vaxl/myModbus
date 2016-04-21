import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TestClient {
    public static void main(String[] args) {
        int serverPort = 5020;
        String addr = "127.0.0.1";
         // System.out.println((Byte.MAX_VALUE.));
        try {
            InetAddress ipAddress = InetAddress.getByName(addr);
            Socket socket = new Socket(ipAddress, serverPort);
            InputStream sin = socket.getInputStream();
            OutputStream sout = socket.getOutputStream();
            byte[] arr = {1,2,3,4,5,6,7,8,9};

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true){
                String r = reader.readLine();
                if (r.equals("e")) break;
                else{
                    sout.write(arr);
                    sout.flush();
                }
            }
            sout.close();

        }catch (Exception e) {e.printStackTrace();}
    }
}
