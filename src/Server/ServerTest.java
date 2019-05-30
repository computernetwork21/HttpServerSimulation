package Server;

import HTTP.HttpRequest;
import HTTP.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ServerTest {
    public static void main(String[] args) throws IOException {
        String serverName = "localhost";

        String startLine = "GET src/Server/Resource/Temp/3t.html HTTP/1.1";
        Map<String, String> headers = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        headers.put("Date", sdf.format(new Date()));
//        headers.put("If-Modified-Since", sdf.format(new Date()));
//        File f = new File("src/Server/Resource/1.jpeg");
//        InputStream ips = new FileInputStream(f);
//        byte[] body = ips.readAllBytes();
//        System.out.println(body.length);
//        headers.put("Content-type", "image/jpeg");
//        headers.put("Content-length", String.valueOf(body.length));
        HttpRequest httpRequest = new HttpRequest(startLine, headers, null);

//        File f = new File("src/Client/Resource/Message1");
//        InputStream ips = new FileInputStream(f);
//        byte[] test = ips.readAllBytes();

        byte[] test = httpRequest.toByteArray();

//        File res = new File("src/Client/Resource/test1");
//        if(!res.exists()){res.createNewFile();}
//        OutputStream ops = new FileOutputStream(res);
//        ops.write(test);
//        ops.flush();
//        ops.close();

        HttpServerHandler httpServerHandler = new HttpServerHandler(test);
        httpServerHandler.process();
        System.out.println(httpServerHandler.getResponseStartLineAndHeaders());


//        int port = 80;
//        try{
//            System.out.println("连接到主机：" + serverName + " ，端口号：" + port);
//            Socket client = null;
//            try {
//                client = new Socket(serverName, port);
//           } catch (IOException e) {
//                e.printStackTrace();
//            }
//            System.out.println("远程主机地址：" + client.getRemoteSocketAddress());
//
//            OutputStream os = client.getOutputStream();
//            os.write(test);
//            os.flush();
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
//            String s;
//            while ((s=br.readLine())!=null){
//                System.out.println(s);
//            }
//            client.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
