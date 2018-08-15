package javmoo;

import java.util.ArrayList;


public class Launcher {

    public static void main(String[] args)
    {
        String baseUrl = "https://javmoo.com/cn/";
        String mediaFolder = "C:/var/www/demo/media/";

        while(true) {
            long startAt = System.currentTimeMillis();
            long finishedAt;

            Process p = new Process(baseUrl, mediaFolder);

            p.run();

            finishedAt = System.currentTimeMillis();

            try {

                System.out.println("Spent " + (finishedAt - startAt) / (1000 * 60 ) + " mins.");

                int min = (int) (Math.random() * 30);  ;
                System.out.println("暂停 " + min + "分钟");
                Thread.sleep(min * 60 * 1000); // 暂停 0 ~ 30 分钟
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
