package javmoo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Video extends Resource {

    private ArrayList<Integer> actressIds;

    Video()
    {
        this.table = "video";
    }

    public void defineFieldsType() {
        this.fieldTypes.put("id", Resource.TYPE_INT);
        this.fieldTypes.put("origin_href", Resource.TYPE_STRING);
        this.fieldTypes.put("title", Resource.TYPE_STRING);
        this.fieldTypes.put("thumbnail", Resource.TYPE_STRING);
        this.fieldTypes.put("poster", Resource.TYPE_STRING);
        this.fieldTypes.put("date", Resource.TYPE_STRING);
        this.fieldTypes.put("identifier", Resource.TYPE_STRING);
    }

    public void beforeSave() throws Exception {
        if (!this.data.containsKey("identifier")) {
            throw new Exception("Require identifier when saving video");
        }
    }

    public ArrayList<Integer> getActressIds()
    {
        if (this.actressIds == null) {
            ArrayList<Integer> ids = new ArrayList<Integer>();
            String sql = "SELECT actress_id FROM actress_video WHERE video_id = " + this.getId();
            try {
                ResultSet rs = this.stmt.executeQuery(sql);
                while (rs.next()) {
                    ids.add(rs.getInt("actress_id"));
                }
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.actressIds = ids;
            }
        }
        return this.actressIds;
    }

    protected void afterLoad(ResultSet rs)
    {
        try {
            if (rs.next()) {
                this.setId(rs.getInt("id"));
                this.setIdentifier(rs.getString("identifier"));
                this.setPoster(rs.getString("poster"));
                this.setThumbnail(rs.getString("thumbnail"));
                this.setTitle(rs.getString("title"));
                this.setDate(rs.getString("date"));
                this.setOriginHref(rs.getString("origin_href"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeAllSample()
    {
        String sql = "DELETE FROM video_sample WHERE video_id = " + this.getId();
        try {
            this.stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addSamples(ArrayList<String> srcs)
    {
        String sql = "INSERT INTO video_sample (video_id, src) VALUES {values} ";

        ArrayList<String> values = new ArrayList<String>();

        for(String scr : srcs) {
            values.add("(" + this.getId() + ", '" + scr + "')");
        }

        String _values = String.join(",", values);

        sql = sql.replace("{values}", _values);

        try {
            System.out.println(sql);
            this.stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setId(int id)
    {
        this.data.put("id", id);
    }

    public String getOriginHref() {
        return (String) this.data.get("origin_href");
    }

    public void setOriginHref(String originHref) {
        this.data.put("origin_href", originHref);
    }

    public String getTitle() {
        return (String) this.data.get("title");
    }

    public void setTitle(String title) {
        this.data.put("title", title);
    }

    public String getIdentifier() {
        return (String) this.data.get("identifier");
    }

    public void setIdentifier(String identifier) {
        this.data.put("identifier", identifier);
    }

    public String getThumbnail() {
        return (String) this.data.get("thumbnail");
    }

    public void setThumbnail(String thumbnail) {
        this.data.put("thumbnail", thumbnail);
    }

    public String getPoster() {
        return (String) this.data.get("poster");
    }

    public void setPoster(String poster) {
        this.data.put("poster", poster);
    }

    public String getDate() {
        return (String) this.data.get("date");
    }

    public void setDate(String date) {
        if (date != null && date.length() >= 10) {
            this.data.put("date", date);
        }
    }

}
