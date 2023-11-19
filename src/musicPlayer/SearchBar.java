package musicPlayer;

import commandIO.CommandInput;
import playerFiles.Library;
import playerFiles.Playlist;
import playerFiles.Podcast;
import playerFiles.Song;

import java.util.ArrayList;
import java.util.List;

public class SearchBar {
    private ArrayList<Song> songResults = new ArrayList<>();
    private ArrayList<Podcast> podcastResults = new ArrayList<>();
    private ArrayList<Playlist> playlistResults = new ArrayList<>();

    void processCommand(CommandInput command, Library library) {
        switch (command.getCommand()) {
            case "search":
                search(command.getType(), command.getFilters(), library);
                break;
            case "select":
                break;
            default:
                break;
        }
    }
    void search(String type, Filters filters, Library library) {
        this.songResults.clear();
        this.playlistResults.clear();
        this.podcastResults.clear();
        switch (type) {
            case "song":
                this.songResults.addAll(library.getSongs());
                filterSongByName(filters.getName());
                filterSongsByAlbum(filters.getAlbum());
                filterSongsByArtist(filters.getArtist());
                filterSongsByGenre(filters.getGenre());
                filterSongsByLyrics(filters.getLyrics());
                filterSongsByYear(filters.getReleaseYear());
                filterSongsByTags(filters.getTags());
                if (this.songResults.size() > 5) {
                    List<Song> sublist = this.songResults.subList(5, this.songResults.size());
                    sublist.clear();
                }
                break;
            case "playlist":
                this.playlistResults.addAll(library.getPlaylists());
                filterRestByName(filters.getName());
                filterRestByOwner(filters.getOwner());
                if (this.playlistResults.size() > 5) {
                    List<Playlist> sublist = this.playlistResults.subList(5, this.playlistResults.size());
                    sublist.clear();
                }
                break;
            case "podcast":
                this.podcastResults.addAll(library.getPodcasts());
                filterRestByName(filters.getName());
                filterRestByOwner(filters.getOwner());
                if (this.podcastResults.size() > 5) {
                    List<Podcast> sublist = this.podcastResults.subList(5, this.podcastResults.size());
                    sublist.clear();
                }
                break;
            default:
                break;
        }
    }

    void filterSongByName(String name) {
        if (name == null)
            return;
        this.songResults.removeIf(song -> !song.getName().startsWith(name));
    }
    void filterSongsByAlbum(String album) {
        if (album == null)
            return;
        this.songResults.removeIf(song -> !song.getAlbum().equals(album));
    }
    void filterSongsByTags(ArrayList<String> tags) {
        if (tags == null || tags.isEmpty())
            return;
        for (String tag : tags) {
            this.songResults.removeIf(song -> !song.getTags().contains(tag));
        }
    }
    void filterSongsByLyrics(String lyrics) {
        if (lyrics == null)
            return;
        this.songResults.removeIf(song -> !song.getLyrics().contains(lyrics));
    }
    void filterSongsByGenre(String genre) {
        if (genre == null)
            return;
        this.songResults.removeIf(song -> !song.getGenre().toLowerCase().equals(genre) &&
                                          !song.getGenre().equals(genre));
    }
    void filterSongsByYear(String year) {
        if (year == null)
            return;
        int yearNumber = Integer.parseInt(year.substring(1));
        if (year.startsWith(">")) {
            this.songResults.removeIf(song -> song.getReleaseYear() < yearNumber);
        } else if (year.startsWith("<")) {
            this.songResults.removeIf(song -> song.getReleaseYear() > yearNumber);
        }
    }
    void filterSongsByArtist(String artist) {
        if (artist == null)
            return;
        this.songResults.removeIf(song -> !song.getArtist().equals(artist));
    }
    void filterRestByName(String name) {
        if (name == null)
            return;
        if (!this.podcastResults.isEmpty()) {
            this.podcastResults.removeIf(podcast -> !podcast.getName().startsWith(name));
        } else if (!this.playlistResults.isEmpty()) {
            this.playlistResults.removeIf(playlist -> !playlist.getName().startsWith(name));
        }
    }
    void filterRestByOwner(String name) {
        if (name == null)
            return;
        if (!this.podcastResults.isEmpty()) {
            this.podcastResults.removeIf(podcast -> !podcast.getOwner().equals(name));
        } else if (!this.playlistResults.isEmpty()) {
            this.playlistResults.removeIf(playlist -> !playlist.getOwner().equals(name));
        }
    }
    void select() {

    }
}
