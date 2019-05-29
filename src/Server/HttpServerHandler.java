package Server;

import HTTP.HttpRequest;
import HTTP.HttpResponse;
import Server.Resource.ResourceKeeper;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HttpServerHandler {

    private HttpResponse httpResponse;
    private HttpRequest httpRequest;
    private Map<Integer, String> codeAndReason = new HashMap<Integer, String>();
    private Map<String, String> mime = new HashMap<>();
    private Map<String, String> reversedmime = new HashMap<>();
    private ResourceKeeper resourceKeeper = new ResourceKeeper();

    /**
     * ServerHandler的构造方法，对应一个请求报文
     * 考虑到主体部分可能非文字，只能采用【字节流】而不是【字符流】
     *
     * @param data 从socket的【字节流】得到请求报文的【字节】信息
     * @throws IOException
     */
    public HttpServerHandler(byte[] data) throws IOException {
        StringBuffer sb = new StringBuffer();
        char temp;
        int flag = 0;
        boolean isBody = false;
        int contentLength = 0;

        codeAndReason.put(200, "OK");
        codeAndReason.put(301, "Moved Permanently");
        codeAndReason.put(302, "Found");
        codeAndReason.put(304, "Not Modified");
        codeAndReason.put(404, "Not Found");
        codeAndReason.put(405, "Method Not Allowed");
        codeAndReason.put(500, "Internal Server Error");

        mime.put("text/plain", ".txt");
        mime.put("text/html", ".html");
        mime.put("image/jpeg", ".jpeg");

        reversedmime.put("txt", "text/plain");
        reversedmime.put("html", "text/html");
        reversedmime.put("jpeg", "image/jpeg");

        ArrayList<Byte> body = new ArrayList<>();
        String startLine;
        Map<String, String> headers = new HashMap<>();

        /*
        对字节数组进行转换，在\r\n出现两次的情况认为首部结束，剩下的是主体部分
         */
        for (int i = 0; i < data.length; i++) {
            if (isBody) {
                body.add(data[i]);
            } else {
                temp = (char) data[i];
                if (temp == '\r' || temp == '\n') {
                    flag++;
                } else {
                    flag = 0;
                }
                if (flag == 4) {
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
        for (int i = 1; i < text.length; i++) {
            if (text[i] != "") {
                String[] header = text[i].split(": ");
                headers.put(header[0], header[1]);
            }
        }

        if (headers.containsKey("Content-length")) {
            contentLength = Integer.parseInt(headers.get("Content-length"));
        }

        /*
        将主体的Byte[]变成byte[]
        是否有更方便的做法？
         */
        byte[] res = new byte[body.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = body.get(i).byteValue();
        }

        httpRequest = new HttpRequest(startLine, headers, res);
    }

    public HttpResponse process() {
        String method = httpRequest.getMethod();
        switch (method) {
            case "GET":
                doGet();
                break;
            case "POST":
                doPost();
                break;
            default:
                do405();
        }
        return this.httpResponse;
    }

    public String getRequestStartLineAndHeaders() {
        return httpRequest.startLineAndHeadersToString();
    }

    public String getResponseStartLineAndHeaders() {
        return httpResponse.startLineAndHeadersToString();
    }

    public String getResponseString() {
        String body = new String(httpResponse.getBody());
        return getResponseStartLineAndHeaders() + body;
    }

    private void doGet() {
        String url = httpRequest.getUrl();
        String ifModified = httpRequest.getHeader("If-Modified-Since");
        if (ifModified != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            File f = new File(url);
            try {
                if (new Date(f.lastModified()).after(sdf.parse(ifModified))) {
                    //读图片的方式不一样
                    String type = url.split("\\.")[1];
                    if(type.equals("jpeg")){
                        try{
                            FileInputStream fileInputStream = new FileInputStream(f);
                            byte[] b = fileInputStream.readAllBytes();
                            do200(b,type);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    else {
                        do200(readFile(url),type);
                    }
             //       do200(readFile(url),type);

                } else {
                    do304();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            String fileName = getFileNameFromUrl(url);
            String type = fileName.split("\\.")[1];
            String status = resourceKeeper.getStatus(fileName);
            if (status == null) {
                status = "";
            }
            switch (status) {
                case "valid":
                    if (url.equals(resourceKeeper.getPath(fileName))) {
                        //读图片的方式不一样
                        if(type.equals("jpeg")){
                            try{
                                File f = new File(url);
                                FileInputStream fileInputStream = new FileInputStream(f);
                                byte[] b = fileInputStream.readAllBytes();
                                do200(b,type);
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                        else {
                            do200(readFile(url),type);
                        }
                        do200(readFile(url),type);

                    } else {
                        do301(resourceKeeper.getPath(fileName));
                    }
                    break;
                case "deleted":
                    do404();
                    break;
                case "temp":
                    String tempFileName = resourceKeeper.getTempFileName(fileName);
                    String tempUrl = "src/Server/Resource/Temp/" + tempFileName;
                    do302(tempUrl);
                    break;
                default:
                    do404();
            }
        }
    }

        private void doPost () {
            String url = httpRequest.getUrl();
            String contentType = httpRequest.getHeader("Content-type");
            String s = httpRequest.getHeader("Content-length");
            if (contentType == null || s == null) {
                do500();
            } else {
                int contentLength = Integer.parseInt(s);
                if (contentLength != httpRequest.getBody().length) {
                    do500();
                } else {
                    String fileName = getNewFileName(contentType);
                    File f = new File(url + fileName);
                    if (!f.exists()) {
                        try {
                            f.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                            do500();
                        }
                    }
                    try {
                        FileOutputStream fop = new FileOutputStream(f);
                        fop.write(httpRequest.getBody());
                        fop.flush();
                        fop.close();
                        resourceKeeper.addFile(fileName, url);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        do500();
                    } catch (IOException e) {
                        e.printStackTrace();
                        do500();
                    }
                    do200("Received!");
                }
            }
        }

        public byte[] getResponse () {
            return httpResponse.toByteArray();
        }

        private void do405 () {
            Map<String, String> headers = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            headers.put("Date", sdf.format(new Date()));
            headers.put("Allow", "GET, POST");
            httpResponse = new HttpResponse(buildStartLine(405), headers, null);
        }

        private void do304 () {
            Map<String, String> headers = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            headers.put("Date", sdf.format(new Date()));
            httpResponse = new HttpResponse(buildStartLine(304), headers, null);
        }

        private void do301 (String newPath){
            Map<String, String> headers = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            headers.put("Date", sdf.format(new Date()));
            headers.put("Location", newPath);
            headers.put("Content-type", "text/plain");
            String s = "资源新地址：" + newPath;
            byte[] body = s.getBytes();
            headers.put("Content-length", String.valueOf(body.length));
            httpResponse = new HttpResponse(buildStartLine(301), headers, body);
        }

        private void do302 (String tempPath){
            Map<String, String> headers = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            headers.put("Date", sdf.format(new Date()));
            headers.put("Location", tempPath);
            headers.put("Content-type", "text/plain");
            String s = "资源临时地址：" + tempPath;
            //   System.out.println("----"+s);
            byte[] body = s.getBytes();
            headers.put("Content-length", String.valueOf(body.length));
            httpResponse = new HttpResponse(buildStartLine(302), headers, body);
        }

        private void do200 (String prompt){
            Map<String, String> headers = new HashMap<>();
            byte[] body = prompt.getBytes();
            headers.put("Content-type", "text/plain");
            headers.put("Content-length", String.valueOf(body.length));
            httpResponse = new HttpResponse(buildStartLine(200), headers, body);
        }

        private void do200 ( byte[] body, String type){
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-type", reversedmime.get(type));
            headers.put("Content-length", String.valueOf(body.length));
            httpResponse = new HttpResponse(buildStartLine(200), headers, body);
        }

        private void do404 () {
            Map<String, String> headers = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            headers.put("Date", sdf.format(new Date()));
            headers.put("Content-type", "text/plain");
            String s = "文件不存在！";
            byte[] body = s.getBytes();
            headers.put("Content-length", String.valueOf(body.length));
            httpResponse = new HttpResponse(buildStartLine(404), headers, body);
        }

        private void do500 () {
            Map<String, String> headers = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            headers.put("Date", sdf.format(new Date()));
            httpResponse = new HttpResponse(buildStartLine(500), headers, null);
        }

        private byte[] readFile (String url){
            File file = new File(url);
            try {
                InputStream in = new FileInputStream(file);
                byte[] body = in.readAllBytes();
                return body;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        private String getFileNameFromUrl (String url){
            String[] t = url.split("/");
            if (t.length == 0) {
                return url;
            } else {
                return t[t.length - 1];
            }
        }

        private String getNewFileName (String type){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String fileName = sdf.format(new Date()) + mime.get(type);
            int i = 1;
            String status = resourceKeeper.getStatus(fileName);
            while (status != null && !status.equals("deleted")) {
                fileName = sdf.format(new Date()) + "(" + String.valueOf(i) + ")" + type;
                i++;
                status = resourceKeeper.getStatus(fileName);
            }
            return fileName;
        }

        private String buildStartLine ( int code){
            StringBuffer sb = new StringBuffer();
            sb.append("HTTP/1.1 ");
            sb.append(code);
            sb.append(" ");
            sb.append(codeAndReason.get(code));
            return sb.toString();
        }

        public boolean getConnectionState () {
            return httpRequest.getConnectionState();
        }
    }
