import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Sender extends Thread {
    private DataOutputStream _DataOutputStream;
    private String name;

    public Sender(Socket _Socket, String name) {
        this.name = name;
        try {
            _DataOutputStream = new DataOutputStream(_Socket.getOutputStream());
        } catch (Exception e) {
            System.out.println("Sender Init Error : " + e);
        }
    }
    @Override
    public void run() {
        Scanner _Scanner = new Scanner(System.in);
        while (_DataOutputStream != null) {
            try {
                _DataOutputStream.writeUTF(name+" >>> " +_Scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Sender RunTime Error : " + e);
                break;
            }
        }
        _Scanner.close();
    }
}