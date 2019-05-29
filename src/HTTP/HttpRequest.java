package HTTP;

import java.util.Map;

public class HttpRequest extends HttpObject {

    private String method;
    private String url;
    private String version;

    public HttpRequest(String startLine, Map<String, String> headers, byte[] body){
        this.startLine = startLine;
        String[] startLineInfo = startLine.split(" ");
        method = startLineInfo[0];
        url = startLineInfo[1];
        version = startLineInfo[2];
        this.headers = headers;
        this.body = body;
        this.headerCount = headers.size();
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public Map<String,String> getHeaders(){
        return headers;
    }

    public boolean getConnectionState(){
        String state = headers.get("Connection");
        if(state!=null && state.equals("close")){
            return false;
        }else {
            return true;
        }
    }
}
