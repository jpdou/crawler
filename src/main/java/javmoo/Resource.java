package javmoo;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

abstract class Resource {

    final static int TYPE_STRING = 1;
    final static int TYPE_INT = 2;
    final static int TYPE_DECIMAL = 3;
    final static int TYPE_UNKNOWN = 1000;

    protected Connection conn;

    protected String table;
    protected String idFiledName = "id";

    /**
     * 表字段数据类型
     */
    protected HashMap<String, Integer> fieldTypes = new HashMap<String, Integer>();
    protected HashMap<String, Object> data = new HashMap<String, Object>();

    Resource() {
        this.conn = Adapter.getConn();

        this.defineFieldsType();
    }

    public abstract void defineFieldsType();

    public Object getData(String key)
    {
        return this.data.get(key);
    }

    public Object getData()
    {
        return this.data;
    }

    public Resource setData(String key, Object value)
    {
        this.data.put(key, value);
        return this;
    }

    public Boolean hasData(String key)
    {
        return this.data.containsKey(key) && this.data.get(key) != null;
    }

    public int getId()
    {
        Object id = this.getData(this.idFiledName);
        if (id == null) {
            id  = 0;
        }
        return (Integer) id;
    }

    public abstract void beforeSave () throws Exception;

    public abstract void afterSave() throws Exception;

    public void save()
    {
        try {
            this.beforeSave();
        } catch (Exception e) {
            System.out.println("Before save Exception: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        HashMap<String, String> data = new HashMap<String, String>();
        ArrayList<String> fields = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();
        String value = null;
        for(String field : this.data.keySet()) {
            Integer type = this.fieldTypes.get(field);
            if (type == null) {
                type = TYPE_UNKNOWN;
            }
            switch (type) {
                case TYPE_STRING:
                    value = "'" + this.data.get(field) + "'";
                    break;
                case TYPE_INT:
                case TYPE_DECIMAL:
                    value = this.data.get(field).toString();
                    break;
                default:
                    System.out.println("Unrecognized field type : " + type + ", field : " + field);
                    break;
            }
            if (value != null) {
                data.put(field, value);
                fields.add(field);
                values.add(value);
            }
        }

        String sql;
        if (this.getId() > 0) { // update
            sql = "UPDATE " + this.table  + " SET {keyValues} WHERE " + this.idFiledName + " = " + this.getId();
            ArrayList<String> keyValues = new ArrayList<String>();
            for (int i = 0; i < fields.size(); i++) {
                keyValues.add(fields.get(i) + "=" + values.get(i));
            }
            sql = sql.replace("{keyValues}", String.join(",",keyValues));
        } else {    // insert
            sql = "INSERT INTO " + this.table  + "({columns}) VALUES ({values})";
            sql = sql.replace("{columns}", String.join(",", fields));
            sql = sql.replace("{values}", String.join(",", values));
        }

        try {
            this.conn.createStatement().execute(sql);

            this.afterSave();

        } catch (SQLException e) {
            System.out.println("Save failed. " + e.getMessage());
            System.out.println("SQL: " + sql);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("After Save Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected abstract void afterLoad(ResultSet rs);

    public void load(int value)
    {
        String sql = this.getLoadSql(value);
        try {
            ResultSet rs = this.conn.createStatement().executeQuery(sql);
            this.afterLoad(rs);
        } catch (SQLException e) {
            System.out.println("Load SQL Error" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Load Error : " + e.getMessage());
        }
    }

    public void load(String value, String field)
    {
        String sql = this.getLoadSql(value, field);
        try {
            ResultSet rs = this.conn.createStatement().executeQuery(sql);
            this.afterLoad(rs);
        } catch (SQLException e) {
            System.out.println("Load SQL Error" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Load Error : " + e.getMessage());
        }
    }

    public void load(int value, String field)
    {
        String sql = this.getLoadSql(value, field);
        try {
            ResultSet rs = this.conn.createStatement().executeQuery(sql);
            this.afterLoad(rs);
        } catch (SQLException e) {
            System.out.println("Is Existed SQL Error" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Is Existed Error : " + e.getMessage());
        }
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
