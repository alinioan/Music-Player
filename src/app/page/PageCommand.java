package app.page;

public interface PageCommand {
    /**
     * Execute.
     */
    void execute();

    /**
     * Undo.
     */
    void undo();
}
