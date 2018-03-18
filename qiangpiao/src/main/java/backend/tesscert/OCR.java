
package backend.tesscert;

import org.jdesktop.swingx.util.OS;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;
import org.apache.commons.io.FileUtils;
public class OCR {
    private final String LANG_OPTION = "-l"; // Ӣ����ĸСдl����������1
    private final String OPTION = "-psm"; // Ӣ����ĸСдl����������1
    private final String EOL = System.getProperty("line.separator");
    private String tessPath = "G:\\google\\Tesseract-OCR";

    // private String tessPath = new File("tesseract").getAbsolutePath();

    public String recognizeText(File imageFile, String imageFormat, String ruler) throws Exception {
        File tempImage = imageFile;// ImageIOHelper.createImage(imageFile,imageFormat);

        /************ ���봦��***begin ***/
        BufferedImage image;
        image = ImageIO.read(imageFile);
        int width = image.getTileWidth();
        int height = image.getTileHeight();

        /*
         * ��Image���д���һ������ѭ������image.getRGB������ȡ��ÿ�������ɫ�� Ȼ�����ɫ���������ŵ�һ��HashMap��ȥ
         */
        Map<Integer, Integer> mapColor = new HashMap<Integer, Integer>();
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                int color = image.getRGB(i, j);
                Integer count = mapColor.get(color);
                if (count == null)
                    count = 0;
                count++;
                mapColor.put(color, count);
            }
        /*
         * �Ѿ��������HashMap��key����ɫ��value����ɫ�ĵ�ĸ�������������õ� list��
         *
         */
        List<Entry<Integer, Integer>> list = new ArrayList<Entry<Integer, Integer>>(
                mapColor.entrySet());
        Collections.sort(list, new Comparator<Entry<Integer, Integer>>() {
            @Override
            public int compare(Entry<Integer, Integer> arg0,
                    Entry<Integer, Integer> arg1) {
                return arg1.getValue() - arg0.getValue();
            }
        });
        list = list.subList(0, 5);// ʵ��ʵ���з��ֺܲ����룬ͬһ����ĸ�Ŵ�ᷢ�ֲ�һ������ɫ

        /*
         * ����ȡ��list�ĵ�һ�����������ɫ��int intBack = list.get(0).getKey();
         * ѭ�����У�����Ǳ���ɫ������֤�����ɫ��if
         * (setColor.contains(color))������ô����ѭ�������򣬸õ�Ϊ������Ҫ���óɱ���ɫ��image.setRGB(i,
         * j, intBack)����
         */
        int intBack = list.get(0).getKey();
        Set<Integer> setColor = new HashSet<Integer>();
        for (Entry<Integer, Integer> entry : list) {
            setColor.add(entry.getKey());
        }
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                int color = image.getRGB(i, j);
                if (setColor.contains(color))
                    continue;
                image.setRGB(i, j, intBack);
            }

        File file = new File(imageFile.getParentFile(), "temp.jpg");
        ImageIO.write(image, "JPEG", file);
        /************ ���봦��***end ***/

        /*** ����רҵ�ӿ�**begin **/
        BufferedImage image_ex;
        image_ex = ImageIO.read(imageFile);
        ImageFilter filter = new ImageFilter(image_ex);
        ;
        File file_ex = new File(imageFile.getParentFile(), "temp_ex.jpg");
        ImageIO.write(filter.lineGrey(), "JPEG", file_ex);// lineGreyЧ���Ѿ��㲻����ˣ����ǻ���ʶ�𲻳���
        /*** ����רҵ�ӿ�**end **/

        File outputFile = new File(imageFile.getParentFile(), "output");
        StringBuffer strB = new StringBuffer();
        List<String> cmd = new ArrayList<String>();
        if (OS.isWindowsXP()) {
            cmd.add(tessPath + "\\tesseract");
        } else if (OS.isLinux()) {
            cmd.add("tesseract");
        } else {
            cmd.add(tessPath + "\\tesseract");
        }
        cmd.add("");
        cmd.add(outputFile.getName());
        cmd.add(LANG_OPTION);
        cmd.add("");
        // cmd.add("chi_sim");
        cmd.add("eng");
        cmd.add("");
        cmd.add(OPTION);
        cmd.add("");
        cmd.add("7");
        cmd.add("");
        cmd.add("-c");
        cmd.add("");
        cmd.add(ruler);
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(imageFile.getParentFile());

        cmd.set(1, "temp_ex.jpg"/* tempImage.getName() */);
        pb.command(cmd);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        // tesseract.exe 1.jpg 1 -l chi_sim
        int w = process.waitFor();

        // ɾ����ʱ���ڹ����ļ�
        // tempImage.delete();
        if(ruler.equals("need_picture")){
             FileUtils.copyFile(file_ex, new File("picture//temp.jpg"));
             return "picture//temp.jpg";
        }
        if (w == 0) {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(
                    outputFile.getAbsolutePath() + ".txt"), "UTF-8"));

            String str;
            while ((str = in.readLine()) != null) {
                strB.append(str);
            }
            in.close();
        } else {
            String msg;
            switch (w) {
                case 1:
                    msg = "Errors accessing files.There may be spaces in your image's filename.";
                    break;
                case 29:
                    msg = "Cannot recongnize the image or its selected region.";
                    break;
                case 31:
                    msg = "Unsupported image format.";
                    break;
                default:
                    msg = "Errors occurred.";
            }
            // tempImage.delete();
            throw new RuntimeException(msg);
        }
        new File(outputFile.getAbsolutePath() + ".txt").delete();
        return strB.toString();
    }
}
