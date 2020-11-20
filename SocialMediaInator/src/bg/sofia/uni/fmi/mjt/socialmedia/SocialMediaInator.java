package bg.sofia.uni.fmi.mjt.socialmedia;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.NoUsersException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameNotFoundException;

public interface SocialMediaInator {

    /**
     * Registers a new user in the platform.
     *
     * @param username
     * @throws IllegalArgumentException       If {@code username} is null
     * @throws UsernameAlreadyExistsException If there is already a user with {@code username}
     *                                        registered in the platform
     */
    void register(String username) throws UsernameAlreadyExistsException;

    /**
     * Publishes a post with {@code description}.
     * -> A post expires in 30 days after it was published
     * -> The description of the post may contain arbitrary number of mentions (i.e @someuser) and hash-tags
     * (i.e #programming)
     * -> If a non-existing user is mentioned in the description, the actual mention does not have any effect
     * -> The tags and mentions are always separated with at least one space from the other words in the description
     * -> The id of each post is generated as follows: [username]-[auto-incremented integer starting from 0]
     *
     * @param username
     * @param publishedOn
     * @param description
     * @return The id of the newly created post
     * @throws IllegalArgumentException  If any of the parameters is null
     * @throws UsernameNotFoundException If a user with {@code username} does not exist in the platform
     */
    String publishPost(String username, LocalDateTime publishedOn, String description);

    /**
     * Publishes a story with {@code description}.
     * -> A story expires in 24 hours after it was published
     * -> The description of the story may contain arbitrary number of mentions (i.e @someuser) and tags
     * (i.e #programming)
     * -> If a non-existing user is mentioned in the description, the actual mention does not have any effect
     * -> The tags and mentions are always separated with at least one space from the other words in the description
     * -> The id of each story is generated as follows: [username]-[auto-incremented integer starting from 0]
     *
     * @param username
     * @param publishedOn
     * @param description
     * @return The id of the newly created story
     * @throws IllegalArgumentException  If any of the parameters is null
     * @throws UsernameNotFoundException If a user with {@code username} does not exist in the platform
     */
    String publishStory(String username, LocalDateTime publishedOn, String description);

    /**
     * Likes a content with id {@code id}.
     *
     * @param username The name of the user who liked the content
     * @param id       The id of the content
     * @throws IllegalArgumentException  If any of the parameters is null
     * @throws UsernameNotFoundException If a user with {@code username} does not exist in the platform
     * @throws ContentNotFoundException  If there is no content with id {@code id} in the platform
     */
    void like(String username, String id);

    /**
     * Comments on a content with id {@code id}.
     *
     * @param username The name of the user who commented the content
     * @param text     The actual comment
     * @param id       The id of the content
     * @throws IllegalArgumentException  If any of the parameters is null
     * @throws UsernameNotFoundException If a user with {@code username} does not exist in the platform
     * @throws ContentNotFoundException  If there is no content with id {@code id} in the platform
     */
    void comment(String username, String text, String id);

    /**
     * Returns the {@code n} most popular content on the platform.
     * -> The popularity of a content is calculated by the total number of likes and comments
     * -> If there is no content in the platform, an empty Collection should be returned
     * -> If the total number of posts and stories is less than {@code n} return as many as available
     * -> The returned Collection should not contain expired content
     *
     * @param n The number of content to be returned
     * @return Unmodifiable collection of Content sorted by popularity in descending order
     * @throws IllegalArgumentException If {@code n} is a negative number
     */
    Collection<Content> getNMostPopularContent(int n);

    /**
     * Returns the {@code n} most recent content of user {@code username}.
     * -> If the given user does not have any content, an empty Collection should be returned.
     * -> If the total number of posts and stories is less than {@code n} return as many as available
     * -> The returned Collection should not contain expired content
     *
     * @param username
     * @param n        The number of content to be returned
     * @return Unmodifiable collection of Content sorted by popularity
     * @throws IllegalArgumentException  If {@code username} is null or {@code n} is a negative number
     * @throws UsernameNotFoundException if a user with {@code username} does not exist in the platform
     */
    Collection<Content> getNMostRecentContent(String username, int n);

    /**
     * Returns the username of the most popular user.
     * -> This is the user which was mentioned most times in stories and posts
     *
     * @throws NoUsersException if there are currently no users in the platform
     */
    String getMostPopularUser();

    /**
     * Returns all posts and stories containing the tag {@code tag} in their description.
     * -> If there are no posts or stories with the given tag in the platform, an empty Collection should be returned
     * -> Note that {@code tag} should start with '#'
     * -> The returned Collection should not contain expired content
     *
     * @param tag
     * @return Unmodifiable collection of Content
     * @throws IllegalArgumentException If {@code tag} is null
     */
    Collection<Content> findContentByTag(String tag);

    /**
     * Returns the activity log of user {@code username}. It contains a history of all activities of a given user.
     * -> The activity log is maintained in reversed chronological order (i.e newest events first).
     * -> It has the following format:
     * HH:mm:ss dd.MM.yy: Commented "[text]" on a content with id [id]
     * HH:mm:ss dd.MM.yy: Liked a content with id [id]
     * HH:mm:ss dd.MM.yy: Created a post with id [id]
     * HH:mm:ss dd.MM.yy: Created a story with id [id]
     * -> HH:mm:ss dd.MM.yy is a time format
     * -> If the given user does not have any activity on the platform, an empty List should be returned
     *
     * @param username
     * @return List of activities in the above format
     * @throws IllegalArgumentException  If {@code username} is null
     * @throws UsernameNotFoundException if a user with {@code username} does not exist in the platform
     */
    List<String> getActivityLog(String username);

}