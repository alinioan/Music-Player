package playerFiles;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PodcastEpisode extends AudioFile{
    private String name;
    private int duration;
    private String description;

    public PodcastEpisode() {}

    public PodcastEpisode(String name, int duration, String description) {
        this.name = name;
        this.duration = duration;
        this.description = description;
    }

    @Override
    public int getFileDuration() {
        return this.duration;
    }
}
