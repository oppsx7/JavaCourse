package bg.sofia.uni.fmi.mjt.socialmedia.comaprators;

import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;

import java.util.Comparator;

public class PopularityComparator implements Comparator<Content> {
    @Override
    public int compare(Content content1, Content content2) {
        return Integer.compare(content2.getPopularityPoints(), content1.getPopularityPoints());
    }
}
