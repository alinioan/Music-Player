package app.user.wrapped;

import app.audio.Files.AudioFile;
import app.utils.Enums;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Wrapped {
    public abstract Wrapped getSortedWrapped();
    public Map<String, Integer> sortAndRetrieveTop5(Map<String, Integer> unsortedMap) {
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(unsortedMap.entrySet());

        entryList.sort((entry1, entry2) -> {
            int valueComparison = entry2.getValue().compareTo(entry1.getValue());
            return (valueComparison == 0) ? entry1.getKey().compareTo(entry2.getKey()) : valueComparison;
        });

        Map<String, Integer> sortedMap = new LinkedHashMap<>();

        int count = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
            count++;
            if (count == 5) {
                break;
            }
        }

        return sortedMap;
    }
}
