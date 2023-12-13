package app.page;

import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.List;

@Getter
public class LikedContentPage implements Visitable{
    private ArrayList<Song> songs;
    private ArrayList<Playlist> playlists;

    public LikedContentPage(List<Song> songs, List<Playlist> playlists) {
        this.songs = (ArrayList<Song>) songs;
        this.playlists = (ArrayList<Playlist>) playlists;
    }

    @Override
    public String accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
