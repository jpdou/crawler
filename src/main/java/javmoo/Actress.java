package javmoo;

import java.sql.ResultSet;
import java.util.ArrayList;

public class Actress extends Resource {
    private int id;
    private String name;
    private String avatar;
    private String homePage;
    private int videoCount;
    private int officeId;
    private String lastUpdated;
    private boolean subscribed;

    private ArrayList<Integer> videoIds;

    Actress()
    {
        this.table = "actress";
    }

    public void save()
    {
        String sql;
        if (this.isExisted(this.getName(), "name")) {
            sql = "update " + this.table +
                    " set avatar = '" + this.getAvatar() +
                    "', home_page = '" + this.getHomePage() +
                    "', video_count = " + this.getVideoCount() +
                    ", office_id = " + this.getOfficeId() +
                    ", last_updated = '" + this.getLastUpdated() +
                    ", subscribed = " + this.isSubscribed() +
                    " where name = '" + this.getName() + "'";
        } else {
            sql = "insert into " + this.table + " (name, avatar, home_page, video_count, office_id, subscribed) " +
                    "values (" +
                    "'" + this.getAvatar() +
                    "', '" + this.getName() +
                    "', '" + this.getHomePage() +
                    "', " + this.getVideoCount() +
                    ", " + this.getOfficeId() +
                    ", " + (this.isSubscribed() ? 1 : 0) +
                    ")";
        }

        System.out.println("save actress :" + sql);
        try {
            this.stmt.execute(sql);
        } catch (Exception e) {
            System.out.println("Error in SQL: " + e.getMessage());
        }
    }

    public ArrayList<Integer> getVideoIds()
    {
        if (this.videoIds == null) {
            ArrayList<Integer> ids = new ArrayList<Integer>();
            String sql = "SELECT video_id FROM actress_video WHERE actress_id = " + this.getId();
            try {
                ResultSet rs = this.stmt.executeQuery(sql);
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(int videoCount) {
        this.videoCount = videoCount;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }
}
