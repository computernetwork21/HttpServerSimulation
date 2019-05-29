package Client;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client extends Thread{
    private Socket client=null;

    public static void main(String[] args) {
        Thread t = new Client(80);
        t.start();
    }

    public Client(int port) {
        try
        {
            client = new Socket("127.0.0.1", port);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void run() {
        try
        {
            InputStream inputStream = client.getInputStream();//服务器端发回的数据
            OutputStream outputStream = client.getOutputStream();//发送给服务器端的数据
            System.out.println("client is ready");

            //request
            ForInput forInput=new ForInput();
            outputStream.write(forInput.getHttpRequest());

            //Response
            //get available byte[].length=availble
            int count = 0;
            Thread.sleep(200);
            while (count == 0) {
                count = inputStream.available();
            }
            byte[] temp=new  byte[count];
            inputStream.read(temp);

            //handle response
            HttpClientHandler hch=new HttpClientHandler(forInput.getHttpRequest(),temp);

            int state=hch.response();
            switch (state){
                case 301:
                    outputStream.write(hch.do301());
                    //发给服务器端
                case  302:
                    outputStream.write(hch.do302());
                    System.out.println("---新报文已发送---");
                    //发给服务器端
            }
//            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}

    //发送/接收
    //设计一套用户指令，包括 发送报文、构建报文（纯文本报文/带文件报文）、打印发送的报文、打印收到的报文的开始行和首部
    //如果是纯文字/文本文件报文，用户可以查看内容（自动？）；如果是图片，则可以查看图片保存的位置
    //对附带文件的报文，文件大小应该做出限制（比如5kb？）
    //待补充……
    //读取用户指令
    //预存示例用的报文？
    //需要打日志吗？


