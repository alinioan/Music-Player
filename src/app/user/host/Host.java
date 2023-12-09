package app.user.host;

import app.user.User;
import app.utils.Enums;

public class Host extends User {
    /**
     * Instantiates a new User.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public Host(String username, int age, String city) {
        super(username, age, city);
        super.setUserType(Enums.UserType.HOST);
    }
}
