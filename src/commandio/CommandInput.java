package commandio;

import lombok.Getter;
import lombok.Setter;
import searchbar.Filters;

@Getter
@Setter
public class CommandInput {
    private String command;
    private String username;
    private int timestamp;
    private int itemNumber;
    private String type;
    private Filters filters;
    private int seed;
    private int playlistId;
    private String playlistName;
}
