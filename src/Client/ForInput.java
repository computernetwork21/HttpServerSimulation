package Client;

import java.io.*;

public class ForInput {

    public static void main(String[] args){
        new ForInput().process("20190528232257.jpeg");
    }
    public void process(String file){
        String fileName="src\\Client\\Resource\\";
        fileName+=file;
        byte[] buffer = null;
        try {
            File f = new File(fileName);
            FileInputStream fis = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i=0;i<buffer.length;i++) {System.out.print(buffer[i]);}
    }
}
