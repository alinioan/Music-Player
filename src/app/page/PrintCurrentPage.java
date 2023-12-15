package app.page;

import app.audio.Collections.Album;
import app.audio.Collections.Playlist;
import app.audio.Collections.Podcast;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.user.artist.Event;
import app.user.artist.Merch;
import app.user.host.Announcement;

import java.util.List;

/**
 * Visitor that prints pages.
 */
public final class PrintCurrentPage implements Visitor {
    /**
     * Print home page
     *
     * @param homePage the page.
     * @return the page as a string
     */
    @Override
    public String visit(final HomePage homePage) {
        return "Liked songs:\n\t" + homePage.getSongs().toString()
                + "\n\nFollowed playlists:\n\t" + homePage.getPlaylists().toString();
    }

    /**
     * Print liked content page
     *
     * @param likedContentPage the page.
     * @return the page as a string
     */
    @Override
    public String visit(final LikedContentPage likedContentPage) {
        StringBuilder page = new StringBuilder();

        page.append("Liked songs:\n\t[");
        for (Song song : likedContentPage.getSongs()) {
            page.append(song.getName());
            page.append(" - ");
            page.append(song.getArtist());
            page.append(", ");
        }
        if (!likedContentPage.getSongs().isEmpty()) {
            page.delete(page.length() - 2, page.length());
        }

        page.append("]\n\nFollowed playlists:\n\t[");
        for (Playlist playlist : likedContentPage.getPlaylists()) {
            page.append(playlist.getName());
            page.append(" - ");
            page.append(playlist.getOwner());
            page.append(", ");
        }
        if (!likedContentPage.getPlaylists().isEmpty()) {
            page.delete(page.length() - 2, page.length());
        }
        page.append("]");

        return new String(page);
    }

    /**
     * Print artist page
     *
     * @param artistPage the page.
     * @return the page as a string
     */
    @Override
    public String visit(final ArtistPage artistPage) {
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

    /**
     * Print the host page
     *
     * @param hostPage the page.
     * @return the page as a string
     */
    @Override
    public String visit(final HostPage hostPage) {
        StringBuilder page = new StringBuilder();
        page.append("Podcasts:\n\t[");
        for (Podcast podcast : hostPage.getPodcasts()) {
            page.append(podcast.getName());
            page.append(":\n\t[");
            for (Episode episode : podcast.getEpisodes()) {
                page.append(episode.getName());
                page.append(" - ");
                page.append(episode.getDescription());
                page.append(", ");
            }
            page.delete(page.length() - 2, page.length());
            page.append("]\n, ");
        }
        page.delete(page.length() - 2, page.length());

        page.append("]\n\nAnnouncements:\n\t[");
        for (Announcement announcement : hostPage.getAnnouncements()) {
            page.append(announcement.getName());
            page.append(":\n\t");
            page.append(announcement.getDescription());
            page.append("\n");
        }
        page.append("]");

        return new String(page);
    }
}
