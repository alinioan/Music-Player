package musicplayer;

import checker.CheckerConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import commandio.CommandInput;
import commandio.CommandOutput;
import playerfiles.Library;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class UserManager {
    private static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";
    private Library library;
    private final HashMap<String, User> users = new HashMap<>();
    private ArrayList<CommandInput> input;
    private final ArrayList<CommandOutput> outputs = new ArrayList<>();

    /**
     * the start point of the implementation
     * @param filePathInput command input file
     * @return array of outputs
     * @throws IOException
     */
    public ArrayNode start(final String filePathInput) throws IOException {
        readInput(filePathInput);
        processCommands();
        return outputResult();
    }

    /**
     * read the commands from file and put them in the input data field of this object
     * @param filePathInput input file
     * @throws IOException
     */
    private void readInput(final String filePathInput) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        // read library
        this.library = objectMapper.readValue(new File(LIBRARY_PATH), Library.class);
        this.library.setPlaylists(new ArrayList<>());
        for (User usr : this.library.getUsers()) {
            users.put(usr.getUsername(), usr);
        }
        // read command
        CommandInput[] commandArray = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH
                                                         + filePathInput), CommandInput[].class);
        this.input = new ArrayList<>(Arrays.asList(commandArray));
    }

    /**
     * call the specific methods to process the commands
     */
    private void processCommands() {
        for (CommandInput command : this.input) {
            User user = users.get(command.getUsername());
            if (user == null) {
                continue;
            }
            CommandOutput crtOutput = user.getPlayer().processCommand(command, library);
            if (crtOutput == null) {
                crtOutput = user.processCommand(command, library);
            }
            if (crtOutput == null) {
                continue;
            }
            crtOutput.setUser(user.getUsername());
            crtOutput.setCommand(command.getCommand());
            crtOutput.setTimestamp(command.getTimestamp());
            this.outputs.add(crtOutput);
        }
    }

    /**
     * create the output array
     * @return an arrya made of output objects
     */
    private ArrayNode outputResult() {
        if (this.outputs.isEmpty()) {
            return null;
        }
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
            if (output.getShowPlaylistOutput() != null
                    && !output.getShowPlaylistOutput().isEmpty()) {
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
