package app.utils;

public class Enums { // diferite enumuri, le-am gurpat pe toate intr-un loc
    public enum Genre {
        POP,
        ROCK,
        RAP
    } // etc

    public enum Visibility {
        PUBLIC,
        PRIVATE
    }

    public enum SearchType {
        SONG,
        PLAYLIST,
        PODCAST
    }

    public enum UserType {
        NORMAL,
        ARTIST,
        HOST
    }

    public enum RepeatMode {
        REPEAT_ALL, REPEAT_ONCE, REPEAT_INFINITE, REPEAT_CURRENT_SONG, NO_REPEAT,
    }

    public enum PlayerSourceType {
        LIBRARY, PLAYLIST, PODCAST
    }

    /**
     * Convert the type given as a string to the specific enum field.
     *
     * @param type the type.
     * @return the enum.
     */
    public static UserType getTypeFromString(String type) {
        switch (type) {
            case "artist" -> { return UserType.ARTIST; }
            case "host" -> { return UserType.HOST; }
            default -> { return UserType.NORMAL; }
        }
    }
}
