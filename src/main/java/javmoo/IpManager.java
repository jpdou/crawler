package javmoo;

import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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

        String url = "http://www.xiongmaodaili.com/xiongmao-web/api/glip?secret=fd02774a625abcdc490c6deb5409525f&orderNo=GL201808132234516AQMDTym&count=10&isTxt=0&proxyType=1";
        HttpGet httpget = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpclient.execute(httpget);

            HttpEntity entity = response.getEntity();

            String html = EntityUtils.toString(entity);

            System.out.println(html);

            JsonParser parse = new JsonParser();
            JsonObject json = (JsonObject) parse.parse(html);  //创建jsonObject对象

            JsonArray result = json.get("obj").getAsJsonArray();

            for (JsonElement item : result) {
                String ip = ((JsonObject) item).get("ip").getAsString();
                String port = ((JsonObject) item).get("port").getAsString();
                this.ips.add(ip + ":" + port);
            }
        } catch (Exception e) {
            System.out.println("Request IPs failed. " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void refresh()
    {
        this.current = 0;
        this.ips.clear();
        this.fetch();
    }

    public String next()
    {
        if (current == this.ips.size()) {
            this.refresh();
        }
        return this.ips.get(current++);
    }
}
