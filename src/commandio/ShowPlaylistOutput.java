package commandio;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ShowPlaylistOutput {
    private int followers;
    private String name;
    private ArrayList<String> songs;
    private String visibility;

    public ShowPlaylistOutput(final int followers, final String name,
                              final ArrayList<String> songs, final String visibility) {
        this.followers = followers;
        this.name = name;
        this.songs = songs;
        this.visibility = visibility;
    }
}
