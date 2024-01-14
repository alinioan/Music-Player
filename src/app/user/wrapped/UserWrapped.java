package app.user.wrapped;

import app.audio.Files.AudioFile;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.user.Monetization;
import app.utils.Enums;
import app.utils.StringPair;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static final double VALUE = 1000000.0;
    @JsonIgnore
    private Map<StringPair, Double> premiumSongRevenue;
    @JsonIgnore
    private Map<String, Monetization> monetization;
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
        premiumSongRevenue = new HashMap<>();
        monetization = new HashMap<>();
        topEpisodesWithHost = new HashMap<>();
    }

    /**
     * Copy constructor.
     *
     * @param wrapped the instance.
     */
    public UserWrapped(final UserWrapped wrapped) {
        this.topArtists = wrapped.getTopArtists();
        this.topEpisodes = wrapped.getTopEpisodes();
        this.topGenres = wrapped.getTopGenres();
        this.topAlbums = wrapped.getTopAlbums();
        this.topAlbumsWithArtist = wrapped.getTopAlbumsWithArtist();
        this.topSongsWithArtist = wrapped.getTopSongsWithArtist();
        topSongsWithArtistPremium = wrapped.getTopSongsWithArtistPremium();
        this.topSongs = wrapped.getTopSongs();
        this.topEpisodesWithHost = wrapped.getTopEpisodesWithHost();
    }

    /**
     * Sort and return wrapped stats.
     *
     * @return the sorted wrapped.
     */
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

    /**
     * Update wrapped stats.
     *
     * @param file the audio file.
     * @param type the type of the file.
     * @param premium if the user is premium.
     * @param hostName name of the host he is listening to.
     */
    public void updateStats(final AudioFile file, final Enums.PlayerSourceType type,
                            final boolean premium, final String hostName) {
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

    private <K> void updateMap(final Map<K, Integer> map, final K key) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + 1);
        } else {
            map.put(key, 1);
        }
    }

    /**
     * Calculate premium revenue.
     */
    public void calculatePremiumRevenue() {
        List<String> checkedArtists = new ArrayList<>();

        for (Map.Entry<StringPair, Integer> entry1 : topSongsWithArtistPremium.entrySet()) {
            double artistListens = 0.0;
            double totalSongs = 0.0;
            for (Map.Entry<StringPair, Integer> entry2 : topSongsWithArtistPremium.entrySet()) {
                totalSongs += entry2.getValue();
            }

            for (Map.Entry<StringPair, Integer> entry2 : topSongsWithArtistPremium.entrySet()) {
                if (!checkedArtists.contains(entry2.getKey().getS2())
                        && entry2.getKey().getS2().equals(entry1.getKey().getS2())) {
                    artistListens += entry2.getValue();
                    Double revenue =  entry2.getValue() * VALUE / totalSongs;
                    if (premiumSongRevenue.containsKey(entry2.getKey())) {
                        premiumSongRevenue.put(entry2.getKey(),
                                revenue + premiumSongRevenue.get(entry2.getKey()));
                    } else {
                        premiumSongRevenue.put(entry2.getKey(), revenue);
                    }
                }
            }
            checkedArtists.add(entry1.getKey().getS2());
            addNewMonetization(artistListens, totalSongs, entry1.getKey().getS2(), (int) VALUE);
        }
        topSongsWithArtistPremium.clear();
    }

    /**
     * Calculate ad revenue.
     *
     * @param price the price of the ad.
     */
    public void calculateAdRevenue(final int price) {
        List<String> checkedArtists = new ArrayList<>();

        for (Map.Entry<StringPair, Integer> entry1 : topSongsWithArtistFree.entrySet()) {
            double artistListens = 0.0;
            double totalSongs = 0.0;
            for (Map.Entry<StringPair, Integer> entry2 : topSongsWithArtistFree.entrySet()) {
                totalSongs += entry2.getValue();
            }

            for (Map.Entry<StringPair, Integer> entry2 : topSongsWithArtistFree.entrySet()) {
                if (!checkedArtists.contains(entry2.getKey().getS2())
                        && entry2.getKey().getS2().equals(entry1.getKey().getS2())) {
                    artistListens += entry2.getValue();
                    Double revenue =  entry2.getValue() * price / totalSongs;
                    if (freeSongRevenue.containsKey(entry2.getKey())) {
                        freeSongRevenue.put(entry2.getKey(),
                                revenue + freeSongRevenue.get(entry2.getKey()));
                    } else {
                        freeSongRevenue.put(entry2.getKey(), revenue);
                    }
                }
            }
            checkedArtists.add(entry1.getKey().getS2());
            addNewMonetization(artistListens, totalSongs, entry1.getKey().getS2(), price);
        }
        topSongsWithArtistFree.clear();
    }

    /**
     * Add new monetization to users stats.
     *
     * @param artistListens listens for that artist.
     * @param totalSongs total songs listened to.
     * @param artist the artist.
     * @param price the price of the monetization (ad price or subscription price).
     */
    private void addNewMonetization(final double artistListens, final double totalSongs,
                                    final String artist, final int price) {
        double songRevenue = artistListens * price / totalSongs;
        if (monetization.containsKey(artist)) {
            Monetization newMonetization = monetization.get(artist);
            newMonetization.setSongRevenue(newMonetization.getSongRevenue() + songRevenue);
             monetization.put(artist, newMonetization);
        } else {
            monetization.put(artist, new Monetization(0.0, songRevenue));
        }
    }

}
