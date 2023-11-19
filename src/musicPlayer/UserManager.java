package musicPlayer;

import checker.CheckerConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import commandIO.CommandInput;
import commandIO.CommandOutput;
import playerFiles.Library;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/*
* TODO: Could make this simpleton maybe, idk check later
*
 */
public class UserManager {
    static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";
    Library library;
    HashMap<String, User> users = new HashMap<>();
    ArrayList<CommandInput> input;
    static int commandId = 0;
    CommandOutput output;

    public void start(String filePathInput) throws IOException{
        readInput(filePathInput);
        // TODO: !!!!!!!!!!!!!!!!!!!!remove this!!!!!!!!!!!!!!!!!!!!
        if (filePathInput.contains("01"))
            processCommands();
    }

    void readInput(String filePathInput) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        // read library
        this.library = objectMapper.readValue(new File(LIBRARY_PATH), Library.class);
        for (User usr : this.library.getUsers()) {
            users.put(usr.getUsername(), usr);
        }
        // read command
        CommandInput[] commandArray = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH + filePathInput), CommandInput[].class);
        this.input = new ArrayList<>(Arrays.asList(commandArray));
    }

    void processCommands() {
        for (CommandInput command : this.input) {
            User user = users.get(command.getUsername());
            user.getPlayer().processCommand(command, library);
//            switch (command.getCommand()) {
//                case "createPlaylist":
//                    break;
//                case "switchVisibility":
//                    break;
//                case "follow":
//                    break;
//                case "showPlaylists":
//                    break;
//                case "showPreferredSongs":
//                    break;
//                case "getTop5Songs":
//                    break;
//                case "getTop5Playlists":
//                    break;
//            }
        }
    }
}
