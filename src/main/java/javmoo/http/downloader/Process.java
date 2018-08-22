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
    }

    @Override
    public void run() {
        while (true) {
            if (this.tasks.size() > 0) {
                Task task = this.tasks.remove(0);
                this.downloadFile(httpclient, task.getLink(), task.getSavePath());
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void downloadFile(CloseableHttpClient httpclient, String url, String path)
    {
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
