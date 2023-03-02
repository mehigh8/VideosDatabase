package comparators;

import entertainment.Video;

import java.util.Comparator;

public final class VideoViewedComparator implements Comparator<Video> {
    @Override
    public int compare(final Video o1, final Video o2) {
        int res = Integer.compare(o1.getTimesInViewed(), o2.getTimesInViewed());
        if (res == 0) {
            return o1.getTitle().compareTo(o2.getTitle());
        }
        return res;
    }
}
