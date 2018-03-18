package backend;

import backend.tesscert.OCR;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class testPicture {
    public static void main(String[] args){
        for (int i=0;i<100;i++){
            String fiveUrl = "http://183.6.175.51:8000/xb/login/yyvalidateCode.do?i=" + i;
            HttpResponse fiveRespose = PGHelpper.GET(fiveUrl,null,"");
            HttpEntity entity_five = fiveRespose.getEntity();
            String path = "G://picture//"+ i +".jpg";
            if(entity_five!=null){
                InputStream inputStream= null;
                try {
                    inputStream = entity_five.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    FileUtils.copyToFile(inputStream, new File(path)); //将图片保存在本次磁盘G盘，命名为xxx.jpg

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String valCode = null;
            try {
                valCode = new OCR().recognizeText(new File(path), "jpg", "myall");
                File file = new File(path);
                file.renameTo(new File("G://picture//" + valCode + ".jpg"));
                //将原文件更改为f:\a\b.xlsx，其中路径是必要的。注意
                //file.delete();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
