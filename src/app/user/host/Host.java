package app.user.host;

import app.audio.Collections.Podcast;
import app.audio.Collections.PodcastOutput;
import app.user.User;
import app.utils.Enums;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class Host extends User {
    private ArrayList<Podcast> podcasts;
    private ArrayList<Announcement> announcements;
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
    }

    public String addPodcast(Podcast podcast) {
        if (!checkPodcastExists(podcast.getName())) {
            return this.getUsername() + " has another podcast with the same name.";
        }
        this.podcasts.add(podcast);
        return this.getUsername() + " has added new podcast successfully.";
    }

    private boolean checkPodcastExists(final String name) {
        for (Podcast podcast : podcasts) {
            if (podcast.getName().equals(name)) {
                return false;
            }
        }
        return true;
    }

    public String addAnnouncement(final String name, final String description) {
        for (Announcement announcement : announcements) {
            if (announcement.getName().equals(name)) {
                return this.getUsername() + " has already added an announcement with this name.";
            }
        }
        announcements.add(new Announcement(name, description));
        return this.getUsername() + " has successfully added new announcement.";
    }

    public String removeAnnouncement(final String name) {
        if (!announcements.removeIf(announcement -> announcement.getName().equals(name))) {
            return this.getUsername() + " has no announcement with the given name.";
        }
        return this.getUsername() + " has successfully deleted the announcement.";
    }

    public ArrayList<PodcastOutput> showPodcasts() {
        ArrayList<PodcastOutput> podcastOutputs = new ArrayList<>();
        for (Podcast podcast : podcasts) {
            podcastOutputs.add(new PodcastOutput(podcast));
        }
        return podcastOutputs;
    }
}
