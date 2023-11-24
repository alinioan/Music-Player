package searchbar;

import commandio.CommandInput;
import commandio.CommandOutput;
import lombok.Getter;
import lombok.Setter;
import playerfiles.AudioFile;
import playerfiles.Playlist;
import playerfiles.Podcast;
import playerfiles.Song;
import playerfiles.Library;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class SearchBar {
    private static final int MAX_SEARCH = 5;
    private ArrayList<Song> songResults = new ArrayList<>();
    private ArrayList<Podcast> podcastResults = new ArrayList<>();
    private ArrayList<Playlist> playlistResults = new ArrayList<>();
    private AudioFile selectedAudioFile = null;

    /**
     * call the specific methods to process the command
     * @param command
     * @param library
     * @return
     */
    public CommandOutput processCommand(final CommandInput command, final Library library) {
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
    private CommandOutput search(final String type, final Filters filters, final Library library) {
        this.songResults = new ArrayList<>();
        this.playlistResults = new ArrayList<>();
        this.podcastResults = new ArrayList<>();
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

    private void filterSong(final Filters filters, final Library library) {
        this.songResults.addAll(library.getSongs());
        filterSongByName(filters.getName());
        filterSongsByAlbum(filters.getAlbum());
        filterSongsByArtist(filters.getArtist());
        filterSongsByGenre(filters.getGenre());
        filterSongsByLyrics(filters.getLyrics());
        filterSongsByYear(filters.getReleaseYear());
        filterSongsByTags(filters.getTags());
        if (this.songResults.size() > MAX_SEARCH) {
            List<Song> sublist = this.songResults.subList(MAX_SEARCH, this.songResults.size());
            sublist.clear();
        }
    }

    private void filterPlaylist(final Filters filters, final Library library) {
        this.playlistResults.addAll(library.getPlaylists());
        filterRestByName(filters.getName());
        filterRestByOwner(filters.getOwner());
        this.playlistResults.removeIf(playlist -> playlist.getVisibility().equals("private"));
        if (this.playlistResults.size() > MAX_SEARCH) {
            List<Playlist> sublist = this.playlistResults.subList(MAX_SEARCH,
                                                                  this.playlistResults.size());
            sublist.clear();
        }
    }

    private void filterPodcasts(final Filters filters, final Library library) {
        this.podcastResults.addAll(library.getPodcasts());
        filterRestByName(filters.getName());
        filterRestByOwner(filters.getOwner());
        if (this.podcastResults.size() > MAX_SEARCH) {
            List<Podcast> sublist = this.podcastResults.subList(MAX_SEARCH,
                                                                this.podcastResults.size());
            sublist.clear();
        }
    }

    private void filterSongByName(final String name) {
        if (name == null) {
            return;
        }
        this.songResults.removeIf(song -> !song.getName().startsWith(name));
    }
    private void filterSongsByAlbum(final String album) {
        if (album == null) {
            return;
        }
        this.songResults.removeIf(song -> !song.getAlbum().equals(album));
    }
    private void filterSongsByTags(final ArrayList<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return;
        }
        for (String tag : tags) {
            this.songResults.removeIf(song -> !song.getTags().contains(tag));
        }
    }
    private void filterSongsByLyrics(final String lyrics) {
        if (lyrics == null) {
            return;
        }
        this.songResults.removeIf(song -> !song.getLyrics().toLowerCase().contains(lyrics)
                        && !song.getLyrics().toLowerCase().contains(lyrics.toLowerCase()));
    }
    private void filterSongsByGenre(final String genre) {
        if (genre == null) {
            return;
        }
        this.songResults.removeIf(song -> !song.getGenre().toLowerCase().equals(genre)
                                  && !song.getGenre().equals(genre));
    }
    private void filterSongsByYear(final String year) {
        if (year == null) {
            return;
        }
        int yearNumber = Integer.parseInt(year.substring(1));
        if (year.startsWith(">")) {
            this.songResults.removeIf(song -> song.getReleaseYear() < yearNumber);
        } else if (year.startsWith("<")) {
            this.songResults.removeIf(song -> song.getReleaseYear() > yearNumber);
        }
    }
    private void filterSongsByArtist(final String artist) {
        if (artist == null) {
            return;
        }
        this.songResults.removeIf(song -> !song.getArtist().equals(artist));
    }
    private void filterRestByName(final String name) {
        if (name == null) {
            return;
        }
        if (!this.podcastResults.isEmpty()) {
            this.podcastResults.removeIf(podcast -> !podcast.getName().startsWith(name));
        } else if (!this.playlistResults.isEmpty()) {
            this.playlistResults.removeIf(playlist -> !playlist.getName().startsWith(name));
        }
    }
    private void filterRestByOwner(final String name) {
        if (name == null) {
            return;
        }
        if (!this.podcastResults.isEmpty()) {
            this.podcastResults.removeIf(podcast -> !podcast.getOwner().equals(name));
        } else if (!this.playlistResults.isEmpty()) {
            this.playlistResults.removeIf(playlist -> !playlist.getOwner().equals(name));
        }
    }
    private CommandOutput select(final int itemNumber) {
        CommandOutput output = new CommandOutput();
        if (this.songResults != null && !this.songResults.isEmpty()) {
            if (this.songResults.size() < itemNumber) {
                output.setMessage("The selected ID is too high.");
            } else {
                this.selectedAudioFile = this.songResults.get(itemNumber - 1);
                this.selectedAudioFile.setName(this.songResults.get(itemNumber - 1).getName());
                this.selectedAudioFile.setFileType("song");
                output.setMessage("Successfully selected "
                                    + this.selectedAudioFile.getName() + ".");
            }
        } else if (this.podcastResults != null && !this.podcastResults.isEmpty()) {
            if (this.podcastResults.size() < itemNumber) {
                output.setMessage("The selected ID is too high.");
            } else {
                this.selectedAudioFile = this.podcastResults.get(itemNumber - 1);
                this.selectedAudioFile.setName(this.podcastResults.get(itemNumber - 1).getName());
                this.selectedAudioFile.setFileType("podcast");
                output.setMessage("Successfully selected "
                                    + this.selectedAudioFile.getName() + ".");
            }
        } else if (this.playlistResults != null) {
            if (this.playlistResults.size() < itemNumber) {
                output.setMessage("The selected ID is too high.");
            } else {
                this.selectedAudioFile = this.playlistResults.get(itemNumber - 1);
                this.selectedAudioFile.setName(this.playlistResults.get(itemNumber - 1).getName());
                this.selectedAudioFile.setFileType("playlist");
                output.setMessage("Successfully selected "
                                    + this.selectedAudioFile.getName() + ".");
            }
        } else {
            output.setMessage("Please conduct a search before making a selection.");
        }
        return output;
    }
}
