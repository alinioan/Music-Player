package app.page;

import app.user.host.Host;

public interface Visitor {
    public String visit(HomePage homePage);
    public String visit(ArtistPage artistPage);
    public String visit(LikedContentPage likedContentPage);
    public String visit(HostPage hostPage);
}
