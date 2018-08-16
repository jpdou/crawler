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
        this.fieldTypes.put("poster", Resource.TYPE_STRING);
        this.fieldTypes.put("date", Resource.TYPE_STRING);
        this.fieldTypes.put("identifier", Resource.TYPE_STRING);
        this.fieldTypes.put("completed", Resource.TYPE_INT);
    }

    public void beforeSave() throws Exception {
        if (!this.data.containsKey("identifier")) {
            throw new Exception("Require identifier when saving video");
        }
        if (this.isCompleted()) {
            this.setCompleted(true);
        }
    }

    @Override
    public void afterSave() throws Exception {
        // 更新 actress last_updated
        if (this.hasData("date")) {
            String[] videoDates = this.getDate().split("-");
            if (videoDates.length >= 3) {
                ArrayList<Actress> actresses = this.getActresses();
                for (Actress actress : actresses) {
                    if (actress.hasData("last_updated")) {
                        String lastUpdated = actress.getLastUpdated();
                        if (lastUpdated.length() > 0) {
                            String[] date = lastUpdated.split("-");
                            if (date.length >= 3) {
                                for (int i = 0; i < 3; i++) {
                                    if (Integer.parseInt(videoDates[i]) > Integer.parseInt(date[i])) {
                                        actress.setLastUpdated(this.getDate());
                                        actress.save();
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        actress.setLastUpdated(this.getDate());
                        actress.save();
                    }
                }
            }
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
                this.setTitle(rs.getString("title"));
                this.setDate(rs.getString("date"));
                this.setOriginHref(rs.getString("origin_href"));
                this.setCompleted(rs.getBoolean("completed"));
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
            this.stmt.execute(sql);
        } catch (Exception e) {
            System.out.println("SQL error: " + e.getMessage());
            System.out.println("SQL: " + sql);
            e.printStackTrace();
        }
    }

    public Boolean isCompleted()
    {
        if (this.getId() > 0) {
            return this.hasData("title") && this.hasData("poster") && this.hasData("identifier");
        }
        return false;
    }

    private void setCompleted(Boolean bool)
    {
        this.data.put("completed", bool ? 1 : 0);
    }

    public ArrayList<Video> getAllUncompletedVideos()
    {
        ArrayList<Video> videos = new ArrayList<Video>();

        String sql = "SELECT * FROM " + this.table + " WHERE completed = 0";
        try {
            ResultSet rs = this.stmt.executeQuery(sql);
            while (rs.next()) {
                Video video = new Video();

                video.setId(rs.getInt("id"));
                video.setIdentifier(rs.getString("identifier"));
                video.setPoster(rs.getString("poster"));
                video.setTitle(rs.getString("title"));
                video.setDate(rs.getString("date"));
                video.setOriginHref(rs.getString("origin_href"));
                video.setCompleted(rs.getBoolean("completed"));

                videos.add(video);
            }
        } catch (SQLException e) {
            System.out.println("Load collection SQL Error" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Load collection Error : " + e.getMessage());
        }

        return videos;
    }

    public ArrayList<Actress> getActresses()
    {
        ArrayList<Actress> actresses = new ArrayList<Actress>();

        String sql = "SELECT actress_video.actress_id FROM " + this.table +
                " JOIN actress_video " +
                " ON " + this.table + ".id = actress_video.video_id" +
                " WHERE " + this.table + ".id = " + this.getId();
        System.out.println(sql);
        try {
            ResultSet rs = this.stmt.executeQuery(sql);
            while (rs.next()) {
                int actressId = rs.getInt("actress_id");
                System.out.println(actressId);
                Actress actress = new Actress();
                actress.load(actressId);
                actresses.add(actress);
            }
        } catch (SQLException e) {
            System.out.println("Load Video Actress ids failed. " + e.getMessage());
            e.printStackTrace();
        }
        return actresses;
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
