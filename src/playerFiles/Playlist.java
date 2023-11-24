package playerFiles;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@Setter
@Getter
public class Playlist extends AudioFile {
    private String name;
    private String visibility;
    private String owner;
    private int id;
    private ArrayList<Song> mainSongs;
    private ArrayList<Song> songs;
    private ArrayList<Song> shuffled = new ArrayList<>();
    int followers = 0;

    public  Playlist() {}

    public Playlist(String name, String visibility, String owner, int id, ArrayList<Song> songs) {
        this.name = name;
        this.visibility = visibility;
        this.owner = owner;
        this.id = id;
        this.songs = songs;
        this.mainSongs = songs;
    }

    public int getPreviousSongsTime(Song currentSong, String list) {
        ArrayList<Song> songs = this.getMainSongs();
        switch (list) {
            case "songs":
                songs = this.getSongs();
                break;
            case "shuffled":
                songs = this.getShuffled();
                break;
            default:
                break;
        }
        int previousSongsTime = 0;
        for (Song song : songs) {
            if (song.equals(currentSong))
                return previousSongsTime;
            previousSongsTime += song.getDuration();
        }
        return previousSongsTime;
    }

    public void generateShuffled(int seed) {
        Random rand = new Random(seed);
        this.shuffled.addAll(this.songs);
        Collections.shuffle(this.shuffled, rand);
    }

    @Override
    public AudioFile deepCopy() {
        AudioFile file = super.deepCopy();
        Playlist copy = new Playlist();
        ((AudioFile) copy).setName(file.getName());
        ((AudioFile) copy).setFileType(file.getFileType());
        copy.name = getName();
        copy.visibility = getVisibility();
        copy.owner = getOwner();
        copy.id = getId();
        copy.songs = getSongs();
        copy.mainSongs = getMainSongs();
        copy.shuffled = getShuffled();
        return copy;
    }

    @Override
    public int getFileDuration() {
        int duration = 0;
        for (Song song : this.songs) {
            duration += song.getDuration();
        }
        return duration;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "name='" + name + '\'' +
                ", visibility='" + visibility + '\'' +
                ", owner='" + owner + '\'' +
                ", id=" + id +
                ", songs=" + songs +
                '}';
    }
}
