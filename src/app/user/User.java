package app.user;

import app.Admin;
import app.audio.Collections.AudioCollection;
import app.audio.Collections.Playlist;
import app.audio.Collections.PlaylistOutput;
import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import app.audio.LibraryEntry;
import app.notifications.Notification;
import app.notifications.NotificationObserver;
import app.page.Invoker;
import app.page.content.ArtistPage;
import app.page.content.HomePage;
import app.page.PageManager;
import app.player.Player;
import app.player.PlayerStats;
import app.searchBar.Filters;
import app.searchBar.SearchBar;
import app.user.artist.Artist;
import app.user.artist.Merch;
import app.user.wrapped.Wrapped;
import app.utils.Enums;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The type User.
 */
public class User implements Comparable<User>, NotificationObserver {
    @Getter
    private String username;
    @Getter
    private int age;
    @Getter
    private String city;
    @Getter
    private ArrayList<Playlist> playlists;
    @Getter
    private ArrayList<Song> likedSongs;
    @Getter
    private ArrayList<Playlist> followedPlaylists;
    @Getter
    private final Player player;
    private final SearchBar searchBar;
    private boolean lastSearched;
    @Setter
    private boolean connectionStatus;
    @Getter @Setter
    private String selectedCreator;
    @Getter @Setter
    private Enums.UserType creatorType;
    @Getter
    private Enums.UserType userType;
    @Getter @Setter
    private String currentPage;
    @Getter @Setter
    private boolean premium;
    @Getter
    private List<String> ownedMerch;
    private List<String> subscribedArtists;
    @Getter
    private List<Notification> notifications;
    @Getter
    private Invoker invoker;

    /**
     * Instantiates a new normal User.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public User(final String username, final int age, final String city) {
        this.username = username;
        this.age = age;
        this.city = city;
        playlists = new ArrayList<>();
        likedSongs = new ArrayList<>();
        followedPlaylists = new ArrayList<>();
        premium = false;
        player = new Player(premium);
        searchBar = new SearchBar(username);
        lastSearched = false;
        this.connectionStatus = true;
        this.userType = Enums.UserType.NORMAL;
        this.currentPage = "Home";
        this.ownedMerch = new ArrayList<>();
        this.subscribedArtists = new ArrayList<>();
        this.notifications = new ArrayList<>();
        this.invoker = new Invoker();
    }

    /**
     * Search array list.
     *
     * @param filters the filters
     * @param type    the type
     * @return the array list
     */
    public ArrayList<String> search(final Filters filters, final String type) {
        searchBar.clearSelection();
        player.stop();

        lastSearched = true;
        ArrayList<String> results = new ArrayList<>();
        if (!type.equals("artist") && !type.equals("host")) {
            List<LibraryEntry> libraryEntries = searchBar.searchLibrary(filters, type);
            for (LibraryEntry libraryEntry : libraryEntries) {
                results.add(libraryEntry.getName());
            }
            return results;
        }
        List<User> userEntries = searchBar.searchUsers(filters, type);
        for (User user : userEntries) {
            results.add(user.getUsername());
        }
        return results;
    }

    /**
     * Select string.
     *
     * @param itemNumber the item number
     * @return the string
     */
    public String select(final int itemNumber) {
        if (!lastSearched) {
            return "Please conduct a search before making a selection.";
        }

        lastSearched = false;

        if (!searchBar.getLastSearchType().equals("artist")
            && !searchBar.getLastSearchType().equals("host")) {
            LibraryEntry selected = searchBar.select(itemNumber);

            if (selected == null) {
                return "The selected ID is too high.";
            }
            return "Successfully selected %s.".formatted(selected.getName());
        }
        User selected = searchBar.selectUser(itemNumber);

        if (selected == null) {
            return "The selected ID is too high.";
        }
        this.creatorType = selected.getUserType();
        this.selectedCreator = selected.getUsername();
        return "Successfully selected " + this.selectedCreator + "'s page.";
    }

    /**
     * Load string.
     *
     * @return the string
     */
    public String load() {
        if (searchBar.getLastSelected() == null) {
            return "Please select a source before attempting to load.";
        }

        if (!searchBar.getLastSearchType().equals("song")
            && ((AudioCollection) searchBar.getLastSelected()).getNumberOfTracks() == 0) {
            return "You can't load an empty audio collection!";
        }

        player.setSource(searchBar.getLastSelected(), searchBar.getLastSearchType());
        searchBar.clearSelection();

        player.pause();

        return "Playback loaded successfully.";
    }

    /**
     * Play pause string.
     *
     * @return the string
     */
    public String playPause() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before attempting to pause or resume playback.";
        }

        player.pause();

        if (player.getPaused()) {
            return "Playback paused successfully.";
        } else {
            return "Playback resumed successfully.";
        }
    }

    /**
     * Repeat string.
     *
     * @return the string
     */
    public String repeat() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before setting the repeat status.";
        }

        Enums.RepeatMode repeatMode = player.repeat();
        String repeatStatus = "";

        switch (repeatMode) {
            case NO_REPEAT -> {
                repeatStatus = "no repeat";
            }
            case REPEAT_ONCE -> {
                repeatStatus = "repeat once";
            }
            case REPEAT_ALL -> {
                repeatStatus = "repeat all";
            }
            case REPEAT_INFINITE -> {
                repeatStatus = "repeat infinite";
            }
            case REPEAT_CURRENT_SONG -> {
                repeatStatus = "repeat current song";
            }
            default -> {
                repeatStatus = "";
            }
        }

        return "Repeat mode changed to %s.".formatted(repeatStatus);
    }

    /**
     * Shuffle string.
     *
     * @param seed the seed
     * @return the string
     */
    public String shuffle(final Integer seed) {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before using the shuffle function.";
        }

        if (!player.getType().equals("playlist") && !player.getType().equals("album")) {
            return "The loaded source is not a playlist or an album.";
        }

        player.shuffle(seed);

        if (player.getShuffle()) {
            return "Shuffle function activated successfully.";
        }
        return "Shuffle function deactivated successfully.";
    }

    /**
     * Forward string.
     *
     * @return the string
     */
    public String forward() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before attempting to forward.";
        }

        if (!player.getType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }

        player.skipNext();

        return "Skipped forward successfully.";
    }

    /**
     * Backward string.
     *
     * @return the string
     */
    public String backward() {
        if (player.getCurrentAudioFile() == null) {
            return "Please select a source before rewinding.";
        }

        if (!player.getType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }

        player.skipPrev();

        return "Rewound successfully.";
    }

    /**
     * Like string.
     *
     * @return the string
     */
    public String like() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before liking or unliking.";
        }

        if (!player.getType().equals("song") && !player.getType().equals("playlist")
            && !player.getType().equals("album")) {
            return "Loaded source is not a song.";
        }

        Song song = (Song) player.getCurrentAudioFile();

        if (likedSongs.contains(song)) {
            likedSongs.remove(song);
            song.dislike();

            return "Unlike registered successfully.";
        }

        likedSongs.add(song);
        song.like();
        return "Like registered successfully.";
    }

    /**
     * Next string.
     *
     * @return the string
     */
    public String next() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before skipping to the next track.";
        }

        player.next();

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before skipping to the next track.";
        }

        return "Skipped to next track successfully. The current track is %s."
                .formatted(player.getCurrentAudioFile().getName());
    }

    /**
     * Prev string.
     *
     * @return the string
     */
    public String prev() {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before returning to the previous track.";
        }

        player.prev();

        return "Returned to previous track successfully. The current track is %s."
                .formatted(player.getCurrentAudioFile().getName());
    }

    /**
     * Create playlist string.
     *
     * @param name      the name
     * @param timestamp the timestamp
     * @return the string
     */
    public String createPlaylist(final String name, final int timestamp) {
        if (playlists.stream().anyMatch(playlist -> playlist.getName().equals(name))) {
            return "A playlist with the same name already exists.";
        }

        playlists.add(new Playlist(name, username, timestamp));

        return "Playlist created successfully.";
    }

    /**
     * Add remove in playlist string.
     *
     * @param id the id
     * @return the string
     */
    public String addRemoveInPlaylist(final int id) {
        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before adding to or removing from the playlist.";
        }

        if (player.getType().equals("podcast")) {
            return "The loaded source is not a song.";
        }

        if (id > playlists.size()) {
            return "The specified playlist does not exist.";
        }

        Playlist playlist = playlists.get(id - 1);

        if (playlist.containsSong((Song) player.getCurrentAudioFile())) {
            playlist.removeSong((Song) player.getCurrentAudioFile());
            return "Successfully removed from playlist.";
        }

        playlist.addSong((Song) player.getCurrentAudioFile());
        return "Successfully added to playlist.";
    }

    /**
     * Switch playlist visibility string.
     *
     * @param playlistId the playlist id
     * @return the string
     */
    public String switchPlaylistVisibility(final Integer playlistId) {
        if (playlistId > playlists.size()) {
            return "The specified playlist ID is too high.";
        }

        Playlist playlist = playlists.get(playlistId - 1);
        playlist.switchVisibility();

        if (playlist.getVisibility() == Enums.Visibility.PUBLIC) {
            return "Visibility status updated successfully to public.";
        }

        return "Visibility status updated successfully to private.";
    }

    /**
     * Show playlists array list.
     *
     * @return the array list
     */
    public ArrayList<PlaylistOutput> showPlaylists() {
        ArrayList<PlaylistOutput> playlistOutputs = new ArrayList<>();
        for (Playlist playlist : playlists) {
            playlistOutputs.add(new PlaylistOutput(playlist));
        }

        return playlistOutputs;
    }

    /**
     * Follow string.
     *
     * @return the string
     */
    public String follow() {
        LibraryEntry selection = searchBar.getLastSelected();
        String type = searchBar.getLastSearchType();

        if (selection == null) {
            return "Please select a source before following or unfollowing.";
        }

        if (!type.equals("playlist")) {
            return "The selected source is not a playlist.";
        }

        Playlist playlist = (Playlist) selection;

        if (playlist.getOwner().equals(username)) {
            return "You cannot follow or unfollow your own playlist.";
        }

        if (followedPlaylists.contains(playlist)) {
            followedPlaylists.remove(playlist);
            playlist.decreaseFollowers();

            return "Playlist unfollowed successfully.";
        }

        followedPlaylists.add(playlist);
        playlist.increaseFollowers();

//        Admin.getUser(playlist.getOwner()).updateNotifications("Playlys");

        return "Playlist followed successfully.";
    }

    /**
     * Gets player stats.
     *
     * @return the player stats
     */
    public PlayerStats getPlayerStats() {
        return player.getStats();
    }

    /**
     * Show preferred songs array list.
     *
     * @return the array list
     */
    public ArrayList<String> showPreferredSongs() {
        ArrayList<String> results = new ArrayList<>();
        for (AudioFile audioFile : likedSongs) {
            results.add(audioFile.getName());
        }

        return results;
    }

    /**
     * Gets preferred genre.
     *
     * @return the preferred genre
     */
    public String getPreferredGenre() {
        String[] genres = {"pop", "rock", "rap"};
        int[] counts = new int[genres.length];
        int mostLikedIndex = -1;
        int mostLikedCount = 0;

        for (Song song : likedSongs) {
            for (int i = 0; i < genres.length; i++) {
                if (song.getGenre().equals(genres[i])) {
                    counts[i]++;
                    if (counts[i] > mostLikedCount) {
                        mostLikedCount = counts[i];
                        mostLikedIndex = i;
                    }
                    break;
                }
            }
        }

        String preferredGenre = mostLikedIndex != -1 ? genres[mostLikedIndex] : "unknown";
        return "This user's preferred genre is %s.".formatted(preferredGenre);
    }

    /**
     * Simulate time.
     *
     * @param time the time
     */
    public void simulateTime(final int time) {
        player.simulatePlayer(time);
    }

    /**
     * Get connection status.
     * true - user is online.
     * false - user is offline.
     *
     * @return the connection Status
     */
    public boolean isOnline() {
        return connectionStatus;
    }

    /**
     * Switch the connection status.
     *
     * @return the output message.
     */
    public String switchConnectionStatus() {
        if (userType != Enums.UserType.NORMAL) {
            return username + " is not a normal user.";
        }
        connectionStatus = !connectionStatus;
        player.setConnectionStatus(connectionStatus);
        return username + " has changed status successfully.";
    }

    public Wrapped getWrapped() {
        return player.getWrapped().getSortedWrapped();
    }

    public String buyPremium() {
        if (isPremium()) {
            return username + " is already a premium user.";
        }
        premium = true;
        player.setPremium(premium);
        return username + " bought the subscription successfully.";
    }

    public String cancelPremium() {
        if (!isPremium()) {
            return username + " is not a premium user.";
        }
        player.getWrapped().calculatePremiumRevenue();
        premium = false;
        player.setPremium(premium);
        return username + " cancelled the subscription successfully.";
    }

    public String adBreak(final int price, final int timestamp,
                          final int nextTimestamp, final boolean isLoad) {
        if (player.queueAd(price, timestamp, nextTimestamp, isLoad)) {
            return "Ad inserted successfully.";
        }
        return username + " is not playing any music.";
    }

    public String buyMerch(final String merchName) {
        ArtistPage artistPage = null;
        if (selectedCreator != null && Admin.getUser(selectedCreator).getUserType() == Enums.UserType.ARTIST) {
            artistPage = PageManager.getArtistPageHashMap().get(selectedCreator);
        } else {
            return "Cannot buy merch from this page.";
        }

        Merch merch = artistPage.getMerch(merchName);
        if (merch != null) {
            Artist artist = (Artist) Admin.getUser(selectedCreator);
            artist.getArtistWrapped()
                  .setMerchRevenue(artist.getArtistWrapped().getMerchRevenue() + merch.getPrice());
            ownedMerch.add(merchName);
        } else {
            return "The merch %s doesn't exist.".formatted(merchName);
        }

        return username + " has added new merch successfully.";
    }

    public String subscribe() {
        if (selectedCreator == null) {
            return "To subscribe you need to be on the page of an artist or host.";
        }

        User creator = Admin.getUser(selectedCreator);
        if (subscribedArtists.contains(selectedCreator)) {
            subscribedArtists.remove(selectedCreator);
            if (creator.getUserType().equals(Enums.UserType.ARTIST)) {
                ((Artist) creator).getSubscribers().remove(this);
            }
            return username + " unsubscribed from %s successfully.".formatted(selectedCreator);
        } else {
            subscribedArtists.add(selectedCreator);
            if (creator.getUserType().equals(Enums.UserType.ARTIST)) {
                ((Artist) creator).getSubscribers().add(this);
            }
            return username + " subscribed to %s successfully.".formatted(selectedCreator);
        }
    }

    public String loadRecommendation() {
        HomePage homePage = PageManager.getHomePageHashMap().get(username);
        if (homePage.getSongRecommendations().isEmpty()
                && homePage.getPlaylistRecommendations().isEmpty()) {
            return "No recommendations available.";
        }
        Filters filters = new Filters(homePage.getLastRecommendation());
        search(filters, homePage.getLastRecommendationType());
        select(1);
        load();
        return "Playback loaded successfully.";
    }

    public String nextPage() {
        if (invoker.getUndoStack().isEmpty()) {
            return "There are no pages left to go forward.";
        }
        invoker.redo();
        return "The user %s has navigated successfully to the next page.".formatted(username);
    }

    public String previousPage() {
        if (invoker.getCommandStack().isEmpty()) {
            return "There are no pages left to go back.";
        }
        invoker.undo();
        return "The user %s has navigated successfully to the previous page.".formatted(username);
    }

    /**
     * Sets the user type.
     *
     * @param userType The Enums.UserType to be set as the user type.
     */
    public void setUserType(final Enums.UserType userType) {
        this.userType = userType;
    }

    /**
     * Compares users by type.
     *
     * @param o the object to be compared.
     * @return the compare result
     */
    @Override
    public int compareTo(final User o) {
        return userType.compareTo(o.getUserType());
    }

    @Override
    public void updateNotifications(String name, String description) {
        Notification notification = new Notification(name, description);
        notifications.add(notification);
    }
}
