package playerFiles;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class Playlist {
    private String name;
    private String visibility;
    private String owner;
    private int id;
    private ArrayList<Song> songs;
}
