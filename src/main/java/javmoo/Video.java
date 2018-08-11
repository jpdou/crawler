package javmoo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Video extends Resource {
    private int id;
    private String originHref;
    private String title;
    private String identifier;
    private String thumbnail;
    private String poster;
    private String date;

    private ArrayList<Integer> actressIds;

    Video()
    {
        this.table = "video";
    }

    public void save()
    {
        String sql;
        if (this.isExisted(this.getIdentifier(), "identifier")) {
            sql = "update " + table + " set origin_href = '" + this.getOriginHref() +
                    "', title = '" + this.getTitle() +
                    "', thumbnail = '" + this.getThumbnail() +
                    "', poster = '" + this.poster +
                    "', date = '" + this.getDate() +
                    "' where identifier = '" + this.getIdentifier() + "'";
        } else {
            sql = "insert into " + table + " (origin_href, title, identifier, thumbnail, poster, date) " +
                    "values (" +
                    "'" + this.getOriginHref() +
                    "', '" + this.getTitle() +
                    "', '" + this.getIdentifier() +
                    "', '" + this.getThumbnail() +
                    "', '" + this.getPoster() +
                    "', '" + this.getDate() +
                    "')";
        }

        try {
            this.stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
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
                this.id = rs.getInt("id");
                this.identifier = rs.getString("identifier");
                this.poster = rs.getString("poster");
                this.thumbnail = rs.getString("thumbnail");
                this.title = rs.getString("title");
                this.date = rs.getString("date");
                this.originHref = rs.getString("origin_href");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public String getOriginHref() {
        return originHref;
    }

    public String getTitle() {
        return title;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getPoster() {
        return poster;
    }

    public String getDate() {
        return date;
    }

    public void setOriginHref(String originHref) {
        this.originHref = originHref;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier.trim();
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
