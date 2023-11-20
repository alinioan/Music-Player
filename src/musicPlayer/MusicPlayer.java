package musicPlayer;

import commandIO.CommandInput;
import commandIO.CommandOutput;
import playerFiles.Library;


public class MusicPlayer {
    private String fileType;
    private int playState;
    private int repeatState;
    private int shuffleState;
    private int durationPassedInCurrentFile;
    private SearchBar searchBar = new SearchBar();

    public CommandOutput processCommand(CommandInput command, Library library) {
        switch (command.getCommand()) {
            case "load":
                break;
            case "playpause":
                break;
            case "repeat":
                break;
            case "shuffle":
                break;
            case "forward":
                break;
            case "backward":
                break;
            case "like":
                break;
            case "prev":
                break;
            case "addRemoveInPlaylist":
                break;
            case "status":
                break;
            default:
                return this.searchBar.processCommand(command, library);
        }
        return null;
    }


}
