package app.user.artist;

import lombok.Getter;
import lombok.Setter;

import java.util.StringTokenizer;

@Getter
@Setter
public class Event {
    private String name;
    private String description;
    private String date;

    /**
     * Event constructor.
     *
     * @param name the name.
     * @param description the description.
     * @param date the date.
     */
    public Event(final String name, final String description, final String date) {
        this.name = name;
        this.description = description;
        this.date = date;
    }

    /**
     * Check if a date is valid.
     *
     * @return true if the date is valid.
     */
    public boolean isValidDate() {
        StringTokenizer dateTokens = new StringTokenizer(this.date, "-");
        if (this.date.matches("\\d{2}-\\d{2}-\\d{4}")) {
            int day = Integer.parseInt(dateTokens.nextToken());
            int month = Integer.parseInt(dateTokens.nextToken());
            int year = Integer.parseInt(dateTokens.nextToken());
            return isValidDay(day, month, year) && isValidMonth(month) && isValidYear(year);
        }
        return false;
    }

    private static boolean isValidDay(final int day, final int month, final int year) {
        return day >= 1 && day <= daysInMonth(month, year);
    }

    private static boolean isValidMonth(final int month) {
        return month >= 1 && month <= 12;
    }

    private static boolean isValidYear(final int year) {
        return year >= 1;
    }

    private static int daysInMonth(final int month, final int year) {
        return switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11 -> 30;
            case 2 -> isLeapYear(year) ? 29 : 28;
            default -> -1;
        };
    }

    private static boolean isLeapYear(final int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}
