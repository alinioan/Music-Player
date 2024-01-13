package app.page;

import app.Admin;
import app.audio.Files.Song;
import app.user.User;
import app.user.artist.Artist;
import app.user.host.Host;
import app.utils.Enums;

public class ChangePageCommand implements PageCommand {
    private User user;
    private String page;
    private String creator;
    private String oldCreator;
    private Enums.UserType oldType;
    private String oldPage;

    public ChangePageCommand(User user, String page) {
        this.user = user;
        this.page = page;
    }

    @Override
    public void execute() {
        oldCreator = user.getSelectedCreator();
        oldType = user.getCreatorType();
        user.setSelectedCreator(null);
        user.setCreatorType(null);
        if ((page.equals("Host") || page.equals("Artist")) && user.getPlayer().getSource() != null) {
            switch (user.getPlayer().getSource().getType()) {
                case LIBRARY -> {
                    Artist artist = (Artist) Admin.getUser(((Song) user.getPlayer()
                            .getSource().getAudioFile()).getArtist());
                    creator = artist.getUsername();
                    user.setCreatorType(Enums.UserType.ARTIST);
                    PageManager.addArtistPage(artist);
                }
                case PLAYLIST -> {
                    Artist artist = (Artist) Admin.getUser(user.getPlayer()
                            .getSource().getAudioCollection().getOwner());
                    creator = artist.getUsername();
                    user.setCreatorType(Enums.UserType.ARTIST);
                    PageManager.addArtistPage(artist);
                }
                case PODCAST -> {
                    Host host = (Host) Admin.getUser(user.getPlayer()
                            .getSource().getAudioCollection().getOwner());
                    creator = host.getUsername();
                    user.setCreatorType(Enums.UserType.HOST);
                    PageManager.addHostPage(host);
                }
                default -> {

                }
            }
        }
        oldPage = user.getCurrentPage();
        user.setCurrentPage(page);
        user.setSelectedCreator(creator);
        if (page.equals("Host")) {
            user.setCreatorType(Enums.UserType.HOST);
        }
        if (page.equals("Artist")) {
            user.setCreatorType(Enums.UserType.ARTIST);
        }
    }

    @Override
    public void undo() {
        user.setCreatorType(oldType);
        user.setSelectedCreator(oldCreator);
        user.setCurrentPage(oldPage);
    }
}
