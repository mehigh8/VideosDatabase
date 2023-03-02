package comparators;

import entertainment.Video;

import java.util.Comparator;

public final class VideoRatingOnlyComparator implements Comparator<Video> {
    @Override
    public int compare(final Video o1, final Video o2) {
        return o2.getRating().compareTo(o1.getRating());
    }
}
