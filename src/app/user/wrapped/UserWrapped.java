package app.user.wrapped;

import app.audio.Files.AudioFile;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.user.Monetization;
import app.user.User;
import app.utils.Enums;
import app.utils.StringPair;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class UserWrapped extends Wrapped {
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
    @JsonIgnore
    private Map<StringPair, Integer> topSongsWithArtistFree;
    @JsonIgnore
    private Map<String, Monetization> adMonetization;
    @JsonIgnore
    private Map<StringPair, Double> freeSongRevenue;
    private Map<String, Integer> topEpisodes;
    @JsonIgnore
    private Map<StringPair, Integer> topEpisodesWithHost;

    public UserWrapped() {
        topArtists = new HashMap<>();
        topGenres = new HashMap<>();
        topSongs = new HashMap<>();
        topAlbums = new HashMap<>();
        topAlbumsWithArtist = new HashMap<>();
        topSongsWithArtist = new HashMap<>();
        topSongsWithArtistPremium = new HashMap<>();
        topEpisodes = new HashMap<>();
        topSongsWithArtistFree = new HashMap<>();
        freeSongRevenue = new HashMap<>();
        adMonetization = new HashMap<>();
        topEpisodesWithHost = new HashMap<>();
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
        this.topEpisodesWithHost = wrapped.getTopEpisodesWithHost();
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

    public void updateStats(AudioFile file, Enums.PlayerSourceType type, boolean premium, String hostName) {
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
                } else {
                    updateMap(topSongsWithArtistFree, pairSong);
                }
            }
            case PODCAST -> {
                Episode episode = (Episode) file;
                updateMap(topEpisodes, episode.getName());
                StringPair pairEpisode = new StringPair(episode.getName(), hostName);
                updateMap(topEpisodesWithHost, pairEpisode);
            }
            default -> {

            }
        }
    }

    public void songOverwritten(AudioFile file, Enums.PlayerSourceType type, boolean premium) {
        switch (type) {
            case LIBRARY, PLAYLIST -> {
                Song song = (Song) file;
                StringPair pairSong = new StringPair(song.getName(), song.getArtist());

                if (!premium && topSongsWithArtistFree.containsKey(pairSong)) {
                    topSongsWithArtistFree.put(pairSong, topSongsWithArtistFree.get(pairSong) - 1);
//                    updateMap(topSongsWithArtistFree, pairSong);
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

    public void calculateAdRevenue(int price) {
        List<String> checkedArtists = new ArrayList<>();

        for (Map.Entry<StringPair, Integer> entry1 : topSongsWithArtistFree.entrySet()) {
            double artistListens = 0.0;
            double totalSongs = 0.0;
            for (Map.Entry<StringPair, Integer> entry2 : topSongsWithArtistFree.entrySet()) {
                totalSongs += entry2.getValue();
            }

            for (Map.Entry<StringPair, Integer> entry2 : topSongsWithArtistFree.entrySet()) {
                if (!checkedArtists.contains(entry2.getKey().getS2()) && entry2.getKey().getS2().equals(entry1.getKey().getS2())) {
                    artistListens += entry2.getValue();
                    Double revenue =  entry2.getValue() * price / totalSongs;
                    if (freeSongRevenue.containsKey(entry2.getKey())) {
                        freeSongRevenue.put(entry2.getKey(), revenue + freeSongRevenue.get(entry2.getKey()));
                    } else {
                        freeSongRevenue.put(entry2.getKey(), revenue);
                    }
                }
            }
            checkedArtists.add(entry1.getKey().getS2());
            addNewMonetization(artistListens, totalSongs, entry1.getKey().getS2(), price);
        }
//        System.out.println(price + " " + adMonetization);
        topSongsWithArtistFree.clear();
    }

    private void addNewMonetization(double artistListens, double totalSongs, String artist, int price) {
        double songRevenue = artistListens * price / totalSongs;
        if (adMonetization.containsKey(artist)) {
            Monetization newMonetization = adMonetization.get(artist);
            newMonetization.setSongRevenue(newMonetization.getSongRevenue() + songRevenue);
            adMonetization.put(artist, newMonetization);
        } else {
            adMonetization.put(artist, new Monetization(0.0, songRevenue));
        }
    }

}
