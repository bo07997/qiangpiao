package backend.ruokuai;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class myRuoKuai {

    public static void main(String args[]) {
        String result = RuoKuai.createByPost("bo07997","bo123456ok","3060","1000","1",
                "b40ffbee5c1cf4e38028c197eb2fc751", "G://piture_del//11.jpg");
//        int a = 0;
        //匹配可预约时间
        String xmlString;
        byte[] strBuffer = null;
        int flen = 0;
        Document document = null;
        File test = new File("G://test.html");
        try {
            InputStream in = new FileInputStream(test);
            flen = (int)test.length();
            strBuffer = new byte[flen];
            in.read(strBuffer, 0, flen);
        } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        xmlString = new String(strBuffer);
        Pattern pattern = Pattern.compile("<a href=\"javascript:booking(.*?);\"><span>");
        // 现在创建 matcher 对象
        Matcher m = pattern.matcher(xmlString);
        String session = null;
        List<String[]> list = new ArrayList<String[]>();
//        while (m.find( )) {
//            boolean is_char = false;
//            String temp = m.group(1);
//            Map<String,String> temp_map = new HashMap<String, String>();
//            String[] ss = temp.split(",");
//            Pattern tmp_pattern = Pattern.compile("&#39;");
//            Matcher tmp_m = tmp_pattern.matcher(ss[0]);
//            if(tmp_m.find()){
//                is_char = true;
//            }
//            Pattern tmp_pattern_map = null;
//            if(is_char==true){
//                tmp_pattern_map = Pattern.compile("&#39;(.*?)&#39;");
//            }
//            else {
//                tmp_pattern_map = Pattern.compile("'(.*?)'");
//            }
//
//            for(int i=0;i<ss.length;i++){
//                Matcher tmp_m_map = tmp_pattern_map.matcher(ss[i]);
//                if(tmp_m_map.find()){
//                    ss[i] = tmp_m_map.group(1);
//                }else {
//                    System.out.println("没有发现合适的参数");
//                }
//            }
//            list.add(ss);
//        }
        //先以逗号分隔

        String xpath = "//*[@id=\"mainForm\"]/div[2]/div/table/tbody/tr";
    }
}
