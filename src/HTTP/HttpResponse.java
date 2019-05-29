package HTTP;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse extends HttpObject{

    private String version;
    private int stateCode;
    private String reason;


    public HttpResponse(String startLine, Map<String, String> headers, byte[] body){
        this.startLine = startLine;
        String[] startLineInfo = startLine.split(" ");
        version = startLineInfo[0];
        stateCode = Integer.parseInt(startLineInfo[1]);
        reason = startLineInfo[2];
        this.headers = headers;
        this.body = body;
        this.headerCount = headers.size();
    }

    public String getVersion() {
        return version;
    }

    public int getStateCode() {
        return stateCode;
    }

    public Map<String,String> getHeaders(){
        return headers;
    }

}
