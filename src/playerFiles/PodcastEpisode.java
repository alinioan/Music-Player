package playerFiles;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PodcastEpisode extends AudioFile{
    private String name;
    private int duration;
    private String description;
}
