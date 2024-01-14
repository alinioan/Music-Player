package app;

import app.audio.Collections.AlbumOutput;
import app.audio.Collections.PlaylistOutput;
import app.audio.Collections.PodcastOutput;
import app.notifications.Notification;
import app.page.PageManager;
import app.player.PlayerStats;
import app.searchBar.Filters;
import app.user.Monetization;
import app.user.wrapped.ArtistWrapped;
import app.user.wrapped.HostWrapped;
import app.user.wrapped.UserWrapped;
import app.user.artist.Artist;
import app.user.User;
import app.user.host.Host;
import app.user.wrapped.Wrapped;
import app.utils.Enums;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.CommandInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Command runner.
 */
public final class CommandRunner {
    private static CommandRunner instance;

    /**
     * The Object mapper.
     */
    private static ObjectMapper objectMapper = new ObjectMapper();

    private CommandRunner() {
    }

    /**
     * Get instance.
     *
     * @return the instance.
     */
    public static CommandRunner getInstance() {
        if (instance == null) {
            instance = new CommandRunner();
        }
        return instance;
    }

    /**
     * Search object node.
     *
     * @param commandInput the command input
     * @return the object node
     */
    public ObjectNode search(final CommandInput commandInput) {
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
    public ObjectNode select(final CommandInput commandInput) {
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
    public ObjectNode load(final CommandInput commandInput) {
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
    public ObjectNode playPause(final CommandInput commandInput) {
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
    public ObjectNode repeat(final CommandInput commandInput) {
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
    public ObjectNode shuffle(final CommandInput commandInput) {
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
    public ObjectNode forward(final CommandInput commandInput) {
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
    public ObjectNode backward(final CommandInput commandInput) {
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
    public ObjectNode like(final CommandInput commandInput) {
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
    public ObjectNode next(final CommandInput commandInput) {
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
    public ObjectNode prev(final CommandInput commandInput) {
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
    public ObjectNode createPlaylist(final CommandInput commandInput) {
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
    public ObjectNode addRemoveInPlaylist(final CommandInput commandInput) {
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
    public ObjectNode switchVisibility(final CommandInput commandInput) {
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
    public ObjectNode showPlaylists(final CommandInput commandInput) {

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
    public ObjectNode follow(final CommandInput commandInput) {
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
    public ObjectNode status(final CommandInput commandInput) {
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
    public ObjectNode showLikedSongs(final CommandInput commandInput) {
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
    public ObjectNode getPreferredGenre(final CommandInput commandInput) {
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
    public ObjectNode getTop5Playlists(final CommandInput commandInput) {
        List<String> playlists = Admin.getTop5Playlists();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(playlists));

        return objectNode;
    }

    /**
     * Get top 5 albums.
     *
     * @param commandInput the command input.
     * @return the 5 albums.
     */
    public ObjectNode getTop5Albums(final CommandInput commandInput) {
        List<String> playlists = Admin.getTop5Albums();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(playlists));

        return objectNode;
    }

    /**
     * Get top 5 artists.
     *
     * @param commandInput the command input.
     * @return the 5 artists.
     */
    public ObjectNode getTop5Artists(final CommandInput commandInput) {
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
    public ObjectNode switchConnectionStatus(final CommandInput commandInput) {
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
    public ObjectNode getOnlineUsers(final CommandInput commandInput) {
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
    public ObjectNode addUser(final CommandInput commandInput) {
        String message = Admin.addUser(commandInput.getUsername(), commandInput.getAge(),
                                commandInput.getCity(),
                                Enums.getTypeFromString(commandInput.getType()));

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Delete user.
     *
     * @param commandInput the command input.
     * @return the output message.
     */
    public ObjectNode deleteUser(final CommandInput commandInput) {
        String message = Admin.deleteUser(commandInput.getUsername());

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Add album.
     *
     * @param commandInput the command input.
     * @return the output message.
     */
    public ObjectNode addAlbum(final CommandInput commandInput) {
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

    /**
     * Remove album.
     *
     * @param commandInput the command input.
     * @return the output message.
     */
    public ObjectNode removeAlbum(final CommandInput commandInput) {
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

    /**
     * Show albums.
     *
     * @param commandInput the command input.
     * @return list of albums.
     */
    public ObjectNode showAlbums(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ArrayList<AlbumOutput> albumOutputs = ((Artist) user).showAlbums();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(albumOutputs));

        return objectNode;
    }

    /**
     * Add event.
     *
     * @param commandInput the command input.
     * @return the output message.
     */
    public ObjectNode addEvent(final CommandInput commandInput) {
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
        String message = ((Artist) user).addEvent(commandInput.getName(),
                                                  commandInput.getDescription(),
                                                  commandInput.getDate());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Remove event.
     *
     * @param commandInput the command input.
     * @return the output message.
     */
    public ObjectNode removeEvent(final CommandInput commandInput) {
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

    /**
     * Add merch.
     *
     * @param commandInput the command input.
     * @return the output message.
     */
    public ObjectNode addMerch(final CommandInput commandInput) {
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
        String message = ((Artist) user).addMerch(commandInput.getName(),
                                                  commandInput.getDescription(),
                                                  commandInput.getPrice());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Add podcast.
     *
     * @param commandInput the command input
     * @return the output message
     */
    public ObjectNode addPodcast(final CommandInput commandInput) {
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

    /**
     * Remove podcasts.
     *
     * @param commandInput the command input.
     * @return the output message.
     */
    public ObjectNode removePodcast(final CommandInput commandInput) {
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

    /**
     * Show podcasts.
     *
     * @param commandInput the command input.
     * @return the podcast.
     */
    public ObjectNode showPodcasts(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ArrayList<PodcastOutput> podcastOutputs = ((Host) user).showPodcasts();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(podcastOutputs));

        return objectNode;
    }

    /**
     * Add an announcement.
     *
     * @param commandInput the command input.
     * @return the output message.
     */
    public ObjectNode addAnnouncement(final CommandInput commandInput) {
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
        String message = ((Host) user).addAnnouncement(commandInput.getName(),
                                                       commandInput.getDescription());
        objectNode.put("message", message);

        return objectNode;
    }

    /**
     * Remove announcement
     *
     * @param commandInput the command input.
     * @return the output message.
     */
    public ObjectNode removeAnnouncement(final CommandInput commandInput) {
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

    /**
     * Get all users.
     *
     * @param commandInput the command input.
     * @return list with all the users.
     */
    public ObjectNode getAllUsers(final CommandInput commandInput) {
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
    private static ObjectNode checkUserNull(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = objectMapper.createObjectNode();
        if (user == null) {
            objectNode.put("command", commandInput.getCommand());
            objectNode.put("user", commandInput.getUsername());
            objectNode.put("timestamp", commandInput.getTimestamp());
            objectNode.put("message",
                    "The username " + commandInput.getUsername() + " doesn't exist.");
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
    private static ObjectNode checkUserOffline(final CommandInput commandInput) {
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
    private static ObjectNode checkUser(final CommandInput commandInput) {
        ObjectNode objectNode = checkUserNull(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        objectNode = checkUserOffline(commandInput);
        return objectNode;
    }

    /**
     * Print current page.
     * @param commandInput the command input.
     * @return the page as a string.
     */
    public ObjectNode printCurrentPage(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }
        String message = PageManager.printCurrentPage(user);
        if (message == null) {
            System.out.println(user.getCurrentPage());
        }
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Change page.
     *
     * @param commandInput the command input.
     * @return the message.
     */
    public ObjectNode changePage(final CommandInput commandInput) {
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

    /**
     * Get wrapped for user.
     *
     * @param commandInput the command.
     * @return the wrapped stats.
     */
    public ObjectNode wrapped(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = checkUserNull(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }

        Wrapped wrapped = user.getWrapped();

        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        if (user.getUserType().equals(Enums.UserType.NORMAL)) {
            UserWrapped userWrapped = (UserWrapped) wrapped;
            if ((userWrapped.getTopArtists().isEmpty() && userWrapped.getTopSongs().isEmpty()
                && userWrapped.getTopAlbums().isEmpty() && userWrapped.getTopEpisodes().isEmpty()
                && userWrapped.getTopGenres().isEmpty())) {
                objectNode.put("message",
                            "No data to show for user " + user.getUsername() + ".");
            } else {
                objectNode.put("result", objectMapper.valueToTree(wrapped));
            }
        } else if (user.getUserType().equals(Enums.UserType.ARTIST)) {
            ArtistWrapped artistWrapped = (ArtistWrapped) wrapped;
            if ((artistWrapped.getListeners() == 0 && artistWrapped.getTopSongs().isEmpty()
                && artistWrapped.getTopAlbums().isEmpty()
                && artistWrapped.getTopFans().isEmpty())) {
                objectNode.put("message",
                            "No data to show for artist " + user.getUsername() + ".");
            } else {
                objectNode.put("result", objectMapper.valueToTree(wrapped));
            }
        } else {
            HostWrapped hostWrapped = (HostWrapped) wrapped;
            if ((hostWrapped.getListeners() == 0 && hostWrapped.getTopEpisodes().isEmpty())) {
                objectNode.put("message",
                            "No data to show for host " + user.getUsername() + ".");
            } else {
                objectNode.put("result", objectMapper.valueToTree(wrapped));
            }
        }
        return objectNode;
    }

    /**
     * Buy premium membership.
     *
     * @param commandInput the command.
     * @return the message.
     */
    public ObjectNode buyPremium(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }

        String message = user.buyPremium();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Cancel premium membership.
     *
     * @param commandInput the command.
     * @return the message.
     */
    public ObjectNode cancelPremium(final CommandInput commandInput) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }

        String message = user.cancelPremium();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Ad break.
     *
     * @param commandInput the command.
     * @param nextCommand the next command.
     * @return the message.
     */
    public ObjectNode adBreak(final CommandInput commandInput,
                                     final CommandInput nextCommand) {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }

        String message = user.adBreak(commandInput.getPrice());
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Buy merch.
     *
     * @param commandInput the command.
     * @return the message.
     */
    public ObjectNode buyMerch(final CommandInput commandInput)  {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }

        String message = user.buyMerch(commandInput.getName());
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * See the bought merch.
     *
     * @param commandInput the command.
     * @return the items.
     */
    public ObjectNode seeMerch(final CommandInput commandInput)  {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }

        List<String> merchList = user.getOwnedMerch();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("result", objectMapper.valueToTree(merchList));
        return objectNode;
    }

    /**
     * Subscribe to a creator.
     *
     * @param commandInput the command.
     * @return the message.
     */
    public ObjectNode subscribe(final CommandInput commandInput)  {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }

        String message = user.subscribe();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Get notification list for user.
     *
     * @param commandInput the command.
     * @return the message.
     */
    public ObjectNode getNotifications(final CommandInput commandInput)  {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }

        List<Notification> notificationList = user.getNotifications();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("notifications", objectMapper.valueToTree(notificationList));
        user.getNotifications().clear();
        return objectNode;
    }

    /**
     * Update recommendations.
     * @param commandInput the command.
     * @return the message.
     */
    public ObjectNode updateRecommendations(final CommandInput commandInput)  {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }

        String message = PageManager.updateRecommendations(user,
                                                           commandInput.getRecommendationType());
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Load recommendations.
     *
     * @param commandInput the command.
     * @return the message
     */
    public ObjectNode loadRecommendations(final CommandInput commandInput)  {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }

        String message = user.loadRecommendation();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Switch page to the next page.
     *
     * @param commandInput the command.
     * @return the message.
     */
    public ObjectNode nextPage(final CommandInput commandInput)  {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }

        String message = user.nextPage();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Switch page to the previous page.
     *
     * @param commandInput the command.
     * @return the message.
     */
    public ObjectNode previousPage(final CommandInput commandInput)  {
        User user = Admin.getUser(commandInput.getUsername());
        ObjectNode objectNode = checkUser(commandInput);
        if (!objectNode.isEmpty()) {
            return objectNode;
        }

        String message = user.previousPage();
        objectNode.put("command", commandInput.getCommand());
        objectNode.put("user", commandInput.getUsername());
        objectNode.put("timestamp", commandInput.getTimestamp());
        objectNode.put("message", message);
        return objectNode;
    }

    /**
     * Calculates end program ranking for artists.
     *
     * @return the ranking.
     */
    public ObjectNode endProgram() {

        ObjectNode objectNode = objectMapper.createObjectNode();
        Map<String, Monetization> monetization = Admin.getMonetizationRanking();

        objectNode.put("command", "endProgram");
        objectNode.put("result", objectMapper.valueToTree(monetization));

        return objectNode;
    }
}
