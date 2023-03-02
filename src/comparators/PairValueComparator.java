package comparators;

import java.util.Comparator;
import java.util.Map;

public final class PairValueComparator implements Comparator<Map.Entry<String, Integer>> {
    @Override
    public int compare(final Map.Entry<String, Integer> o1, final Map.Entry<String, Integer> o2) {
        return Integer.compare(o2.getValue(), o1.getValue());
    }
}
