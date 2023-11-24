package musicplayer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerStatus {
    private String name;
    private int remainedTime;
    private String repeat;
    private boolean shuffle;
    private boolean paused;

    public PlayerStatus() {

    }

    public PlayerStatus(final String name, final int remainedTime, final String repeat,
                        final boolean shuffle, final boolean paused) {
        this.name = name;
        this.remainedTime = remainedTime;
        this.repeat = repeat;
        this.shuffle = shuffle;
        this.paused = paused;
    }

    /**
     * To string method
     * @return the object as string
     */
    @Override
    public String toString() {
        return "Stats{"
                + "name='" + name + '\''
                + ", remainedTime=" + remainedTime
                + ", repeat='" + repeat + '\''
                + ", shuffle=" + shuffle
                + ", paused=" + paused
                + '}';
    }
}
