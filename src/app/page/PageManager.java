package app.page;

import app.user.User;
import app.user.artist.Artist;
import app.user.host.Host;
import app.utils.Enums;
import lombok.Getter;

import java.util.HashMap;

@Getter
public class PageManager {
    private static HashMap<String, HomePage> homePageHashMap = new HashMap<>();
    private static HashMap<String, LikedContentPage> likedContentPageHashMap = new HashMap<>();
    private static HashMap<String, ArtistPage> artistPageHashMap = new HashMap<>();
    private static HashMap<String, HostPage> hostPageHashMap = new HashMap<>();

    private static void addNormalPages(User user) {
         HomePage homePage = new HomePage(user.getLikedSongs(), user.getFollowedPlaylists());
         homePageHashMap.put(user.getUsername(), homePage);
         LikedContentPage likedContentPage = new LikedContentPage(user.getLikedSongs(), user.getPlaylists());
         likedContentPageHashMap.put(user.getUsername(), likedContentPage);
    }

    private static void addArtistPage(Artist artist) {
        ArtistPage artistPage = new ArtistPage(artist.getAlbums(), artist.getEvents(), artist.getMerches());
        artistPageHashMap.put(artist.getUsername(), artistPage);
    }

    private static void addHostPage(Host host) {

    }

    public static void updatePages(User user) {
        switch (user.getUserType()) {
            case ARTIST -> addArtistPage((Artist) user);
            case HOST -> addHostPage((Host) user);
            case NORMAL -> addNormalPages(user);
        }
    }

    public static String printCurrentPage(User user) {
        Visitor pagePrinter = new PrintCurrentPage();
        if (user.getCreatorType() == null) {
            if (user.getCurrentPage().equals("home")) {
                return homePageHashMap.get(user.getUsername()).accept(pagePrinter);
            }
            return likedContentPageHashMap.get(user.getUsername()).accept(pagePrinter);
        }

        if (user.getCreatorType().equals(Enums.UserType.ARTIST)) {
            return artistPageHashMap.get(user.getSlectedCreator()).accept(pagePrinter);
        }

        if (user.getCreatorType().equals(Enums.UserType.HOST)) {
            return artistPageHashMap.get(user.getSlectedCreator()).accept(pagePrinter);
        }
        return "";
    }

    public static void reset() {
        homePageHashMap = new HashMap<>();
        likedContentPageHashMap = new HashMap<>();
        artistPageHashMap = new HashMap<>();
        hostPageHashMap = new HashMap<>();
    }
}
