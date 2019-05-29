package Client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

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
            String fileName="src\\Client\\Resource\\"+"Message1";
            File file=new File(fileName);
            Scanner sc=new Scanner(file);
            String request = "";
            while(true) {
                request = request + sc.nextLine();
                if (!sc.hasNext()){break;}
            }
            outputStream.write(request.getBytes());
            byte[] temp=new byte[102400];
            inputStream.read(temp);
            byte[] out=copyValidByte(temp);
            HttpClientHandler hch=new HttpClientHandler(request.getBytes(),out);

            int state=hch.response();
            switch (state){
                case 301:
                    outputStream.write(hch.do301());
                    //发给服务器端
                case  302:
                    outputStream.write(hch.do302());
                    //发给服务器端
            }
//            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private byte[] copyValidByte(byte[] read){
        ArrayList<Byte> t = new ArrayList<>();
        int count = 0;
        for(byte b : read){
            if(b != 0){
                t.add(b);
                count++;
            }else {
                break;
            }
        }
        byte[] data = new byte[count];
        for(int i=0; i<count; i++){
            data[i] = t.get(i);
        }
        return data;
    }

    //发送/接收
    //设计一套用户指令，包括 发送报文、构建报文（纯文本报文/带文件报文）、打印发送的报文、打印收到的报文的开始行和首部
    //如果是纯文字/文本文件报文，用户可以查看内容（自动？）；如果是图片，则可以查看图片保存的位置
    //对附带文件的报文，文件大小应该做出限制（比如5kb？）
    //待补充……
    //读取用户指令
    //预存示例用的报文？
    //需要打日志吗？

}
