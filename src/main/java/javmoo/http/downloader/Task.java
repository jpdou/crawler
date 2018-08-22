package javmoo.http.downloader;

public class Task {

    private String link;

    private String savePath;

    public Task(String link, String savePath)
    {
        this.link = link;
        this.savePath = savePath;
    }

    public String getLink() {
        return link;
    }

    public String getSavePath() {
        return savePath;
    }
}
