package playerFiles;

import lombok.Getter;
import lombok.Setter;
import musicPlayer.User;


import java.util.ArrayList;

@Setter
@Getter
public class Library {
    private ArrayList<Song> songs;
    private ArrayList<Podcast> podcasts;
    private ArrayList<User> users;
    private ArrayList<Playlist> playlists = new ArrayList<>();
}
