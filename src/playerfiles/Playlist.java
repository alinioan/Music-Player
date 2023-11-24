package playerfiles;

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
    private int followers = 0;

    public  Playlist() {

    }

    /**
     * constructor
     * @param name
     * @param visibility
     * @param owner
     * @param id
     * @param songs
     */
    public Playlist(final String name, final String visibility, final String owner,
                    final int id, final ArrayList<Song> songs) {
        this.name = name;
        this.visibility = visibility;
        this.owner = owner;
        this.id = id;
        this.songs = songs;
        this.mainSongs = songs;
    }

    /**
     * get all the duration of all the songs before the one given
     * @param currentSong song in the playlist
     * @param list in which ArrayList should this duration be calculated.
     *             Default looks in the main array
     * @return duration
     */
    public int getPreviousSongsTime(final Song currentSong, final String list) {
        ArrayList<Song> crtSongs = this.getMainSongs();
        switch (list) {
            case "songs":
                crtSongs = this.getSongs();
                break;
            case "shuffled":
                crtSongs = this.getShuffled();
                break;
            default:
                break;
        }
        int previousSongsTime = 0;
        for (Song song : crtSongs) {
            if (song.equals(currentSong)) {
                return previousSongsTime;
            }
            previousSongsTime += song.getDuration();
        }
        return previousSongsTime;
    }

    /**
     * generate shuffled list
     * @param seed seed for random numbers
     */
    public void generateShuffled(final int seed) {
        Random rand = new Random(seed);
        this.shuffled.addAll(this.songs);
        Collections.shuffle(this.shuffled, rand);
    }

    /**
     * Deep copy of the AudioFile object
     * @return the deep copy
     */
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

    /**
     * Get duration of playlist
     * @return playlist duration
     */
    @Override
    public int getFileDuration() {
        int duration = 0;
        for (Song song : this.songs) {
            duration += song.getDuration();
        }
        return duration;
    }

    /**
     * To string method
     * @return the object as a string
     */
    @Override
    public String toString() {
        return "Playlist{"
                + "name='" + name + '\''
                + ", visibility='" + visibility + '\''
                + ", owner='" + owner + '\''
                + ", id=" + id
                + ", songs=" + songs
                + '}';
    }
}
