package app.user.artist;

import app.audio.Collections.Album;
import app.audio.Collections.AlbumOutput;
import app.audio.Files.Song;
import app.user.User;
import app.utils.Enums;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class Artist extends User {
    private ArrayList<Album> albums;
    private ArrayList<Event> events;
    private ArrayList<Merch> merches;

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
        super.setConnectionStatus(false);
        super.setCurrentPage("");
        albums = new ArrayList<>();
        events = new ArrayList<>();
        merches = new ArrayList<>();
    }

    public String addAlbum(final String name, final Integer releaseYear,
                           final String description, final ArrayList<Song> songs) {
        Album album = new Album(name, this.getUsername(), releaseYear, description, songs);
        if (checkAlbumExists(album.getName())) {
            return this.getUsername() + " has another album with the same name.";
        }
        super.getPlaylists().add(album);
        this.albums.add(album);
        return this.getUsername() + " has added new album successfully.";
    }

    private boolean checkAlbumExists(String name) {
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

    public String addEvent(final String name, final String description, final String date) {
        Event newEvent = new Event(name, description, date);
        if (checkEventExists(newEvent.getName())) {
            return this.getUsername() + " has another event with the same name.";
        }
        if (!newEvent.isValidDate()) {
            return  "Event for " + this.getUsername() + " does not have a valid date.";
        }
        this.events.add(newEvent);
        return this.getUsername() + " has added new event successfully.";
    }

    private boolean checkEventExists(String name) {
        for (Event event : events) {
            if (event.getName().equals(name))
                return true;
        }
        return false;
    }

    public String addMerch(final String name, final String description, final Integer price) {
        Merch newMerch = new Merch(name, description, price);
        if (checkMerchExists(newMerch.getName())) {
            return this.getUsername() + " has merchandise with the same name.";
        }
        if (newMerch.getPrice() < 0) {
            return  "Price for merchandise can not be negative.";
        }
        this.merches.add(newMerch);
        return this.getUsername() + " has added new merchandise successfully.";
    }

    private boolean checkMerchExists(String name) {
        for (Merch merch : merches) {
            if (merch.getName().equals(name))
                return true;
        }
        return false;
    }
}
