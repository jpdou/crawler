package javmoo.http;

import java.util.ArrayList;

import javmoo.http.downloader.Process;
import javmoo.http.downloader.Task;

public class Downloader {

    private static final int limit = 20;

    private static ArrayList<Task> tasks;

    private static ArrayList<Thread> threads;

    static
    {
        Downloader.tasks = new ArrayList<Task>();

        Downloader.threads = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            Process process = new Process(Downloader.tasks);
            Thread thread = new Thread(process);
            thread.start();
            Downloader.threads.add(thread);
        }
    }

    /**
     * 下载队列未满时, 加入任务并返回
     * 下载队列已满时, 阻塞在 add 方法中, 等下载队列有空位时, 加入任务后再返回
     * @param task 文件下载任务
     */
    public static void add(Task task)
    {
        while (Downloader.tasks.size() == limit * 2) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Downloader.tasks.add(task);
    }
}
