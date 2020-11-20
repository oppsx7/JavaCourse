package bg.sofia.uni.fmi.mjt.socialmedia.comaprators;

import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;

import java.time.LocalDateTime;
import java.util.Comparator;

public class RecentnessComparator implements Comparator<Content> {
    @Override
    public int compare(Content content1, Content content2) {
        return Integer.compare(content2.secondsFromNow(LocalDateTime.now()),
                content1.secondsFromNow(LocalDateTime.now()));
    }
}
