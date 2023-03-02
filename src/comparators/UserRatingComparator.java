package comparators;

import user.User;

import java.util.Comparator;

public final class UserRatingComparator implements Comparator<User> {
    @Override
    public int compare(final User o1, final User o2) {
        int res = Integer.compare(o1.getRatingCount(), o2.getRatingCount());
        if (res == 0) {
            return o1.getUsername().compareTo(o2.getUsername());
        }
        return res;
    }
}
