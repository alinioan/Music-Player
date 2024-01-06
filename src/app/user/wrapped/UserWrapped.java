package app.user.wrapped;

import app.audio.Files.AudioFile;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.utils.Enums;
import app.utils.StringPair;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class UserWrapped implements Wrapped {
    private Map<String, Integer> topArtists;
    private Map<String, Integer> topGenres;
    private Map<String, Integer> topSongs;
    private Map<String, Integer> topAlbums;
    @JsonIgnore
    private Map<StringPair, Integer> topAlbumsWithArtist;
    @JsonIgnore
    private Map<StringPair, Integer> topSongsWithArtist;
    @JsonIgnore
    private Map<StringPair, Integer> topSongsWithArtistPremium;
    private Map<String, Integer> topEpisodes;

    public UserWrapped() {
        topArtists = new HashMap<>();
        topGenres = new HashMap<>();
        topSongs = new HashMap<>();
        topAlbums = new HashMap<>();
        topAlbumsWithArtist = new HashMap<>();
        topSongsWithArtist = new HashMap<>();
        topSongsWithArtistPremium = new HashMap<>();
        topEpisodes = new HashMap<>();
    }

    public UserWrapped(UserWrapped wrapped) {
        this.topArtists = wrapped.getTopArtists();
        this.topEpisodes = wrapped.getTopEpisodes();
        this.topGenres = wrapped.getTopGenres();
        this.topAlbums= wrapped.getTopAlbums();
        this.topAlbumsWithArtist = wrapped.getTopAlbumsWithArtist();
        this.topSongsWithArtist = wrapped.getTopSongsWithArtist();
        topSongsWithArtistPremium = wrapped.getTopSongsWithArtistPremium();
        this.topSongs = wrapped.getTopSongs();
    }

    @JsonIgnore
    public Wrapped getSortedWrapped() {
        UserWrapped sortedWrapped = new UserWrapped();
        sortedWrapped.setTopAlbums(sortAndRetrieveTop5(this.getTopAlbums()));
        sortedWrapped.setTopArtists(sortAndRetrieveTop5(this.getTopArtists()));
        sortedWrapped.setTopEpisodes(sortAndRetrieveTop5(this.getTopEpisodes()));
        sortedWrapped.setTopSongs(sortAndRetrieveTop5(this.getTopSongs()));
        sortedWrapped.setTopGenres(sortAndRetrieveTop5(this.getTopGenres()));
        return sortedWrapped;
    }

    private Map<String, Integer> sortAndRetrieveTop5(Map<String, Integer> unsortedMap) {
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

    public void updateStats(AudioFile file, Enums.PlayerSourceType type, boolean premium) {
        switch (type) {
            case LIBRARY, PLAYLIST -> {
                Song song = (Song) file;
                updateMap(topSongs, song.getName());
                updateMap(topGenres, song.getGenre());
                updateMap(topArtists, song.getArtist());
                updateMap(topAlbums, song.getAlbum());

                StringPair pairAlbum = new StringPair(song.getAlbum(), song.getArtist());
                updateMap(topAlbumsWithArtist, pairAlbum);

                StringPair pairSong = new StringPair(song.getName(), song.getArtist());
                updateMap(topSongsWithArtist, pairSong);
                if (premium) {
                    updateMap(topSongsWithArtistPremium, pairSong);
                }

            }
            case PODCAST -> {
                Episode episode = (Episode) file;
                if (topEpisodes.containsKey(episode.getName())) {
                    topEpisodes.put(episode.getName(), topEpisodes.get(episode.getName()) + 1);
                } else {
                    topEpisodes.put(episode.getName(), 1);
                }
            }
            default -> {

            }
        }
    }

    private <K> void updateMap(Map<K, Integer> map, K key) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + 1);
        } else {
            map.put(key, 1);
        }
    }

    @Override
    public String toString() {
        return "UserWrapped{" +
//                "topArtists=" + topArtists +
//                ", topGenres=" + topGenres +
//                ", topSongs=" + topSongs +
//                ", topAlbums=" + topAlbums +
//                ", topAlbumsWithArtist=" + topAlbumsWithArtist +
//                ", topSongsWithArtist=" + topSongsWithArtist +
                ", topSongsWithArtistPremium=" + topSongsWithArtistPremium +
//                ", topEpisodes=" + topEpisodes +
                '}';
    }
}
