package app.page.content;

import app.Admin;
import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import app.page.Visitable;
import app.page.Visitor;
import app.user.User;
import app.user.artist.Artist;
import app.user.wrapped.ArtistWrapped;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Getter
@Setter
public final class HomePage implements Visitable {
    private ArrayList<String> playlists;
    private ArrayList<String> songs;
    private static final int LIMIT = 5;
    private ArrayList<String> songRecommendations;
    private ArrayList<String> playlistRecommendations;
    private String lastRecommendation;
    private String lastRecommendationType;
    private static final int MINTIME = 30;

    /**
     * Constructor instantiates with the top 5 songs by likes and the top 5 playlist by likes
     *
     * @param likedSongs the songs
     * @param followedPlaylist the playlist
     */
    public HomePage(final ArrayList<Song> likedSongs, final ArrayList<Playlist> followedPlaylist) {
        this.songRecommendations = new ArrayList<>();
        this.playlistRecommendations = new ArrayList<>();
        List<Song> sortedSongs = new ArrayList<>(likedSongs);
        sortedSongs.sort(Comparator.comparingInt(Song::getLikes).reversed());
        this.songs = new ArrayList<>();
        int count = 0;
        for (Song song : sortedSongs) {
            if (count >= LIMIT) {
                break;
            }
            this.songs.add(song.getName());
            count++;
        }
        List<Playlist> sortedPlaylist = new ArrayList<>(followedPlaylist);
        sortedPlaylist.sort(Comparator.comparingInt(HomePage::calculateTotalLikes));
        this.playlists = new ArrayList<>();
        count = 0;
        for (Playlist playlist : sortedPlaylist) {
            if (count >= LIMIT) {
                break;
            }
            this.playlists.add(playlist.getName());
            count++;
        }
    }

    private static int calculateTotalLikes(final Playlist playlist) {
        int totalLikes = 0;
        if (playlist.getSongs() != null) {
            for (Song song : playlist.getSongs()) {
                if (song.getLikes() != null) {
                    totalLikes += song.getLikes();
                }
            }
        }
        return totalLikes;
    }

    /**
     * Recommend song.
     * @param user the user.
     * @return the message.
     */
    public String recommendSong(final User user) {
        int seed = user.getPlayer().getSource().getAudioFile().getDuration()
                - user.getPlayer().getSource().getDuration();
        if (seed < MINTIME) {
            return "No new recommendations were found";
        }
        Random rand = new Random(seed);
        List<Song> sameGenreSongs = new ArrayList<>(Admin.getSongs());
        sameGenreSongs.removeIf(song -> !Objects.equals(song.getGenre(),
                                ((Song) user.getPlayer().getSource().getAudioFile()).getGenre()));
        Song randomSong = sameGenreSongs.get(rand.nextInt(sameGenreSongs.size()));
        songRecommendations.add(randomSong.getName());
        lastRecommendation = randomSong.getName();
        lastRecommendationType = "song";
        return ("The recommendations for user "
                + "%s have been updated successfully.").formatted(user.getUsername());
    }

    /**
     * Recommend playlist.
     *
     * @param user the user.
     * @return the message.
     */
    public String recommendPlaylist(final User user) {
        if (user.getLikedSongs().isEmpty() && user.getPlaylists().isEmpty()
                && user.getFollowedPlaylists().isEmpty()) {
            return "No new recommendations were found";
        }

        playlistRecommendations.add("%s's recommendations".formatted(user.getUsername()));
        return ("The recommendations for user "
                + "%s have been updated successfully.").formatted(user.getUsername());
    }

    /**
     * Recommend fan playlist.
     *
     * @param user the user.
     * @return the message.
     */
    public String recommendFanPlaylist(final User user) {
        Artist artist = (Artist) Admin.getUser(((Song) user.getPlayer()
                            .getSource().getAudioFile()).getArtist());
        ArtistWrapped wrapped = artist.getTemporaryWrapped();
        wrapped.setTopFans(new ArrayList<>(wrapped.sortAndRetrieveTop5(wrapped
                                                    .getTopFansMap()).keySet()));
        if (wrapped.getListeners() == 0) {
            return "No new recommendations were found";
        }
        if (wrapped.getTopFans() != null) {
            boolean playlistCreated = false;
            for (String username : wrapped.getTopFans()) {
                User fan = Admin.getUser(username);
                if (!fan.getLikedSongs().isEmpty() || !fan.getPlaylists().isEmpty()
                        || !fan.getFollowedPlaylists().isEmpty()) {
                    playlistCreated = true;
                }
            }
            if (!playlistCreated) {
                return "No new recommendations were found";
            }
        }
        playlistRecommendations.add("%s Fan Club recommendations".formatted(artist.getUsername()));
        return ("The recommendations for user "
                + "%s have been updated successfully.").formatted(user.getUsername());
    }

    @Override
    public String accept(final Visitor visitor) {
        return visitor.visit(this);
    }
}
