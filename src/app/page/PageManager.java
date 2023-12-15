package app.page;

import app.user.User;
import app.user.artist.Artist;
import app.user.host.Host;
import app.utils.Enums;
import lombok.Getter;

import java.util.HashMap;

@Getter
public final class PageManager {
    private static HashMap<String, HomePage> homePageHashMap = new HashMap<>();
    private static HashMap<String, LikedContentPage> likedContentPageHashMap = new HashMap<>();
    private static HashMap<String, ArtistPage> artistPageHashMap = new HashMap<>();
    private static HashMap<String, HostPage> hostPageHashMap = new HashMap<>();

    private PageManager() {

    }

    /**
     * Create a pages from user and add them to the maps.
     *
     * @param user the user.
     */
    private static void addNormalPages(final User user) {
         HomePage homePage = new HomePage(user.getLikedSongs(), user.getFollowedPlaylists());
         homePageHashMap.put(user.getUsername(), homePage);
         LikedContentPage likedContentPage = new LikedContentPage(user.getLikedSongs(),
                                                    user.getFollowedPlaylists());
         likedContentPageHashMap.put(user.getUsername(), likedContentPage);
    }

    /**
     * Create page from artist and add it to the map.
     *
     * @param artist the artist.
     */
    private static void addArtistPage(final Artist artist) {
        ArtistPage artistPage = new ArtistPage(artist.getAlbums(),
                                    artist.getEvents(), artist.getMerches());
        artistPageHashMap.put(artist.getUsername(), artistPage);
    }

    /**
     * Create page from host and add it to the map.
     *
     * @param host the host.
     */
    private static void addHostPage(final Host host) {
        HostPage hostPage = new HostPage(host.getPodcasts(), host.getAnnouncements());
        hostPageHashMap.put(host.getUsername(), hostPage);
    }

    /**
     * Update pages.
     *
     * @param user the user.
     */
    public static void updatePages(final User user) {
        switch (user.getUserType()) {
            case ARTIST -> addArtistPage((Artist) user);
            case HOST -> addHostPage((Host) user);
            case NORMAL -> addNormalPages(user);
            default -> {

            }
        }
    }

    /**
     * Print current page.
     *
     * @param user the user.
     * @return output message.
     */
    public static String printCurrentPage(final User user) {
        Visitor pagePrinter = new PrintCurrentPage();
        if (user.getCreatorType() == null) {
            if (user.getCurrentPage().equals("Home")) {
                return homePageHashMap.get(user.getUsername()).accept(pagePrinter);
            }
            return likedContentPageHashMap.get(user.getUsername()).accept(pagePrinter);
        }

        if (user.getCreatorType().equals(Enums.UserType.ARTIST)) {
            return artistPageHashMap.get(user.getSelectedCreator()).accept(pagePrinter);
        }

        if (user.getCreatorType().equals(Enums.UserType.HOST)) {
            return hostPageHashMap.get(user.getSelectedCreator()).accept(pagePrinter);
        }
        return null;
    }

    /**
     * Change page from user.
     *
     * @param user the user.
     * @param nextPage the next page.
     * @return the output message.
     */
    public static String changePage(final User user, final String nextPage) {
        user.setSelectedCreator(null);
        user.setCreatorType(null);
        user.setCurrentPage(nextPage);
        return user.getUsername() + " accessed " + nextPage + " successfully.";
    }

    /**
     * Resets the page maps.
     */
    public static void reset() {
        homePageHashMap = new HashMap<>();
        likedContentPageHashMap = new HashMap<>();
        artistPageHashMap = new HashMap<>();
        hostPageHashMap = new HashMap<>();
    }
}
