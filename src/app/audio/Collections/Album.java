package app.audio.Collections;

import app.audio.Files.Song;

import java.util.ArrayList;

public class Album extends Playlist {
    private Integer releaseYear;
    private String description;

    public Album(String name, String owner, Integer releaseYear, String description, ArrayList<Song> songs) {
        super(name, owner);
        this.description = description;
        this.releaseYear = releaseYear;
        super.setSongs(songs);
    }
}
