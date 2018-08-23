package javmoo.http.downloader;

public class Task {

    private String link;

    private String savePath;

    private int failedTimes = 0;

    public static final int failedTimeLimit = 3;

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

    public int getFailedTimes() {
        return failedTimes;
    }

    public void increaseFailedTimes() {
        this.failedTimes++;
    }
}
