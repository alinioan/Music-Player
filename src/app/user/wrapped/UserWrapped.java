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
    private Map<String, Integer> topEpisodes;

    public UserWrapped() {
        topArtists = new HashMap<>();
        topGenres = new HashMap<>();
        topSongs = new HashMap<>();
        topAlbums = new HashMap<>();
        topAlbumsWithArtist = new HashMap<>();
        topSongsWithArtist = new HashMap<>();
        topEpisodes = new HashMap<>();
    }

    public UserWrapped(UserWrapped wrapped) {
        this.topArtists = wrapped.getTopArtists();
        this.topEpisodes = wrapped.getTopEpisodes();
        this.topGenres = wrapped.getTopGenres();
        this.topAlbums= wrapped.getTopAlbums();
        this.topAlbumsWithArtist = wrapped.getTopAlbumsWithArtist();
        this.topSongsWithArtist = wrapped.getTopSongsWithArtist();
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

    public void updateStats(AudioFile file, Enums.PlayerSourceType type) {
        switch (type) {
            case LIBRARY, PLAYLIST -> {
                Song song = (Song) file;
                if (topSongs.containsKey(song.getName())) {
                    topSongs.put(song.getName(), topSongs.get(song.getName()) + 1);
                } else {
                    topSongs.put(song.getName(), 1);
                }

                if (topGenres.containsKey(song.getGenre())) {
                    topGenres.put(song.getGenre(), topGenres.get(song.getGenre()) + 1);
                } else {
                    topGenres.put(song.getGenre(), 1);
                }

                if (topArtists.containsKey(song.getArtist())) {
                    topArtists.put(song.getArtist(), topArtists.get(song.getArtist()) + 1);
                } else {
                    topArtists.put(song.getArtist(), 1);
                }

                if (topAlbums.containsKey(song.getAlbum())) {
                    topAlbums.put(song.getAlbum(), topAlbums.get(song.getAlbum()) + 1);
                } else {
                    topAlbums.put(song.getAlbum(), 1);
                }

                StringPair pairAlbum = new StringPair(song.getAlbum(), song.getArtist());
                if (topAlbumsWithArtist.containsKey(pairAlbum)) {
                    topAlbumsWithArtist.put(pairAlbum, topAlbumsWithArtist.get(pairAlbum) + 1);
                } else {
                    topAlbumsWithArtist.put(pairAlbum, 1);
                }

                StringPair pairSong = new StringPair(song.getName(), song.getArtist());
                if (topSongsWithArtist.containsKey(pairSong)) {
                    topSongsWithArtist.put(pairSong, topSongsWithArtist.get(pairSong) + 1);
                } else {
                    topSongsWithArtist.put(pairSong, 1);
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

    @Override
    public String toString() {
        return "Wrapped{" +
                "topArtis=" + topArtists +
                ", topGenres=" + topGenres +
                ", topSongs=" + topSongs +
                ", topAlbums=" + topAlbums +
                ", topEpisodes=" + topEpisodes +
                '}';
    }
}
