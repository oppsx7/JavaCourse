package bg.sofia.uni.fmi.mjt.socialmedia;

import bg.sofia.uni.fmi.mjt.socialmedia.comaprators.MentionsComparator;
import bg.sofia.uni.fmi.mjt.socialmedia.comaprators.PopularityComparator;
import bg.sofia.uni.fmi.mjt.socialmedia.comaprators.RecentnessComparator;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Post;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Story;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.NoUsersException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Collection;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

public class EvilSocialInator implements SocialMediaInator {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yy");
    private static int postsCount = 0;
    private Map<String, User> users = new HashMap<>();

    public List<Content> filterByPopularity() {
        List<Content> upToDateContent = getUpToDateContent();
        Collections.sort(upToDateContent, new PopularityComparator());

        return upToDateContent;
    }

    public List<Content> filterByRecentness(String username) {

        List<Content> upToDateContent = new ArrayList<>();
        for (Content content : getUpToDateContent()) {
            if (content.getId().contains(username)) {
                upToDateContent.add(content);
            }
        }
        Collections.sort(upToDateContent, new RecentnessComparator());

        return upToDateContent;
    }

    public List<Content> getUpToDateContent() {

        List<Content> upToDateContent = new ArrayList<>();
        for (User user : users.values()) {
            for (Content content : user.getContents()) {
                if (((Post) content).getPostDate().until(LocalDateTime.now(), DAYS) > 30) {
                    content.setExpirationStatus(true);
                    continue;
                }
                if (((Story) content).getStoryDate().until(LocalDateTime.now(), HOURS) > 24) {
                    content.setExpirationStatus(true);
                    continue;
                }
                upToDateContent.add(content);
            }
        }
        return upToDateContent;
    }

    public String publishContent(String username, LocalDateTime publishedOn, String description, Boolean isStory)
            throws IllegalArgumentException, UsernameNotFoundException {
        if (username == null || publishedOn == null || description == null) {
            throw new IllegalArgumentException("One of the arguments has no value");
        }

        if (!users.containsKey(username)) {
            throw new UsernameNotFoundException("User with this name does not exists");
        }

        String id = username + "-" + postsCount++;
        User user = users.get(username);

        for (String name : users.keySet()) {
            if (description.contains("@" + name) && users.get(name) != null) {
                users.get(name).increaseMentions();
            }
        }
        if (isStory) {
            Content story = new Story(id, description, publishedOn);
            addTags(description, id, story, user, "story");
            user.addContent(story);
        } else {
            Content post = new Post(id, description, publishedOn);
            addTags(description, id, post, user, "post");
            user.addContent(post);
        }
        users.put(username, user);

        return id;
    }

    public void addTags(String description, String id, Content content, User user, String contentType) {
        for (String word : description.split(" ")) {
            if (word.length() > 1 && word.contains("#")) {
                content.addTag(word);
                String formattedDateTime = LocalDateTime.now().format(formatter);
                String createdContent = formattedDateTime + ": Created a " + contentType + " with id " + id;
                user.addActivityInLog(createdContent);
            }
        }
    }

    @Override
    public void register(String username) throws IllegalArgumentException, UsernameAlreadyExistsException {

        if (username == null) {
            throw new IllegalArgumentException("Product name should not be empty !");
        }

        if (users.containsKey(username)) {
            throw new UsernameAlreadyExistsException("Username already exists!");
        }

        users.put(username, new User(username, new ArrayList<Content>()));

    }

    @Override
    public String publishPost(String username, LocalDateTime publishedOn, String description)
            throws IllegalArgumentException, UsernameNotFoundException {
        return publishContent(username, publishedOn, description, false);
    }

    @Override
    public String publishStory(String username, LocalDateTime publishedOn, String description)
            throws IllegalArgumentException, UsernameNotFoundException {

        return publishContent(username, publishedOn, description, true);
    }

    @Override
    public void like(String username, String id) throws IllegalArgumentException,
            UsernameNotFoundException, ContentNotFoundException {
        if (username == null || id == null) {
            throw new IllegalArgumentException("Invalid parameters");
        }
        if (!users.containsKey(username)) {
            throw new UsernameNotFoundException("User with this name does not exist");
        }
        for (User user : users.values()) {
            for (Content content : user.getContents()) {
                if (content.getId().equals(id)) {
                    content.addLike(username);
                    String formattedDateTime = LocalDateTime.now().format(formatter);
                    String likeMessageInLog = formattedDateTime + ": Liked a content with id " + id;
                    user.addActivityInLog(likeMessageInLog);
                    return;
                }
            }
        }
        throw new ContentNotFoundException("Content with id " + id + " does not exist");
    }

    @Override
    public void comment(String username, String text, String id) throws IllegalArgumentException,
            UsernameNotFoundException, ContentNotFoundException {
        if (username == null || id == null) {
            throw new IllegalArgumentException("Invalid parameters");
        }
        if (!users.containsKey(username)) {
            throw new UsernameNotFoundException("User with this name does not exist");
        }

        for (User user : users.values()) {
            for (Content content : user.getContents()) {
                if (content.getId().equals(id)) {
                    content.addComment(username, text);
                    String formattedDateTime = LocalDateTime.now().format(formatter);
                    String commentMessageInLog =
                            formattedDateTime + ": Commented " + text + " on a content with id " + id;
                    user.addActivityInLog(commentMessageInLog);
                    return;
                }
            }
        }
        throw new ContentNotFoundException("Content with id " + id + " does not exist");
    }

    @Override
    public Collection<Content> getNMostPopularContent(int n) throws IllegalArgumentException {
        if (n < 0) {
            throw new IllegalArgumentException("Number must be a positive one.");
        }

        List<Content> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (filterByPopularity().get(i) != null) {
                list.add(filterByPopularity().get(i));
            } else {
                continue;
            }
        }

        return Collections.unmodifiableList(list);
    }

    @Override
    public Collection<Content> getNMostRecentContent(String username, int n)
            throws IllegalArgumentException, UsernameNotFoundException {
        if (username == null || n < 0) {
            throw new IllegalArgumentException("Number must be a positive one.");
        }
        if (!users.keySet().contains(username)) {
            throw new UsernameNotFoundException("Wrong username.");
        }

        List<Content> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (filterByRecentness(username).get(i) != null) {
                list.add(filterByPopularity().get(i));
            }
        }
        return Collections.unmodifiableList(list);
    }

    @Override
    public String getMostPopularUser() throws NoUsersException {
        if (users.isEmpty()) {
            throw new NoUsersException("No users in the platform.");
        }
        List<User> sortedUsers = (List<User>) users.values();
        Collections.sort(sortedUsers, new MentionsComparator());
        return sortedUsers.get(0).getUsername();
    }

    @Override
    public Collection<Content> findContentByTag(String tag) throws IllegalArgumentException {
        if (tag == null) {
            throw new IllegalArgumentException("Invalid tag.");
        }
        List<Content> contentsByTag = new ArrayList<>();
        for (User user : users.values()) {
            for (Content content : user.getContents()) {
                if (!content.getExpirationStatus() && content.getTags().contains(tag)) {
                    contentsByTag.add(content);
                }
            }
        }
        return Collections.unmodifiableList(contentsByTag);
    }

    @Override
    public List<String> getActivityLog(String username) {
        for (String userName : users.keySet()) {
            if (userName.equals(username)) {
                return users.get(username).getActivityLog();
            }
        }
        return new ArrayList<>();
    }
}
