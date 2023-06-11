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

                IsAllSendMessage("# [ " + name + " ] 님이 입장하셨습니다.");

                while (_DataInputStream != null) {
                    String msg;
                    msg = _DataInputStream.readUTF();

                    if (msg.replaceAll(name + " >>> ", "").startsWith("@")) {
                        if (msg.replaceAll(name + " >>> ", "").trim().equals("@접속자")){
                            _DataOutputStream.writeUTF(IsShowUserList(name));
                        } else if (msg.replaceAll(name + " >>> ", "").trim().startsWith("@귓속말")){
                            String[] msgTemp = msg.replaceAll(name + " >>> ", "").trim().split(" ",3);
                            if (msgTemp==null||msgTemp.length<3){
                                _DataOutputStream.writeUTF("# 귓속말 사용법이 잘못되었습니다.\r\n# @귓속말 [상대방이름] [보낼메시지].");
                            } else {
                                String toName = msgTemp[1];
                                String toMsg = msgTemp[2];
                                if (clientMap.containsKey(toName)){
                                    IsSendToMsg(name,toName,toMsg);
                                }else {
                                    _DataOutputStream.writeUTF("# 해당 유저가 존재하지 않습니다.");
                                }
                            }
                        }else {
                            _DataOutputStream.writeUTF("# 잘못된 명령어 입니다.");
                        }
                    } else {
                        IsAllSendMessage(msg, name);
                    }
                }
            } catch (Exception e) {
                System.out.println("ServerReceiver RunTime Error : " + e + "[" + name + "]");
            } finally {
                clientMap.remove(name);
                IsAllSendMessage("# [ " + name + " ] 님이 퇴장하셨습니다.");
            }
        }



        public boolean IsAllSendMessage(String msg, String name) {
            Iterator it = clientMap.keySet().iterator();

            while (it.hasNext()) {
                try {
                    Object _Object = it.next();
                    DataOutputStream it_out = (DataOutputStream) clientMap.get(_Object);
                    if (!_Object.toString().equals(name)) {
                        it_out.writeUTF(msg);
                    }
                } catch (IOException e) {
                    System.out.println("All Send Mag Error : " + e);
                }
            }
            return true;
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

        public String IsShowUserList(String name) {
            StringBuilder _StringBuilder = new StringBuilder("==접속자목록==\r\n");

            Iterator it = clientMap.keySet().iterator();

            while (it.hasNext()){
                try {
                    String key = (String) it.next();
                    if (key.equals(name)){
                        key += " (*) ";
                    }
                    _StringBuilder.append(key+"\r\n");
                }catch (Exception e){
                    System.out.println("예외 : "+e);
                }
            }
            _StringBuilder.append("=="+clientMap.size()+"명 접속중==\r\n");
            return _StringBuilder.toString();
        }

        public void IsSendToMsg(String fromName, String toName, String msg){
            try {
                clientMap.get(toName).writeUTF("## 귓 : from["+fromName+"] >>> "+msg);
                clientMap.get(fromName).writeUTF("## 귓 : from["+toName+"] >>> "+msg);
            }catch (Exception e){
                System.out.println("예외 : "+e);
            }
        }
    }
}