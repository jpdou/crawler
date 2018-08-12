package javmoo;

import java.util.ArrayList;


public class Launcher {

    public static void main(String[] args)
    {
        int concurrent = 1;

        String baseUrl = "https://javmoo.com/cn/";
        String mediaFolder = "C:/var/www/demo/media/";

        ArrayList<Process> processes = new ArrayList<Process>();
        ArrayList<Thread> threads = new ArrayList<Thread>();

        Task task = new Task(100);

        long startAt = System.currentTimeMillis();
        long finishedAt;

        for(int i = 0; i < concurrent; i++) {
            Process p = new Process(task, baseUrl, mediaFolder);
            Thread t = new Thread(p);
            t.setName("process-" + i);
            t.start();

            p.setThread(t);
            threads.add(t);
            processes.add(p);
        }

        while(!processes.isEmpty()) {
            for (Process p : processes) {
                if (p.isFinished()) {
                    Thread t = p.getThread();
                    processes.remove(p);
                    threads.remove(t);
                    System.out.println(t.getName() + " is finished. ");
                }
            }
            System.out.println("Left " + processes.size() + " process(es). ");
            try {
                Thread.sleep(60000); // 暂停一分钟
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        finishedAt = System.currentTimeMillis();

        System.out.println("Spent " + (finishedAt - startAt) / (1000 * 60 ) + " mins.");
    }

}
