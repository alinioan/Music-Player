package app.page;

public interface Visitor {

    /**
     * Visit home page
     * @param homePage the page.
     * @return the page as a string.
     */
    String visit(HomePage homePage);

    /**
     * Visit artist page
     * @param artistPage the page.
     * @return the page as a string.
     */
    String visit(ArtistPage artistPage);

    /**
     * Visit likedContent page
     * @param likedContentPage the page.
     * @return the page as a string.
     */
    String visit(LikedContentPage likedContentPage);

    /**
     * Visit host page
     * @param hostPage the page.
     * @return the page as a string.
     */
    String visit(HostPage hostPage);
}
