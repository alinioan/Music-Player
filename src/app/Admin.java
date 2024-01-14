package app;

import app.audio.Collections.Album;
import app.audio.Collections.Playlist;
import app.audio.Collections.Podcast;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.page.PageManager;
import app.user.Monetization;
import app.user.artist.Artist;
import app.user.host.Host;
import app.user.User;
import app.user.wrapped.UserWrapped;
import app.utils.Enums;
import app.utils.StringPair;
import fileio.input.EpisodeInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import fileio.input.UserInput;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * The type Admin.
 */
public final class Admin {
    @Getter
    private static List<User> users = new ArrayList<>();
    private static List<Song> songs = new ArrayList<>();
    private static List<Podcast> podcasts = new ArrayList<>();
    @Getter
    private static int timestamp = 0;
    private static final int LIMIT = 5;
    private static final double VALUE = 1000000.0;
    private static final double HOUNDRED = 100.0;
    private static Map<String, Monetization> monetization = new HashMap<>();

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

    public static Song getSong(final String name) {
        for (Song song : songs) {
            if (song.getName().equals(name)) {
                return song;
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
        return artist.getUsername() + " deleted the album successfully.";
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
            if (!crtUser.getUserType().equals(Enums.UserType.NORMAL)) {
                continue;
            }
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
                if (songAlbum.getName().equals(sourceFileName)
                        && songAlbum.getAlbum().equals(Admin.getSong(sourceFileName).getAlbum())) {
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

    public static Map<String, Monetization> getMonetizationRanking() {
        Map<StringPair, Double> totalSongEarnings = new HashMap<>();
        for (User user : users) {
            if (user.getUserType().equals(Enums.UserType.NORMAL)) {

                UserWrapped userWrapped = user.getPlayer().getWrapped();
                putRevenue(totalSongEarnings, userWrapped.getFreeSongRevenue());
                putRevenue(totalSongEarnings, userWrapped.getPremiumSongRevenue());
                putMonetization(userWrapped.getMonetization());

                calculatePremiumRevenue(userWrapped, totalSongEarnings);

                if (userWrapped.getTopSongsWithArtistPremium().isEmpty()) {
                    for (Map.Entry<String, Integer> entry : userWrapped.getTopArtists().entrySet()) {
                        if (!monetization.containsKey(entry.getKey())) {
                            monetization.put(entry.getKey(), new Monetization(0.0, "N/A", 0.0));
                        }
                    }
                }
            }
        }
        setMostProfitableSongs(totalSongEarnings);
        addMerchRevenue();
        roundMonetization();
        sortMonetization();
        return monetization;
    }

    private static void calculatePremiumRevenue(UserWrapped userWrapped,
                                                Map<StringPair, Double> totalSongEarnings) {
        List<String> checkedArtists = new ArrayList<>();
        for (Map.Entry<StringPair, Integer> entry1
                : userWrapped.getTopSongsWithArtistPremium().entrySet()) {
            double artistListens = 0.0;
            double totalSongs = 0.0;
            for (Map.Entry<StringPair, Integer> entry2
                    : userWrapped.getTopSongsWithArtistPremium().entrySet()) {
                totalSongs += entry2.getValue();
            }

            for (Map.Entry<StringPair, Integer> entry2
                    : userWrapped.getTopSongsWithArtistPremium().entrySet()) {
                if (!checkedArtists.contains(entry2.getKey().getS2())
                        && entry2.getKey().getS2().equals(entry1.getKey().getS2())) {
                    artistListens += entry2.getValue();
                    Double revenue =  entry2.getValue() * VALUE / totalSongs;
                    if (totalSongEarnings.containsKey(entry2.getKey())) {
                        totalSongEarnings.put(entry2.getKey(),
                                              revenue + totalSongEarnings.get(entry2.getKey()));
                    } else {
                        totalSongEarnings.put(entry2.getKey(), revenue);
                    }
                }
            }
            checkedArtists.add(entry1.getKey().getS2());
            addNewMonetization(artistListens, totalSongs, entry1.getKey().getS2());
        }
    }

    private static void putRevenue(Map<StringPair, Double> totalSongEarnings,
                                       Map<StringPair, Double> freeSongRevenue) {
        for (Map.Entry<StringPair, Double> entry : freeSongRevenue.entrySet()) {
            if (totalSongEarnings.containsKey(entry.getKey())) {
                totalSongEarnings.put(entry.getKey(),
                                      entry.getValue() + totalSongEarnings.get(entry.getKey()));
            } else {
                totalSongEarnings.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private static void putMonetization(Map<String, Monetization> adMonetization) {
        for (Map.Entry<String, Monetization> entry : adMonetization.entrySet()) {
            if (monetization.containsKey(entry.getKey())) {
                Monetization newMonetization = monetization.get(entry.getKey());
                newMonetization.setSongRevenue(entry.getValue().getSongRevenue() + newMonetization.getSongRevenue());
                newMonetization.setMerchRevenue(entry.getValue().getMerchRevenue() + newMonetization.getMerchRevenue());
                monetization.put(entry.getKey(), newMonetization);
            } else {
                monetization.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private static void addNewMonetization(double artistListens, double totalSongs, String artist) {
        double songRevenue = artistListens * VALUE / totalSongs;
        if (monetization.containsKey(artist)) {
            Monetization newMonetization = monetization.get(artist);
            newMonetization.setSongRevenue(newMonetization.getSongRevenue() + songRevenue);
            monetization.put(artist, newMonetization);
        } else {
            monetization.put(artist, new Monetization(0.0, songRevenue));
        }
    }

    private static void setMostProfitableSongs(Map<StringPair, Double> totalSongEarnings) {
        List<String> checkedArtists = new ArrayList<>();

        for (Map.Entry<StringPair, Double> entry1 : totalSongEarnings.entrySet()) {
            double revenue = 0.0;
            String topSong = null;

            for (Map.Entry<StringPair, Double> entry2 : totalSongEarnings.entrySet()) {
                if (!checkedArtists.contains(entry2.getKey().getS2()) && entry2.getKey().getS2().equals(entry1.getKey().getS2())) {
                    if (entry2.getValue() > revenue) {
                        revenue = entry2.getValue();
                        topSong = entry2.getKey().getS1();
                    } else if(entry2.getValue() == revenue && topSong.compareTo(entry2.getKey().getS1()) > 0) {
                        topSong = entry2.getKey().getS1();
                    }
                }
            }

            if (topSong != null) {
                Monetization newMonetization = monetization.get(entry1.getKey().getS2());
                newMonetization.setMostProfitableSong(topSong);
                monetization.put(entry1.getKey().getS2(), newMonetization);
            }
            checkedArtists.add(entry1.getKey().getS2());
        }
    }

    static void addMerchRevenue() {
        for (User user : users) {
            if (user.getUserType().equals(Enums.UserType.ARTIST)) {
                Artist artist = (Artist) user;
                if (artist.getArtistWrapped().getMerchRevenue() > 0) {
                    Monetization newMonetization = monetization.get(artist.getUsername());
                    if (newMonetization != null) {
                        newMonetization.setMerchRevenue(artist.getArtistWrapped().getMerchRevenue());
                        monetization.put(artist.getUsername(), newMonetization);
                    } else {
                        monetization.put(artist.getUsername(),
                                new Monetization(artist.getArtistWrapped().getMerchRevenue()));
                    }
                }
            }
        }
    }

    private static void sortMonetization() {
        List<Map.Entry<String, Monetization>> entryList = new ArrayList<>(monetization.entrySet());

        entryList.sort((entry1, entry2) -> {
            int valueComparison = entry2.getValue().compareTo(entry1.getValue());
            return (valueComparison == 0) ? entry1.getKey().compareTo(entry2.getKey()) : valueComparison;
        });

        Map<String, Monetization> sortedMap = new LinkedHashMap<>();

        int count = 1;
        for (Map.Entry<String, Monetization> entry : entryList) {
            entry.getValue().setRanking(count);
            sortedMap.put(entry.getKey(), entry.getValue());
            count++;
        }
        monetization = sortedMap;
    }

    private static void roundMonetization() {
        for (Map.Entry<String, Monetization> entry : monetization.entrySet()) {
            entry.getValue().setSongRevenue(Math.round(entry.getValue().getSongRevenue() * HOUNDRED) / HOUNDRED);
        }
    }

    /**
     * Reset.
     */
    public static void reset() {
        users = new ArrayList<>();
        songs = new ArrayList<>();
        podcasts = new ArrayList<>();
        monetization = new HashMap<>();
        PageManager.reset();
        timestamp = 0;
    }
}
