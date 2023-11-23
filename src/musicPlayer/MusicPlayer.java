package musicPlayer;

import commandIO.CommandInput;
import commandIO.CommandOutput;
import lombok.Getter;
import lombok.Setter;
import playerFiles.*;
import searchBar.SearchBar;

import java.util.ArrayList;
import java.util.HashMap;

@Setter
@Getter
public class MusicPlayer {
    private AudioFile loadedFile;
    private boolean playState = false;
    private int startTime = 0;
    private int elapsedTime = 0;
    private String repeatState = "No Repeat";
    private boolean shuffleState = false;
    private final SearchBar searchBar = new SearchBar();
    private HashMap<String, Integer> podcastElapsedTime = new HashMap<>();
    private Song repeatedSong;
    private int repeatStartTime;
    private int repeatedElapsed;
    private int playlistRepeatStartTime;

    public CommandOutput processCommand(CommandInput command, Library library) {
        switch (command.getCommand()) {
            case "load":
                return load(command);
            case "playPause":
                return playPause(command);
            case "repeat":
                return repeat(command);
            case "shuffle":
                break;
            case "forward":
                break;
            case "backward":
                break;
            case "prev":
                break;
            case "status":
                return status(command);
            default:
                if (command.getCommand().equals("search")) {
                    savePodcastTime(command.getTimestamp());
                    this.loadedFile = null;
                }
                return this.searchBar.processCommand(command, library);
        }
        return null;
    }
    private CommandOutput load(CommandInput command) {
        CommandOutput output = new CommandOutput();
        savePodcastTime(command.getTimestamp());
        if (this.searchBar.getSelectedAudioFile() != null) {
            // init player
            this.playState = true;
            this.startTime = command.getTimestamp();
            this.elapsedTime = 0;
            this.repeatState = "No Repeat";
            output.setMessage("Playback loaded successfully.");
            this.loadedFile = this.searchBar.getSelectedAudioFile().deepCopy();
            if (this.loadedFile.getFileType().equals("playlist")) {
                if (((Playlist)this.searchBar.getSelectedAudioFile()).getSongs().isEmpty()) {
                    output.setMessage("You can't load an empty audio collection!");
                }
            } else if (this.loadedFile.getFileType().equals("podcast")) {
                if (!this.podcastElapsedTime.isEmpty())
                    this.elapsedTime = this.podcastElapsedTime.get(this.loadedFile.getName());
                if (((Podcast)this.searchBar.getSelectedAudioFile()).getEpisodes().isEmpty()) {
                    output.setMessage("You can't load an empty audio collection!");
                }
            }
            this.searchBar.setSelectedAudioFile(null);
        } else {
            output.setMessage("Please select a source before attempting to load.");
        }
        return output;
    }

    private CommandOutput repeat(CommandInput command) {
        CommandOutput output = new CommandOutput();
        if (this.loadedFile == null) {
            output.setMessage("Please load a source before setting the repeat status.");
            this.repeatState = "No Repeat";
            return output;
        }
        if (this.loadedFile.getFileType().equals("playlist")) {
            switch (this.repeatState) {
                case "No Repeat":
                    this.repeatedSong = null;
                    this.repeatStartTime = 0;
                    this.repeatedElapsed = 0;
                    this.repeatState = "Repeat All";
                    break;
                case "Repeat All":
                    this.repeatedSong = getSongFromPlaylist(command.getTimestamp());
                    ArrayList<Song> songs = ((Playlist) this.loadedFile).getSongs();
                    int previousSongsTime = 0;
                    for (int i = 0; i < songs.size(); i++) {
                        if (songs.get(i).equals(this.repeatedSong))
                            break;
                        previousSongsTime += songs.get(i).getDuration();
                    }
                    this.playlistRepeatStartTime = this.startTime + //this.elapsedTime +
                            ((command.getTimestamp() - this.startTime) / this.loadedFile.getFileDuration()) * loadedFile.getFileDuration();;
                    this.repeatStartTime = this.playlistRepeatStartTime + previousSongsTime;
                    System.out.println("playlist repeat: " + this.playlistRepeatStartTime + " crt: " + command.getTimestamp() + " repaet start: " + this.repeatStartTime + " song: " + this.repeatedSong.getDuration());
                    this.repeatState = "Repeat Current Song";
                    break;
                case "Repeat Current Song":
                    this.repeatedElapsed = command.getTimestamp() - this.repeatStartTime - (command.getTimestamp() - this.repeatStartTime) % this.repeatedSong.getDuration();
                    this.startTime = this.playlistRepeatStartTime;
                    this.repeatState = "No Repeat";
                    break;
            }
        } else {
            switch (this.repeatState) {
                case "No Repeat":
                    this.repeatState = "Repeat Once";
                    break;
                case "Repeat Once":
                    if (command.getTimestamp() - this.startTime > this.loadedFile.getFileDuration())
                        this.startTime = this.startTime + this.loadedFile.getFileDuration();
                    this.repeatState = "Repeat Infinite";
                    break;
                case "Repeat Infinite":
                    this.startTime += command.getTimestamp() - this.startTime - (command.getTimestamp() - this.startTime) % this.loadedFile.getFileDuration();
                    this.repeatState = "No Repeat";
                    break;
            }
        }
        output.setMessage("Repeat mode changed to " + this.repeatState.toLowerCase() + ".");
        return output;
    }

    private void savePodcastTime(int timestamp) {
        int podcastElapsed;
        if (this.playState)
            podcastElapsed = this.elapsedTime + timestamp - startTime;
        else
            podcastElapsed = this.elapsedTime;
        if (this.loadedFile == null || !this.loadedFile.getFileType().equals("podcast"))
            return;
        this.podcastElapsedTime.put(this.loadedFile.getName(), podcastElapsed);
    }

    private CommandOutput playPause(CommandInput command) {
        CommandOutput output = new CommandOutput();
        if (this.loadedFile == null) {
            output.setMessage("Please load a source before attempting to pause or resume playback.");
            return output;
        }
        if (this.playState) {
            this.elapsedTime += command.getTimestamp() - this.startTime;
            savePodcastTime(command.getTimestamp());
            this.playState = false;
            output.setMessage("Playback paused successfully.");
        } else {
            this.startTime = command.getTimestamp();
            this.playState = true;
            output.setMessage("Playback resumed successfully.");
        }
        return output;
    }

    private CommandOutput status(CommandInput command) {
        CommandOutput output = new CommandOutput();
        if (this.loadedFile == null) {
            PlayerStatus stats = new PlayerStatus("", 0, this.repeatState, this.shuffleState, true);
            output.setStats(stats);
            return output;
        }
        String filename;
        filename = getStatusName(command);
        int remainedTime = calculateTimeStatus(command);
        if (remainedTime <= 0)
            filename = "";
        PlayerStatus stats = new PlayerStatus(filename, remainedTime, this.repeatState, this.shuffleState, !this.playState);
        output.setStats(stats);
        return output;
    }

    private int calculateTimeStatus(CommandInput command) {
        int remainedTime = 0;
        if (!this.playState) {
            remainedTime = this.loadedFile.getFileDuration() - this.elapsedTime;
            if (this.loadedFile.getFileType().equals("podcast"))
                remainedTime = getEpisodeRemainedTime(command.getTimestamp());
            if (this.loadedFile.getFileType().equals("playlist"))
                remainedTime = getPlaylistRemained(command.getTimestamp());
            return remainedTime;
        }
        switch (this.repeatState) {
            case "No Repeat":
                remainedTime = this.loadedFile.getFileDuration() - (command.getTimestamp() - startTime) - this.elapsedTime;
                if (remainedTime <= 0) {
                    this.loadedFile = null;
                    remainedTime = 0;
                    this.elapsedTime = 0;
                    this.playState = false;
                } else {
                    if (this.loadedFile.getFileType().equals("podcast"))
                        remainedTime = getEpisodeRemainedTime(command.getTimestamp());
                    if (this.loadedFile.getFileType().equals("playlist"))
                        remainedTime = getPlaylistRemained(command.getTimestamp());
                }
                break;
            case "Repeat Once":
                if (command.getTimestamp() - startTime > this.loadedFile.getFileDuration())
                    this.repeatState = "No Repeat";
                remainedTime = this.loadedFile.getFileDuration() - (command.getTimestamp() - startTime) % this.loadedFile.getFileDuration() - this.elapsedTime;
                if (remainedTime <= 0) {
                    this.loadedFile = null;
                    remainedTime = 0;
                    this.elapsedTime = 0;
                    this.playState = false;
                    this.repeatState = "No Repeat";
                }
                break;
            case "Repeat Infinite":
                remainedTime = this.loadedFile.getFileDuration() - (command.getTimestamp() - startTime) % this.loadedFile.getFileDuration() - this.elapsedTime;
                break;
            case "Repeat All":
                remainedTime = getPlaylistRemained(command.getTimestamp());
                break;
            case "Repeat Current Song":
                remainedTime = this.repeatedSong.getDuration() - (command.getTimestamp() - this.repeatStartTime);
                if (remainedTime <= 0)
                    remainedTime = this.repeatedSong.getDuration() + remainedTime;
                break;
            default:
                break;
        }
        return remainedTime;
    }

    // TODO: move to corresponding class
    private int getEpisodeRemainedTime(int timestamp) {
        int podcastElapsed;
        if (this.playState)
            podcastElapsed = this.elapsedTime + timestamp - startTime;
        else
            podcastElapsed = this.elapsedTime;
        for (PodcastEpisode episode : ((Podcast) this.loadedFile).getEpisodes()) {
            podcastElapsed -= episode.getDuration();
            if (podcastElapsed < 0) {
                podcastElapsed += episode.getDuration();
                return episode.getDuration() - podcastElapsed;
            }
        }
        return 0;
    }

    // TODO: move to corresponding class
    private int getPlaylistRemained(int timestamp) {
        int playlistElapsed;
        if (this.playState) {
            if (!this.repeatState.equals("No Repeat"))
                playlistElapsed = (this.elapsedTime + timestamp - this.startTime) % this.loadedFile.getFileDuration();
            else
                playlistElapsed = this.elapsedTime + timestamp - this.startTime - this.repeatedElapsed;
        }
        else
            playlistElapsed = this.elapsedTime;
        if (this.repeatState.equals("Repeat Current Song")) {
            return (timestamp - this.repeatStartTime) % repeatedSong.getDuration();
        }
        for (Song song : ((Playlist)this.loadedFile).getSongs()) {
            playlistElapsed -= song.getDuration();
            if (playlistElapsed < 0) {
                playlistElapsed += song.getDuration();
                return song.getDuration() - playlistElapsed;
            }
        }
        return 0;
    }

    private String getStatusName(CommandInput command) {
        String filename;
        if (loadedFile.getFileType().equals("song")) {
            filename = this.loadedFile.getName();
        } else if (loadedFile.getFileType().equals("playlist")) {
            filename = getSongFromPlaylist(command.getTimestamp()).getName();
        } else {
            filename = getEpisodeFromPodcast(command.getTimestamp()).getName();
        }
        return filename;
    }

    // TODO: move to corresponding class
    PodcastEpisode getEpisodeFromPodcast(int timestamp) {
        if (!this.loadedFile.getFileType().equals("podcast"))
            return null;
        int podcastElapsed;
        if (this.playState)
            podcastElapsed = this.elapsedTime + timestamp - startTime;
        else
            podcastElapsed = this.elapsedTime;
        for (PodcastEpisode episode : ((Podcast) this.loadedFile).getEpisodes()) {
            podcastElapsed -= episode.getDuration();
            if (podcastElapsed < 0)
                return episode;
        }
        PodcastEpisode emptyEpisode = new PodcastEpisode();
        emptyEpisode.setName("");
        return emptyEpisode;
    }

    // TODO: move to corresponding class
    Song getSongFromPlaylist(int timestamp) {
        if (!this.loadedFile.getFileType().equals("playlist")) {
            return null;
        }
        if (this.repeatState.equals("Repeat Current Song"))
            return this.repeatedSong;
        int playlistElapsed;
        if (this.playState) {
            if (this.repeatState.equals("No Repeat"))
                playlistElapsed = this.elapsedTime + timestamp - this.startTime - this.repeatedElapsed;
            else
                playlistElapsed = (this.elapsedTime + timestamp - this.startTime) % this.loadedFile.getFileDuration();
        }
        else
            playlistElapsed = this.elapsedTime;
        for (Song song : ((Playlist)this.loadedFile).getSongs()) {
            playlistElapsed -= song.getDuration();
            if (playlistElapsed < 0)
                return song;
        }
        Song emptySong = new Song();
        emptySong.setName("");
        return emptySong;
    }
}
