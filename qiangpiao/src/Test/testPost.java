package Test;
import org.apache.http.Header;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import java.util.regex.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
public class testPost {
    private CloseableHttpClient httpclient;
    private HttpPost httppost;// 用于提交登陆数据
    private HttpGet httpget;// 用于获得登录后的页面
    private String login_success;// 用于构造上面的HttpGet

    public  testPost() {
        httpclient = HttpClients.createDefault();
        // mis登陆界面网址
        httpget = new HttpGet("http://183.6.175.51:8000/xb/xbywyy/selectType.jsp");
    }

    public void logIn() throws Exception {

        httpget.setHeader("Content-Type", "application/x-www-form-urlencoded");

        try {
            HttpResponse response = httpclient.execute(httpget);
            Header[] h =response.getAllHeaders();
            for (Header header : h) {
                System.out.println(header.toString());
            }
            String cookies = response.getLastHeader("Set-Cookie").getValue();
            System.out.println("用户: "+"  cookie信息: "+response.getLastHeader("Set-Cookie").getValue());
            // 创建 Pattern 对象
            Pattern pattern = Pattern.compile("JSESSIONID=(.*?);");
            // 现在创建 matcher 对象
            Matcher m = pattern.matcher(cookies);
            String session = null;
            if (m.find( )) {
                session = m.group(0) ;
            }

            //选择类别下一步
            String selBookingType = "02010101";

            HttpPost type_httppost = new HttpPost("http://183.6.175.51:8000/xb/xbywyy/selectMaterial.jsp");
            type_httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            type_httppost.setHeader("cookie", session);
            // 你还可以通过 PostMethod/GetMethod 设置更多的请求后数据
            // 例如，referer 从哪里来的，UA 像搜索引擎都会表名自己是谁，无良搜索引擎除外
            type_httppost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64");
            type_httppost.setEntity(new StringEntity("selBookingType=" + selBookingType, "utf-8"));
            HttpResponse re = httpclient.execute(type_httppost);


            HttpGet all_httpget = new HttpGet("http://183.6.175.51:8000/xb/xbywyy/userInfo.jsp");
            all_httpget.setHeader("cookie", session);
            // 你还可以通过 PostMethod/GetMethod 设置更多的请求后数据
            // 例如，referer 从哪里来的
            all_httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64");
            HttpResponse all_re = httpclient.execute(all_httpget);

            HttpEntity entity = all_re.getEntity();
            String strResult = EntityUtils.toString(entity,"UTF-8");
            int a = 0;
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

    public void PrintText(String name) throws IOException {
        httpget = new HttpGet(login_success);
        HttpResponse re2 = null;

        try {
            re2 = httpclient.execute(httpget);
            // 输出登录成功后的页面
            String str = EntityUtils.toString(re2.getEntity());

            System.out.println("\n"+name+"首页信息如下:");
            System.out.println(str.substring(8250,8400));
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            httppost.abort();
            httpget.abort();
            httpclient.close();
        }
    }

    public static void main(String[] args) throws Exception {
        String name = "xxxxx", password = "xxxxxx";
        // 自己的账号，口令
        testPost lr = new testPost();
        lr.logIn();
        lr.PrintText(name);
    }
}

