package comparators;

import java.util.Comparator;
import java.util.Map;

public final class PairValueNameComparator implements Comparator<Map.Entry<String, Double>> {
    @Override
    public int compare(final Map.Entry<String, Double> o1, final Map.Entry<String, Double> o2) {
        int res = o1.getValue().compareTo(o2.getValue());
        if (res == 0) {
            return o1.getKey().compareTo(o2.getKey());
        }
        return res;
    }
}
