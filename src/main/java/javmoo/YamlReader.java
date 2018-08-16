package javmoo;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

public class YamlReader {

    private Map data;

    YamlReader(String env)
    {
        InputStream input = null;
        try {
            input = new FileInputStream(new File("src/main/resources/properties.yaml"));
            Yaml yaml = new Yaml();
            Map map = yaml.load(input);

            this.data = (Map) map.get(env);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Object get(String path)
    {
        if (path.contains("/")) {   // 多层
            String[] keys = path.split("/");
            Object result = this.data;
            for (String key : keys) {
                Map map = (Map) result;
                if (map.containsKey(key)) {
                    result = map.get(key);
                } else {
                    result = null;
                    break;
                }
            }
            return result;
        } else {    // 单层
            return this.data.get(path);
        }
    }
}
