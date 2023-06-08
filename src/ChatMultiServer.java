import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatMultiServer {
    static HashMap<String, DataOutputStream> clientMap;

    public static void main(String[] args) {
        ExecutorService _ExecutorService = Executors.newFixedThreadPool(5);
//        참가자를 6명으로 제한함.

        clientMap = new HashMap<String, DataOutputStream>();
        Collections.synchronizedMap(clientMap);

        System.out.println("# 서버 준비 완료...");
        try (ServerSocket server = new ServerSocket(7777)){
            System.out.println("# 연결 대기중 ...");
            while (true) {
                Socket _Socket = server.accept();
                ServerReceiver _ServerReceiver = new ServerReceiver(_Socket);
                _ExecutorService.submit(_ServerReceiver);
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    static class ServerReceiver extends Thread {
        Socket _Socket;
        DataInputStream _DataInputStream;
        DataOutputStream _DataOutputStream;

        public ServerReceiver(Socket _Socket) {
            this._Socket = _Socket;
            try {
                _DataInputStream = new DataInputStream(_Socket.getInputStream());
                _DataOutputStream = new DataOutputStream(_Socket.getOutputStream());
            } catch (Exception e){
                System.out.println("ServerReceiver Init Error : " + e);
            }
        }

        @Override
        public void run() {
            String name = "";
            try {
                System.out.print("서버의 소켓 : " + _Socket);
//                name = _DataInputStream.readUTF();
//                System.out.println(name);
                while ((name = _DataInputStream.readUTF()) != null) {
                    if (clientMap.containsKey(name)){
                        _DataOutputStream.writeUTF("!errCode404");
                    } else {
                        System.out.println(name);
                        _DataOutputStream.writeUTF("");
                        break;
                    }
                }

                clientMap.put(name, _DataOutputStream);
                while (_DataInputStream != null) {
                    String msg;
                    msg = _DataInputStream.readUTF();

                    IsAllSendMessage(msg);
                }
            } catch (Exception e) {
                System.out.println("ServerReceiver RunTime Error : " + e + "[" + name + "]");
            }
        }
        public boolean IsAllSendMessage(String msg) {
            Iterator it = clientMap.keySet().iterator();

            while (it.hasNext()) {
                try {
                    DataOutputStream it_out = (DataOutputStream) clientMap.get(it.next());
                    it_out.writeUTF(msg);
                } catch (IOException e) {
                    System.out.println("All Send Mag Error : " + e);
                }
            }
            return true;
        }
    }
}
