package app.user.wrapped;

import app.user.artist.Artist;
import app.user.host.Host;
import app.utils.StringPair;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class HostWrapped extends Wrapped {
    private int listeners;
    private Map<String, Integer> topEpisodes;
    @JsonIgnore
    private Set<String> listenersSet;

    public HostWrapped() {
        listeners = 0;
        topEpisodes = new HashMap<>();
        listenersSet = new HashSet<>();
    }

    @Override
    @JsonIgnore
    public Wrapped getSortedWrapped() {
        HostWrapped sortedWrapped = new HostWrapped();
        sortedWrapped.setListeners(this.getListeners());
        sortedWrapped.setTopEpisodes(sortAndRetrieveTop5(this.getTopEpisodes()));
        return sortedWrapped;
    }

    public void updateStats(UserWrapped userWrapped, Host host, String username) {
        for (Map.Entry<StringPair, Integer> entry : userWrapped.getTopEpisodesWithHost().entrySet()) {
            if (entry.getKey().getS2().equals(host.getUsername())) {
                if (!listenersSet.contains(username)) {
                    listeners++;
                    listenersSet.add(username);
                }
                if (topEpisodes.containsKey(entry.getKey().getS1())) {
                    topEpisodes.put(entry.getKey().getS1(), entry.getValue() + topEpisodes.get(entry.getKey().getS1()));
                } else {
                    topEpisodes.put(entry.getKey().getS1(), entry.getValue());
                }
            }
        }

    }
}
