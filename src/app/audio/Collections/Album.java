package app.audio.Collections;

import app.audio.Files.Song;

import java.util.ArrayList;

public class Album extends Playlist {
    private Integer releaseYear;
    private String description;

    public Album(final String name, final String owner, final Integer releaseYear,
                 final String description, final ArrayList<Song> songs) {
        super(name, owner);
        this.description = description;
        this.releaseYear = releaseYear;
        super.setSongs(songs);
    }

    /**
     * Get album likes.
     * Add up all the likes from each song.
     *
     * @return the number of likes.
     */
    public Integer getAlbumLikes() {
        Integer likes = 0;
        for (Song song : this.getSongs()) {
            likes += song.getLikes();
        }
        return likes;
    }

    /**
     * Match description.
     *
     * @param descriptionMatch the description.
     * @return the boolean.
     */
    @Override
    public boolean matchesDescription(final String descriptionMatch) {
        return this.description.toLowerCase().startsWith(descriptionMatch);
    }
}
