package musicPlayer;

import commandIO.CommandInput;
import commandIO.CommandOutput;
import commandIO.ShowPlaylistOutput;
import lombok.Getter;
import lombok.Setter;
import playerFiles.AudioFile;
import playerFiles.Library;
import playerFiles.Playlist;
import playerFiles.Song;

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

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", age=" + age +
                ", city='" + city + '\'' +
                ", player=" + player +
                '}';
    }

    CommandOutput processCommand(CommandInput command, Library library) {
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
                break;
            case "showPreferredSongs":
                return showPreferredSongs();
            case "showPlaylists":
                return showPlaylists();
            default:
                break;
        }
        return null;
    }

    CommandOutput like(CommandInput command, Library library) {
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
            loadedSong = (Song)loadedFile;
        }
        int index = this.likedSongs.indexOf(loadedSong);
        System.out.println(command.getTimestamp());
        if (index == -1) {
            index = library.getSongs().indexOf(loadedSong);
            Song likedSong = library.getSongs().get(index);
            likedSong.setTotalLikes(likedSong.getTotalLikes() + 1);
            loadedSong.setTotalLikes(likedSong.getTotalLikes());
            this.likedSongs.add(likedSong);
            output.setMessage("Like registered successfully.");
        } else {
            index = library.getSongs().indexOf(loadedSong);
            Song likedSong = library.getSongs().get(index);
            likedSong.setTotalLikes(likedSong.getTotalLikes() - 1);
            loadedSong.setTotalLikes(likedSong.getTotalLikes());
            this.likedSongs.remove(likedSong);
            output.setMessage("Unlike registered successfully.");
        }
        return output;
    }

    CommandOutput addRemoveInPlaylist(CommandInput command) {
        CommandOutput output = new CommandOutput();
        if (this.player.getLoadedFile() == null) {
            output.setMessage("Please load a source before adding to or removing from the playlist.");
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
        if (this.player.getLoadedFile().getFileType().equals("song"))
            song = (Song) this.player.getLoadedFile();
        else
            song = this.player.getSongFromPlaylist(command.getTimestamp());
        if (this.ownPlaylists.get(command.getPlaylistId() - 1).getSongs().contains(song)) {
            this.ownPlaylists.get(command.getPlaylistId() - 1).getSongs().remove(song);
            output.setMessage("Successfully removed from playlist.");
        } else {
            this.ownPlaylists.get(command.getPlaylistId() - 1).getSongs().add(song);
            output.setMessage("Successfully added to playlist.");
        }
        return output;
    }

    CommandOutput createPlaylist(CommandInput command, Library library) {
        CommandOutput output = new CommandOutput();
        for (Playlist playlistIter : library.getPlaylists()) {
            if (playlistIter.getName().equals(command.getPlaylistName())) {
                output.setMessage("A playlist with the same name already exists.");
                return output;
            }
        }
        Playlist playlist = new Playlist(command.getPlaylistName(), "public",
                                         this.username, library.getPlaylists().size() + 1, new ArrayList<Song>());
        library.getPlaylists().add(playlist);
        this.ownPlaylists.add(playlist);
        output.setMessage("Playlist created successfully.");
        return output;
    }

    CommandOutput switchVisibility(CommandInput command) {
        CommandOutput output = new CommandOutput();
        if (command.getPlaylistId() > this.ownPlaylists.size()) {
            output.setMessage("The specified playlist ID is too high.");
            return output;
        }
        if (this.ownPlaylists.get(command.getPlaylistId() - 1).getVisibility().equals("public")) {
            this.ownPlaylists.get(command.getPlaylistId() - 1).setVisibility("private");
            output.setMessage("Visibility status updated successfully to false");
        } else {
            this.ownPlaylists.get(command.getPlaylistId() - 1).setVisibility("public");
            output.setMessage("Visibility status updated successfully to true");
        }
        return output;
    }

    CommandOutput showPreferredSongs() {
        CommandOutput output = new CommandOutput();
        ArrayList<String> songNames = new ArrayList<>();
        for (Song song : this.likedSongs) {
            songNames.add(song.getName());
        }
        output.setLikedSongs(songNames);
        return output;
    }

    CommandOutput showPlaylists() {
        CommandOutput output = new CommandOutput();
        ArrayList<ShowPlaylistOutput> result = new ArrayList<>();
        for (Playlist playlist : this.ownPlaylists) {
            ArrayList<String> songNames = new ArrayList<>();
            for (Song song : playlist.getSongs()) {
                songNames.add(song.getName());
            }
            result.add(new ShowPlaylistOutput(playlist.getFollowers(), playlist.getName(), songNames, playlist.getVisibility()));
        }
        output.setShowPlaylistOutput(result);
        return output;
    }

}
