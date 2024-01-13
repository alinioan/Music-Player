package app.page.content;

import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import app.page.Visitable;
import app.page.Visitor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class LikedContentPage implements Visitable {
    private ArrayList<Song> songs;
    private ArrayList<Playlist> playlists;

    public LikedContentPage(final List<Song> songs, final List<Playlist> playlists) {
        this.songs = (ArrayList<Song>) songs;
        this.playlists = (ArrayList<Playlist>) playlists;
    }

    @Override
    public String accept(final Visitor visitor) {
        return visitor.visit(this);
    }
}
