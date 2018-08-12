package javmoo;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class IpManager {

    ArrayList<String> ips = new ArrayList<String>();
    private int current = 0;

    IpManager()
    {
        fetch();
    }

    public void fetch()
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String url = "http://www.xicidaili.com/nn/";
        HttpGet httpget = new HttpGet(url);
        httpget.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
        try {
            CloseableHttpResponse response = httpclient.execute(httpget);

            HttpEntity entity = response.getEntity();

            String html = EntityUtils.toString(entity);
            Document doc = Jsoup.parse(html);

            Element list = doc.getElementById("ip_list");
            if (list != null) {
                String ip = "", port = "";
                for (Element tr : list.select("tr")) {
                    Elements tds = tr.select("td");
                    if (tds != null) {
                        int i = 0;
                        for (Element td : tds) {
                            if (i == 1) {
                                ip = td.text();
                            } else if (i == 2) {
                                port = td.text();
                            }
                            if (ip.length() > 4 && port.length() > 1) {
                                this.ips.add(ip + ":" + port);
                            }
                            i++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Request IPs failed. " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void refresh()
    {
        this.ips.clear();
        this.fetch();
    }

    public String next()
    {
        return this.ips.get(current++);
    }
}
