package app.page;

import lombok.Getter;

import java.util.Deque;
import java.util.LinkedList;

@Getter
public class Invoker {
    private Deque<PageCommand> commandStack = new LinkedList<>();
    private Deque<PageCommand> undoStack = new LinkedList<>();


    /**
     * Clear up all the used resources
     */
    public void restart() {
        commandStack.clear();
        undoStack.clear();
    }

    /**
     * Executes a given command
     *
     * @param command the command
     */
    public void execute(final PageCommand command) {
        command.execute();
        commandStack.push(command);
    }

    /**
     * Undo the latest command
     */
    public void undo() {
        PageCommand command = commandStack.pop();
        command.undo();
        undoStack.push(command);
    }

    /**
     * Redo command previously undone. Cannot perform a redo after an execute,
     * only after at least one undo.
     */
    public void redo() {
        if (!undoStack.isEmpty()) {
            PageCommand command = undoStack.pop();
            command.execute();
            commandStack.push(command);
        }
    }
}


