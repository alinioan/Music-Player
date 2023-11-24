package musicplayer;

import commandio.CommandInput;
import commandio.CommandOutput;
import commandio.ShowPlaylistOutput;
import lombok.Getter;
import lombok.Setter;
import playerfiles.AudioFile;
import playerfiles.Library;
import playerfiles.Playlist;
import playerfiles.Song;

import java.util.ArrayList;

@Getter
@Setter
public class User {
    private String username;
    private int age;
    private String city;
    private MusicPlayer player = new MusicPlayer();
    private ArrayList<Song> likedSongs = new ArrayList<>();
    private ArrayList<Playlist> ownPlaylists = new ArrayList<>();
    private ArrayList<Playlist> followedPlaylists = new ArrayList<>();

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "User{"
                + "username='" + username + '\''
                + ", age=" + age
                + ", city='" + city + '\''
                + ", player=" + player
                + '}';
    }

    /**
     * call the specific methods to process the command
     * @param command command to process
     * @param library the library of the music player
     * @return the output of the command
     */
    CommandOutput processCommand(final CommandInput command, final Library library) {
        switch (command.getCommand()) {
            case "like":
                return like(command, library);
            case "addRemoveInPlaylist":
                return addRemoveInPlaylist(command);
            case "createPlaylist":
                return createPlaylist(command, library);
            case "switchVisibility":
                return switchVisibility(command);
            case "follow":
                return follow();
            case "showPreferredSongs":
                return showPreferredSongs();
            case "showPlaylists":
                return showPlaylists();
            default:
                break;
        }
        return null;
    }

    private CommandOutput like(final CommandInput command, final Library library) {
        CommandOutput output = new CommandOutput();
        AudioFile loadedFile = this.player.getLoadedFile();
        if (loadedFile == null) {
            output.setMessage("Please load a source before liking or unliking.");
            return output;
        }
        if (loadedFile.getFileType().equals("podcast")) {
            output.setMessage("Loaded source is not a song.");
            return output;
        }
        Song loadedSong = null;
        if (loadedFile.getFileType().equals("playlist")) {
            loadedSong = this.player.getSongFromPlaylist(command.getTimestamp());
        } else {
            loadedSong = (Song) loadedFile;
        }
        int index = this.likedSongs.indexOf(loadedSong);
        if (index == -1) {
            index = library.getSongs().indexOf(loadedSong);
            if (index == -1) {
                return output;
            }
            Song likedSong = library.getSongs().get(index);
            likedSong.setTotalLikes(likedSong.getTotalLikes() + 1);
            loadedSong.setTotalLikes(likedSong.getTotalLikes());
            this.likedSongs.add(likedSong);
            output.setMessage("Like registered successfully.");
        } else {
            index = library.getSongs().indexOf(loadedSong);
            if (index == -1) {
                return output;
            }
            Song likedSong = library.getSongs().get(index);
            likedSong.setTotalLikes(likedSong.getTotalLikes() - 1);
            loadedSong.setTotalLikes(likedSong.getTotalLikes());
            this.likedSongs.remove(likedSong);
            output.setMessage("Unlike registered successfully.");
        }
        return output;
    }

    private CommandOutput addRemoveInPlaylist(final CommandInput command) {
        CommandOutput output = new CommandOutput();
        if (this.player.getLoadedFile() == null) {
            output.setMessage("Please load a source before adding to "
                              + "or removing from the playlist.");
            return output;
        }
        if (this.player.getLoadedFile().getFileType().equals("podcast")) {
            output.setMessage("The loaded source is not a song.");
            return output;
        }
        if (command.getPlaylistId() > this.ownPlaylists.size()) {
            output.setMessage("The specified playlist does not exist.");
            return output;
        }
        Song song;
        if (this.player.getLoadedFile().getFileType().equals("song")) {
            song = (Song) this.player.getLoadedFile();
        } else {
            song = this.player.getSongFromPlaylist(command.getTimestamp());
        }
        if (this.ownPlaylists.get(command.getPlaylistId() - 1).getSongs().contains(song)) {
            this.ownPlaylists.get(command.getPlaylistId() - 1).getSongs().remove(song);
            output.setMessage("Successfully removed from playlist.");
        } else {
            this.ownPlaylists.get(command.getPlaylistId() - 1).getSongs().add(song);
            output.setMessage("Successfully added to playlist.");
        }
        return output;
    }

    private CommandOutput createPlaylist(final CommandInput command, final Library library) {
        CommandOutput output = new CommandOutput();
        for (Playlist playlistIter : library.getPlaylists()) {
            if (playlistIter.getName().equals(command.getPlaylistName())) {
                output.setMessage("A playlist with the same name already exists.");
                return output;
            }
        }
        Playlist playlist = new Playlist(command.getPlaylistName(), "public",
                                         this.username, library.getPlaylists().size() + 1,
                                         new ArrayList<Song>());
        library.getPlaylists().add(playlist);
        this.ownPlaylists.add(playlist);
        output.setMessage("Playlist created successfully.");
        return output;
    }

    private CommandOutput switchVisibility(final CommandInput command) {
        CommandOutput output = new CommandOutput();
        if (command.getPlaylistId() > this.ownPlaylists.size()) {
            output.setMessage("The specified playlist ID is too high.");
            return output;
        }
        if (this.ownPlaylists.get(command.getPlaylistId() - 1).getVisibility().equals("public")) {
            this.ownPlaylists.get(command.getPlaylistId() - 1).setVisibility("private");
            output.setMessage("Visibility status updated successfully to private.");
        } else {
            this.ownPlaylists.get(command.getPlaylistId() - 1).setVisibility("public");
            output.setMessage("Visibility status updated successfully to public.");
        }
        return output;
    }

    private CommandOutput follow() {
        CommandOutput output = new CommandOutput();
        AudioFile loadedFile = this.player.getSearchBar().getSelectedAudioFile();
        if (loadedFile == null) {
            output.setMessage("Please select a source before following or unfollowing.");
            return output;
        }
        if (!loadedFile.getFileType().equals("playlist")) {
            output.setMessage("The selected source is not a playlist.");
            return output;
        }
        Playlist playlist = (Playlist) loadedFile;
        if (playlist.getOwner().equals(this.username)) {
            output.setMessage("You cannot follow or unfollow your own playlist.");
            return output;
        }
        if (this.followedPlaylists.contains(playlist)) {
            playlist.setFollowers(playlist.getFollowers() - 1);
            output.setMessage("Playlist unfollowed successfully.");
        } else {
            playlist.setFollowers(playlist.getFollowers() + 1);
            output.setMessage("Playlist followed successfully.");
        }

        return output;
    }

    private CommandOutput showPreferredSongs() {
        CommandOutput output = new CommandOutput();
        ArrayList<String> songNames = new ArrayList<>();
        for (Song song : this.likedSongs) {
            songNames.add(song.getName());
        }
        output.setLikedSongs(songNames);
        return output;
    }

    private CommandOutput showPlaylists() {
        CommandOutput output = new CommandOutput();
        ArrayList<ShowPlaylistOutput> result = new ArrayList<>();
        for (Playlist playlist : this.ownPlaylists) {
            ArrayList<String> songNames = new ArrayList<>();
            for (Song song : playlist.getSongs()) {
                songNames.add(song.getName());
            }
            result.add(new ShowPlaylistOutput(playlist.getFollowers(),
                       playlist.getName(), songNames, playlist.getVisibility()));
        }
        output.setShowPlaylistOutput(result);
        return output;
    }

}
