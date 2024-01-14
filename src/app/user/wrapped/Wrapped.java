package app.user.wrapped;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Wrapped {
    private static final int FIVE = 5;

    /**
     * Get the sorted wrapped.
     *
     * @return the wrapped.
     */
    public abstract Wrapped getSortedWrapped();

    /**
     * Sorts the items in the map by value and only gets the top 5.
     * @param unsortedMap the map to be sorted.
     * @return sorted map with only 5 elements.
     */
    public Map<String, Integer> sortAndRetrieveTop5(final Map<String, Integer> unsortedMap) {
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(unsortedMap.entrySet());

        entryList.sort((entry1, entry2) -> {
            int valueComparison = entry2.getValue().compareTo(entry1.getValue());
            return (valueComparison == 0) ? entry1.getKey().
                                compareTo(entry2.getKey()) : valueComparison;
        });

        Map<String, Integer> sortedMap = new LinkedHashMap<>();

        int count = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
            count++;
            if (count == FIVE) {
                break;
            }
        }

        return sortedMap;
    }
}
