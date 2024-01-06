package app.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Monetization implements Comparable<Monetization> {
    double merchRevenue;
    String mostProfitableSong;
    int ranking;
    double songRevenue;

    public Monetization(double merchRevenue, double songRevenue) {
        this.merchRevenue = merchRevenue;
        this.songRevenue = songRevenue;
    }

    public Monetization(double merchRevenue, String mostProfitableSong, double songRevenue) {
        this.merchRevenue = merchRevenue;
        this.mostProfitableSong = mostProfitableSong;
        this.songRevenue = songRevenue;
    }

    @Override
    public int compareTo(Monetization monetization) {
        return Double.compare(this.songRevenue + this.merchRevenue, monetization.songRevenue + monetization.merchRevenue);
    }
}
