package playerfiles;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class Podcast extends AudioFile {
    private String name;
    private String owner;
    private ArrayList<PodcastEpisode> episodes;

    public Podcast() {

    }

    /**
     * constructor
     * @param name
     * @param owner
     * @param episodes
     */
    public Podcast(final String name, final String owner,
                   final ArrayList<PodcastEpisode> episodes) {
        this.name = name;
        this.owner = owner;
        this.episodes = episodes;
    }

    /**
     * get all the duration of all the episodes before the one given
     * @param currentEp episode in podcast
     * @return duration
     */
    public int getPreviousEpTime(final PodcastEpisode currentEp) {
        int previousSongsTime = 0;
        for (PodcastEpisode ep : this.episodes) {
            if (ep.equals(currentEp)) {
                return previousSongsTime;
            }
            previousSongsTime += ep.getDuration();
        }
        return previousSongsTime;
    }

    /**
     * Deep copy of the AudioFile object
     * @return the deep copy
     */
    @Override
    public AudioFile deepCopy() {
        AudioFile file = super.deepCopy();
        Podcast copy = new Podcast();
        ((AudioFile) copy).setName(file.getName());
        ((AudioFile) copy).setFileType(file.getFileType());
        copy.name = getName();
        copy.owner = getOwner();
        copy.episodes = getEpisodes();
        return copy;
    }

    /**
     * Get duration of playlist
     * @return playlist duration
     */
    @Override
    public int getFileDuration() {
        int duration = 0;
        for (PodcastEpisode ep : this.episodes) {
            duration += ep.getDuration();
        }
        return duration;
    }

    /**
     * To string method
     * @return the object as a string
     */
    @Override
    public String toString() {
        return "Podcast{"
                + "name='" + name + '\''
                + ", owner='" + owner + '\''
                + ", episodes=" + episodes
                + "}\n";
    }
}
