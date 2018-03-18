
package backend.tesscert;

import java.io.File;
import java.io.IOException;

public class Test {

    /** */
    /**
     * @param args
     */
    public static void main(String[] args) {
        for (int i=0;i<100;i++){
            String path = "G://picture//"+ i +".jpg";
            try {
                String valCode = new OCR().recognizeText(new File(path), "jpg", "myletter");
                System.out.println(i + ":" + valCode);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
