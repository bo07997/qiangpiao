package Test;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.Map;

public  class PGHelpper {
    public static CloseableHttpClient httpclient = HttpClients.createDefault();
    public static HttpPost httppost;// 用于提交登陆数据
    public static HttpGet httpget;// 用于获得登录后的页面

    public static HttpResponse GET(String url, Map<String,String> Header, String cookie){
        httpget = new HttpGet(url);
        HttpResponse get = null;
        if (Header != null){
            for (Map.Entry<String,String> entry : Header.entrySet()) {
                httpget.setHeader(entry.getKey(), entry.getValue());
            }
        }
        if(cookie!=null){
            httpget.setHeader("Cookie", cookie);
        }
        try {
            get = httpclient.execute(httpget);
        }
        catch (Exception ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        return get;
    }

    public static HttpResponse POST(String url, Map<String,String> Header, String cookie, StringEntity entity){
        httppost = new HttpPost(url);
        HttpResponse post = null;
        if (Header != null){
            for (Map.Entry<String,String> entry : Header.entrySet()) {
                httppost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        if(cookie!=null){
            httppost.setHeader("Cookie", cookie);
        }
        if(entity!=null){
            httppost.setEntity(entity);
        }
        try {
            post = httpclient.execute(httppost);
        }
        catch (Exception ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

        return post;
    }
    public static void close_get(){
        httpget.abort();
    }
    public static void close_post(){
        httppost.abort();
    }
}
