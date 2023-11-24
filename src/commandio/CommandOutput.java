package commandio;

import lombok.Getter;
import lombok.Setter;
import musicplayer.PlayerStatus;

import java.util.ArrayList;

@Setter
@Getter
public class CommandOutput {
    private String command;
    private String user;
    private int timestamp;
    private String message;
    private ArrayList<String> result = new ArrayList<>();
    private PlayerStatus stats;
    private ArrayList<ShowPlaylistOutput> showPlaylistOutput;
    private ArrayList<String> likedSongs = new ArrayList<>();
}
