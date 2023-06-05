import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.net.Socket;

public class Receiver extends Thread {
    private DataInputStream _DataInputStream;

    public Receiver(Socket _Socket) {
        try {
            _DataInputStream = new DataInputStream(_Socket.getInputStream());
        } catch (Exception e) {
            System.out.println("Receiver Init Error : " + e);
        }
    }

    @Override
    public void run() {
        while (_DataInputStream != null) {
            try {
                System.out.println(_DataInputStream.readUTF());
            } catch (Exception e) {
                System.out.println("Receiver RunTime Error : " + e);
                break;
            }
        }
    }
}