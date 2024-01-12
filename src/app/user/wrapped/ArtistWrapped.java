package app.user.wrapped;

import app.Admin;
import app.audio.Files.AudioFile;
import app.user.artist.Artist;
import app.utils.Enums;
import app.utils.StringPair;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class ArtistWrapped extends Wrapped {
    int listeners;
    private Map<String, Integer> topAlbums;
    @JsonIgnore
    private Map<String, Integer> topFansMap;
    private List<String> topFans;
    private Map<String, Integer> topSongs;
    @JsonIgnore
    private double merchRevenue;

    public ArtistWrapped() {
        listeners = 0;
        this.topFansMap = new HashMap<>();
        this.topAlbums = new HashMap<>();
        this.topSongs = new HashMap<>();
        merchRevenue = 0;
    }

    @Override
    @JsonIgnore
    public Wrapped getSortedWrapped() {
        ArtistWrapped sortedWrapped = new ArtistWrapped();
        sortedWrapped.setTopAlbums(sortAndRetrieveTop5(this.getTopAlbums()));
        sortedWrapped.setListeners(this.getListeners());
        sortedWrapped.setTopFans(new ArrayList<>(sortAndRetrieveTop5(this.getTopFansMap()).keySet()));
        sortedWrapped.setTopSongs(sortAndRetrieveTop5(this.getTopSongs()));
        return sortedWrapped;
    }

    public void updateStats(UserWrapped userWrapped, Artist artist, String username) {
        if (userWrapped.getTopArtists().containsKey(artist.getUsername())) {
            listeners++;
        }
        if (userWrapped.getTopArtists().containsKey(artist.getUsername())) {
            topFansMap.put(username, userWrapped.getTopArtists().get(artist.getUsername()));
        }
        for (Map.Entry<StringPair, Integer> entry : userWrapped.getTopSongsWithArtist().entrySet()) {
            if (entry.getKey().getS2().equals(artist.getUsername())) {
                if (topSongs.containsKey(entry.getKey().getS1())) {
                    topSongs.put(entry.getKey().getS1(), entry.getValue() + topSongs.get(entry.getKey().getS1()));
                } else {
                    topSongs.put(entry.getKey().getS1(), entry.getValue());
                }
            }
        }
        for (Map.Entry<StringPair, Integer> entry : userWrapped.getTopAlbumsWithArtist().entrySet()) {
            if (artist.getUsername().equals(entry.getKey().getS2())) {
                if (topAlbums.containsKey(entry.getKey().getS1())) {
                    topAlbums.put(entry.getKey().getS1(), entry.getValue() + topAlbums.get(entry.getKey().getS1()));
                } else {
                    topAlbums.put(entry.getKey().getS1(), entry.getValue());
                }
            }
        }
    }

    @Override
    public String toString() {
        return "ArtistWrapped{" +
                "listeners=" + listeners +
                ", topAlbums=" + topAlbums +
                ", topFansMap=" + topFansMap +
                ", topFans=" + topFans +
                ", topSongs=" + topSongs +
                '}';
    }
}
