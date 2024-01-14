package app.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Monetization implements Comparable<Monetization> {
    private double merchRevenue;
    private String mostProfitableSong;
    private int ranking;
    private double songRevenue;

    public Monetization(final double merchRevenue) {
        this.merchRevenue = merchRevenue;
        this.mostProfitableSong = "N/A";
    }

    public Monetization(final double merchRevenue, final double songRevenue) {
        this.merchRevenue = merchRevenue;
        this.songRevenue = songRevenue;
    }

    public Monetization(final double merchRevenue, final String mostProfitableSong,
                        final double songRevenue) {
        this.merchRevenue = merchRevenue;
        this.mostProfitableSong = mostProfitableSong;
        this.songRevenue = songRevenue;
    }

    /**
     * Compare to.
     *
     * @param monetization the object to be compared.
     * @return the boolean.
     */
    @Override
    public int compareTo(final Monetization monetization) {
        return Double.compare(this.songRevenue + this.merchRevenue,
                monetization.songRevenue + monetization.merchRevenue);
    }
}
