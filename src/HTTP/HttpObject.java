package HTTP;

import java.util.Map;

public class HttpObject {

    protected String startLine;

    protected int headerCount;

    public String getStartLine() {
        return startLine;
    }

    public void setStartLine(String startLine) {
        this.startLine = startLine;
    }

    public int getHeaderCount() {
        return headerCount;
    }

    public void setHeaderCount(int headerCount) {
        this.headerCount = headerCount;
    }

    public void addHeaders(String name, String value){
        this.headers.put(name, value);
        this.headerCount++;
    }

    public String getHeader(String name){
        if(headers.containsKey(name)){
            return this.headers.get(name);
        }else {
            return null;
        }
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    protected Map<String, String> headers;

    protected byte[] body;

    public String startLineAndHeadersToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(startLine + "\r\n");
        for (String name : headers.keySet()) {
            sb.append(name + ": " + headers.get(name) + "\r\n");
        }
        return sb.toString();
    }

    public byte[] toByteArray(){
        byte[] bt1 = this.startLineAndHeadersToString().getBytes();
        byte[] bt2 = this.body;
        if(bt2 == null){
            return bt1;
        }else {
            return byteMerger(bt1, bt2);
        }
    }

    private static byte[] byteMerger(byte[] bt1, byte[] bt2){
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }
}
