package javmoo;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import java.io.*;
import java.util.ArrayList;

public class Process implements Runnable {

    private String baseUrl;
    private String mediaFolder;

    private Task task;
    private Thread thread;
    private boolean finished = false;

    Process(Task task, String baseUrl, String mediaFolder) {
        this.task = task;

        this.baseUrl = baseUrl;
        this.mediaFolder = mediaFolder;
    }

    public void run()
    {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        ArrayList<Video> videos = this.parseVideoFromList(httpclient);

        for(Video video : videos) {
            this.parseVideoDetails(video, httpclient);
        }

        this.finished = true;
    }

    public boolean isFinished()
    {
        return this.finished;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    private ArrayList<Video> parseVideoFromList(CloseableHttpClient httpclient)
    {
        ArrayList<Video> videos = new ArrayList<Video>();
        while (true) {
            int p = this.task.getNext();
            if (p < 0) {
                break;
            }
            System.out.println("Start fetch page " + p + " ...");
            String url = this.baseUrl + "page/" + p;

            System.out.println(url);

            HttpGet httpget = new HttpGet(url);

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
            } catch (Exception e) {
                System.out.println("Parse Video From List Error : " + e.getMessage());
                e.printStackTrace();
            }
        }

        return videos;
    }

    public void parseVideoDetails(Video video, CloseableHttpClient httpclient)
    {
        HttpGet httpget = new HttpGet(this.baseUrl + video.getOriginHref());
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
                System.out.println("before load : " + video.getIdentifier());
                video.load(video.getIdentifier(), "identifier");
                System.out.println("video id = " + video.getId());

                Element avatarContainer = container.getElementById("avatar-waterfall");
                if (avatarContainer != null) {
                    Actress actress;
                    for (Element box : avatarContainer.children()) {
                        String name = box.select(">span").text();

                        actress = ActressManager.getActress(name);
                        System.out.println(actress);
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
                    video.addSamples(srcs);
                }
            }
        } catch (Exception e) {
            System.out.println("Parse Video Details Error : " + e.getMessage() + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void downloadFile(CloseableHttpClient httpclient, String url, String path)
    {
        File f = new File(path);
        File dir = new File(f.getParent());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        HttpGet httpget = new HttpGet(url);
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
}
