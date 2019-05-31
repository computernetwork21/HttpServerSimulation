package Client;

import HTTP.HttpRequest;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ForInput {
    private HttpRequest httpRequest;
    private String FileName_Suffix="";
    ForInput(Map<String,String> fileMap) {
        Scanner sc = new Scanner(System.in);
        System.out.println("");
        String command="";
            System.out.println("Method:");
            command=sc.nextLine();

        if (command.equals("POST")) {
            String startLine = "POST src/Server/Resource/New/ HTTP/1.1";
            Map<String, String> headers = new HashMap<>();
            headers.put("Connection", "keep-alive");
            System.out.println("");
            System.out.println("Input file name with suffix:");
            try {
                FileName_Suffix = "src\\Client\\Resource\\";
                FileName_Suffix += sc.nextLine();
                File f = new File(FileName_Suffix);
                InputStream is = new FileInputStream(f);
                byte[] body = is.readAllBytes();

                String flag="";
                while (true){
                    System.out.println("In to mode 500 ? [yes/no]");
                    flag=sc.nextLine();
                    if (flag.equals("yes")||flag.equals("no")){
                        break;
                    }
                }
                if (flag.equals("yes")){
                    //500 模式
                    Random random=new Random(1);
                    int length= random.nextInt();
                    headers.put("Content-length",String.valueOf(length));

                    String[] temp=FileName_Suffix.split("\\.");
                    if (temp[1].equals("jpeg")){
                        headers.put("Content-type","text/txt");
                    }
                    else if (temp[1].equals("html")){
                        headers.put("Content-type","image/jpeg");
                    }
                    else if (temp[1].equals("txt")){
                        headers.put("Content-type","text/html");
                    }
                }else {//正常模式
                    String[] temp=FileName_Suffix.split("\\.");
                    if (temp[1].equals("jpeg")){
                        headers.put("Content-type","image/jpeg");
                    }
                    else if (temp[1].equals("html")){
                        headers.put("Content-type","text/html");
                    }
                    else if (temp[1].equals("txt")){
                        headers.put("Content-type","text/plain");
                    }

                    headers.put("Content-length", String.valueOf(body.length));
                }
                httpRequest = new HttpRequest(startLine, headers, body);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(command.equals("GET")){
            Map<String, String> headers = new HashMap<>();
            headers.put("Connection", "keep-alive");
            System.out.println("");
            System.out.println("Input file name with suffix:");
            FileName_Suffix=sc.nextLine();
            String Url=fileMap.get(FileName_Suffix);
            String startLine = "GET "+Url+" HTTP/1.1";
            File file =new File("src\\Client\\Resource\\"+FileName_Suffix);
            if(file.exists()){
                Calendar cal = Calendar.getInstance();
                long time = file.lastModified();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                cal.setTimeInMillis(time);
                String IMS=formatter.format(cal.getTime());
                headers.put("If-Modified-Since",IMS);
            }

            String[] temp=FileName_Suffix.split("\\.");
            if (temp[1].equals("jpeg")){
                headers.put("Accept","image/jpeg");
            }
            else if (temp[1].equals("html")){
                headers.put("Accept","text/html");
            }
            else if (temp[1].equals("txt")){
                headers.put("Accept","text/plain");
            }

            httpRequest = new HttpRequest(startLine, headers, null);
        }
        else {
            String startLine=command+" "+"/"+" HTTP/1.1";
            Map<String, String> headers = new HashMap<>();
            headers.put("Connection", "keep-alive");
            httpRequest=new HttpRequest(startLine,headers,null);
        }
//20190528232257.jpeg
    }

    public byte[] getHttpRequest(){
        return this.httpRequest.toByteArray();
    }

    public String getFileName_Suffix(){return this.FileName_Suffix;}

    public HttpRequest getRequest(){ return this.httpRequest;}
}