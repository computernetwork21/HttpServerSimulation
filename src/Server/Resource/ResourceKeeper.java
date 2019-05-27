package Server.Resource;

import java.util.Map;

public class ResourceKeeper {
    private Map<String, String> fileStatus;//{文件名，状态valid, deleted, temp}

    private Map<String, String> filePath;//{文件名，路径}

    public ResourceKeeper(){

    }

    public String getStatus(String fileName){return fileStatus.get(fileName);}

    public String getPath(String fileName){return filePath.get(fileName);}

    public void addFile(String fileName, String url){
        fileStatus.put(fileName, "valid");
        filePath.put(fileName, url+fileName);
    }
}
