package Client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client extends Thread{
    private Socket client=null;
    private Map<String,String> fileMap;//已知服务器的映射

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
        String NewUrl="";
        setFileMap();//初始化fileMap
        try
        {
            InputStream inputStream = client.getInputStream();//服务器端发回的数据
            OutputStream outputStream = client.getOutputStream();//发送给服务器端的数据
            System.out.println("Client is ready.");
            while (true){

                ForInput forInput=new ForInput(fileMap);
                System.out.println("");
                System.out.println("***REQUEST***");
                System.out.println(forInput.getRequest().startLineAndHeadersToString());
                outputStream.write(forInput.getHttpRequest());

                while (true){
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

                    Thread.sleep(200);
                    System.out.println("***RESPONSE***");
                    System.out.println(hch.getResponseStartLineAndHeaders());

                    if(state==301) {
                        outputStream.write(hch.do301());
                        System.out.println("***REQUEST***");
                        System.out.println(hch.getRequestStartLineAndHeaders());
                        NewUrl = hch.getNewUrl();
                        fileMap.put(forInput.getFileName_Suffix(),NewUrl);
                        //发给服务器端
                    }
                    else if(state==302){
                        System.out.println("***REQUEST***");
                        System.out.println(hch.getRequestStartLineAndHeaders());
                        outputStream.write(hch.do302());
//                        System.out.println("---新报文已发送---");
                        //发给服务器端
                    }else {
                        break;
                    }

                    outputStream.flush();
                    Thread.sleep(4000);
                }

                String code="";
                while (true){
                    System.out.println("Do you want to exit? [yes/no]");
                    Scanner scanner=new Scanner(System.in);
                    code=scanner.nextLine();
                    if(code.equals("yes")||code.equals("no")){
                        break;
                    }
                }
                if (code.equals("yes")){
                    break;
                }
            }
        }


        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setFileMap(){
        fileMap=new HashMap<>();
        String DefaultTargetUrl="src/Server/Resource/New/";
        fileMap.put("1.jpeg",DefaultTargetUrl+"1.jpeg");
        fileMap.put("1_301.jpeg",DefaultTargetUrl+"1_301.jpeg");
        fileMap.put("1_302.jpeg",DefaultTargetUrl+"1_302.jpeg");
        fileMap.put("2.txt",DefaultTargetUrl+"2.txt");
        fileMap.put("2_301.txt",DefaultTargetUrl+"2_301.txt");
        fileMap.put("2_302.txt",DefaultTargetUrl+"2_302.txt");
        fileMap.put("3.html",DefaultTargetUrl+"3.html");
        fileMap.put("3_301.html",DefaultTargetUrl+"3_301.html");
        fileMap.put("3_302.html",DefaultTargetUrl+"3_302.html");
        fileMap.put("4.txt",DefaultTargetUrl+"4.txt");
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


