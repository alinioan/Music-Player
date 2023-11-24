package playerfiles;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PodcastEpisode extends AudioFile {
    private String name;
    private int duration;
    private String description;

    public PodcastEpisode() {

    }

    /**
     * constructor
     * @param name
     * @param duration
     * @param description
     */
    public PodcastEpisode(final String name, final int duration, final String description) {
        this.name = name;
        this.duration = duration;
        this.description = description;
    }

    /**
     * Get duration of playlist
     * @return playlist duration
     */
    @Override
    public int getFileDuration() {
        return this.duration;
    }
}
