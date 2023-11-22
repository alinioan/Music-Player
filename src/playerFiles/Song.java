package playerFiles;

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

    public Song() {}

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

    public Song(String name, int duration, String album, ArrayList<String> tags, String lyrics, String genre, int releaseYear, String artist, int totalLikes) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return duration == song.duration && releaseYear == song.releaseYear && totalLikes == song.totalLikes && Objects.equals(name, song.name) && Objects.equals(album, song.album) && Objects.equals(tags, song.tags) && Objects.equals(lyrics, song.lyrics) && Objects.equals(genre, song.genre) && Objects.equals(artist, song.artist);
    }

    @Override
    public int getFileDuration() {
        return this.getDuration();
    }

    @Override
    public String toString() {
        return "Song{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", album='" + album + '\'' +
                ", tags=" + tags +
                ", lyrics='" + lyrics + '\'' +
                ", genre='" + genre + '\'' +
                ", releaseYear=" + releaseYear +
                ", artist='" + artist + '\'' +
                ", totalLikes=" + totalLikes +
                "}\n";
    }
}
