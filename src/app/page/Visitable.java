package app.page;

public interface Visitable {
    /**
     * Accept visitor.
     *
     * @param visitor the visitor.
     * @return the string.
     */
    String accept(Visitor visitor);
}
