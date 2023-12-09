package app.user;

import app.audio.Collections.Album;
import app.audio.Collections.AlbumOutput;
import app.audio.Files.Song;
import app.utils.Enums;
import lombok.Getter;

import java.util.ArrayList;

public class Artist extends User {
    @Getter
    private ArrayList<Album> albums = new ArrayList<>();
    /**
     * Instantiates a new User.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public Artist(String username, int age, String city) {
        super(username, age, city);
        super.setUserType(Enums.UserType.ARTIST);
        albums = new ArrayList<>();
    }

    public String addAlbum(final String name, final Integer releaseYear,
                           final String description, final ArrayList<Song> songs) {
        Album album = new Album(name, this.getUsername(), releaseYear, description, songs);
        if (checkAlbumExists(album.getName())) {
            return this.getUsername() + " has another album with the same name.";
        }
        this.albums.add(album);
        return this.getUsername() + " has added new album successfully.";
    }

    boolean checkAlbumExists(String name) {
        for (Album album : albums) {
            if (album.getName().equals(name))
                return true;
        }
        return false;
    }

    /**
     * Show album array list.
     *
     * @return the array list
     */
    public ArrayList<AlbumOutput> showAlbums() {
        ArrayList<AlbumOutput> albumOutputs = new ArrayList<>();
        for (Album album : this.albums) {
            albumOutputs.add(new AlbumOutput(album));
        }

        return albumOutputs;
    }
}
