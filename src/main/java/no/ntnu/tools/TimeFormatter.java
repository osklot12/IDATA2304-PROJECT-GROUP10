package no.ntnu.tools;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * A class formatting the current time.
 */
public class TimeFormatter {
    /**
     * Does not allow creating instances of the class.
     */
    private TimeFormatter() {}

    /**
     * Formats the current time into HH:mm format.
     *
     * @return the current time formatted
     */
    public static String now() {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return currentTime.format(formatter);
    }
}
