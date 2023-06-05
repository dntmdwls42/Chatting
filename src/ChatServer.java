import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    public static void main(String[] args) {
        System.out.println("# 서버 준비 완료...");
        try (ServerSocket server = new ServerSocket(7777)){
            System.out.println("# 연결 대기중 ...");
            Socket _Socket = server.accept();

            System.out.println("# 서버의 소켓 : " +_Socket);

            Sender _Sender = new Sender(_Socket, "Server");
            Receiver _Receiver = new Receiver(_Socket);

            _Sender.start();
            _Receiver.start();
        } catch (Exception e) {
            System.out.println("Server Error : " + e);
        }
    }
}
