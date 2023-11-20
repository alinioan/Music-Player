package musicPlayer;

import commandIO.CommandInput;
import commandIO.CommandOutput;
import lombok.Getter;
import lombok.Setter;
import playerFiles.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class SearchBar {
    private ArrayList<Song> songResults = new ArrayList<>();
    private ArrayList<Podcast> podcastResults = new ArrayList<>();
    private ArrayList<Playlist> playlistResults = new ArrayList<>();

    private AudioFile selectedAudioFile = null;


    CommandOutput processCommand(CommandInput command, Library library) {
        switch (command.getCommand()) {
            case "search":
                return search(command.getType(), command.getFilters(), library);
            case "select":
                return select(command.getItemNumber());
            default:
                break;
        }
        return null;
    }
    CommandOutput search(String type, Filters filters, Library library) {
        this.songResults.clear();
        this.playlistResults.clear();
        this.podcastResults.clear();
        CommandOutput output = new CommandOutput();
        switch (type) {
            case "song":
                filterSong(filters, library);
                output.setMessage("Search returned " + this.songResults.size() + " results");
                for (Song song : this.songResults) {
                    output.getResult().add(song.getName());
                }
                break;
            case "playlist":
                filterPlaylist(filters, library);
                output.setMessage("Search returned " + this.playlistResults.size() + " results");
                for (Playlist playlist : this.playlistResults) {
                    output.getResult().add(playlist.getName());
                }
                break;
            case "podcast":
                filterPodcasts(filters, library);
                output.setMessage("Search returned " + this.podcastResults.size() + " results");
                for (Podcast podcast : this.podcastResults) {
                    output.getResult().add(podcast.getName());
                }
                break;
            default:
                break;
        }
        return output;
    }

    void filterSong(Filters filters, Library library) {
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
    }

    void filterPlaylist(Filters filters, Library library) {
        this.playlistResults.addAll(library.getPlaylists());
        filterRestByName(filters.getName());
        filterRestByOwner(filters.getOwner());
        if (this.playlistResults.size() > 5) {
            List<Playlist> sublist = this.playlistResults.subList(5, this.playlistResults.size());
            sublist.clear();
        }
    }

    void filterPodcasts(Filters filters, Library library) {
        this.podcastResults.addAll(library.getPodcasts());
        filterRestByName(filters.getName());
        filterRestByOwner(filters.getOwner());
        if (this.podcastResults.size() > 5) {
            List<Podcast> sublist = this.podcastResults.subList(5, this.podcastResults.size());
            sublist.clear();
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
    CommandOutput select(int itemNumber) {
        CommandOutput output = new CommandOutput();
        if (this.songResults != null && !this.songResults.isEmpty()) {
            if (this.songResults.size() < itemNumber) {
                output.setMessage("The selected ID is too high.");
            } else {
                this.selectedAudioFile = this.songResults.get(itemNumber - 1);
                this.selectedAudioFile.setName(this.songResults.get(itemNumber - 1).getName());
                this.selectedAudioFile.setFileType("song");
                output.setMessage("Successfully selected " + this.selectedAudioFile.getName() + ".");
            }
        } else if (this.podcastResults != null && !this.podcastResults.isEmpty()) {
            if (this.podcastResults.size() < itemNumber) {
                output.setMessage("The selected ID is too high.");
            } else {
                this.selectedAudioFile = this.podcastResults.get(itemNumber - 1);
                this.selectedAudioFile.setName(this.podcastResults.get(itemNumber - 1).getName());
                this.selectedAudioFile.setFileType("podcast");
                output.setMessage("Successfully selected " + this.selectedAudioFile.getName() + ".");
            }
        } else if (this.playlistResults != null && !this.playlistResults.isEmpty()) {
            if (this.playlistResults.size() < itemNumber) {
                output.setMessage("The selected ID is too high.");
            } else {
                this.selectedAudioFile = this.playlistResults.get(itemNumber - 1);
                this.selectedAudioFile.setName(this.playlistResults.get(itemNumber - 1).getName());
                this.selectedAudioFile.setFileType("playlist");
                output.setMessage("Successfully selected " + this.selectedAudioFile.getName() + ".");
            }
        } else {
            output.setMessage("Please conduct a search before making a selection.");
        }
        return output;
    }
}
