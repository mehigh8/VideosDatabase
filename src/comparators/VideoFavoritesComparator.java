package comparators;

import entertainment.Video;

import java.util.Comparator;

public final class VideoFavoritesComparator implements Comparator<Video> {
    @Override
    public int compare(final Video o1, final Video o2) {
        int res = Integer.compare(o1.getTimesInFavorites(), o2.getTimesInFavorites());
        if (res == 0) {
            return o1.getTitle().compareTo(o2.getTitle());
        }
        return res;
    }
}
