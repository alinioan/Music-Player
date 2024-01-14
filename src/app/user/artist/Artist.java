package app.user.artist;

import app.Admin;
import app.audio.Collections.Album;
import app.audio.Collections.AlbumOutput;
import app.audio.Files.Song;
import app.user.User;
import app.user.wrapped.ArtistWrapped;
import app.user.wrapped.UserWrapped;
import app.user.wrapped.Wrapped;
import app.utils.Enums;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Artist extends User {
    private ArrayList<Album> albums;
    private ArrayList<Event> events;
    private ArrayList<Merch> merches;
    @Setter
    private ArtistWrapped artistWrapped;
    private List<User> subscribers;

    /**
     * Instantiates a new User.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public Artist(final String username, final int age, final String city) {
        super(username, age, city);
        super.setUserType(Enums.UserType.ARTIST);
        super.setConnectionStatus(false);
        super.setCurrentPage("");
        albums = new ArrayList<>();
        events = new ArrayList<>();
        merches = new ArrayList<>();
        artistWrapped = new ArtistWrapped();
        subscribers = new ArrayList<>();
        subscribers = new ArrayList<>();
    }

    /**
     * Add a new album.
     *
     * @param name the name.
     * @param releaseYear the year.
     * @param description the description.
     * @param songs the songs.
     * @return output message.
     */
    public String addAlbum(final String name, final Integer releaseYear,
                           final String description, final ArrayList<Song> songs) {
        Album album = new Album(name, this.getUsername(), releaseYear, description, songs);
        if (checkAlbumExists(album.getName())) {
            return this.getUsername() + " has another album with the same name.";
        }
        super.getPlaylists().add(album);
        this.albums.add(album);
        for (User subscriber : subscribers) {
            subscriber.updateNotifications("New Album", "New Album from %s.".formatted(getUsername()));
        }
        return this.getUsername() + " has added new album successfully.";
    }

    /**
     * Check if an album already exists.
     *
     * @param name the name.
     * @return true if it exists.
     */
    private boolean checkAlbumExists(final String name) {
        for (Album album : albums) {
            if (album.getName().equals(name)) {
                return true;
            }
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

    /**
     * Add a new event.
     *
     * @param name the name.
     * @param description the descriptionn.
     * @param date the date.
     * @return output message.
     */
    public String addEvent(final String name, final String description, final String date) {
        Event newEvent = new Event(name, description, date);
        if (checkEventExists(newEvent.getName())) {
            return this.getUsername() + " has another event with the same name.";
        }
        if (!newEvent.isValidDate()) {
            return  "Event for " + this.getUsername() + " does not have a valid date.";
        }
        this.events.add(newEvent);
        for (User subscriber : subscribers) {
            subscriber.updateNotifications("New Event", "New Event from %s.".formatted(getUsername()));
        }
        return this.getUsername() + " has added new event successfully.";
    }

    /**
     * Check if an event with that name already exists.
     *
     * @param name the name.
     * @return true if it exists.
     */
    private boolean checkEventExists(final String name) {
        for (Event event : events) {
            if (event.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove an event.
     *
     * @param name the name.
     * @return output message.
     */
    public String removeEvent(final String name) {
        if (!events.removeIf(event -> event.getName().equals(name))) {
            return this.getUsername() + " doesn't have an event with the given name.";
        }
        return this.getUsername() + " deleted the event successfully.";
    }

    /**
     * Add merch.
     *
     * @param name the name.
     * @param description the description.
     * @param price the price.
     * @return output message.
     */
    public String addMerch(final String name, final String description, final Integer price) {
        Merch newMerch = new Merch(name, description, price);
        if (checkMerchExists(newMerch.getName())) {
            return this.getUsername() + " has merchandise with the same name.";
        }
        if (newMerch.getPrice() < 0) {
            return  "Price for merchandise can not be negative.";
        }
        this.merches.add(newMerch);
        for (User subscriber : subscribers) {
            subscriber.updateNotifications("New Merchandise", "New Merchandise from %s.".formatted(getUsername()));
        }
        return this.getUsername() + " has added new merchandise successfully.";
    }

    /**
     * Check if a merch with that name already exists.
     *
     * @param name the name.
     * @return true if it finds a merch with that name.
     */
    private boolean checkMerchExists(final String name) {
        for (Merch merch : merches) {
            if (merch.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the total likes of the artists songs.
     *
     * @return number of like.
     */
    public Integer getArtistLikes() {
        Integer likes = 0;
        for (Album album : albums) {
            likes += album.getAlbumLikes();
        }
        return likes;
    }

    public Album getAlbum(final String name) {
        for (Album album : albums) {
            if (album.getName().equals(name)) {
                return album;
            }
        }
        return null;
    }

    public ArtistWrapped getTemporaryWrapped() {
        ArtistWrapped temporaryWrapped = new ArtistWrapped();
        for (User user : Admin.getUsers()) {
            UserWrapped userWrapped = user.getPlayer().getWrapped();
            temporaryWrapped.updateStats(userWrapped, this, user.getUsername());
        }
        return temporaryWrapped;
    }

    @Override
    public Wrapped getWrapped() {
        for (User user : Admin.getUsers()) {
            UserWrapped userWrapped = user.getPlayer().getWrapped();
            artistWrapped.updateStats(userWrapped, this, user.getUsername());
        }
        return artistWrapped.getSortedWrapped();
    }
}
