package app.page;

import app.audio.Collections.Album;
import app.user.artist.Event;
import app.user.artist.Merch;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public final class ArtistPage implements Visitable {
    private ArrayList<Album> albums;
    private ArrayList<Event> events;
    private ArrayList<Merch> merches;

    public ArtistPage(final ArrayList<Album> albums, final ArrayList<Event> events,
                      final ArrayList<Merch> merches) {
        this.albums = albums;
        this.events = events;
        this.merches = merches;
    }

    @Override
    public String accept(final Visitor visitor) {
        return visitor.visit(this);
    }
}
