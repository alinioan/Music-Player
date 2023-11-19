package musicPlayer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String username;
    private int age;
    private String city;
    private MusicPlayer player = new MusicPlayer();

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", age=" + age +
                ", city='" + city + '\'' +
                ", player=" + player +
                '}';
    }
}
