package Server;

import java.net.*;
import java.io.*;

public class Server extends Thread{
    private ServerSocket serverSocket;

    public static void main(String [] args) {
        int port = 80;
        try
        {
            Thread t = new Server(port);
            t.run();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void run() {
        while(true)
        {
            try
            {
                System.out.println("等待远程连接，端口号为：" + serverSocket.getLocalPort() + "...");
                Socket server = serverSocket.accept();
                System.out.println("远程主机地址：" + server.getRemoteSocketAddress());

                InputStream ips = server.getInputStream();
                OutputStream ops = server.getOutputStream();
                while (true){
                    byte[] b = new byte[ips.available()];
                    if(ips.read(b) != 0){
                        HttpServerHandler httpServerHandler = new HttpServerHandler(b);
                        System.out.println("***收到新报文***");
                        httpServerHandler.process();
                        System.out.println(httpServerHandler.getRequestStartLineAndHeaders());
                        ops.write(httpServerHandler.getResponse());
                        ops.flush();
                        System.out.println("***响应报文已发送***");
                        System.out.println(httpServerHandler.getResponseStartLineAndHeaders());
                        if(!httpServerHandler.getConnectionState()){
                            System.out.println("***长连接关闭***");
                            break;
                        }
                    }
                }

                server.close();
            }catch(SocketTimeoutException s)
            {
                System.out.println("Socket timed out!");
                break;
            }catch(IOException e)
            {
                e.printStackTrace();
                break;
            }
        }
    }

}
