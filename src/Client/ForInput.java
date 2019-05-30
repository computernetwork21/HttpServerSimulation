package Client;

import HTTP.HttpRequest;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ForInput {
    private HttpRequest httpRequest;

    ForInput() {
        Scanner sc = new Scanner(System.in);
        System.out.println("POST/GET?");
        if (sc.nextLine().equals("POST")) {
            String startLine = "POST src/Server/Resource/New/ HTTP/1.1";
            Map<String, String> headers = new HashMap<>();
            headers.put("Connection", "keep-alive");
            headers.put("If-Modified-Since", "2019-05-20 09:19:29");

            System.out.println("Input file name:");
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
            headers.put("If-modified-Since", "2019-05-20 09:19:29");

            System.out.println("Input file name:");
            String filename=sc.nextLine();
            String startLine = "GET src/Server/Resource/New/"+filename+" HTTP/1.1";
            String[] temp=filename.split(".");
            switch (temp[1]){
                case "jpeg":
                    headers.put("Accept","image/jpeg");
                case "html":
                    headers.put("Accept","text/html");
                case "txt":
                    headers.put("Accept","text/plain");
            }
            byte[] body=new byte[0];
            httpRequest = new HttpRequest(startLine, headers, body);
        }
//20190528232257.jpeg
    }
    public byte[] getHttpRequest(){
        return this.httpRequest.toByteArray();
    }

}