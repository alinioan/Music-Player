package app.user.artist;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Merch {
    private String name;
    private String description;
    private Integer price;

    public Merch(final String name, final String description, final Integer price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
