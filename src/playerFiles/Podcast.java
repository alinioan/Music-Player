package playerFiles;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class Podcast {
    private String name;
    private String owner;
    private ArrayList<PodcastEpisode> episodes;

    @Override
    public String toString() {
        return "Podcast{" +
                "name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", episodes=" + episodes +
                "}\n";
    }
}
