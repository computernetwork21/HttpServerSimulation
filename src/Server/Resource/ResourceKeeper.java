package Server.Resource;

import java.util.HashMap;
import java.util.Map;

public class ResourceKeeper {
    private Map<String, String> fileStatus = new HashMap<>();//{文件名，状态valid, deleted, temp}

    private Map<String, String> filePath = new HashMap<>();//{文件名，路径}

    public ResourceKeeper(){
        fileStatus.put("1.jpeg", "valid");
        fileStatus.put("1.png", "valid");
        fileStatus.put("2.txt", "valid");
        fileStatus.put("3.html", "valid");
        fileStatus.put("3t.html", "temp");
        fileStatus.put("4.txt", "deleted");

        filePath.put("1.jpeg", "src/Server/Resource/New/1.jpeg");
        filePath.put("1.png", "src/Server/Resource/1.png");
        filePath.put("2.txt", "src/Server/Resource/New/2.txt");
        filePath.put("3.html", "src/Server/Resource/New/3.html");
        filePath.put("3t.html", "src/Server/Resource/Temp/3t.html");
    }

    public String getStatus(String fileName){
        return fileStatus.get(fileName);
    }

    public String getPath(String fileName){return filePath.get(fileName);}

    public void addFile(String fileName, String url){
        fileStatus.put(fileName, "valid");
        filePath.put(fileName, url+fileName);
    }
}
