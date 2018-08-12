package javmoo;

import java.sql.*;

public class Conn {

    private static Statement stmt;

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/crawler?serverTimezone=GMT%2B8&useSSL=false&autoReconnect=true";

    private static final String USER = "root";
    private static final String PASS = "toor";

    private static void initialize()
    {
        try{
            // 注册 JDBC 驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 打开链接
            Connection conn = DriverManager.getConnection(DB_URL,USER,PASS);

            stmt = conn.createStatement();
        }catch(SQLException se){
            // 处理 JDBC 错误
            System.out.println("Connect MySQL failed. " + se.getMessage());
        }catch(Exception e){
            // 处理 Class.forName 错误
            System.out.println("Connect MySQL failed2. " + e.getMessage());
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
