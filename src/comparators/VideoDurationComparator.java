package comparators;

import entertainment.Movie;
import entertainment.Series;
import entertainment.Video;

import java.util.Comparator;

public final class VideoDurationComparator implements Comparator<Video> {
    @Override
    public int compare(final Video o1, final Video o2) {
        int d1 = (o1 instanceof Movie ? ((Movie) o1).getDuration() : ((Series) o1).getDuration());
        int d2 = (o2 instanceof Movie ? ((Movie) o2).getDuration() : ((Series) o2).getDuration());
        int res = Integer.compare(d1, d2);
        if (res == 0) {
            return o1.getTitle().compareTo(o2.getTitle());
        }
        return res;
    }
}
