package comparators;

import actor.Actor;

import java.util.Comparator;

public final class ActorAverageComparator implements Comparator<Actor> {
    @Override
    public int compare(final Actor o1, final Actor o2) {
        int res = o1.getAverageRating().compareTo(o2.getAverageRating());
        if (res == 0) {
            return o1.getName().compareTo(o2.getName());
        }
        return res;
    }
}
