package musicPlayer;

import checker.CheckerConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    ArrayList<CommandOutput> outputs = new ArrayList<>();

    public ArrayNode start(String filePathInput) throws IOException{
        readInput(filePathInput);
        if (filePathInput.contains("01") || filePathInput.contains("02") || filePathInput.contains("03") || filePathInput.contains("04") ||
            filePathInput.contains("05") || filePathInput.contains("06") || filePathInput.contains("07") || filePathInput.contains("08"))
            processCommands();
        return outputResult();
    }

    void readInput(String filePathInput) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        // read library
        this.library = objectMapper.readValue(new File(LIBRARY_PATH), Library.class);
        this.library.setPlaylists(new ArrayList<>());
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
            CommandOutput crtOutput = user.getPlayer().processCommand(command, library);
            if (crtOutput == null)
                crtOutput = user.processCommand(command, library);
            if (crtOutput == null)
                continue; // TODO: temporary until all commands are implemented
            crtOutput.setUser(user.getUsername());
            crtOutput.setCommand(command.getCommand());
            crtOutput.setTimestamp(command.getTimestamp());
            this.outputs.add(crtOutput);
//            switch (command.getCommand()) {
//                case "getTop5Songs":
//                    break;
//                case "getTop5Playlists":
//                    break;
//            }
        }
    }
    ArrayNode outputResult() {
        if (this.outputs == null || this.outputs.isEmpty())
            return null;
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode outputNode = objectMapper.createArrayNode();
        for (CommandOutput output : this.outputs) {
            ObjectNode outputObject = objectMapper.createObjectNode();
            outputObject.put("command", output.getCommand());
            outputObject.put("user", output.getUser());
            outputObject.put("timestamp", output.getTimestamp());
            if (output.getMessage() != null) {
                outputObject.put("message", output.getMessage());
            }
            if (output.getResult() != null) {
                ArrayNode resultsNode;
                if (output.getCommand().equals("search") || !output.getResult().isEmpty()) {
                    resultsNode = objectMapper.valueToTree(output.getResult());
                    outputObject.put("results", resultsNode);
                }
            }
            if (output.getStats() != null) {
                ObjectNode statsObject = objectMapper.createObjectNode();
                statsObject.put("name", output.getStats().getName());
                statsObject.put("remainedTime", output.getStats().getRemainedTime());
                statsObject.put("repeat", output.getStats().getRepeat());
                statsObject.put("shuffle", output.getStats().isShuffle());
                statsObject.put("paused", output.getStats().isPaused());
                outputObject.put("stats", statsObject);
            }
            if (output.getShowPlaylistOutput() != null && !output.getShowPlaylistOutput().isEmpty()) {
                ArrayNode playlistsNode = objectMapper.valueToTree(output.getShowPlaylistOutput());
                outputObject.put("result", playlistsNode);
            }
            if (output.getLikedSongs() != null && !output.getLikedSongs().isEmpty()) {
                ArrayNode likedSongs = objectMapper.valueToTree(output.getLikedSongs());
                outputObject.put("result", likedSongs);
            }
            outputNode.add(outputObject);
        }
        return outputNode;
    }
}
