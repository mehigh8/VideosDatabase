package comparators;

import actor.Actor;

import java.util.Comparator;

public final class ActorAwardsComparator implements Comparator<Actor> {
    @Override
    public int compare(final Actor o1, final Actor o2) {
        int res = Integer.compare(o1.getNumberOfAwards(),
                o2.getNumberOfAwards());
        if (res == 0) {
            return o1.getName().compareTo(o2.getName());
        }
        return res;
    }
}
