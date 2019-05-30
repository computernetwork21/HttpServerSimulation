package Client;

import HTTP.HttpRequest;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ForInput {
    private HttpRequest httpRequest;

    ForInput(String Url) {
        Scanner sc = new Scanner(System.in);
        System.out.println("POST/GET?");
        if (sc.nextLine().equals("POST")) {
            String startLine = "POST src/Server/Resource/New/ HTTP/1.1";
            Map<String, String> headers = new HashMap<>();
            headers.put("Connection", "keep-alive");
            System.out.println("Input file name with suffix:");
            try {
                String fileName = "src\\Client\\Resource\\";
                fileName += sc.nextLine();
                File f = new File(fileName);
                InputStream is = new FileInputStream(f);
                byte[] body = is.readAllBytes();

                headers.put("Content-type", "image/jpeg");
                headers.put("Content-length", String.valueOf(body.length));
                httpRequest = new HttpRequest(startLine, headers, body);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Map<String, String> headers = new HashMap<>();
            headers.put("Connection", "keep-alive");
//            headers.put("If-modified-Since", "2019-05-20 09:19:29");

            System.out.println("Input file name with suffix:");
            String FileName_Suffix=sc.nextLine();
            String startLine = "GET "+Url+FileName_Suffix+" HTTP/1.1";

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
            System.out.println("accepted:"+headers.get("Accept"));

            byte[] body=new byte[0];
            httpRequest = new HttpRequest(startLine, headers, body);
        }
//20190528232257.jpeg
    }
    public byte[] getHttpRequest(){
        return this.httpRequest.toByteArray();
    }

}