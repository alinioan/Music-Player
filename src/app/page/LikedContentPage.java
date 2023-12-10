package app.page;

import app.audio.Collections.Playlist;
import app.audio.Files.Song;

import java.util.ArrayList;

public class LikedContentPage implements Visitable{
    ArrayList<Song> songs;
    ArrayList<Playlist> playlists;

    public LikedContentPage(ArrayList<Song> songs, ArrayList<Playlist> playlists) {
        this.songs = songs;
        this.playlists = playlists;
    }

    @Override
    public String accept(Visitor visitor) {
        return null;
    }
}
