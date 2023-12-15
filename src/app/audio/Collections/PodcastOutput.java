package app.audio.Collections;

import app.audio.Files.Episode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class PodcastOutput {
    private String name;
    private ArrayList<String> episodes;

    public PodcastOutput(final Podcast podcast) {
        this.name = podcast.getName();
        this.episodes = new ArrayList<>();
        for (Episode episode : podcast.getEpisodes()) {
            this.episodes.add(episode.getName());
        }
    }
}
