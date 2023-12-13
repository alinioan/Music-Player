package app.page;

import app.audio.Collections.Podcast;
import app.user.host.Announcement;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class HostPage implements Visitable {
    private ArrayList<Podcast> podcasts;
    private ArrayList<Announcement> announcements;

    public HostPage(final ArrayList<Podcast> podcasts, final ArrayList<Announcement> announcements) {
        this.podcasts = podcasts;
        this.announcements = announcements;
    }

    public String accept(Visitor visitor) {
        return visitor.visit(this);
    }
}
