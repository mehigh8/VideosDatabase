package comparators;

import entertainment.Movie;
import entertainment.Series;
import entertainment.Video;

import java.util.Comparator;

public final class VideoRatingComparator implements Comparator<Video> {
    @Override
    public int compare(final Video o1, final Video o2) {
        Double r1 = (o1 instanceof Movie
                ? ((Movie) o1).averageRating() : ((Series) o1).averageRating());
        Double r2 = (o2 instanceof Movie
                ? ((Movie) o2).averageRating() : ((Series) o2).averageRating());
        int res = Double.compare(r1, r2);
        if (res == 0) {
            return o1.getTitle().compareTo(o2.getTitle());
        }
        return res;
    }
}
