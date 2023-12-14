package app;

import app.audio.Collections.AlbumOutput;
import app.audio.Collections.PlaylistOutput;
import app.audio.Collections.PodcastOutput;
import app.page.PageManager;
import app.player.PlayerStats;
import app.searchBar.Filters;
import app.user.artist.Artist;
import app.user.User;
import app.user.host.Host;
import app.utils.Enums;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;
import org.checkerframework.checker.units.qual.C;
import org.xml.sax.helpers.AttributesImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Command runner.
 */
public final class CommandRunner {
    /**
     * The Object mapper.
     */
    private static ObjectMapper objectMapper = new ObjectMapper();

    private CommandRunner() {
    }

    /**
     * Search object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode search(final CommandInput commandInput) {
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            objectNode.put("results",  objectMapper.valueToTree(new ArrayList<>()));
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        Filters filters = new Filters(commandInput.getFilters());
        String type = commandInput.getType();

        ArrayList<String> results = user.search(filters, type);
        String message = "Search returned " + results.size() + " results";

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        objectNode.put("results", objectMapper.valueToTree(results));

        return objectNode;
    }

    /**
     * Select object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode select(final CommandInput commandInput) {
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());

        String message = user.select(commandInput.getItemNumber());

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Load object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode load(final CommandInput commandInput) {
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.load();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Play pause object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode playPause(final CommandInput commandInput) {
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.playPause();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Repeat object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode repeat(final CommandInput commandInput) {
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.repeat();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Shuffle object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode shuffle(final CommandInput commandInput) {
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        Integer seed = commandInput.getSeed();
        String message = user.shuffle(seed);

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Forward object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode forward(final CommandInput commandInput) {
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.forward();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Backward object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode backward(final CommandInput commandInput) {
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.backward();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Like object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode like(final CommandInput commandInput) {
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.like();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Next object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode next(final CommandInput commandInput) {
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.next();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Prev object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode prev(final CommandInput commandInput) {
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.prev();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Create playlist object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode createPlaylist(final CommandInput commandInput) {
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.createPlaylist(commandInput.getPlaylistName(),
                                             commandInput.getTimestamp());

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Add remove in playlist object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode addRemoveInPlaylist(final CommandInput commandInput) {
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.addRemoveInPlaylist(commandInput.getPlaylistId());

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Switch visibility object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode switchVisibility(final CommandInput commandInput) {
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.switchPlaylistVisibility(commandInput.getPlaylistId());

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Show playlists object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode showPlaylists(final CommandInput commandInput) {

        User user = Admin.getUser(commandInput.getUsername());
        ArrayList<PlaylistOutput> playlists = user.showPlaylists();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(playlists));

        return objectNode;
    }

    /**
     * Follow object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode follow(final CommandInput commandInput) {
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        String message = user.follow();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Status object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode status(final CommandInput commandInput) {
        ObjectNode objectNode = checkUserNull(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        PlayerStats stats = user.getPlayerStats();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("stats", objectMapper.valueToTree(stats));

        return objectNode;
    }

    /**
     * Show liked songs object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode showLikedSongs(final CommandInput commandInput) {
        ObjectNode objectNode = checkUserNull(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        ArrayList<String> songs = user.showPreferredSongs();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(songs));

        return objectNode;
    }

    /**
     * Gets preferred genre.
     *
     * @param commandInput the command input
     * @return the preferred genre
     */
    public static ObjectNode getPreferredGenre(final CommandInput commandInput) {
        ObjectNode objectNode = checkUserNull(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }

        User user = Admin.getUser(commandInput.getUsername());
        String preferredGenre = user.getPreferredGenre();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(preferredGenre));

        return objectNode;
    }

    /**
     * Gets top 5 songs.
     *
     * @param commandInput the command input
     * @return the top 5 songs
     */
    public static ObjectNode getTop5Songs(final CommandInput commandInput) {
        List<String> songs = Admin.getTop5Songs();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(songs));

        return objectNode;
    }

    /**
     * Gets top 5 playlists.
     *
     * @param commandInput the command input
     * @return the top 5 playlists
     */
    public static ObjectNode getTop5Playlists(final CommandInput commandInput) {
        List<String> playlists = Admin.getTop5Playlists();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(playlists));

        return objectNode;
    }

    public static ObjectNode getTop5Albums(final CommandInput commandInput) {
        List<String> playlists = Admin.getTop5Albums();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(playlists));

        return objectNode;
    }

    public static ObjectNode getTop5Artists(final CommandInput commandInput) {
        List<String> playlists = Admin.getTop5Artists();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(playlists));

        return objectNode;
    }

    /**
     * Switch the connection status.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public static ObjectNode switchConnectionStatus(final CommandInput commandInput) {
        ObjectNode objectNode = checkUserNull(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }

        User user = Admin.getUser(commandInput.getUsername());
        String message = user.switchConnectionStatus();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Get online users.
     *
     * @param commandInput the command input.
     * @return list of online users.
     */
    public static ObjectNode getOnlineUsers(final CommandInput commandInput) {
        List<String> onlineUsers = Admin.getOnlineUsers();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(onlineUsers));

        return objectNode;
    }

    /**
     * Add a new user.
     *
     * @param commandInput the command input.
     * @return the object node.
     */
    public static ObjectNode addUser(final CommandInput commandInput) {
        String message = Admin.addUser(commandInput.getUsername(), commandInput.getAge(),
                                commandInput.getCity(), Enums.getTypeFromString(commandInput.getType()));

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("message", message);
        return objectNode;
    }

    public static ObjectNode deleteUser(final CommandInput commandInput) {
        String message = Admin.deleteUser(commandInput.getUsername());

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("message", message);
        return objectNode;
    }

    public static ObjectNode addAlbum(final CommandInput commandInput) {
        ObjectNode objectNode = checkUserNull(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        String message = Admin.addAlbum(user, commandInput.getName(), commandInput.getReleaseYear(),
                                        commandInput.getDescription(), commandInput.getSongs());

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("message", message);
        return objectNode;
    }

    public static ObjectNode removeAlbum(final CommandInput commandInput) {
        ObjectNode objectNode = checkUserNull(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        String message = Admin.removeAlbum(user, commandInput.getName());

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("message", message);
        return objectNode;
    }

    public static ObjectNode showAlbums(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ArrayList<AlbumOutput> albumOutputs = ((Artist) user).showAlbums();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(albumOutputs));

        return objectNode;
    }

    public static ObjectNode addEvent(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());

        ObjectNode objectNode = checkUserNull(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        if (!user.getUserType().equals(Enums.UserType.ARTIST)) {
            objectNode.put("message", commandInput.getUsername() + " is not an artist.");
            return objectNode;
        }
        String message = ((Artist) user).addEvent(commandInput.getName(), commandInput.getDescription(), commandInput.getDate());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode removeEvent(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());

        ObjectNode objectNode = checkUserNull(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        if (!user.getUserType().equals(Enums.UserType.ARTIST)) {
            objectNode.put("message", commandInput.getUsername() + " is not an artist.");
            return objectNode;
        }
        String message = ((Artist) user).removeEvent(commandInput.getName());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode addMerch(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());

        ObjectNode objectNode = checkUserNull(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        if (!user.getUserType().equals(Enums.UserType.ARTIST)) {
            objectNode.put("message", commandInput.getUsername() + " is not an artist.");
            return objectNode;
        }
        String message = ((Artist) user).addMerch(commandInput.getName(), commandInput.getDescription(), commandInput.getPrice());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode addPodcast(final CommandInput commandInput) {
        ObjectNode objectNode = checkUserNull(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        String message = Admin.addPodcast(user, commandInput.getName(), commandInput.getEpisodes());

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("message", message);
        return objectNode;
    }

    public static ObjectNode removePodcast(final CommandInput commandInput) {
        ObjectNode objectNode = checkUserNull(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        User user = Admin.getUser(commandInput.getUsername());
        String message = Admin.removePodcast(user, commandInput.getName());

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("message", message);
        return objectNode;
    }

    public static ObjectNode showPodcasts(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ArrayList<PodcastOutput> podcastOutputs = ((Host) user).showPodcasts();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(podcastOutputs));

        return objectNode;
    }

    public static ObjectNode addAnnouncement(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());

        ObjectNode objectNode = checkUserNull(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        if (!user.getUserType().equals(Enums.UserType.HOST)) {
            objectNode.put("message", commandInput.getUsername() + " is not a host.");
            return objectNode;
        }
        String message = ((Host) user).addAnnouncement(commandInput.getName(), commandInput.getDescription());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode removeAnnouncement(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());

        ObjectNode objectNode = checkUserNull(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());

        if (!user.getUserType().equals(Enums.UserType.HOST)) {
            objectNode.put("message", commandInput.getUsername() + " is not a host.");
            return objectNode;
        }
        String message = ((Host) user).removeAnnouncement(commandInput.getName());
        objectNode.put("message", message);

        return objectNode;
    }

    public static ObjectNode getAllUsers(final CommandInput commandInput) {
        List<String> allUser = Admin.getAllUsers();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(allUser));

        return objectNode;
    }

    /**
     * Check if the user from the command exists.
     *
     * @param commandInput command input.
     * @return object node with error message if the user doesn't exist or an empty object node.
     */
    static ObjectNode checkUserNull(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (user == null) {
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", "The username " + commandInput.getUsername() + " doesn't exist.");
            return objectNode;
        }
        return objectNode;
    }

    /**
     * Check if the user from the command is offline.
     *
     * @param commandInput command input.
     * @return object node with error message if the user is offline or an empty object node.
     */
    static ObjectNode checkUserOffline(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (!user.isOnline()) {
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message", user.getUsername() + " is offline.");
            return objectNode;
        }
        return objectNode;
    }

    /**
     * Check both if a user exists and if it is offline.
     *
     * @param commandInput command input.
     * @return the object node.
     */
    static ObjectNode checkUser(final CommandInput commandInput) {
        ObjectNode objectNode = checkUserNull(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        objectNode = checkUserOffline(commandInput);
        return objectNode;
    }

    public static ObjectNode printCurrentPage(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        String message = PageManager.printCurrentPage(user);
        if (message == null)
            System.out.println(user.getCurrentPage());
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    public static ObjectNode changePage(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }

        String message = PageManager.changePage(user, commandInput.getNextPage());

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }
}
