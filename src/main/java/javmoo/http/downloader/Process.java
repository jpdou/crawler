package javmoo.http.downloader;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public class Process implements Runnable {

    private ArrayList<Task> tasks;

    private RequestConfig requestConfig;
    private CloseableHttpClient httpclient;

    public Process(ArrayList<Task> tasks)
    {
        this.tasks = tasks;
        this.httpclient = HttpClients.createDefault();
        this.requestConfig = RequestConfig.custom()
                .setSocketTimeout(10000)
                .setConnectTimeout(10000)
                .build();
    }

    @Override
    public void run() {
        while (true) {
            Task task = this.nextTask();
            if (task != null) {
                this.downloadFile(task);
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    synchronized private Task nextTask()
    {
        if (this.tasks.size() > 0) {
            return this.tasks.remove(0);
        }
        return null;
    }

    private void downloadFile(Task task)
    {
        String url = task.getLink();
        String path = task.getSavePath();

        if (this.isFileExisted(path)) {
            return;
        }

        System.out.println("Start download file: " + url);
        File f = new File(path);
        File dir = new File(f.getParent());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        HttpGet httpget = new HttpGet(url);
        httpget.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = httpclient.execute(httpget);

            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();

                FileUtils.copyInputStreamToFile(is, f);
            } else if (task.getFailedTimes() < Task.failedTimeLimit){
                this.retry(task);
            }
        } catch (Exception e) {
            System.out.println("Download file error: " + e.getMessage());
            if (task.getFailedTimes() < Task.failedTimeLimit){
                this.retry(task);
            }
        }
    }

    private boolean isFileExisted(String path)
    {
        File f = new File(path);
        return f.exists();
    }

    /**
     * 下载失败的任务重新加入下载队列
     * @param task 下载任务
     */
    private void retry(Task task)
    {
        task.increaseFailedTimes();
        this.tasks.add(task);
    }
}
