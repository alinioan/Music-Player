package musicPlayer;

import commandIO.CommandInput;
import commandIO.CommandOutput;
import lombok.Getter;
import lombok.Setter;
import playerFiles.*;
import searchBar.SearchBar;

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

    public CommandOutput processCommand(CommandInput command, Library library) {
        switch (command.getCommand()) {
            case "load":
                return load(command);
            case "playPause":
                return playPause(command);
            case "repeat":
                break;
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

    private void savePodcastTime(int timestamp) {
        // TODO: might need to change this for pause and stuff
        Integer podcastElapsed = this.elapsedTime + timestamp - startTime;
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
        int remainedTime;
        if (this.playState) {
            remainedTime = this.loadedFile.getFileDuration() - (command.getTimestamp() - startTime) - elapsedTime;
        } else {
            remainedTime = this.loadedFile.getFileDuration() - elapsedTime;
        }
        if (remainedTime <= 0) {
            filename = "";
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
        PlayerStatus stats = new PlayerStatus(filename, remainedTime, this.repeatState, this.shuffleState, !this.playState);
        output.setStats(stats);
        return output;
    }

    private int getEpisodeRemainedTime(int timestamp) {
        int podcastEplased;
        if (this.playState)
            podcastEplased = this.elapsedTime + timestamp - startTime;
        else
            podcastEplased = this.elapsedTime;
        for (PodcastEpisode episode : ((Podcast) this.loadedFile).getEpisodes()) {
            podcastEplased -= episode.getDuration();
            if (podcastEplased < 0) {
                podcastEplased += episode.getDuration();
                return episode.getDuration() - podcastEplased;
            }
        }
        return 0;
    }

    private int getPlaylistRemained(int timestamp) {
        int playlistElapsed;
        if (this.playState)
            playlistElapsed = this.elapsedTime + timestamp - startTime;
        else
            playlistElapsed = elapsedTime;
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

    PodcastEpisode getEpisodeFromPodcast(int timestamp) {
        if (!this.loadedFile.getFileType().equals("podcast"))
            return null;
        int podcastEplased;
        if (this.playState)
            podcastEplased = this.elapsedTime + timestamp - startTime;
        else
            podcastEplased = this.elapsedTime;
        for (PodcastEpisode episode : ((Podcast) this.loadedFile).getEpisodes()) {
            podcastEplased -= episode.getDuration();
            if (podcastEplased < 0)
                return episode;
        }
        PodcastEpisode emptyEpisode = new PodcastEpisode();
        emptyEpisode.setName("");
        return emptyEpisode;
    }

    Song getSongFromPlaylist(int timestamp) {
        if (!this.loadedFile.getFileType().equals("playlist")) {
            return null;
        }
        int playlistElapsed;
        if (this.playState)
            playlistElapsed = this.elapsedTime + timestamp - startTime;
        else
            playlistElapsed = elapsedTime;
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
