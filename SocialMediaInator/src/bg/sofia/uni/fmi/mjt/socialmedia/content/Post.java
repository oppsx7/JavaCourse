package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collection;

public class Post implements Content {

    private String id;
    private String description;
    private LocalDateTime dateTime;
    private Boolean isExpired;
    private List<String> likesUserNames;
    private Map<String, String> comments;
    private List<String> mentionedUsers;
    private List<String> tags;

    public Post(String id, String description, LocalDateTime publishedOn) {
        this.id = id;
        this.description = description;
        this.dateTime = publishedOn;
        likesUserNames = new ArrayList<>();
        comments = new HashMap<>();
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getPostDate() {
        return dateTime;
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    @Override
    public void addLike(String username) {
        likesUserNames.add(username);
    }

    @Override
    public void addComment(String username, String text) {
        comments.put(username, text);
    }

    @Override
    public int getPopularityPoints() {
        return getNumberOfComments() + getNumberOfLikes();
    }

    @Override
    public int secondsFromNow(LocalDateTime timeNow) {
        return (int) this.dateTime.until(timeNow, ChronoUnit.SECONDS);
    }

    @Override
    public void setExpirationStatus(Boolean status) {
        isExpired = status;
    }

    @Override
    public Boolean getExpirationStatus() {
        return isExpired;
    }

    @Override
    public int getNumberOfLikes() {
        return likesUserNames.size();
    }

    @Override
    public int getNumberOfComments() {
        return comments.size();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Collection<String> getTags() {
        return tags;
    }

    @Override
    public Collection<String> getMentions() {
        return mentionedUsers;
    }
}
