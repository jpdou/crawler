package javmoo;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Process implements Runnable {

    private CloseableHttpClient httpclient;

    private String baseUrl;
    private String mediaFolder;

    private RequestConfig requestConfig;

    Process(String baseUrl, String mediaFolder) {

        httpclient = HttpClients.createDefault();

        this.baseUrl = baseUrl;
        this.mediaFolder = mediaFolder;
    }

    public void run()
    {
        // 找出之前 信息不完善的 video, 继续解析并保存
        Video video = new Video();
        ArrayList<Video> videos = video.getAllUncompletedVideos();
        for (Video _video : videos) {
            for (int i = 0; i < 3; i++) {
                System.out.println("olooo");
                if (this.parseVideoDetails(_video)) {
                    break;
                }
            }
            System.out.println("llplplp");
            try {
                int sec = (int) (Math.random() * 30);
                System.out.println("Will sleep " + sec + " seconds... ");
                Thread.sleep(sec * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 解析 video 列表页
        this.parsePages(this.baseUrl, 100);

        // 检查订阅 actresses 有无更新
        Actress actress = new Actress();
        ArrayList<Actress> actresses = actress.getSubscribedActresses();
        for (Actress _actress : actresses) {
            this.parsePages(_actress.getHomePage(), 25);
        }
    }

    private void parsePages(String startUrl, int pageCount)
    {
        Task task = new Task(pageCount);
        int p = task.getNext();

        ArrayList<Video> videos = new ArrayList<Video>();   // 从 video list 里解析的 video 集合

        while (p > 0) {
            String url = startUrl + "page/" + p;
            for (int i = 0; i < 3; i++) {
                if( this.parseVideoFromList(url, videos) ) {
                    break;
                }
            }

            if (videos.size() > 0) {    // 解析正确
                Boolean pageLoaded = true;
                for(Video video : videos) {
                    // 如果 video 信息已完善, 则跳过
                    video.load(video.getIdentifier(), "identifier");
                    if (video.isCompleted()) {
                        continue;
                    }
                    pageLoaded = false;
                    // 每个 video 页面最多尝试 3 次
                    for (int j =0; j < 3; j++) {
                        if (this.parseVideoDetails(video)) {
                            break;
                        }
                    }
                }
                if (pageLoaded) {   // 这一页上面的所有 video 信息都已经完善了
                    System.out.println("这一页上面的所有 video 信息都已经完善了.");
                    break;  // 停止爬取 video 列表页
                }
                videos.clear(); // 清空 video 集合
            } else {
                System.out.println("Fetch page " + p + "failed. ");
            }
            p = task.getNext();
        }
        System.out.println("爬取 video 列表页完毕");
    }

    private Boolean parseVideoFromList(String listUrl, ArrayList<Video> videos)
    {
        System.out.println("Start fetch page " + listUrl + " ...");

        HttpGet httpget = new HttpGet(listUrl);
        httpget.setConfig(this.getRequestConfig());
        httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");

        try {
            CloseableHttpResponse response = httpclient.execute(httpget);

            HttpEntity entity = response.getEntity();

            String html = EntityUtils.toString(entity);

            Document doc = Jsoup.parse(html);

            Element content = doc.getElementById("waterfall");

            for (Element element: content.children()) {

                String originHref = element.select("a").first().attr("href");

                if (originHref.contains(this.baseUrl)) {
                    originHref = originHref.replaceAll(this.baseUrl, "");
                }

                String thumbnail = element.select("img").first().attr("src");
                String identifier = element.select("date").first().html();
                String date = element.select("date").last().html();

                Video video = new Video();

                video.load(identifier, "identifier");

                if (video.getId() == 0) {   // video 不存在
                    video.setIdentifier(identifier);
                    video.setDate(date);
                    video.setOriginHref(originHref);
                    video.setThumbnail(thumbnail);
                    video.save();
                    video.load(identifier, "identifier");
                }
                videos.add(video);
            }
            response.close();
            System.out.println("Got one page. ");
            return true;
        } catch (Exception e) {
            System.out.println("Parse Video From List Error : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Boolean parseVideoDetails(Video video)
    {
        String url = this.baseUrl + video.getOriginHref();
        System.out.println("Start fetch " + url);
        HttpGet httpget = new HttpGet(url);
        httpget.setConfig(this.getRequestConfig());
        try {
            CloseableHttpResponse response = httpclient.execute(httpget);

            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                HttpEntity entity = response.getEntity();
                String html = EntityUtils.toString(entity);

                Document doc = Jsoup.parse(html);

                Element container = doc.body().select(">.container").first();

                // title
                String title = container.select("h3").text();
                video.setTitle(title);

                // thumbnail
                String path = "video/thumbnail/" + video.getIdentifier() + this.getFileExtensionFromUrl(video.getThumbnail());
                if (!this.isFileExisted(this.mediaFolder + path)) {
                    this.downloadFile(httpclient, video.getThumbnail(), this.mediaFolder + path);
                }
                video.setThumbnail(path);

                // poster
                String posterUrl = container.select(".movie>div.screencap>a>img").attr("src");
                path = "video/poster/" + video.getIdentifier() + this.getFileExtensionFromUrl(posterUrl);
                if (!this.isFileExisted(this.mediaFolder + path)) {
                    this.downloadFile(httpclient, posterUrl, this.mediaFolder + path);
                }
                video.setPoster(path);

                video.save();

                video.load(video.getIdentifier(), "identifier");

                Element avatarContainer = container.getElementById("avatar-waterfall");
                if (avatarContainer != null) {
                    Actress actress;
                    for (Element box : avatarContainer.children()) {
                        String name = box.select(">span").text();

                        actress = ActressManager.getActress(name);
                        if (actress == null) {
                            System.out.println("Actress is not existed: " + name);
                            actress = new Actress();
                            String homePage = avatarContainer.select("a").attr("href");
                            String avatarUrl = avatarContainer.select("img").attr("src");
                            path = "actress/avatar/" + name + this.getFileExtensionFromUrl(avatarUrl);
                            if (this.isFileExisted(this.mediaFolder + path)) {
                                this.downloadFile(httpclient, avatarUrl, this.mediaFolder + path);
                            }

                            actress.setHomePage(homePage);
                            actress.setAvatar(path);
                            actress.setName(name);
                            actress.save();

                            actress = ActressManager.getActress(name);
                        }

                        System.out.println("Actress id = " + actress.getId());
                        if (!actress.hasVideo(video.getIdentifier())) {
                            actress.addVideo(video);
                        }
                    }
                }

                Element sampleContainer = container.getElementById("sample-waterfall");
                if (sampleContainer != null) {
                    int count = 0;
                    ArrayList<String> srcs = new ArrayList<String>();
                    for (Element sampleEl : sampleContainer.children()) {
                        String src = sampleEl.select(".sample-box").attr("href");

                        path = "video/sample/" + video.getIdentifier() + "/" + count +  this.getFileExtensionFromUrl(src);
                        if (!this.isFileExisted(this.mediaFolder + path)) {
                            this.downloadFile(httpclient, src, this.mediaFolder + path);
                        }

                        srcs.add(path);

                        count++;
                    }
                    video.removeAllSample();
                    video.addSamples(srcs);
                }
                return true;
            }
            response.close();
        } catch (Exception e) {
            System.out.println("Parse Video Details Error : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private void downloadFile(CloseableHttpClient httpclient, String url, String path)
    {
        System.out.println("Start download file: " + url);
        File f = new File(path);
        File dir = new File(f.getParent());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        HttpGet httpget = new HttpGet(url);
        httpget.setConfig(this.getRequestConfig());
        try {
            CloseableHttpResponse response = httpclient.execute(httpget);

            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();

                FileUtils.copyInputStreamToFile(is, f);
            }
        } catch (Exception e) {
            System.out.println("Download file error: " + e.getMessage());
        }
    }

    private String getFileExtensionFromUrl(String url)
    {
        int idx = url.lastIndexOf('.');
        return url.substring(idx);
    }

    private boolean isFileExisted(String path)
    {
        File f = new File(path);
        return f.exists();
    }

    private RequestConfig getRequestConfig()
    {
        if (this.requestConfig == null) {
            this.requestConfig = RequestConfig.custom()
                .setSocketTimeout(10000)
                .setConnectTimeout(10000)
                .build();
        }
        return requestConfig;
    }
}
