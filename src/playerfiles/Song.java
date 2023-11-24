package playerfiles;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Objects;

@Getter
@Setter
public class Song extends AudioFile {
    private String name;
    private int duration;
    private String album;
    private ArrayList<String> tags;
    private String lyrics;
    private String genre;
    private int releaseYear;
    private String artist;
    private int totalLikes = 0;

    public Song() {

    }

    /**
     * Deep copy of the AudioFile object
     * @return the deep copy
     */
    @Override
    public AudioFile deepCopy() {
        AudioFile file = super.deepCopy();
        Song copy = new Song();
        ((AudioFile) copy).setName(file.getName());
        ((AudioFile) copy).setFileType(file.getFileType());
        copy.name = getName();
        copy.duration = getDuration();
        copy.album = getAlbum();
        copy.tags = getTags();
        copy.lyrics = getLyrics();
        copy.genre = getGenre();
        copy.releaseYear = getReleaseYear();
        copy.artist = getArtist();
        copy.totalLikes = getTotalLikes();
        return copy;
    }

    /**
     * constructor
     * @param name
     * @param duration
     * @param album
     * @param tags
     * @param lyrics
     * @param genre
     * @param releaseYear
     * @param artist
     * @param totalLikes
     */
    public Song(final String name, final int duration, final String album,
                final ArrayList<String> tags, final String lyrics,
                final String genre, final int releaseYear, final String artist,
                final int totalLikes) {
        this.name = name;
        this.duration = duration;
        this.album = album;
        this.tags = tags;
        this.lyrics = lyrics;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.artist = artist;
        this.totalLikes = totalLikes;
    }

    /**
     * equals
     * @param o
     * @return true if it equals, false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Song song = (Song) o;
        return duration == song.duration && releaseYear == song.releaseYear
                && totalLikes == song.totalLikes && Objects.equals(name, song.name)
                && Objects.equals(album, song.album) && Objects.equals(tags, song.tags)
                && Objects.equals(lyrics, song.lyrics) && Objects.equals(genre, song.genre)
                && Objects.equals(artist, song.artist);
    }

    /**
     * hashcode
     * @return the hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, duration, album, tags, lyrics,
                genre, releaseYear, artist, totalLikes);
    }

    /**
     * Get duration of song
     * @return playlist duration
     */
    @Override
    public int getFileDuration() {
        return this.getDuration();
    }

    /**
     * to string
     * @return the object as string
     */
    @Override
    public String toString() {
        return "Song{"
                + "name='" + name + '\''
                + ", duration=" + duration
                + ", album='" + album + '\''
                + ", tags=" + tags
                + ", lyrics='" + lyrics + '\''
                + ", genre='" + genre + '\''
                + ", releaseYear=" + releaseYear
                + ", artist='" + artist + '\''
                + ", totalLikes=" + totalLikes
                + "}\n";
    }
}
