package Server.Resource;

import java.util.HashMap;
import java.util.Map;

public class ResourceKeeper {
    private Map<String, String> fileStatus = new HashMap<>();//{文件名，状态valid, deleted, temp}

    private Map<String, String> filePath = new HashMap<>();//{文件名，路径}

    private Map<String, String> tempFile = new HashMap<>();//{文件名，临时文件名}

    public ResourceKeeper(){
        //保存在原路径的
        fileStatus.put("1.jpeg", "valid");
        fileStatus.put("1.png", "valid");
        fileStatus.put("2.txt", "valid");
        fileStatus.put("3.html", "valid");
        //301,文件在newPath文件夹里
        fileStatus.put("1_301.jpeg", "valid");
        fileStatus.put("2_301.txt", "valid");
        fileStatus.put("3_301.html", "valid");
        //302
        fileStatus.put("1_302.jpeg", "temp");
        fileStatus.put("2_302.txt", "temp");
        fileStatus.put("3_302.html", "temp");
        fileStatus.put("1t.jpeg", "valid");
        fileStatus.put("2t.txt", "valid");
        fileStatus.put("3t.html", "valid");

        fileStatus.put("4.txt", "deleted");

        //200
        filePath.put("1.jpeg", "src/Server/Resource/New/1.jpeg");
        filePath.put("2.txt", "src/Server/Resource/New/2.txt");
        filePath.put("3.html", "src/Server/Resource/New/3.html");
        //301
        filePath.put("1_301.jpeg", "src/Server/Resource/NewPath/1_301.jpeg");
        filePath.put("2_301.txt", "src/Server/Resource/NewPath/2_301.txt");
        filePath.put("3_301.html", "src/Server/Resource/NewPath/3_301.html");
        //302
        filePath.put("1t.jpeg", "src/Server/Resource/Temp/1t.jpeg");
        filePath.put("2t.txt", "src/Server/Resource/Temp/2t.txt");
        filePath.put("3t.html", "src/Server/Resource/Temp/3t.html");


        filePath.put("1.png", "src/Server/Resource/1.png");



        tempFile.put("1_302.jpeg", "1t.jpeg");
        tempFile.put("2_302.txt", "2t.txt");
        tempFile.put("3_302.html", "3t.html");

    }

    public String getStatus(String fileName){
        return fileStatus.get(fileName);
    }

    public String getPath(String fileName){return filePath.get(fileName);}

    public void addFile(String fileName, String url){
        fileStatus.put(fileName, "valid");
        filePath.put(fileName, url+fileName);
    }

    public String getTempFileName(String fileName){
        return tempFile.get(fileName);
    }
}
