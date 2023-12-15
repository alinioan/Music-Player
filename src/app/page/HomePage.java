package app.page;

import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
public final class HomePage implements Visitable {
    private ArrayList<String> playlists;
    private ArrayList<String> songs;
    private static final int LIMIT = 5;

    /**
     * Constructor instantiates with the top 5 songs by likes and the top 5 playlist by likes
     *
     * @param likedSongs the songs
     * @param followedPlaylist the playlist
     */
    public HomePage(final ArrayList<Song> likedSongs, final ArrayList<Playlist> followedPlaylist) {
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

    @Override
    public String accept(final Visitor visitor) {
        return visitor.visit(this);
    }
}
