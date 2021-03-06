package javmoo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Actress extends Resource {

    private ArrayList<Integer> videoIds;

    public Actress()
    {
        this.table = "actress";
    }

    public void defineFieldsType() {
        this.fieldTypes.put("id", Resource.TYPE_INT);
        this.fieldTypes.put("name", Resource.TYPE_STRING);
        this.fieldTypes.put("avatar", Resource.TYPE_STRING);
        this.fieldTypes.put("home_page", Resource.TYPE_STRING);
        this.fieldTypes.put("video_count", Resource.TYPE_INT);
        this.fieldTypes.put("office_id", Resource.TYPE_INT);
        this.fieldTypes.put("last_updated", Resource.TYPE_STRING);
    }

    public ArrayList<Integer> getVideoIds()
    {
        if (this.videoIds == null) {
            ArrayList<Integer> ids = new ArrayList<Integer>();
            String sql = "SELECT video_id FROM actress_video WHERE actress_id = " + this.getId();
            try {
                ResultSet rs = this.conn.createStatement().executeQuery(sql);
                while (rs.next()) {
                    ids.add(rs.getInt("video_id"));
                }
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.videoIds = ids;
            }
        }
        return this.videoIds;
    }

    public void beforeSave() throws Exception {
        if (!this.data.containsKey("name")) {
            throw new Exception("Require name when saving actress");
        }
    }

    @Override
    public void afterSave() throws Exception {

    }

    protected void afterLoad(ResultSet rs) {
        try {
            if (rs.next()) {
                this.setId(rs.getInt("id"));
                this.setName(rs.getString("name"));
                this.setAvatar(rs.getString("avatar"));
                this.setHomePage(rs.getString("home_page"));
                this.setVideoCount(rs.getInt("video_count"));
                this.setOfficeId(rs.getInt("office_id"));
                this.setLastUpdated(rs.getString("last_updated"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Video> getVideos()
    {
        ArrayList<Video> videos = new ArrayList<Video>();

        for(int videoId : getVideoIds()) {
            Video video = new Video();
            video.load(videoId);
            videos.add(video);
        }

        return videos;
    }

    public boolean hasVideo(String identifier)
    {
        int count = 0;
        String sql = "SELECT count(*) FROM actress " +
                "JOIN actress_video ON actress.id = actress_video.actress_id " +
                "JOIN video ON actress_video.video_id = video.id " +
                "WHERE video.identifier = '" + identifier +
                "' LIMIT 1";
        try {

            ResultSet rs = this.conn.createStatement().executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt("count(*)");
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("SQL Error: " + e.getMessage());
            System.out.println("SQL: " + sql);
            e.printStackTrace();
        }
        return count > 0;
    }

    public boolean hasVideo(int id)
    {
        int count = 0;
        String sql = "SELECT count(*) FROM actress " +
                "JOIN actress_video ON actress.id = actress_video.actress_id " +
                "JOIN video ON actress_video.video_id = " + id +
                "LIMIT 1";
        try {
            ResultSet rs = this.conn.createStatement().executeQuery(sql);
            if (rs.next()) {
                count = rs.getInt("count(*)");
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count > 0;
    }

    public void addVideo(Video video)
    {
        String sql = "insert into actress_video (actress_id, video_id) " +
                "values (" +
                this.getId() +
                ", " + video.getId() +
                ")";
        try {

            this.conn.createStatement().execute(sql);
        } catch (Exception e) {
            System.out.println("Error in SQL: " + e.getMessage());
            System.out.println("SQL: " + sql);
            e.printStackTrace();
        }
    }

    public ArrayList<Actress> getFavoriteActresses()
    {
        ArrayList<Actress> actresses = new ArrayList<Actress>();
        String sql = "SELECT t.* FROM " + this.table + " as t " +
                "JOIN user_favorite_actress as favorite ON t.id = favorite.actress_id " +
                "GROUP BY t.id";
        try {
            ResultSet rs = this.conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                Actress actress = new Actress();

                actress.setId(rs.getInt("id"));
                actress.setName(rs.getString("name"));
                actress.setAvatar(rs.getString("avatar"));
                actress.setHomePage(rs.getString("home_page"));
                actress.setVideoCount(rs.getInt("video_count"));
                actress.setOfficeId(rs.getInt("office_id"));
                actress.setLastUpdated(rs.getString("last_updated"));

                actresses.add(actress);
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Error in SQL: " + e.getMessage());
            System.out.println("SQL: " + sql);
            e.printStackTrace();
        }
        return actresses;
    }

    public void setId(int id) {
        this.data.put("id", id);
    }

    public String getName() {
        return (String) this.data.get("name");
    }

    public void setName(String name) {
        this.data.put("name", name);
    }

    public String getAvatar() {
        return (String) this.data.get("avatar");
    }

    public void setAvatar(String avatar) {
        this.data.put("avatar", avatar);
    }

    public String getHomePage() {
        return (String) this.data.get("home_page");
    }

    public void setHomePage(String homePage) {
        this.data.put("home_page", homePage);
    }

    public int getVideoCount() {
        return (Integer) this.data.get("video_count");
    }

    public void setVideoCount(int videoCount) {
        this.data.put("video_count", videoCount);
    }

    public int getOfficeId() {
        return (Integer) this.data.get("office_id");
    }

    public void setOfficeId(int officeId) {
        this.data.put("office_id", officeId);
    }

    public String getLastUpdated() {
        return (String) this.data.get("last_updated");
    }

    public void setLastUpdated(String lastUpdated) {
        this.data.put("last_updated", lastUpdated);
    }
}
