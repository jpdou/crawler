package javmoo;

import java.sql.*;
import java.util.Map;

public class Conn {

    private static Statement stmt;

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://{host}:{port}/{dbname}?serverTimezone=GMT%2B8&useSSL=false&autoReconnect=true";

    private static void initialize()
    {
        try{

            YamlReader yamlReader = YamlReader.getInstance();

            // 注册 JDBC 驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            Map databaseProperties = (Map) yamlReader.get("database");

            String dbUrl = DB_URL.replace("{host}", (String) databaseProperties.get("host"));
            dbUrl = dbUrl.replace("{port}", ((Integer) databaseProperties.get("port")).toString());
            dbUrl = dbUrl.replace("{dbname}", (String) databaseProperties.get("dbname"));

            String user = (String) databaseProperties.get("user");

            String pass = (String) databaseProperties.get("password");

            // 打开链接
            Connection conn = DriverManager.getConnection(dbUrl,user,pass);

            stmt = conn.createStatement();
        }catch(SQLException se){
            // 处理 JDBC 错误
            System.out.println("Connect MySQL failed. " + se.getMessage());
        }catch(Exception e){
            // 处理 Class.forName 错误
            System.out.println("Connect MySQL failed2. " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Statement getInstance()
    {
        if (stmt == null) {
            initialize();
        }
        return stmt;
    }
}
