package javmoo;

import java.sql.*;

abstract class Resource {

    Statement stmt;

    protected String table;
    protected String idFiledName = "id";

    Resource()
    {
        this.stmt = Conn.getInstance();
    }

    Boolean isExisted(String value, String field)
    {
        int count = 0;
        try {
            String sql = this.getCountSql(value, field);
            ResultSet rs = this.stmt.executeQuery(sql);

            if (rs.next()) {
                count = rs.getInt("count(*)");
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Is Existed SQL Error" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Is Existed Error : " + e.getMessage());
        }
        return count > 0;
    }

    public void load(String value)
    {

    }

    public void load(int value)
    {
        String sql = this.getLoadSql(value);
        try {
            ResultSet rs = this.stmt.executeQuery(sql);
            this.afterLoad(rs);
        } catch (SQLException e) {
            System.out.println("Is Existed SQL Error" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Is Existed Error : " + e.getMessage());
        }

    }

    protected abstract void afterLoad(ResultSet rs);

    public void load(String value, String field)
    {

    }

    public void load(int value, String field)
    {

    }

    protected String getLoadSql(String value, String field)
    {
        return "SELECT * FROM " + this.table + " WHERE " + field + " = '" + value + "' LIMIT 1";
    }

    protected String getLoadSql(int value)
    {
        return "SELECT * FROM " + this.table + " WHERE " + this.idFiledName + " = " + value + " LIMIT 1";
    }

    protected String getLoadSql(int value, String field) {
        return "SELECT * FROM " + this.table + " WHERE " + field + " = " + value + " LIMIT 1";
    }

    protected String getCountSql(String value, String field)
    {
        return "SELECT count(*) FROM " + this.table + " WHERE " + field + " = '" + value + "'";
    }
}
