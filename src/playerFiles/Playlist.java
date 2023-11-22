package playerFiles;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class Playlist extends AudioFile {
    private String name;
    private String visibility;
    private String owner;
    private int id;
    private ArrayList<Song> songs;
    int followers = 0;

    public  Playlist() {}

    public Playlist(String name, String visibility, String owner, int id, ArrayList<Song> songs) {
        this.name = name;
        this.visibility = visibility;
        this.owner = owner;
        this.id = id;
        this.songs = songs;
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
