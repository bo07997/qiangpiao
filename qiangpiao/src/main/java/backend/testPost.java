package backend;

import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import backend.ruokuai.RuoKuai;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;
import java.util.Scanner;
import java.util.regex.*;
import org.apache.commons.io.FileUtils;
import org.apache.http.util.EntityUtils;
import backend.tesscert.*;
import net.sf.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Text;
import org.dom4j.io.SAXReader;
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
            String session = "";
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
            PGHelpper.close_post();

            //图像验证码
            //首先生成小数
            number = this.make_float();
            String fiveUrl = "http://183.6.175.51:8000/xb/validcode/createValidateCode.do?random=" + number;
            HttpResponse fiveRespose = PGHelpper.GET(fiveUrl,null,session);
            HttpEntity entity_five = fiveRespose.getEntity();
            String path = "G://picture//"+ number.substring(2,10) +".jpg";
            if(entity_five!=null){
                InputStream inputStream=entity_five.getContent();
                FileUtils.copyToFile(inputStream, new File(path)); //将图片保存在本次磁盘G盘，命名为xxx.jpg
            }
            String valCode = null;
            try {
                valCode = new OCR().recognizeText(new File(path), "jpg", "myletter");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            PGHelpper.close_get();//关闭连接
            //构建经办人信息
            //1.首先从网站获取
            JSONObject json = JSONObject.fromObject(strResult_four);
            String sfzmhm = json.getString("sfzmhm");
            String gsmc = json.getString("gsmc");
            String jbrndsjhm = json.getString("jbrndsjhm");
            String sydjhm = json.getString("sydjhm");
            Map<String,String> info_map = new HashMap<String,String>();
            info_map.put("zjmc","B");//证件类型
            info_map.put("zjhm",sfzmhm);//证件号码
            info_map.put("xm",gsmc);//姓名
            info_map.put("sjhm","15622107997");//手机号码
            info_map.put("sh",sydjhm);//商号
            info_map.put("ywlx","0201");
            info_map.put("ywlb","02010101");
            info_map.put("startRec","1");
            info_map.put("endRec","");
            info_map.put("captcha",valCode);
            String form ="";
            for (Map.Entry<String,String> entry : info_map.entrySet()) {
                form += (entry.getKey() + "=" + entry.getValue() + "&");
            }
            form = form.substring(0,form.length() - 1);
            StringEntity sixEntity = new StringEntity(form, "utf-8");
            sixEntity.setContentType("application/x-www-form-urlencoded");
            String sixUrl = "http://183.6.175.51:8000/xb/xbywyy/queryXbywyy.do";

            Map<String,String> sixHeader = new HashMap();
            sixHeader.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64");
            sixHeader.put("Content-Type","application/x-www-form-urlencoded");//这个一定要加
            HttpResponse sixResponse = PGHelpper.POST(sixUrl, sixHeader,session,sixEntity);
            HttpEntity six_entity = sixResponse.getEntity();
            String sixResult = EntityUtils.toString(six_entity,"UTF-8");
            JSONObject sixJson = JSONObject.fromObject(sixResult);
            String six_end = sixJson.getString("type");
            PGHelpper.close_post();
            if (!six_end.equals("success") ){
                System.out.println("跳转到手机验证码界面失败!");
            }
            ///xb/xbywyy/authSMCode.jsp
            ///xb/xbywyy/sendXbywYyMsg.do
            //提交
            //手机验证码获取----------------------------------------------------------
            String phone = "15622107997";
            String sevenUrl = "http://183.6.175.51:8000/xb/xbywyy/sendXbywYyMsg.do";
            StringEntity sevenEntity = new StringEntity("mobilePhone=" + phone, "utf-8");
            HttpResponse sevenResponse = PGHelpper.POST(sevenUrl, fourHeader,session,sevenEntity);
            HttpEntity entity_seven = sevenResponse.getEntity();
            String strResult_seven = EntityUtils.toString(entity_seven,"UTF-8");
            PGHelpper.close_post();
            Scanner scanner = new Scanner(System.in);
            String in_code = scanner.next();

            //手机验证码提交-------------------------------------------------------
            String eightUrl = "http://183.6.175.51:8000/xb/xbywyy/validXbywYyMsg.do";
            StringEntity eightEntity = new StringEntity("input=" + in_code, "utf-8");
            HttpResponse eightResponse = PGHelpper.POST(eightUrl, fourHeader,session,eightEntity);
            HttpEntity entity_eight = eightResponse.getEntity();
            String strResult_eight = EntityUtils.toString(entity_eight,"UTF-8");
            PGHelpper.close_post();
            JSONObject eight_json = JSONObject.fromObject(strResult_eight);
            if (eight_json.getString("type").equals("file") ){
                System.out.println(eight_json.getString("detail"));
            }
            //获取最后一步验证码
            int code_i = 0;
            while (true){
                List<String[]> date_result = null;
                while(true){
                    //跳转到最后的booking界面-----------------------------------
                    String nightUrl = "http://183.6.175.51:8000/xb/xbywyy/bookingList.jsp";
                    HttpResponse nightRespose = PGHelpper.GET(nightUrl,null, session);
                    HttpEntity entity = nightRespose.getEntity();
                    String strResult = EntityUtils.toString(entity,"UTF-8");
                    PGHelpper.close_get();
                    // 自动发现可预约时间-------------------------------------
                    date_result = this.find_book_date(strResult);
                    Pattern tmp = Pattern.compile("已约满");
                    Matcher tmp_m = tmp.matcher(strResult);
                    if(tmp_m.find()){
                        System.out.println("成功刷新预约界面。。。。。。。");
                    }
                    else {
                        System.out.println("进入非法页面，请重新操作！");
                    }
                    if(date_result.size() > 0){
                        break;
                    }
                    System.out.println("没有可用的预约时间。。。。。。。");
                    Thread.sleep(3000);
                }
                System.out.println("找到可预约时间，开始预约。。。。。。。");
                String tenUrl = "http://183.6.175.51:8000/xb/login/yyvalidateCode.do?i=" + code_i++;
                HttpResponse tenRespose = PGHelpper.GET(tenUrl,null,session);
                HttpEntity entity_ten = tenRespose.getEntity();
                String path_end = "picture//temp_source.jpg";
                if(entity_five!=null){
                    InputStream inputStream=entity_ten.getContent();
                    FileUtils.copyToFile(inputStream, new File(path_end)); //将图片保存在本次磁盘G盘，命名为xxx.jpg
                }
                String location = "";
                try {
                    System.out.println("验证码图片清洗。。。。。。。");
                    location = new OCR().recognizeText(new File(path_end), "jpg", "need_picture");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PGHelpper.close_get();//关闭连接
                System.out.println("调用专业接口识别验证码。。。。。。。");
                //调用专业识别验证码接口
                String result = RuoKuai.createByPost("bo07997","bo123456ok","3060","1000","1",
                        "b40ffbee5c1cf4e38028c197eb2fc751", location);
                Pattern tmp_pattern_last = Pattern.compile("<Result>(.*?)</Result>");
                Matcher tmp_m_last = tmp_pattern_last.matcher(result);
                String val_code = "";
                if(tmp_m_last.find()){
                    System.out.println("识别成功，即将提交预约。。。。。。。");
                    val_code = tmp_m_last.group(1);
                }
                else {
                    System.out.println("识别失败，重新刷新预约界面。。。。。。。");
                    continue;
                }
                //最后提交预约
                Map<String,String> last_map = new HashMap<String,String>();
                last_map.put("peid",date_result.get(0)[0]);//id
                last_map.put("bookingDate",date_result.get(0)[1]);//date
                last_map.put("bookingTime",date_result.get(0)[2]);//time
                last_map.put("yyvalidateCode",val_code);//code

                String lastForm ="";
                for (Map.Entry<String,String> entry : last_map.entrySet()) {
                    lastForm += (entry.getKey() + "=" + entry.getValue() + "&");
                }
                lastForm = lastForm.substring(0,lastForm.length() - 1);
                StringEntity endEntity = new StringEntity(lastForm, "utf-8");
                endEntity.setContentType("application/x-www-form-urlencoded");
                String endUrl = "http://183.6.175.51:8000/xb/xbywyy/wsyy.do";

                Map<String,String> endHeader = new HashMap<String,String>();
                sixHeader.put("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64");
                sixHeader.put("Content-Type","application/x-www-form-urlencoded");//这个一定要加
                HttpResponse endResponse = PGHelpper.POST(endUrl, endHeader,session,endEntity);
                HttpEntity end_entity = endResponse.getEntity();
                String endResult = EntityUtils.toString(end_entity,"UTF-8");
                JSONObject endJson = JSONObject.fromObject(endResult);
                String end_end = endJson.getString("type");
                PGHelpper.close_post();
                System.out.println(end_end.toString());
                //Connection
//                HttpEntity entity1 = nightRespose.getEntity();
//                String strResult2 = EntityUtils.toString(entity,"UTF-8");
//                PrintWriter writer = new PrintWriter("G://io.html", "UTF-8");
//                writer.println(strResult);
//                writer.close();
            }

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
    //获取可预约时间
    public List<String[]> find_book_date(String xmlString){
        Pattern pattern = Pattern.compile("<a href=\"javascript:booking(.*?);\"><span>");
        // 现在创建 matcher 对象
        Matcher m = pattern.matcher(xmlString);
        List<String[]> list = new ArrayList<String[]>();
        while (m.find( )) {
            boolean is_char = false;
            String temp = m.group(1);
            String[] ss = temp.split(",");
            Pattern tmp_pattern = Pattern.compile("&#39;");
            Matcher tmp_m = tmp_pattern.matcher(ss[0]);
            if(tmp_m.find()){
                is_char = true;
            }
            Pattern tmp_pattern_map = null;
            if(is_char==true){
                tmp_pattern_map = Pattern.compile("&#39;(.*?)&#39;");
            }
            else {
                tmp_pattern_map = Pattern.compile("'(.*?)'");
            }

            for(int i=0;i<ss.length;i++){
                Matcher tmp_m_map = tmp_pattern_map.matcher(ss[i]);
                if(tmp_m_map.find()){
                    ss[i] = tmp_m_map.group(1);
                }else {
                    System.out.println("没有发现合适的参数");
                }
            }
            list.add(ss);
        }
        return list;
    }

    public static void main(String[] args) throws Exception {
        // 自己的账号，口令
        testPost lr = new testPost();
        //lr.make_float();
        lr.logIn();
    }
}

