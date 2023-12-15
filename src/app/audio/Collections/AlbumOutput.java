package app.audio.Collections;

import app.audio.Files.Song;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public final class AlbumOutput {
    private String name;
    private ArrayList<String> songs = new ArrayList<>();

    public AlbumOutput(final Album album) {
        this.name = album.getName();
        this.songs = new ArrayList<>();
        for (Song song : album.getSongs()) {
            this.songs.add(song.getName());
        }
    }
}
