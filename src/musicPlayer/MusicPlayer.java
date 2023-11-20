package musicPlayer;

import commandIO.CommandInput;
import commandIO.CommandOutput;
import playerFiles.*;


public class MusicPlayer {
    private AudioFile loadedFile;
    private boolean playState = false;
    private int startTime = 0;
    private int elapsedTime = 0;
    private String repeatState = "No Repeat";
    private boolean shuffleState = false;
    private final SearchBar searchBar = new SearchBar();

    public CommandOutput processCommand(CommandInput command, Library library) {
        switch (command.getCommand()) {
            case "load":
                return load(command);
            case "playPause":
                return playpause(command);
            case "repeat":
                break;
            case "shuffle":
                break;
            case "forward":
                break;
            case "backward":
                break;
            case "like":
                break;
            case "prev":
                break;
            case "addRemoveInPlaylist":
                break;
            case "status":
                return status(command);
            default:
                if (command.getCommand().equals("search"))
                    this.loadedFile = null;
                return this.searchBar.processCommand(command, library);
        }
        return null;
    }
    CommandOutput load(CommandInput command) {
        CommandOutput output = new CommandOutput();
        this.playState = true;
        this.startTime = command.getTimestamp();
        this.elapsedTime = 0;
        output.setMessage("Playback loaded successfully.");
        if (this.searchBar.getSelectedAudioFile() != null) {
            this.loadedFile = this.searchBar.getSelectedAudioFile();
            if (this.loadedFile.getFileType().equals("playlist")) {
                if (((Playlist)this.searchBar.getSelectedAudioFile()).getSongs().isEmpty()) {
                    output.setMessage("You can't load an empty audio collection!");
                }
            } else if (this.loadedFile.getFileType().equals("podcast")) {
                if (((Podcast)this.searchBar.getSelectedAudioFile()).getEpisodes().isEmpty()) {
                    output.setMessage("You can't load an empty audio collection!");
                }
            }
        } else {
            output.setMessage("Please select a source before attempting to load.");
            this.playState = false;
        }
        return output;
    }

    CommandOutput playpause(CommandInput command) {
        CommandOutput output = new CommandOutput();
        if (this.loadedFile == null) {
            output.setMessage("Please load a source before attempting to pause or resume playback.");
            return output;
        }
        if (this.playState) {
            this.elapsedTime += command.getTimestamp() - this.startTime;
            this.playState = false;
            output.setMessage("Playback paused successfully.");
        } else {
            this.startTime = command.getTimestamp();
            this.playState = true;
            output.setMessage("Playback resumed successfully.");
        }
        return output;
    }

    CommandOutput status(CommandInput command) {
        CommandOutput output = new CommandOutput();
        int remainedTime = 0;
        if (this.playState) {
            remainedTime = getFileDuration() - (command.getTimestamp() - startTime) - elapsedTime;
        } else {
            remainedTime = getFileDuration() - elapsedTime;
        }
        if (remainedTime < 0) {
            this.loadedFile = null;
            remainedTime = 0;
            this.elapsedTime = 0;
            this.playState = false;
        }
        String filename;
        if (this.loadedFile == null) {
            filename = "";
        } else {
            filename = this.loadedFile.getName();
        }

        Stats stats = new Stats(filename, remainedTime, this.repeatState, this.shuffleState, !this.playState);
        output.setStats(stats);
        return output;
    }

    int getFileDuration() {
        if (this.loadedFile.getFileType().equals("song"))
            return ((Song)this.loadedFile).getDuration();
        // TODO: calc file duration for podcast and playlist
        return 0;
    }
}
