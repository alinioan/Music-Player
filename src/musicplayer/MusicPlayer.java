package musicplayer;

import commandio.CommandInput;
import commandio.CommandOutput;
import lombok.Getter;
import lombok.Setter;
import playerfiles.Library;
import playerfiles.Song;
import playerfiles.Playlist;
import playerfiles.Podcast;
import playerfiles.PodcastEpisode;
import playerfiles.AudioFile;
import searchbar.SearchBar;

import java.util.ArrayList;
import java.util.HashMap;

@Setter
@Getter
public class MusicPlayer {
    private static final int FW_BW_TIME = 90;
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

    /**
     * call the specific methods to process the command
     * @param command command to process
     * @param library library of the music player
     * @return command output
     */
    public CommandOutput processCommand(final CommandInput command, final Library library) {
        switch (command.getCommand()) {
            case "load":
                return load(command);
            case "playPause":
                return playPause(command);
            case "repeat":
                return repeat(command);
            case "shuffle":
                return shuffle(command);
            case "forward":
            case "backward":
                return forwardBackward(command);
            case "next":
                return next(command);
            case "prev":
                return prev(command);
            case "status":
                return status(command);
            default:
                if (command.getCommand().equals("search")) {
                    savePodcastTime(command.getTimestamp());
                    this.loadedFile = null;
                }
                return this.searchBar.processCommand(command, library);
        }
    }
    private CommandOutput load(final CommandInput command) {
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
                if (((Playlist) this.searchBar.getSelectedAudioFile()).getSongs().isEmpty()) {
                    output.setMessage("You can't load an empty audio collection!");
                }
                ((Playlist) this.loadedFile).setMainSongs(((Playlist) this.loadedFile).getSongs());
            } else if (this.loadedFile.getFileType().equals("podcast")) {
                if (!this.podcastElapsedTime.isEmpty()) {
                    this.elapsedTime = this.podcastElapsedTime.get(this.loadedFile.getName());
                }
                if (((Podcast) this.searchBar.getSelectedAudioFile()).getEpisodes().isEmpty()) {
                    output.setMessage("You can't load an empty audio collection!");
                }
            }
            this.searchBar.setSelectedAudioFile(null);
            this.searchBar.setPlaylistResults(null);
            this.searchBar.setPodcastResults(null);
            this.searchBar.setSongResults(null);
        } else {
            output.setMessage("Please select a source before attempting to load.");
        }
        return output;
    }

    private CommandOutput repeat(final CommandInput command) {
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
                    int previousSongsTime = ((Playlist) this.loadedFile).getPreviousSongsTime(
                                                                this.repeatedSong, "main");
                    this.playlistRepeatStartTime = this.startTime
                            + ((command.getTimestamp() - this.startTime)
                            / this.loadedFile.getFileDuration()) * loadedFile.getFileDuration();
                    this.repeatStartTime = this.playlistRepeatStartTime + previousSongsTime;
                    this.repeatState = "Repeat Current Song";
                    break;
                case "Repeat Current Song":
                    this.repeatedElapsed = command.getTimestamp() - this.repeatStartTime
                                           - (command.getTimestamp() - this.repeatStartTime)
                                           % this.repeatedSong.getDuration();
                    this.startTime = this.playlistRepeatStartTime;
                    this.repeatState = "No Repeat";
                    break;
                default:
                    break;
            }
        } else {
            switch (this.repeatState) {
                case "No Repeat":
                    this.repeatState = "Repeat Once";
                    break;
                case "Repeat Once":
                    if (command.getTimestamp() - this.startTime
                            > this.loadedFile.getFileDuration()) {
                        this.startTime = this.startTime + this.loadedFile.getFileDuration();
                    }
                    this.repeatState = "Repeat Infinite";
                    break;
                case "Repeat Infinite":
                    this.startTime += command.getTimestamp() - this.startTime
                                      - (command.getTimestamp() - this.startTime)
                                      % this.loadedFile.getFileDuration();
                    this.repeatState = "No Repeat";
                    break;
                default:
                    break;
            }
        }
        output.setMessage("Repeat mode changed to " + this.repeatState.toLowerCase() + ".");
        return output;
    }

    /**
     * Saves the podcast time in the players hashmap to be later used at the next load
     * @param timestamp timestamp at witch the save is made
     */
    private void savePodcastTime(final int timestamp) {
        int podcastElapsed;
        if (this.playState) {
            podcastElapsed = this.elapsedTime + timestamp - startTime;
        } else {
            podcastElapsed = this.elapsedTime;
        }
        if (this.loadedFile == null || !this.loadedFile.getFileType().equals("podcast")) {
            return;
        }
        if (podcastElapsed > this.loadedFile.getFileDuration()) {
            this.podcastElapsedTime.put(this.loadedFile.getName(), 0);
        } else {
            this.podcastElapsedTime.put(this.loadedFile.getName(), podcastElapsed);
        }
    }

    private CommandOutput playPause(final CommandInput command) {
        CommandOutput output = new CommandOutput();
        if (this.loadedFile == null) {
            output.setMessage("Please load a source before attempting"
                              + " to pause or resume playback.");
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

    private CommandOutput shuffle(final CommandInput command) {
        CommandOutput output = new CommandOutput();
        if (this.loadedFile == null) {
            output.setMessage("Please load a source before using the shuffle function.");
            return output;
        }
        if (this.startTime + this.loadedFile.getFileDuration() - command.getTimestamp() < 0)  {
            output.setMessage("Please load a source before using the shuffle function.");
            this.shuffleState = false;
            return output;
        }
        if (!this.loadedFile.getFileType().equals("playlist")) {
            output.setMessage("The loaded source is not a playlist.");
            return output;
        }
        Playlist playlist = (Playlist) this.loadedFile;
        if (this.shuffleState) {
            Song currentSong = getSongFromPlaylist(command.getTimestamp());
            int previousTimesSongs = playlist.getPreviousSongsTime(currentSong, "songs");
            int previousTimesShuffled = playlist.getPreviousSongsTime(currentSong, "shuffled");
            this.startTime = this.startTime + previousTimesShuffled - previousTimesSongs;

            ((Playlist) this.loadedFile).setMainSongs(((Playlist) this.loadedFile).getSongs());
            this.shuffleState = false;
            output.setMessage("Shuffle function deactivated successfully.");
        } else {
            playlist.setShuffled(new ArrayList<>());
            playlist.generateShuffled(command.getSeed());
            Song currentSong = getSongFromPlaylist(command.getTimestamp());

            int previousTimesSongs = playlist.getPreviousSongsTime(currentSong, "songs");
            int previousTimesShuffled = playlist.getPreviousSongsTime(currentSong, "shuffled");
            this.startTime = this.startTime + previousTimesSongs - previousTimesShuffled;

            playlist.setMainSongs(playlist.getShuffled());
            this.shuffleState = true;
            output.setMessage("Shuffle function activated successfully.");
        }
        return output;
    }

    private CommandOutput forwardBackward(final CommandInput command) {
        CommandOutput output = new CommandOutput();
        if (this.loadedFile == null) {
            if (command.getCommand().equals("forward")) {
                return forward(command);
            } else {
                return backward(command);
            }
        }
        if (!this.loadedFile.getFileType().equals("podcast")) {
            output.setMessage("The loaded source is not a podcast.");
            return output;
        }
        if (command.getCommand().equals("forward")) {
            return forward(command);
        } else {
            return backward(command);
        }
    }

    private CommandOutput forward(final CommandInput command) {
        CommandOutput output = new CommandOutput();
        if (this.loadedFile == null) {
            output.setMessage("Please load a source before attempting to forward.");
            return output;
        }
        int remainedTime = getEpisodeRemainedTime(command.getTimestamp());
        if (remainedTime > FW_BW_TIME) {
            this.startTime -= FW_BW_TIME;
        } else {
            PodcastEpisode ep = getEpisodeFromPodcast(command.getTimestamp());
            this.startTime = command.getTimestamp() - (this.loadedFile.getFileDuration()
                             + ((Podcast) this.loadedFile).getPreviousEpTime(ep));
        }
        output.setMessage("Skipped forward successfully.");
        return output;
    }

    private CommandOutput backward(final CommandInput command) {
        CommandOutput output = new CommandOutput();
        if (this.loadedFile == null) {
            output.setMessage("Please load a source before attempting to rewind.");
            return output;
        }
        PodcastEpisode episode = getEpisodeFromPodcast(command.getTimestamp());
        int prevTimes = ((Podcast) this.loadedFile).getPreviousEpTime(episode);
        int episodeElapsed = episode.getDuration()
                             - (command.getTimestamp() - (this.startTime + prevTimes));
        if (episodeElapsed >= FW_BW_TIME) {
            this.startTime += FW_BW_TIME;
        } else {
            this.startTime = this.startTime + episodeElapsed;
        }
        output.setMessage("Rewound successfully.");
        return output;
    }

    private CommandOutput next(final CommandInput command) {
        CommandOutput output = new CommandOutput();
        if (this.loadedFile == null) {
            output.setMessage("Please load a source before skipping to the next track.");
            return output;
        }
        switch (this.repeatState) {
            case "No Repeat":
                switch (this.loadedFile.getFileType()) {
                    case "song":
                        this.startTime += this.loadedFile.getFileDuration();
                        output.setMessage("Please load a source before "
                                          + "skipping to the next track.");
                        break;
                    case "playlist":
                        Song currentSong = getSongFromPlaylist(command.getTimestamp());
                        this.startTime = command.getTimestamp()
                                         - (currentSong.getDuration()
                                         + ((Playlist) this.loadedFile).getPreviousSongsTime(
                                                                    currentSong, "main"));
                        output.setMessage("Skipped to next track successfully."
                                          + " The current track is "
                                          + getSongFromPlaylist(command.getTimestamp()).getName()
                                          + ".");
                        break;
                    case "podcast":
                        PodcastEpisode ep = getEpisodeFromPodcast(command.getTimestamp());
                        this.startTime = command.getTimestamp() - (ep.getDuration()
                                         + ((Podcast) this.loadedFile).getPreviousEpTime(ep));
                        output.setMessage("Skipped to next track successfully."
                                          + " The current track is "
                                          + getEpisodeFromPodcast(command.getTimestamp()).getName()
                                          + ".");
                        break;
                    default:
                        break;
                }
                break;
            case "Repeat Once":
                break;
            case "Repeat Infinite":
                break;
            case "Repeat All":
                break;
            case "Repeat Current Song":
                break;
            default:
                break;
        }
        return output;
    }

    private CommandOutput prev(final CommandInput command) {
        CommandOutput output = new CommandOutput();
        if (this.loadedFile == null) {
            output.setMessage("Please load a source before returning to the previous track.");
            return output;
        }
        switch (this.repeatState) {
            case "No Repeat":
                switch (this.loadedFile.getFileType()) {
                    case "song":
                        this.startTime = command.getTimestamp();
                        output.setMessage("Returned to previous track successfully. "
                                          + "The current track is "
                                          + this.loadedFile.getName() + ".");
                        break;
                    case "playlist":
                        Song currentSong = getSongFromPlaylist(command.getTimestamp());
                        Playlist playlist = (Playlist) this.loadedFile;
                        if (playlist.getMainSongs().indexOf(currentSong) == 0) {
                            this.startTime = command.getTimestamp();
                        } else {
                            int previousTimes = playlist.getPreviousSongsTime(currentSong, "main");
                            if (this.startTime + previousTimes == command.getTimestamp()) {
                                this.startTime = command.getTimestamp()
                                             + playlist.getPreviousSongsTime(getSongFromPlaylist(
                                                     command.getTimestamp() - 1), "main");
                            } else {
                                this.startTime += command.getTimestamp()
                                                  - (this.startTime + previousTimes);
                            }
                        }
                        output.setMessage("Returned to previous track successfully. "
                                          + "The current track is "
                                          + getSongFromPlaylist(command.getTimestamp()).getName()
                                          + ".");
                        break;
                    case "podcast":
                        PodcastEpisode ep = getEpisodeFromPodcast(command.getTimestamp());
                        if (((Podcast) this.loadedFile).getEpisodes().indexOf(ep) == 0) {
                            this.startTime = command.getTimestamp();
                        } else {
                            int previousTimes = ((Podcast) this.loadedFile).getPreviousEpTime(ep);
                            if (this.startTime + previousTimes == command.getTimestamp()) {
                                this.startTime = command.getTimestamp()
                                                 + ((Podcast) this.loadedFile).getPreviousEpTime(
                                                         getEpisodeFromPodcast(
                                                         command.getTimestamp() - 1));
                            } else {
                                this.startTime += command.getTimestamp()
                                                  - (this.startTime + previousTimes);
                            }
                        }
                        System.out.println(this.startTime);
                        output.setMessage("Returned to previous track successfully."
                                          + " The current track is "
                                          + getEpisodeFromPodcast(command.getTimestamp()).getName()
                                          + ".");
                        break;
                    default:
                        break;
                }
                break;
            case "Repeat Once":
                break;
            case "Repeat Infinite":
                break;
            case "Repeat All":
                break;
            case "Repeat Current Song":
                break;
            default:
                break;
        }
        return output;
    }

    private CommandOutput status(final CommandInput command) {
        CommandOutput output = new CommandOutput();
        if (this.loadedFile == null) {
            PlayerStatus stats = new PlayerStatus("", 0, this.repeatState, this.shuffleState, true);
            output.setStats(stats);
            return output;
        }
        String filename;
        filename = getStatusName(command);
        int remainedTime = calculateTimeStatus(command);
        if (remainedTime <= 0) {
            this.loadedFile = null;
            filename = "";
        }
        PlayerStatus stats = new PlayerStatus(filename, remainedTime, this.repeatState,
                                              this.shuffleState, !this.playState);
        output.setStats(stats);
        return output;
    }

    /**
     * Calculates the remained time for status
     * @param command the status command, used for the time
     * @return the remained time for the status
     */
    private int calculateTimeStatus(final CommandInput command) {
        int remainedTime = 0;
        if (!this.playState) {
            remainedTime = this.loadedFile.getFileDuration() - this.elapsedTime;
            if (this.loadedFile.getFileType().equals("podcast")) {
                remainedTime = getEpisodeRemainedTime(command.getTimestamp());
            }
            if (this.loadedFile.getFileType().equals("playlist")) {
                remainedTime = getPlaylistRemained(command.getTimestamp());
            }
            return remainedTime;
        }
        switch (this.repeatState) {
            case "No Repeat":
                remainedTime = this.loadedFile.getFileDuration()
                               - (command.getTimestamp() - startTime) - this.elapsedTime;
                if (remainedTime <= 0) {
                    this.loadedFile = null;
                    remainedTime = 0;
                    this.elapsedTime = 0;
                    this.playState = false;
                } else {
                    if (this.loadedFile.getFileType().equals("podcast")) {
                        remainedTime = getEpisodeRemainedTime(command.getTimestamp());
                    }
                    if (this.loadedFile.getFileType().equals("playlist")) {
                        remainedTime = getPlaylistRemained(command.getTimestamp());
                    }
                }
                break;
            case "Repeat Once":
                if (command.getTimestamp() - startTime > this.loadedFile.getFileDuration()) {
                    this.repeatState = "No Repeat";
                }
                remainedTime = this.loadedFile.getFileDuration()
                               - (command.getTimestamp() - startTime)
                               % this.loadedFile.getFileDuration() - this.elapsedTime;
                if (remainedTime <= 0) {
                    this.loadedFile = null;
                    remainedTime = 0;
                    this.elapsedTime = 0;
                    this.playState = false;
                    this.repeatState = "No Repeat";
                }
                break;
            case "Repeat Infinite":
                remainedTime = this.loadedFile.getFileDuration()
                               - (command.getTimestamp() - startTime)
                               % this.loadedFile.getFileDuration() - this.elapsedTime;
                break;
            case "Repeat All":
                remainedTime = getPlaylistRemained(command.getTimestamp());
                break;
            case "Repeat Current Song":
                remainedTime = this.repeatedSong.getDuration()
                               - (command.getTimestamp() - this.repeatStartTime);
                if (remainedTime <= 0) {
                    remainedTime = this.repeatedSong.getDuration() + remainedTime;
                }
                break;
            default:
                break;
        }
        return remainedTime;
    }

    /**
     * get the name of the current track in the player
     * @param command the status command used for the timestamp
     * @return the name of the track
     */
    private String getStatusName(final CommandInput command) {
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

    /**
     * calculate the remained time of an episode in the loaded podcast
     * should really be in the Podcast class
     * @param timestamp
     * @return the remained time
     */
    private int getEpisodeRemainedTime(final int timestamp) {
        int podcastElapsed;
        if (this.playState) {
            podcastElapsed = this.elapsedTime + timestamp - startTime;
        } else {
            podcastElapsed = this.elapsedTime;
        }
        for (PodcastEpisode episode : ((Podcast) this.loadedFile).getEpisodes()) {
            podcastElapsed -= episode.getDuration();
            if (podcastElapsed < 0) {
                podcastElapsed += episode.getDuration();
                return episode.getDuration() - podcastElapsed;
            }
        }
        return 0;
    }

    /**
     * calculate the remained time of a song in the loaded playlist
     * should really be in the Playlist class
     * @param timestamp
     * @return the remained time
     */
    private int getPlaylistRemained(final int timestamp) {
        int playlistElapsed;
        if (this.playState) {
            if (!this.repeatState.equals("No Repeat")) {
                playlistElapsed = (this.elapsedTime + timestamp - this.startTime)
                                  % this.loadedFile.getFileDuration();
            } else {
                playlistElapsed = this.elapsedTime + timestamp
                                  - this.startTime - this.repeatedElapsed;
            }
        } else {
            playlistElapsed = this.elapsedTime;
        }
        if (this.repeatState.equals("Repeat Current Song")) {
            return (timestamp - this.repeatStartTime) % repeatedSong.getDuration();
        }
        for (Song song : ((Playlist) this.loadedFile).getMainSongs()) {
            playlistElapsed -= song.getDuration();
            if (playlistElapsed < 0) {
                playlistElapsed += song.getDuration();
                return song.getDuration() - playlistElapsed;
            }
        }
        return 0;
    }

    /**
     * gets the episode currently playing from the loaded podcast
     * @param timestamp
     * @return the episode
     */
    PodcastEpisode getEpisodeFromPodcast(final int timestamp) {
        if (!this.loadedFile.getFileType().equals("podcast")) {
            return null;
        }
        int podcastElapsed;
        if (this.playState) {
            podcastElapsed = this.elapsedTime + timestamp - startTime;
        } else {
            podcastElapsed = this.elapsedTime;
        }
        for (PodcastEpisode episode : ((Podcast) this.loadedFile).getEpisodes()) {
            podcastElapsed -= episode.getDuration();
            if (podcastElapsed < 0) {
                return episode;
            }
        }
        PodcastEpisode emptyEpisode = new PodcastEpisode();
        emptyEpisode.setName("");
        return emptyEpisode;
    }

    /**
     * gets the song currently playing from the loaded playlist
     * @param timestamp
     * @return the song
     */
    Song getSongFromPlaylist(final int timestamp) {
        if (!this.loadedFile.getFileType().equals("playlist")) {
            return null;
        }
        if (this.repeatState.equals("Repeat Current Song")) {
            return this.repeatedSong;
        }
        int playlistElapsed;
        if (this.playState) {
            if (this.repeatState.equals("No Repeat")) {
                playlistElapsed = this.elapsedTime + timestamp - this.startTime
                                  - this.repeatedElapsed;
            } else {
                playlistElapsed = (this.elapsedTime + timestamp - this.startTime)
                                  % this.loadedFile.getFileDuration();
            }
        } else {
            playlistElapsed = this.elapsedTime;
        }
        for (Song song : ((Playlist) this.loadedFile).getMainSongs()) {
            playlistElapsed -= song.getDuration();
            if (playlistElapsed < 0) {
                return song;
            }
        }
        Song emptySong = new Song();
        emptySong.setName("");
        return emptySong;
    }
}
