import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.net.Socket;
import java.util.Scanner;
import java.io.DataOutputStream;

public class ChatClient {
    public static void main(String[] args) {
        try {
            Socket _Socket = new Socket("localhost", 7777);
            System.out.println("# 서버에 연결되었습니다.");
            System.out.println("# 클라이언트의 소켓 : "+_Socket);

            String name = "";
            while (true) {
                System.out.println("사용 할 이름(ID)를 입력해 주세요 >>> ");
                Scanner _Scanner = new Scanner(System.in);
                name = _Scanner.next();

                DataOutputStream _DataOutputStream = new DataOutputStream(_Socket.getOutputStream());
                _DataOutputStream.writeUTF(name);
                _DataOutputStream.flush();

                DataInputStream _DataInputStream = new DataInputStream(_Socket.getInputStream());
                String msg = _DataInputStream.readUTF();

                if (msg.equals("!errCode404")){
                    System.out.println("# 채팅방에 동일한 이름이 존재합니다.");
                } else {
                    break;
                }
            }
            Sender sender = new Sender(_Socket, name);
            Receiver receiver = new Receiver(_Socket);

            sender.start();
            receiver.start();
        } catch (Exception e) {
            System.out.println("Client Error : " + e);
        }
    }
}
