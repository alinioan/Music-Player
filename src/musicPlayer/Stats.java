package musicPlayer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stats {
    private String name;
    private int remainedTime;
    private String repeat;
    private boolean shuffle;
    private boolean paused;

    public Stats() {}

    public Stats(String name, int remainedTime, String repeat, boolean shuffle, boolean paused) {
        this.name = name;
        this.remainedTime = remainedTime;
        this.repeat = repeat;
        this.shuffle = shuffle;
        this.paused = paused;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "name='" + name + '\'' +
                ", remainedTime=" + remainedTime +
                ", repeat='" + repeat + '\'' +
                ", shuffle=" + shuffle +
                ", paused=" + paused +
                '}';
    }
}
