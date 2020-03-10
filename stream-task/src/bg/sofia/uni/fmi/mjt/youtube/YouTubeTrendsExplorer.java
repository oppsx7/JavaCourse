package bg.sofia.uni.fmi.mjt.youtube;

import bg.sofia.uni.fmi.mjt.youtube.model.TrendingVideo;

import java.io.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


public class YouTubeTrendsExplorer {


    private List<TrendingVideo> trendingVideos = null;

    public YouTubeTrendsExplorer(InputStream dataInput) {
        Scanner s = new Scanner(dataInput).useDelimiter("\\t");
        String result = s.hasNext() ? s.next() : "";
        try (BufferedReader reader = new BufferedReader(new FileReader(result))) {
            trendingVideos = reader.lines().map(TrendingVideo::createVideo).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public Collection<TrendingVideo> getTrendingVideos() {
        return trendingVideos;
    }

    public String findIdOfLeastLikedVideo() {
        return trendingVideos.stream()
                .min(Comparator.comparing(TrendingVideo::getLikes))
                .get()
                .getId();
    }

    public String findIdOfMostLikedLeastDislikedVideo() {
        return trendingVideos.stream()
                .min(Comparator.comparing(x -> x.getLikes() - x.getDislikes()))
                .get()
                .getId();
    }

    public List<String> findDistinctTitlesOfTop3VideosByViews() {
        return trendingVideos.stream()
                .sorted(Comparator.comparing(TrendingVideo::getViews).reversed())
                .map(TrendingVideo::getTitle)
                .collect(Collectors.toList());
    }

    public String findIdOfMostTaggedVideo() {
        return trendingVideos.stream()
                .filter(video -> video.getViews() <= 100000)
                .max(Comparator.comparing(x -> x.getTags().size()))
                .get()
                .getId();

    }
//id-to se povtarq
    //kolko puti se sreshta vuv faila
    public String findTitleOfFirstVideoTrendingBefore100KViews() {

    }

}
