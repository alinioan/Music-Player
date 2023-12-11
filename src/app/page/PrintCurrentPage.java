package app.page;

import app.audio.Collections.Album;
import app.user.artist.Event;
import app.user.artist.Merch;

import java.util.List;

public class PrintCurrentPage implements Visitor {
    @Override
    public String visit(HomePage homePage) {
        return "Liked songs:\n\t" + homePage.getSongs().toString() + "\n\nFollowed playlists:\n\t" + homePage.getPlaylists().toString();
    }

    @Override
    public String visit(LikedContentPage likedContentPage) {
        return null;
    }

    @Override
    public String visit(ArtistPage artistPage) {
        StringBuilder page = new StringBuilder();
        page.append("Albums:\n\t[");
        List<Album> albums = artistPage.getAlbums();
        for (Album album : albums) {
            page.append(album.getName());
            page.append(", ");
        }
        page.delete(page.length() - 2, page.length());

        page.append("]\n\nMerch:\n\t[");
        List<Merch> merches = artistPage.getMerches();
        for (Merch merch : merches) {
            page.append(merch.getName());
            page.append(" - ");
            page.append(merch.getPrice());
            page.append(":\n\t");
            page.append(merch.getDescription());
            page.append(", ");
        }
        if (!merches.isEmpty()) {
            page.delete(page.length() - 2, page.length());
        }

        page.append("]\n\nEvents:\n\t[");
        List<Event> events = artistPage.getEvents();
        for (Event event : events) {
            page.append(event.getName());
            page.append(" - ");
            page.append(event.getDate());
            page.append(":\n\t");
            page.append(event.getDescription());
            page.append(", ");
        }
        if (!events.isEmpty()) {
            page.delete(page.length() - 2, page.length());
        }
        page.append("]");

        return new String(page);
    }

    @Override
    public String visit(HostPage hostPage) {
        return null;
    }
}