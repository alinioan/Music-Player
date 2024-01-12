package app.user.host;

import app.Admin;
import app.audio.Collections.Podcast;
import app.audio.Collections.PodcastOutput;
import app.user.User;
import app.user.wrapped.HostWrapped;
import app.user.wrapped.UserWrapped;
import app.user.wrapped.Wrapped;
import app.utils.Enums;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class Host extends User {
    private ArrayList<Podcast> podcasts;
    private ArrayList<Announcement> announcements;
    private HostWrapped hostWrapped;

    /**
     * Instantiates a new User.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public Host(final String username, final int age, final String city) {
        super(username, age, city);
        super.setUserType(Enums.UserType.HOST);
        super.setCurrentPage("");
        podcasts = new ArrayList<>();
        announcements = new ArrayList<>();
        hostWrapped = new HostWrapped();
    }

    /**
     * Add a podcast.
     *
     * @param podcast the podcast.
     * @return output message.
     */
    public String addPodcast(final Podcast podcast) {
        if (!checkPodcastExists(podcast.getName())) {
            return this.getUsername() + " has another podcast with the same name.";
        }
        this.podcasts.add(podcast);
        return this.getUsername() + " has added new podcast successfully.";
    }

    /**
     * Check if host already has a podcast with this name.
     *
     * @param name the name to check.
     * @return true if no podcast with the name exists.
     */
    private boolean checkPodcastExists(final String name) {
        for (Podcast podcast : podcasts) {
            if (podcast.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds an announcement to hosts announcements list.
     * @param name the name of the announcement.
     * @param description the description of the announcement.
     * @return output message.
     */
    public String addAnnouncement(final String name, final String description) {
        for (Announcement announcement : announcements) {
            if (announcement.getName().equals(name)) {
                return this.getUsername() + " has already added an announcement with this name.";
            }
        }
        announcements.add(new Announcement(name, description));
        return this.getUsername() + " has successfully added new announcement.";
    }

    /**
     * Removes an announcement from hosts announcements list.
     *
     * @param name the name of the announcement.
     * @return output message.
     */
    public String removeAnnouncement(final String name) {
        if (!announcements.removeIf(announcement -> announcement.getName().equals(name))) {
            return this.getUsername() + " has no announcement with the given name.";
        }
        return this.getUsername() + " has successfully deleted the announcement.";
    }

    /**
     * Shows hosts podcasts.
     *
     * @return the podcast outputs.
     */
    public ArrayList<PodcastOutput> showPodcasts() {
        ArrayList<PodcastOutput> podcastOutputs = new ArrayList<>();
        for (Podcast podcast : podcasts) {
            podcastOutputs.add(new PodcastOutput(podcast));
        }
        return podcastOutputs;
    }

    public Wrapped getWrapped() {
        for (User user : Admin.getUsers()) {
            UserWrapped userWrapped = user.getPlayer().getWrapped();
            hostWrapped.updateStats(userWrapped, this, user.getUsername());
        }
        return hostWrapped.getSortedWrapped();
    }
}
