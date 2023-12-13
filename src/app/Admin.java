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

import java.util.*;

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

    public static List<String> getTop5Albums() {
        List<Playlist> sortedAlbums = new ArrayList<>(getAlbums());
        sortedAlbums.sort(Comparator.comparingInt(Playlist::getFollowers)
                .reversed()
                .thenComparing(Playlist::getTimestamp, Comparator.naturalOrder()));
        List<String> topAlbums = new ArrayList<>();
        int count = 0;
        for (Playlist playlist : sortedAlbums) {
            if (count >= LIMIT) {
                break;
            }
            topAlbums.add(playlist.getName());
            count++;
        }
        return topAlbums;
    }

    /**
     * Get online users.
     *
     * @return the users.
     */
    public static List<String> getOnlineUsers() {
        List<String> onlineUsers = new ArrayList<>();
        for (User user : users) {
            if (user.isOnline()) {
                onlineUsers.add(user.getUsername());
            }
        }
        return onlineUsers;
    }

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
    public static String addUser(final String name, final Integer age, final String city, final Enums.UserType type) {
        User newUser = null;
        switch (type) {
            case NORMAL -> newUser = new User(name, age, city);
            case ARTIST -> newUser = new Artist(name, age, city);
            case HOST -> newUser = new Host(name, age, city);
        }
        for (User user : users) {
            if (user.getUsername().equals(newUser.getUsername())) {
                return "The username " + user.getUsername() + " is already taken.";
            }
        }
        users.add(newUser);
        return "The username " + newUser.getUsername() + " has been added successfully.";
    }

    public static String deleteUser(final String username) {
        User deleteUser = Admin.getUser(username);
        if (deleteUser == null) {
            return "The username " + username + " doesn't exist.";
        }
        for (User user : users) {
            if (user.getUserType().equals(Enums.UserType.NORMAL)) {
                if (user.getSlectedCreator() != null && user.getSlectedCreator().equals(username)) {
                    return username + " can't be deleted.";
                }
                if (!checkArtistAlbumsDelete(user, deleteUser)) {
                    return username + " can't be deleted.";
                }
            }
        }
        switch (deleteUser.getUserType()) {
            case ARTIST -> deleteSongsArtist((Artist) deleteUser);
            case NORMAL -> deleteFollowedPlaylistsUser(deleteUser);
            default -> {}
        }
        users.remove(deleteUser);
        return username + " was successfully deleted.";
    }

    private static boolean checkArtistAlbumsDelete(User crtUser, User artist) {
        String sourceCollectionName = null;
        String sourceFileName = null;

        if (crtUser.getPlayer().getSource() != null) {
            if (crtUser.getPlayer().getSource().getAudioCollection() != null) {
                sourceCollectionName = crtUser.getPlayer().getSource().getAudioCollection().getName();
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

    private static void deleteSongsArtist(Artist artist) {
        for (Album album : artist.getAlbums()) {
            songs.removeAll(album.getSongs());
            for (User user : users) {
                user.getLikedSongs().removeAll(album.getSongs());
                // TODO: maybe have to remove songs from playlist idk
            }
        }
    }

    private static void deleteFollowedPlaylistsUser(User deletedUser) {
        for (User user : users) {
            user.getFollowedPlaylists().removeIf(playlist -> playlist.getOwner().equals(deletedUser.getUsername()));
        }
        for (Playlist playlist : deletedUser.getFollowedPlaylists()) {
            playlist.decreaseFollowers();
        }
    }

    public static String addAlbum(final User user, final String name, final Integer releaseYear,
                                  final String description, final ArrayList<SongInput> songInputs) {
        if (!user.getUserType().equals(Enums.UserType.ARTIST)) {
            return user.getUsername() + " is not an artist.";
        }

        ArrayList<Song> albumSongs = new ArrayList<>();
        for (SongInput songInput : songInputs) {
            for (Song songCheck : albumSongs) {
                if (songCheck.getName().equals(songInput.getName())) {
                    return user.getUsername() + " has the same song at least twice in this album.";
                }
            }
            albumSongs.add(new Song(songInput.getName(), songInput.getDuration(), songInput.getAlbum(),
                           songInput.getTags(), songInput.getLyrics(), songInput.getGenre(),
                           songInput.getReleaseYear(), songInput.getArtist()));
        }
        songs.addAll(albumSongs);
        return ((Artist) user).addAlbum(name, releaseYear, description, albumSongs);
    }

    public static String removeAlbum(final User user, String name) {
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
                    // TODO: maybe have to remove songs from playlist idk
                }
            }
        }
        if (albumRemove == null) {
            return artist.getUsername() + " doesn't have an album with the given name.";
        }
        artist.getAlbums().remove(albumRemove);
        return artist.getUsername() + " deleted the album successfully.\n";
    }

    private static boolean checkAlbumDelete(Album albumRemove) {
        for (User crtUser : users) {
            String sourceCollectionName = null;
            String sourceFileName = null;
            if (crtUser.getPlayer().getSource() != null) {
                if (crtUser.getPlayer().getSource().getAudioCollection() != null) {
                    sourceCollectionName = crtUser.getPlayer().getSource().getAudioCollection().getName();
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

    public static String addPodcast(final User user, final String name, final ArrayList<EpisodeInput> episodeInputs) {
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
            podcastEpisodes.add(new Episode(episodeInput.getName(), episodeInput.getDuration(), episodeInput.getDescription()));
        }
        Podcast podcastNew = new Podcast(name, user.getUsername(), podcastEpisodes);
        String message = ((Host) user).addPodcast(podcastNew);
        if (!message.contains("same name")) {
            podcasts.add(podcastNew);
        }
        return message;
    }

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

    private static boolean checkPodcastDelete(Podcast podcastRemove) {
        for (User crtUser : users) {
            String sourceCollectionName = null;
            if (crtUser.getPlayer().getSource() != null) {
                if (crtUser.getPlayer().getSource().getAudioCollection() != null) {
                    sourceCollectionName = crtUser.getPlayer().getSource().getAudioCollection().getName();
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
