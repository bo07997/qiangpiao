package Test;
import org.apache.http.Header;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import java.util.regex.*;
import org.apache.commons.io.FileUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
public class testPost {

    public  testPost() {
//        httpclient = HttpClients.createDefault();
//        // mis登陆界面网址
//        httpget = new HttpGet("http://183.6.175.51:8000/xb/xbywyy/selectType.jsp");
    }

    public void logIn() throws Exception {

        //httpget.setHeader("Content-Type", "application/x-www-form-urlencoded");

        try {
            HttpResponse firstResponse = PGHelpper.GET("http://183.6.175.51:8000/xb/xbywyy/selectType.jsp",null,null);
            PGHelpper.close_get();//关闭连接
            String cookies = firstResponse.getLastHeader("Set-Cookie").getValue();
            System.out.println("获得cookie信息: "+firstResponse.getLastHeader("Set-Cookie").getValue());
            // 创建 Pattern 对象
            Pattern pattern = Pattern.compile("JSESSIONID=(.*?);");
            // 现在创建 matcher 对象
            Matcher m = pattern.matcher(cookies);
            String session = null;
            if (m.find( )) {
                session = "JSESSIONID=" + m.group(1) ;
            }

            //选择新办预约类别下一步--------------------------------------------------------------
            String selBookingType = "02010101";
            String secondUrl = "http://183.6.175.51:8000/xb/xbywyy/selectMaterial.jsp";
            Map<String,String> secondHeader = new HashMap();
            secondHeader.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64");
            secondHeader.put("Content-Type","application/x-www-form-urlencoded");//这个一定要加
            StringEntity secondEntity = new StringEntity("selBookingType=" + selBookingType, "utf-8");
            HttpResponse secondResponse = PGHelpper.POST(secondUrl, secondHeader,session,secondEntity);
            PGHelpper.close_post();//关闭连接

            //材料清单选择----------------------------------------------------------------------
            String threeUrl = "http://183.6.175.51:8000/xb/xbywyy/userInfo.jsp";
            HttpResponse threeRespose = PGHelpper.GET(threeUrl,null,session);
            PGHelpper.close_get();//关闭连接

            //流水号填写----------------------------------------------------------------------可以获取一些信息，返回json
            String number = "20180226160543906";
            String fourUrl = "http://183.6.175.51:8000/xb/xbywyy/queryXbywzhBySerialNumber.do";
            Map<String,String> fourHeader = new HashMap();
            fourHeader.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64");
            fourHeader.put("Content-Type","application/x-www-form-urlencoded");//这个一定要加
            StringEntity fourEntity = new StringEntity("sblsh=" + number, "utf-8");
            HttpResponse fourResponse = PGHelpper.POST(fourUrl, fourHeader,session,fourEntity);
            HttpEntity entity_four = fourResponse.getEntity();
            String strResult_four = EntityUtils.toString(entity_four,"UTF-8");

            //图像验证码
            //首先生成小数
            number = this.make_float();
            String fiveUrl = "http://183.6.175.51:8000/xb/validcode/createValidateCode.do?random=" + number;
            HttpResponse fiveRespose = PGHelpper.GET(fiveUrl,null,session);
            HttpEntity entity_five = fiveRespose.getEntity();
            if(entity_five!=null){
                InputStream inputStream=entity_five.getContent();
                FileUtils.copyToFile(inputStream, new File("G://validate.png")); //将图片保存在本次磁盘D盘，命名为xxx.png
            }
            PGHelpper.close_get();//关闭连接
            //Connection
            HttpEntity entity = fiveRespose.getEntity();
            String strResult = EntityUtils.toString(entity,"UTF-8");
            PrintWriter writer = new PrintWriter("G://io.html", "UTF-8");
            writer.println(strResult);
            writer.close();


        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //获取小数
    public String make_float(){
        float Max = 1.0f, Min = 0.0f;
        BigDecimal db = new BigDecimal(Math.random() * (Max - Min) + Min);
        String result = db.setScale(16, BigDecimal.ROUND_HALF_UP)// 保留16位小数并四舍五入
                .toString();
        return result;
    }

    public static void main(String[] args) throws Exception {
        String name = "xxxxx", password = "xxxxxx";
        // 自己的账号，口令
        testPost lr = new testPost();
        //lr.make_float();
        lr.logIn();
    }
}

