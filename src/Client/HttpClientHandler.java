package Client;

import HTTP.HttpRequest;
import HTTP.HttpResponse;

import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class HttpClientHandler {

    //解析报文，并处理
    //对mime类型的处理
    //对301、302、304的处理???重定向的处理

    private static String method; //请求报文的方法
    private static String url;  //请求报文的路径
    private static byte[] body0;  //请求报文的body
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private String newUrl="";

    private static   Map<String, String> mimes = new HashMap<>();


    public HttpClientHandler(byte[] requestData,byte[] responseData) throws IOException{
        mimes.put("text/plain", ".txt");
        mimes.put("text/html", ".html");
        mimes.put("image/jpeg", ".jpeg");
        //解析请求报文
        StringBuffer sb0 = new StringBuffer();
        char temp0;
        int flag0 = 0;

        /**
         * 开始行，\r\n首次出现
         */
        for(int i=0; i<requestData.length; i++){
            temp0 = (char) requestData[i];
            if(temp0 == '\r' || temp0 == '\n'){
                flag0++;
            }else {
                flag0 = 0;
            }
            if(flag0 == 2){
                break;
            }
            sb0.append(temp0);
        }
        String[] startLineInfo = sb0.toString().split(" ");
        method=startLineInfo[0];
        url=startLineInfo[1];

        //body
        flag0=0;
        int j=0;
        for(; j<requestData.length; j++){
            temp0 = (char) requestData[j];
            if(temp0 == '\r' || temp0 == '\n'){
                flag0++;
            }else {
                flag0 = 0;
            }
            if(flag0 == 4){
                break;
            }
        }
        //body0=requestData.toString().substring(j).getBytes();
        body0= Arrays.copyOfRange(requestData,j,requestData.length);

        //解析响应报文
        StringBuffer sb = new StringBuffer();
        char temp;
        int flag = 0;
        boolean isBody = false;
        int contentLength = 0;

        ArrayList<Byte> body = new ArrayList<>();
        String startLine="";
        Map<String, String> headers = new HashMap<>();

        /*
        对字节数组进行转换，在\r\n出现两次的情况认为首部结束，剩下的是主体部分
         */
        for(int i=0; i<responseData.length; i++){
            if(isBody){
                body.add(responseData[i]);
            }else {
                temp = (char) responseData[i];
                if(temp == '\r' || temp == '\n'){
                    flag++;
                }else {
                    flag = 0;
                }
                if(flag == 4){
                    isBody = true;
                }
                sb.append(temp);
            }
        }

        /*
        对开始行和首部信息进行读取，默认每行的结尾都是\r\n
         */
        String[] text = sb.toString().split("\r\n");
        startLine = text[0];

        for (int i=1; i<text.length; i++){
            if(text[i] != ""){
                String[] header = text[i].split(": ");
                headers.put(header[0], header[1]);
            }
        }

        if(headers.containsKey("Content-length")){
            contentLength = Integer.parseInt(headers.get("Content-length"));
        }

        /*
        将主体的Byte[]变成byte[]
        是否有更方便的做法？
         */
        byte[] res = new byte[body.size()];
        for(int i=0; i<body.size(); i++){
            res[i] = body.get(i);
        }
        httpResponse = new HttpResponse(startLine, headers, res);
    }

    /**
     * 客户端做出响应
     * 返回给客户端状态码，提示下一步操作
     */
    public int response(){
        //检查状态码
        int statusCode = httpResponse.getStateCode();
        switch (statusCode){
            case 200:
                return do200();
            case 301:
                return 301;
            case 302:
                return 302;
            case 304:
                return do304();
            case 404:                                           //暂定
                System.out.println("Not found");
                return 404;
            case 405:
                System.out.println("方法不支持。");
                return 405;
            default:
                System.out.println("Internal server error");
                return 500;
        }
    }

    public void show(String fileName){
        String filePath = "src\\Client\\Resource"+fileName;
        String mime = httpResponse.getHeader("Content-type");
        switch (mime){
            case ".txt":
                filePath=filePath+".txt";
                break;
            case  ".html":
                filePath=filePath+".html";
                break;
            default:
                filePath=filePath+".jpeg";
                break;
        }
        File file = new File(filePath);
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private int do200(){
        if(method.equals("POST")){
            //请求报文方法是POST
            System.out.println("服务器端已收到。"); //暂定
            return 2001;
        }

        //请求报文是get方法
   /*     //用户输入文件名，验证是否重名，然后保存
        System.out.println("请输入一个文件名：");
        InputStreamReader is = new InputStreamReader(System.in); //new构造InputStreamReader对象
        BufferedReader br = new BufferedReader(is); //拿构造的方法传到BufferedReader中，此时获取到的就是整个缓存流
        String fileName="";
        System.out.println("content type is "+httpResponse.getHeader("Content-type"));
        while(true){
            try {
                fileName=br.readLine();
                fileName="src\\Client\\Resource\\"+fileName+mimes.get(httpResponse.getHeader("Content-type"));
                if(new File(fileName).exists()){
                    //文件已存在
                    System.out.println("文件已存在，请重新输入文件名。");
                }
                else{
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    */
        //fileName从响应报文的url里来
        String fileName = getFileNameFromUrl(url);
        fileName="src/Client/Resource/"+fileName;
        File file = new File(fileName);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            if(httpResponse.getHeader("Content-type").equals("image/jpeg")){
                FileOutputStream fop = new FileOutputStream(file);
                fop.write(httpResponse.getBody());
                fop.flush();
                fop.close();
            }
            else{
                FileWriter fw = new FileWriter(file);
                fw.write(new String(httpResponse.getBody()));
                fw.close();
            }
            System.out.println("文件已保存在"+fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 2002;

    }
    /*
    public void saveFile(String fileName){
        String filePath = "src\\Client\\Resource"+fileName;
        String mime = httpResponse.getHeaders().get("Content-type");
        switch (mime){
            case ".txt":
                filePath=filePath+".txt";
                break;
            case  ".html":
                filePath=filePath+".html";
                break;
            default:
                filePath=filePath+".jpeg";
                break;
        }
        File file = new File(filePath);
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            fw.write(httpResponse.getBody().toString());
            fw.close();
            System.out.println("文件已保存在"+filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } */

    public byte[] do301(){
        //更新URL
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept","*");
        String url=new String(Arrays.copyOfRange(httpResponse.getBody(),18,httpResponse.getBody().length));
        newUrl=url;
        headers.put("Host",url);
        httpRequest=new HttpRequest(buildStartLine(url),headers,body0);
        return http2bytes();
    }
    public byte[] do302(){
        //更新URL
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept","*");
        String url=new String(Arrays.copyOfRange(httpResponse.getBody(),21,httpResponse.getBody().length));
        headers.put("Host",url);
        httpRequest=new HttpRequest(buildStartLine(url),headers,body0);
        return http2bytes();
    }
    private int do304(){
        System.out.println("已刷新。");
        return 304;
    }

    private String buildStartLine(String url){
     //   System.out.println("-------new url is "+url);
        //请求报文<method><url><version>
        StringBuffer sb = new StringBuffer();
        sb.append(method);
        sb.append(" ");
        sb.append(url);
        sb.append(" HTTP/1.1 ");
        return sb.toString();
    }

    private byte[] http2bytes(){
        String temp = "";
        temp=temp+httpRequest.getMethod()+" "+httpRequest.getUrl()+" "+httpRequest.getVersion()+"\r\n";
        Map<String,String> headers = httpRequest.getHeaders();
        for(String key:headers.keySet()){
            temp=temp+key+": "+headers.get(key)+"\r\n";
        }
        temp=temp+"\r\n"+httpRequest.getBody().toString()+"\r\n";
        return temp.getBytes();
    }

    private String getFileNameFromUrl (String url){
        String[] t = url.split("/");
        if (t.length == 0) {
            return url;
        } else {
            return t[t.length - 1];
        }
    }
    public String getNewUrl(){
        return newUrl;
    }
}
