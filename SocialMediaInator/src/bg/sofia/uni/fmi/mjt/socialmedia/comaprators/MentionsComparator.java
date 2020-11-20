package bg.sofia.uni.fmi.mjt.socialmedia.comaprators;

import bg.sofia.uni.fmi.mjt.socialmedia.User;

import java.util.Comparator;

public class MentionsComparator implements Comparator<User> {
    @Override
    public int compare(User o1, User o2) {
        return Integer.compare(o2.getMentions(), o1.getMentions());
    }
}
