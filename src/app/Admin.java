package app;

import app.audio.Collections.Album;
import app.audio.Collections.Playlist;
import app.audio.Collections.Podcast;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.page.PageManager;
import app.user.artist.Artist;
import app.user.host.Host;
import app.user.User;
import app.utils.Enums;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;

/**
 * The type Admin.
 */
public final class Admin {
    @Getter
    private static List<User> users = new ArrayList<>();
    private static List<Song> songs = new ArrayList<>();
    private static List<Podcast> podcasts = new ArrayList<>();
    private static int timestamp = 0;
    private static final int LIMIT = 5;

    private Admin() {
    }

    /**
     * Sets users.
     *
     * @param userInputList the user input list
     */
    public static void setUsers(final List<UserInput> userInputList) {
        users = new ArrayList<>();
        for (UserInput userInput : userInputList) {
            users.add(new User(userInput.getUsername(), userInput.getAge(), userInput.getCity()));
        }
    }

    /**
     * Sets songs.
     *
     * @param songInputList the song input list
     */
    public static void setSongs(final List<SongInput> songInputList) {
        songs = new ArrayList<>();
        for (SongInput songInput : songInputList) {
            songs.add(new Song(songInput.getName(), songInput.getDuration(), songInput.getAlbum(),
                    songInput.getTags(), songInput.getLyrics(), songInput.getGenre(),
                    songInput.getReleaseYear(), songInput.getArtist()));
        }
    }

    /**
     * Sets podcasts.
     *
     * @param podcastInputList the podcast input list
     */
    public static void setPodcasts(final List<PodcastInput> podcastInputList) {
        podcasts = new ArrayList<>();
        for (PodcastInput podcastInput : podcastInputList) {
            List<Episode> episodes = new ArrayList<>();
            for (EpisodeInput episodeInput : podcastInput.getEpisodes()) {
                episodes.add(new Episode(episodeInput.getName(),
                                         episodeInput.getDuration(),
                                         episodeInput.getDescription()));
            }
            podcasts.add(new Podcast(podcastInput.getName(), podcastInput.getOwner(), episodes));
        }
    }

    /**
     * Gets songs.
     *
     * @return the songs
     */
    public static List<Song> getSongs() {
        return new ArrayList<>(songs);
    }

    /**
     * Gets podcasts.
     *
     * @return the podcasts
     */
    public static List<Podcast> getPodcasts() {
        return new ArrayList<>(podcasts);
    }

    /**
     * Gets playlists.
     *
     * @return the playlists
     */
    public static List<Playlist> getPlaylists() {
        List<Playlist> playlists = new ArrayList<>();
        for (User user : users) {
            if (user.getUserType().equals(Enums.UserType.NORMAL)) {
                playlists.addAll(user.getPlaylists());
            }
        }
        return playlists;
    }

    /**
     * Get albums.
     *
     * @return the list of albums.
     */
    public static List<Album> getAlbums() {
        List<Album> albums = new ArrayList<>();
        for (User user : users) {
            if (user.getUserType().equals(Enums.UserType.ARTIST)) {
                albums.addAll(((Artist) user).getAlbums());
            }
        }
        return albums;
    }

    /**
     * Gets user.
     *
     * @param username the username
     * @return the user
     */
    public static User getUser(final String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Update timestamp.
     *
     * @param newTimestamp the new timestamp
     */
    public static void updateTimestamp(final int newTimestamp) {
        for (User user : users) {
            PageManager.updatePages(user);
        }
        int elapsed = newTimestamp - timestamp;
        timestamp = newTimestamp;
        if (elapsed == 0) {
            return;
        }

        for (User user : users) {
            user.simulateTime(elapsed);
            PageManager.updatePages(user);
        }
    }

    /**
     * Gets top 5 songs.
     *
     * @return the top 5 songs
     */
    public static List<String> getTop5Songs() {
        List<Song> sortedSongs = new ArrayList<>(songs);
        sortedSongs.sort(Comparator.comparingInt(Song::getLikes).reversed());
        List<String> topSongs = new ArrayList<>();
        int count = 0;
        for (Song song : sortedSongs) {
            if (count >= LIMIT) {
                break;
            }
            topSongs.add(song.getName());
            count++;
        }
        return topSongs;
    }

    /**
     * Gets top 5 playlists.
     *
     * @return the top 5 playlists
     */
    public static List<String> getTop5Playlists() {
        List<Playlist> sortedPlaylists = new ArrayList<>(getPlaylists());
        sortedPlaylists.sort(Comparator.comparingInt(Playlist::getFollowers)
                .reversed()
                .thenComparing(Playlist::getTimestamp, Comparator.naturalOrder()));
        List<String> topPlaylists = new ArrayList<>();
        int count = 0;
        for (Playlist playlist : sortedPlaylists) {
            if (count >= LIMIT) {
                break;
            }
            topPlaylists.add(playlist.getName());
            count++;
        }
        return topPlaylists;
    }

    /**
     * Get top 5 albums.
     *
     * @return the top 5 albums.
     */
    public static List<String> getTop5Albums() {
        List<Album> sortedAlbums = new ArrayList<>(getAlbums());
        sortedAlbums.sort(Comparator.comparingInt(Album::getAlbumLikes)
                .reversed()
                .thenComparing(Album::getName, Comparator.naturalOrder()));
        List<String> topAlbums = new ArrayList<>();
        int count = 0;
        for (Album album : sortedAlbums) {
            if (count >= LIMIT) {
                break;
            }
            topAlbums.add(album.getName());
            count++;
        }
        return topAlbums;
    }

    /**
     * Get top 5 artist.
     *
     * @return the top 5 artists.
     */
    public static List<String> getTop5Artists() {
        List<Artist> sortedArtists = new ArrayList<>();

        for (User user : users) {
            if (user.getUserType().equals(Enums.UserType.ARTIST)) {
                sortedArtists.add((Artist) user);
            }
        }
        sortedArtists.sort(Comparator.comparingInt(Artist::getArtistLikes)
                .reversed()
                .thenComparing(Artist::getUsername, Comparator.naturalOrder()));
        List<String> topArtists = new ArrayList<>();
        int count = 0;
        for (Artist artist : sortedArtists) {
            if (count >= LIMIT) {
                break;
            }
            topArtists.add(artist.getUsername());
            count++;
        }
        return topArtists;
    }

    /**
     * Get online users.
     *
     * @return the users.
     */
    public static List<String> getOnlineUsers() {
        List<String> onlineUsers = new ArrayList<>();
        for (User user : users) {
            if (user.isOnline() && user.getUserType().equals(Enums.UserType.NORMAL)) {
                onlineUsers.add(user.getUsername());
            }
        }
        return onlineUsers;
    }

    /**
     * Get all users.
     *
     * @return list of users.
     */
    public static List<String> getAllUsers() {
        Collections.sort(users);
        List<String> allUsers = new ArrayList<>();
        for (User user : users) {
            allUsers.add(user.getUsername());
        }
        return allUsers;
    }

    /**
     * Add a new user.
     *
     * @param name the username.
     * @param age the age.
     * @param city the city.
     * @param type the type fo the user.
     * @return output message.
     */
    public static String addUser(final String name, final Integer age,
                                 final String city, final Enums.UserType type) {
        User newUser = new User(name, age, city);
        switch (type) {
            case ARTIST -> newUser = new Artist(name, age, city);
            case HOST -> newUser = new Host(name, age, city);
            default -> {

            }
        }
        for (User user : users) {
            if (user.getUsername().equals(newUser.getUsername())) {
                return "The username " + user.getUsername() + " is already taken.";
            }
        }
        users.add(newUser);
        return "The username " + newUser.getUsername() + " has been added successfully.";
    }

    /**
     * Delete a user.
     *
     * @param username the username.
     * @return the output message.
     */
    public static String deleteUser(final String username) {
        User deleteUser = Admin.getUser(username);
        if (deleteUser == null) {
            return "The username " + username + " doesn't exist.";
        }
        for (User user : users) {
            if (user.getUserType().equals(Enums.UserType.NORMAL)) {
                if (user.getSelectedCreator() != null
                    && user.getSelectedCreator().equals(username)) {
                    return username + " can't be deleted.";
                }
                if (!checkArtistAlbumsDelete(user, deleteUser)) {
                    return username + " can't be deleted.";
                }
                if (!checkUsersPlaylistsDelete(user, deleteUser)) {
                    return username + " can't be deleted.";
                }
                if (!checkHostPodcastsDelete(user, deleteUser)) {
                    return username + " can't be deleted.";
                }
            }
        }
        switch (deleteUser.getUserType()) {
            case ARTIST -> deleteSongsArtist((Artist) deleteUser);
            case NORMAL -> deleteFollowedPlaylistsUser(deleteUser);
            default -> {

            }
        }
        users.remove(deleteUser);
        return username + " was successfully deleted.";
    }

    /**
     * Check if an artist can be deleted.
     * Checks if the albums of an artist or any song of theirs isn't currently
     * playing for a specific user.
     * Also checks if any song from the artist isn't in the playlists of a specific user.
     *
     * @param crtUser user which the sources come from.
     * @param artist artist to be deleted.
     * @return true if the artist can be deleted safely.
     */
    private static boolean checkArtistAlbumsDelete(final User crtUser, final User artist) {
        String sourceCollectionName = null;
        String sourceFileName = null;

        if (crtUser.getPlayer().getSource() != null) {
            if (crtUser.getPlayer().getSource().getAudioCollection() != null) {
                sourceCollectionName = crtUser.getPlayer().getSource().
                                                getAudioCollection().getName();
            }
            if (crtUser.getPlayer().getSource().getAudioFile() != null) {
                sourceFileName = crtUser.getPlayer().getSource().getAudioFile().getName();
            }
        }

        if (!artist.getUserType().equals(Enums.UserType.ARTIST)) {
            return true;
        }

        for (Album album : ((Artist) artist).getAlbums()) {
            if (album.getName().equals(sourceCollectionName)) {
                return false;
            }

            for (Song songAlbum : album.getSongs()) {
                if (songAlbum.getName().equals(sourceFileName)) {
                    return false;
                }

                for (Playlist playlist : crtUser.getPlaylists()) {
                    for (Song songPlaylist : playlist.getSongs()) {
                        if (songAlbum.getName().equals(songPlaylist.getName())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Check if a normal user can be deleted.
     * Checks if the playlists of the user aren't currently playing for a specific user
     *
     * @param crtUser user to check this for.
     * @param deleteUser user to be deleted.
     * @return true if the user can be deleted safely.
     */
    private static boolean checkUsersPlaylistsDelete(final User crtUser, final User deleteUser) {
        String sourceCollectionName = null;
        String sourceFileName = null;

        if (crtUser.getPlayer().getSource() != null) {
            if (crtUser.getPlayer().getSource().getAudioCollection() != null) {
                sourceCollectionName = crtUser.getPlayer().getSource().
                                                getAudioCollection().getName();
            }
            if (crtUser.getPlayer().getSource().getAudioFile() != null) {
                sourceFileName = crtUser.getPlayer().getSource().getAudioFile().getName();
            }
        }

        for (Playlist playlist : deleteUser.getPlaylists()) {
            if (playlist.getName().equals(sourceCollectionName)) {
                return false;
            }

            for (Song song : playlist.getSongs()) {
                if (song.getName().equals(sourceFileName)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if a host can be deleted.
     * Checks if the podcasts of the host or any episodes from these podcast aren't currently
     * playing for a specific user.
     *
     * @param crtUser user to check this for.
     * @param host host to be deleted.
     * @return true if the host can be deleted safely.
     */
    private static boolean checkHostPodcastsDelete(final User crtUser, final User host) {
        String sourceCollectionName = null;
        String sourceFileName = null;

        if (crtUser.getPlayer().getSource() != null) {
            if (crtUser.getPlayer().getSource().getAudioCollection() != null) {
                sourceCollectionName = crtUser.getPlayer().getSource().
                                                getAudioCollection().getName();
            }
            if (crtUser.getPlayer().getSource().getAudioFile() != null) {
                sourceFileName = crtUser.getPlayer().getSource().getAudioFile().getName();
            }
        }

        if (!host.getUserType().equals(Enums.UserType.HOST)) {
            return true;
        }

        for (Podcast podcast : ((Host) host).getPodcasts()) {
            if (podcast.getName().equals(sourceCollectionName)) {
                return false;
            }

            for (Episode episode : podcast.getEpisodes()) {
                if (episode.getName().equals(sourceFileName)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Delete the songs of an artist.
     *
     * @param artist the artist.
     */
    private static void deleteSongsArtist(final Artist artist) {
        for (Album album : artist.getAlbums()) {
            songs.removeAll(album.getSongs());
            for (User user : users) {
                user.getLikedSongs().removeAll(album.getSongs());
            }
        }
    }

    /**
     * Delete the playlist of a user that will be deleted from all the places where it was followed.
     *
     * @param deletedUser the deleted user.
     */
    private static void deleteFollowedPlaylistsUser(final User deletedUser) {
        for (User user : users) {
            user.getFollowedPlaylists().removeIf(playlist -> playlist.getOwner().
                                                                equals(deletedUser.getUsername()));
        }
        for (Playlist playlist : deletedUser.getFollowedPlaylists()) {
            playlist.decreaseFollowers();
        }
    }

    /**
     * Add album.
     *
     * @param user the user
     * @param name the name
     * @param releaseYear the year
     * @param description the description
     * @param songInputs the songs
     * @return the output message
     */
    public static String addAlbum(final User user, final String name,
                                  final Integer releaseYear, final String description,
                                  final ArrayList<SongInput> songInputs) {
        if (!user.getUserType().equals(Enums.UserType.ARTIST)) {
            return user.getUsername() + " is not an artist.";
        }

        for (Album album : ((Artist) user).getAlbums()) {
            if (album.getName().equals(name)) {
                return user.getUsername() + " has another album with the same name.";
            }
        }

        ArrayList<Song> albumSongs = new ArrayList<>();
        for (SongInput songInput : songInputs) {
            for (Song songCheck : albumSongs) {
                if (songCheck.getName().equals(songInput.getName())) {
                    return user.getUsername() + " has the same song at least twice in this album.";
                }
            }
            albumSongs.add(new Song(songInput.getName(), songInput.getDuration(),
                           songInput.getAlbum(),
                           songInput.getTags(), songInput.getLyrics(), songInput.getGenre(),
                           songInput.getReleaseYear(), songInput.getArtist()));
        }
        songs.addAll(albumSongs);
        return ((Artist) user).addAlbum(name, releaseYear, description, albumSongs);
    }

    /**
     * Remove album
     * @param user the user
     * @param name the name
     * @return the output message.
     */
    public static String removeAlbum(final User user, final String name) {
        if (!user.getUserType().equals(Enums.UserType.ARTIST)) {
            return user.getUsername() + " is not an artist.";
        }
        Artist artist = (Artist) user;
        Album albumRemove = null;
        for (Album album : artist.getAlbums()) {
            if (album.getName().equals(name)) {
                albumRemove = album;
                if (!checkAlbumDelete(albumRemove)) {
                    return artist.getUsername() + " can't delete this album.";
                }
                songs.removeAll(albumRemove.getSongs());
                for (User userIter : users) {
                    userIter.getLikedSongs().removeAll(albumRemove.getSongs());
                }
            }
        }
        if (albumRemove == null) {
            return artist.getUsername() + " doesn't have an album with the given name.";
        }
        artist.getAlbums().remove(albumRemove);
        return artist.getUsername() + " deleted the album successfully.\n";
    }

    /**
     * Check if an album can be deleted.
     * It goes through all the users sources and check if the album of a song from it is playing.
     *
     * @param albumRemove the album that will be deleted.
     * @return true if the album can be deleted safely.
     */
    private static boolean checkAlbumDelete(final Album albumRemove) {
        for (User crtUser : users) {
            String sourceCollectionName = null;
            String sourceFileName = null;
            if (crtUser.getPlayer().getSource() != null) {
                if (crtUser.getPlayer().getSource().getAudioCollection() != null) {
                    sourceCollectionName = crtUser.getPlayer().getSource().
                                                    getAudioCollection().getName();
                }
                if (crtUser.getPlayer().getSource().getAudioFile() != null) {
                    sourceFileName = crtUser.getPlayer().getSource().getAudioFile().getName();
                }
            }

            if (albumRemove.getName().equals(sourceCollectionName)) {
                return false;
            }

            for (Song songAlbum : albumRemove.getSongs()) {
                if (songAlbum.getName().equals(sourceFileName)) {
                    return false;
                }

                for (Playlist playlist : crtUser.getPlaylists()) {
                    for (Song songPlaylist : playlist.getSongs()) {
                        if (songAlbum.getName().equals(songPlaylist.getName())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Add podcast
     *
     * @param user the user
     * @param name the name
     * @param episodeInputs the episodes
     * @return the output message
     */
    public static String addPodcast(final User user, final String name,
                                    final ArrayList<EpisodeInput> episodeInputs) {
        if (!user.getUserType().equals(Enums.UserType.HOST)) {
            return user.getUsername() + " is not a host.";
        }
        ArrayList<Episode> podcastEpisodes = new ArrayList<>();
        for (EpisodeInput episodeInput : episodeInputs) {
            for (Episode episodeCheck : podcastEpisodes) {
                if (episodeCheck.getName().equals(episodeInput.getName())) {
                    return user.getUsername() + " has the same episode in this podcast.";
                }
            }
            podcastEpisodes.add(new Episode(episodeInput.getName(), episodeInput.getDuration(),
                                            episodeInput.getDescription()));
        }
        Podcast podcastNew = new Podcast(name, user.getUsername(), podcastEpisodes);
        String message = ((Host) user).addPodcast(podcastNew);
        if (!message.contains("same name")) {
            podcasts.add(podcastNew);
        }
        return message;
    }

    /**
     * Remove podcast.
     *
     * @param user the user
     * @param name the name
     * @return the output message
     */
    public static String removePodcast(final User user, final String name) {
        if (!user.getUserType().equals(Enums.UserType.HOST)) {
            return user.getUsername() + " is not a host.";
        }
        Host host = (Host) user;
        Podcast podcastRemove = null;
        for (Podcast podcast : host.getPodcasts()) {
            if (podcast.getName().equals(name)) {
                podcastRemove = podcast;
                if (!checkPodcastDelete(podcastRemove)) {
                    return host.getUsername() + " can't delete this podcast.";
                }
                podcasts.remove(podcastRemove);
            }
        }
        if (podcastRemove == null) {
            return host.getUsername() + " doesn't have a podcast with the given name.";
        }
        host.getPodcasts().remove(podcastRemove);
        return host.getUsername() + " deleted the podcast successfully.";
    }

    /**
     * Check if a podcast can be deleted.
     * It goes through all the users sources and check if the podcast is playing.
     *
     * @param podcastRemove the podcast that will be deleted.
     * @return true if the podcast can be deleted safely.
     */
    private static boolean checkPodcastDelete(final Podcast podcastRemove) {
        for (User crtUser : users) {
            String sourceCollectionName = null;
            if (crtUser.getPlayer().getSource() != null) {
                if (crtUser.getPlayer().getSource().getAudioCollection() != null) {
                    sourceCollectionName = crtUser.getPlayer().getSource().
                                                getAudioCollection().getName();
                }
            }

            if (podcastRemove.getName().equals(sourceCollectionName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Reset.
     */
    public static void reset() {
        users = new ArrayList<>();
        songs = new ArrayList<>();
        podcasts = new ArrayList<>();
        PageManager.reset();
        timestamp = 0;
    }
}
