package Server;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerTest {
    public static void main(String[] args) {
        String serverName = "localhost";

        String startLine = "POST Server/Resource/New/ HTTP/1.1";
        Map<String, String> headers = new HashMap<>();
        File f = new File("Server/Resource/1.jpg");

        byte[] test = "你好".getBytes();
        int port = 80;
        try{
            System.out.println("连接到主机：" + serverName + " ，端口号：" + port);
            Socket client = null;
            try {
                client = new Socket(serverName, port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("远程主机地址：" + client.getRemoteSocketAddress());

            OutputStream os = client.getOutputStream();
            os.write(test);
            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            System.out.println("服务器响应： " + br.readLine());
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
