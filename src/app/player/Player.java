package app.player;

import app.audio.Collections.AudioCollection;
import app.audio.Collections.Playlist;
import app.audio.Collections.Podcast;
import app.audio.Files.AudioFile;
import app.audio.LibraryEntry;
import app.user.wrapped.UserWrapped;
import app.utils.Enums;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Player.
 */
public final class Player {
    private Enums.RepeatMode repeatMode;
    private boolean shuffle;
    private boolean paused;
    @Getter
    private PlayerSource source;
    @Getter
    private String type;
    private final int skipTime = 90;
    private boolean connectionStatus;
    @Getter
    private UserWrapped wrapped;
    private boolean premium;
    private boolean adQueued;
    private int adPrice;
    private ArrayList<PodcastBookmark> bookmarks = new ArrayList<>();


    /**
     * Instantiates a new Player.
     */
    public Player(final boolean premium) {
        this.repeatMode = Enums.RepeatMode.NO_REPEAT;
        this.paused = true;
        this.connectionStatus = true;
        this.wrapped = new UserWrapped();
        this.premium = premium;
        adQueued = false;
    }

    /**
     * Stop.
     */
    public void stop() {
        if ("podcast".equals(this.type)) {
            bookmarkPodcast();
        }

        repeatMode = Enums.RepeatMode.NO_REPEAT;
        paused = true;
        source = null;
        shuffle = false;
    }

    private void bookmarkPodcast() {
        if (source != null && source.getAudioFile() != null) {
            PodcastBookmark currentBookmark =
                    new PodcastBookmark(source.getAudioCollection().getName(),
                                        source.getIndex(),
                                        source.getDuration());
            bookmarks.removeIf(bookmark -> bookmark.getName().equals(currentBookmark.getName()));
            bookmarks.add(currentBookmark);
        }
    }

    /**
     * Create source player source.
     *
     * @param type      the type
     * @param entry     the entry
     * @param bookmarkList the bookmarks
     * @return the player source
     */
    public PlayerSource createSource(final String fileType,
                                     final LibraryEntry entry,
                                     final List<PodcastBookmark> bookmarkList) {
        if ("song".equals(fileType)) {
            return new PlayerSource(Enums.PlayerSourceType.LIBRARY, (AudioFile) entry,
                    premium, adPrice, adQueued);
        } else if ("playlist".equals(fileType)) {
            return new PlayerSource(Enums.PlayerSourceType.PLAYLIST, (AudioCollection) entry,
                    premium, adPrice, adQueued);
        } else if ("podcast".equals(fileType)) {
            return createPodcastSource((AudioCollection) entry, bookmarkList);
        } else if ("album".equals(fileType)) {
            return new PlayerSource(Enums.PlayerSourceType.PLAYLIST, (AudioCollection) entry,
                    premium, adPrice, adQueued);
        }

        return null;
    }

    private PlayerSource createPodcastSource(final AudioCollection collection,
                                             final List<PodcastBookmark> bookmarkList) {
        for (PodcastBookmark bookmark : bookmarkList) {
            if (bookmark.getName().equals(collection.getName())) {
                return new PlayerSource(Enums.PlayerSourceType.PODCAST, collection, bookmark);
            }
        }
        return new PlayerSource(Enums.PlayerSourceType.PODCAST, collection,
                                premium, adPrice, adQueued);
    }

    /**
     * Sets source.
     *
     * @param entry the entry
     * @param sourceType  the sourceType
     */
    public void setSource(final LibraryEntry entry, final String sourceType) {
        if ("podcast".equals(this.type)) {
            bookmarkPodcast();
        }
        if (sourceType.equals("song")) {
            wrapped.updateStats((AudioFile) entry, Enums.PlayerSourceType.LIBRARY,
                    premium, null);
        }
        if (sourceType.equals("playlist") || sourceType.equals("album")) {
            wrapped.updateStats(((Playlist) entry).getTrackByIndex(0),
                    Enums.PlayerSourceType.PLAYLIST, premium, null);
        }
        if (sourceType.equals("podcast")) {
            wrapped.updateStats(((Podcast) entry).getTrackByIndex(0),
                    Enums.PlayerSourceType.PODCAST, premium, ((Podcast) entry).getOwner());
        }
        this.adQueued = false;
        this.adPrice = 0;

        this.type = sourceType;
        this.source = createSource(sourceType, entry, bookmarks);
        this.repeatMode = Enums.RepeatMode.NO_REPEAT;
        this.shuffle = false;
        this.paused = true;
    }

    /**
     * Pause.
     */
    public void pause() {
        paused = !paused;
    }

    /**
     * Shuffle.
     *
     * @param seed the seed
     */
    public void shuffle(final Integer seed) {
        if (seed != null) {
            source.generateShuffleOrder(seed);
        }

        if (source.getType() == Enums.PlayerSourceType.PLAYLIST) {
            shuffle = !shuffle;
            if (shuffle) {
                source.updateShuffleIndex();
            }
        }
    }

    /**
     * Repeat enums . repeat mode.
     *
     * @return the enums . repeat mode
     */
    public Enums.RepeatMode repeat() {
        if (repeatMode == Enums.RepeatMode.NO_REPEAT) {
            if (source.getType() == Enums.PlayerSourceType.LIBRARY) {
                repeatMode = Enums.RepeatMode.REPEAT_ONCE;
            } else {
                repeatMode = Enums.RepeatMode.REPEAT_ALL;
            }
        } else {
            if (repeatMode == Enums.RepeatMode.REPEAT_ONCE) {
                repeatMode = Enums.RepeatMode.REPEAT_INFINITE;
            } else {
                if (repeatMode == Enums.RepeatMode.REPEAT_ALL) {
                    repeatMode = Enums.RepeatMode.REPEAT_CURRENT_SONG;
                } else {
                    repeatMode = Enums.RepeatMode.NO_REPEAT;
                }
            }
        }

        return repeatMode;
    }

    /**
     * Simulate player.
     *
     * @param time the time
     */
    public void simulatePlayer(final int time) {
        int elapsedTime = time;
        if (!paused && connectionStatus) {
            while (elapsedTime >= source.getDuration()) {
                elapsedTime -= source.getDuration();
                next();
                if (paused) {
                    break;
                }
            }
            if (!paused) {
                source.skip(-elapsedTime, wrapped);
            }
        }
    }

    /**
     * Next.
     */
    public void next() {
        if (source.getDuration() == 0) {
            String hostName = null;
            if (source.getType().equals(Enums.PlayerSourceType.PODCAST)) {
                hostName = source.getAudioCollection().getOwner();
            }
            wrapped.updateStats(getCurrentAudioFile(), source.getType(), premium, hostName);
        }
        paused = source.setNextAudioFile(repeatMode, shuffle, wrapped);
        if (repeatMode == Enums.RepeatMode.REPEAT_ONCE) {
            repeatMode = Enums.RepeatMode.NO_REPEAT;
        }

        if (source.getDuration() == 0 && paused) {
            stop();
        }
    }

    /**
     * Prev.
     */
    public void prev() {
        source.setPrevAudioFile(shuffle, wrapped);
        paused = false;
    }

    private void skip(final int duration) {
        source.skip(duration, wrapped);
        paused = false;
    }

    /**
     * Skip next.
     */
    public void skipNext() {
        if (source.getType() == Enums.PlayerSourceType.PODCAST) {
            skip(-skipTime);
        }
    }

    /**
     * Skip prev.
     */
    public void skipPrev() {
        if (source.getType() == Enums.PlayerSourceType.PODCAST) {
            skip(skipTime);
        }
    }

    /**
     * Gets current audio file.
     *
     * @return the current audio file
     */
    public AudioFile getCurrentAudioFile() {
        if (source == null) {
            return null;
        }
        return source.getAudioFile();
    }

    /**
     * Gets paused.
     *
     * @return the paused
     */
    public boolean getPaused() {
        return paused;
    }

    /**
     * Gets shuffle.
     *
     * @return the shuffle
     */
    public boolean getShuffle() {
        return shuffle;
    }

    /**
     * Retrieves the current connection status.
     *
     * @return The current connection status as a boolean.
     */
    public boolean getConnectionStatus() {
        return connectionStatus;
    }

    /**
     * Sets the connection status.
     *
     * @param connectionStatus The boolean value representing the connection status to be set.
     */
    public void setConnectionStatus(final boolean connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    /**
     * Gets stats.
     *
     * @return the stats
     */
    public PlayerStats getStats() {
        String filename = "";
        int duration = 0;
        if (source != null && source.getAudioFile() != null) {
            filename = source.getAudioFile().getName();
            duration = source.getDuration();
        } else {
            stop();
        }

        return new PlayerStats(filename, duration, repeatMode, shuffle, paused);
    }

    /**
     * Set premium status.
     *
     * @param premium premium status.
     */
    public void setPremium(final boolean premium) {
        this.premium = premium;
        if (source != null) {
            source.setPremium(premium);
        }
    }

    /**
     * Queue an ad.
     *
     * @param price the ad price.
     * @return true if add is queued successfully
     */
    public boolean queueAd(final int price) {
        if (this.source != null && !this.source.getAudioFile().getName().equals("Ad Break")) {
            this.adQueued = true;
            this.adPrice = price;
            this.source.setAdQueued(true);
            this.source.setAdPrice(price);
            return true;
        }
        return false;
    }
}
