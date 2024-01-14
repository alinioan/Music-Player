package app.user.wrapped;

import app.user.host.Host;
import app.utils.StringPair;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    /**
     * Sort and return wrapped stats.
     *
     * @return the sorted wrapped.
     */
    @Override
    @JsonIgnore
    public Wrapped getSortedWrapped() {
        HostWrapped sortedWrapped = new HostWrapped();
        sortedWrapped.setListeners(this.getListeners());
        sortedWrapped.setTopEpisodes(sortAndRetrieveTop5(this.getTopEpisodes()));
        return sortedWrapped;
    }

    /**
     * Update the stats for the host wrapped.
     *
     * @param userWrapped the user wrapped where the listens are taken from.
     * @param host the host.
     * @param username the username of the listener.
     */
    public void updateStats(final UserWrapped userWrapped, final Host host, final String username) {
        for (Map.Entry<StringPair, Integer> entry
                : userWrapped.getTopEpisodesWithHost().entrySet()) {
            if (entry.getKey().getS2().equals(host.getUsername())) {
                if (!listenersSet.contains(username)) {
                    listeners++;
                    listenersSet.add(username);
                }
                if (topEpisodes.containsKey(entry.getKey().getS1())) {
                    topEpisodes.put(entry.getKey().getS1(),
                                    entry.getValue() + topEpisodes.get(entry.getKey().getS1()));
                } else {
                    topEpisodes.put(entry.getKey().getS1(), entry.getValue());
                }
            }
        }

    }
}
