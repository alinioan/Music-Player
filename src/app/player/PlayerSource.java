package app.player;

import app.Admin;
import app.audio.Collections.AudioCollection;
import app.audio.Files.AudioFile;
import app.user.wrapped.UserWrapped;
import app.utils.Enums;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The type Player source.
 */
public class PlayerSource {
    @Getter
    private Enums.PlayerSourceType type;
    @Getter
    private AudioCollection audioCollection;
    @Getter
    private AudioFile audioFile;
    @Getter
    private int index;
    private int indexShuffled;
    private int remainedDuration;
    private final List<Integer> indices = new ArrayList<>();
    @Getter @Setter
    private boolean premium;
    @Getter @Setter
    private boolean adQueued;
    @Setter
    private int adPrice;
    /**
     * Instantiates a new Player source.
     *
     * @param type      the type
     * @param audioFile the audio file
     */
    public PlayerSource(final Enums.PlayerSourceType type, final AudioFile audioFile,
                        final boolean premium, final int adPrice, final boolean adQueued) {
        this.type = type;
        this.audioFile = audioFile;
        this.remainedDuration = audioFile.getDuration();
        this.premium = premium;
        this.adPrice = adPrice;
        this.adQueued = adQueued;
    }

    /**
     * Instantiates a new Player source.
     *
     * @param type            the type
     * @param audioCollection the audio collection
     */
    public PlayerSource(final Enums.PlayerSourceType type, final AudioCollection audioCollection,
                        final boolean premium, final int adPrice, final boolean adQueued) {
        this.type = type;
        this.audioCollection = audioCollection;
        this.audioFile = audioCollection.getTrackByIndex(0);
        this.index = 0;
        this.indexShuffled = 0;
        this.remainedDuration = audioFile.getDuration();
        this.premium = premium;
        this.adPrice = adPrice;
        this.adQueued = adQueued;
    }

    /**
     * Instantiates a new Player source.
     *
     * @param type            the type
     * @param audioCollection the audio collection
     * @param bookmark        the bookmark
     */
    public PlayerSource(final Enums.PlayerSourceType type,
                        final AudioCollection audioCollection,
                        final PodcastBookmark bookmark) {
        this.type = type;
        this.audioCollection = audioCollection;
        this.index = bookmark.getId();
        this.remainedDuration = bookmark.getTimestamp();
        this.audioFile = audioCollection.getTrackByIndex(index);
    }

    /**
     * Gets duration.
     *
     * @return the duration
     */
    public int getDuration() {
        return remainedDuration;
    }

    /**
     * Sets next audio file.
     *
     * @param repeatMode the repeat mode
     * @param shuffle    the shuffle
     * @return the next audio file
     */
    public boolean setNextAudioFile(final Enums.RepeatMode repeatMode,
                                    final boolean shuffle,
                                    final UserWrapped wrapped) {
        boolean isPaused = false;

        if (type == Enums.PlayerSourceType.LIBRARY) {
            if (repeatMode != Enums.RepeatMode.NO_REPEAT) {
                remainedDuration = audioFile.getDuration();
            } else {
                if (adQueued) {
                    setAudioFile(Admin.getSong("Ad Break"));
                    wrapped.calculateAdRevenue(adPrice);
                    remainedDuration = audioFile.getDuration();
                    adQueued = false;
                } else {
                    remainedDuration = 0;
                    isPaused = true;
                }
            }
        } else {
            if (repeatMode == Enums.RepeatMode.REPEAT_ONCE
                || repeatMode == Enums.RepeatMode.REPEAT_CURRENT_SONG
                || repeatMode == Enums.RepeatMode.REPEAT_INFINITE) {
                remainedDuration = audioFile.getDuration();
            } else if (repeatMode == Enums.RepeatMode.NO_REPEAT) {
                if (shuffle) {
                    if (indexShuffled == indices.size() - 1) {
                        remainedDuration = 0;
                        isPaused = true;
                    } else {
                        indexShuffled++;

                        index = indices.get(indexShuffled);
                        updateAudioFile(wrapped);
                        remainedDuration = audioFile.getDuration();
                    }
                } else {
                    if (index == audioCollection.getNumberOfTracks() - 1) {
                        if (adQueued) {
                            setAudioFile(Admin.getSong("Ad Break"));
                            wrapped.calculateAdRevenue(adPrice);
                            remainedDuration = audioFile.getDuration();
                            adQueued = false;
                        } else {
                            remainedDuration = 0;
                            isPaused = true;

                        }
                    } else {
                        index++;
                        updateAudioFile(wrapped);
                        remainedDuration = audioFile.getDuration();
                    }
                }
            } else if (repeatMode == Enums.RepeatMode.REPEAT_ALL) {
                if (shuffle) {
                    indexShuffled = (indexShuffled + 1) % indices.size();
                    index = indices.get(indexShuffled);
                } else {
                    index = (index + 1) % audioCollection.getNumberOfTracks();
                }
                updateAudioFile(wrapped);
                remainedDuration = audioFile.getDuration();
            }
        }

        return isPaused;
    }

    /**
     * Sets prev audio file.
     *
     * @param shuffle the shuffle
     */
    public void setPrevAudioFile(final boolean shuffle, UserWrapped wrapped) {
        if (type == Enums.PlayerSourceType.LIBRARY) {
            remainedDuration = audioFile.getDuration();
        } else {
            if (remainedDuration != audioFile.getDuration()) {
                remainedDuration = audioFile.getDuration();
            } else {
                if (shuffle) {
                    if (indexShuffled > 0) {
                        indexShuffled--;
                    }
                    index = indices.get(indexShuffled);
                    updateAudioFile(wrapped);
                    remainedDuration = audioFile.getDuration();
                } else {
                    if (index > 0) {
                        index--;
                    }
                    updateAudioFile(wrapped);
                    remainedDuration = audioFile.getDuration();
                }
            }
        }
    }

    /**
     * Generate shuffle order.
     *
     * @param seed the seed
     */
    public void generateShuffleOrder(final Integer seed) {
        indices.clear();
        Random random = new Random(seed);
        for (int i = 0; i < audioCollection.getNumberOfTracks(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices, random);
    }

    /**
     * Update shuffle index.
     */
    public void updateShuffleIndex() {
        for (int i = 0; i < indices.size(); i++) {
            if (indices.get(i) == index) {
                indexShuffled = i;
                break;
            }
        }
    }

    /**
     * Skip.
     *
     * @param duration the duration
     */
    public void skip(final int duration, UserWrapped wrapped) {
        remainedDuration += duration;
        if (remainedDuration > audioFile.getDuration()) {
            remainedDuration = 0;
            index++;
            updateAudioFile(wrapped);
        } else if (remainedDuration < 0) {
            remainedDuration = 0;
        }
    }

    private void updateAudioFile(UserWrapped wrapped) {
        if (adQueued) {
            setAudioFile(Admin.getSong("Ad Break"));
            wrapped.calculateAdRevenue(adPrice);
            adQueued = false;
            index--;
        } else {
            setAudioFile(audioCollection.getTrackByIndex(index));
            wrapped.updateStats(audioCollection.getTrackByIndex(index), this.getType(), premium, audioCollection.getOwner());
        }
    }

    /**
     * Sets audio file.
     *
     * @param audioFile the audio file
     */
    public void setAudioFile(final AudioFile audioFile) {
        this.audioFile = audioFile;
    }

}
