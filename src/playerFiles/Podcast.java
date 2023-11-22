package playerFiles;

import lombok.Getter;
import lombok.Setter;

import javax.print.attribute.standard.PDLOverrideSupported;
import java.util.ArrayList;

@Setter
@Getter
public class Podcast extends AudioFile {
    private String name;
    private String owner;
    private ArrayList<PodcastEpisode> episodes;

    public Podcast() {}

    public Podcast(String name, String owner, ArrayList<PodcastEpisode> episodes) {
        this.name = name;
        this.owner = owner;
        this.episodes = episodes;
    }

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

    @Override
    public int getFileDuration() {
        int duration = 0;
        for (PodcastEpisode ep : this.episodes) {
            duration += ep.getDuration();
        }
        return duration;
    }

    @Override
    public String toString() {
        return "Podcast{" +
                "name='" + name + '\'' +
                ", owner='" + owner + '\'' +
                ", episodes=" + episodes +
                "}\n";
    }
}
