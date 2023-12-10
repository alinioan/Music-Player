package app.page;

import app.audio.Collections.Album;
import app.user.artist.Event;
import app.user.artist.Merch;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class ArtistPage implements Visitable {
    private ArrayList<Album> albums;
    private ArrayList<Event> events;
    private ArrayList<Merch> merches;

    public ArtistPage(ArrayList<Album> albums, ArrayList<Event> events, ArrayList<Merch> merches) {
        this.albums = albums;
        this.events = events;
        this.merches = merches;
    }

    @Override
    public String accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
