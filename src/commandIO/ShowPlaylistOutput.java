package commandIO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ShowPlaylistOutput {
    int followers;
    String name;
    ArrayList<String> songs;
    String visibility;

    public ShowPlaylistOutput(int followers, String name, ArrayList<String> songs, String visibility) {
        this.followers = followers;
        this.name = name;
        this.songs = songs;
        this.visibility = visibility;
    }
}
