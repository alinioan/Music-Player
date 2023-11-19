package commandIO;

import lombok.Getter;
import lombok.Setter;
import musicPlayer.Filters;

import java.util.ArrayList;

@Getter
@Setter
public class CommandInput {
    String command;
    String username;
    int timestamp;
    int itemNumber;
    String type;
    Filters filters;
    int seed;
    int playlistId;
    String playlistName;

    @Override
    public String toString() {
        return "CommandInput{" +
                "command='" + command + '\'' +
                ", username='" + username + '\'' +
                ", timestamp=" + timestamp +
                ", type='" + type + '\'' +
                ", filters=" + filters +
                ", seed=" + seed +
                ", playlistId=" + playlistId +
                ", playlistName='" + playlistName + '\'' +
                '}';
    }
}
