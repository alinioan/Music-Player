package commandIO;

import lombok.Getter;
import lombok.Setter;
import musicPlayer.Stats;

import java.util.ArrayList;

@Setter
@Getter
public class CommandOutput {
    String command;
    String user;
    int timestamp;
    String message;
    ArrayList<String> result = new ArrayList<>();
    Stats stats;

    @Override
    public String toString() {
        return "CommandOutput{" +
                "command='" + command + '\'' +
                ", user='" + user + '\'' +
                ", timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", result=" + result +
                ", stats=" + stats +
                '}';
    }
}
