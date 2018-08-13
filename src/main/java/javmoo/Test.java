package javmoo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Test {
    public static void main(String[] args)
    {
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//
//        HttpHost proxy = new HttpHost("116.226.30.118", 31382);
//
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setSocketTimeout(20000)
//                .setConnectTimeout(20000)
//                .setProxy(proxy)
//                .build();
//
//        HttpGet httpget = new HttpGet("https://javmoo.com/cn/");
//        httpget.setConfig(requestConfig);
//        httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
//
//        try {
//            CloseableHttpResponse response = httpclient.execute(httpget);
//
//            HttpEntity entity = response.getEntity();
//
//            String html = EntityUtils.toString(entity);
//
//            System.out.println(html);;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        IpManager ipManager = new IpManager();
    }
}
