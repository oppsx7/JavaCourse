package bg.sofia.uni.fmi.mjt.socialmedia;

import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String username;
    private int mentions;
    private List<Content> contents;
    private List<String> activityLog;

    public User(String username, List<Content> contents) {
        this.username = username;
        this.mentions = 0;
        this.contents = contents;
        this.activityLog = new ArrayList<>();
    }

    public void addContent(Content content) {
        contents.add(content);
    }

    public int getMentions() {
        return mentions;
    }

    public String getUsername() {
        return username;
    }

    public void increaseMentions() {
        this.mentions++;
    }

    public List<Content> getContents() {
        return contents;
    }

    public List<String> getActivityLog() {
        return activityLog;
    }

    public void addActivityInLog(String activity) {
        this.activityLog.add(activity);
    }
}
