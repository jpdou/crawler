package javmoo;

public class Launcher {

    public static void main(String[] args)
    {
        String env = args[0];   // 读取命令行都一个参数 env, 可能的值: developer, production
        String baseDir = args[1]; // 读取命令行都二个参数 baseUrl

        YamlReader.initialize(baseDir, env);
        YamlReader yamlReader = YamlReader.getInstance();

        String baseUrl = (String) yamlReader.get("crawler/base_url");
        String mediaFolder = (String) yamlReader.get("crawler/media_folder");

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
