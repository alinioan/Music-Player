package app.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Monetization {
    double merchRevenue;
    String mostProfitableSong;
    int ranking;
    double songRevenue;

    public Monetization(int ranking) {
        this.ranking = ranking;
        this.merchRevenue = 0;
        this.mostProfitableSong = "N/A";
        this.songRevenue = 0;
    }
}
