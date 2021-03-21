package frc.robot.utility.logger;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Logging utilities for robot printouts to reduce the amount of RIOLog spam.
 */
public class RobotLogger {
    /**
     * Format for overall messages
     */
    private static final String format = "[{0}]: {1}";

    /**
     * Date time formatter with format of hours(24):minutes:seconds.fraction/seconds
     */
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
                    .withLocale(Locale.getDefault())
                    .withZone(ZoneId.systemDefault());

    /**
     * Defines an interface for determining whether a message will be printed or not.
     *
     * @see RobotLogger#logIf(String, Loggable)
     */
    public interface Loggable { boolean canLog(); }

    private static ArrayList<String> alreadyPrinted = new ArrayList<>();
    private static HashMap<String, String> lastPrint = new HashMap<>();

    /**
     * Resets the memory of both collections so that any calls to
     * logOnce(String) or logOnChange(String, String) will perform
     * as they did on first init.
     */
    public static void reset() {
        alreadyPrinted.clear();
        lastPrint.clear();
    }

    /**
     * Logs a message once, even if called multiple times.
     *
     * @param message Message to send
     */
    public static void logOnce(String message) {
        if(logIf(message, () -> !alreadyPrinted.contains(message)))
            alreadyPrinted.add(message);
    }

    /**
     * Logs a message only if the value has changed since the last call.
     *
     * @param id Unique identifier for the message
     * @param message The message to send
     * @return Whether or not the message was printed
     */
    public static boolean logOnChange(String id, String message) {
        // First time the message is sent
        if(!lastPrint.containsKey(id)) {
            log(message);
            lastPrint.put(id, message);
            return true;
        }

        boolean wasPrinted = logIf(message, () -> !lastPrint.get(id).equalsIgnoreCase(message));
        lastPrint.put(id, message);

        return wasPrinted;
    }

    /**
     * Logs a message if the condition provided is true
     *
     * @param message The message to send
     * @param loggable The interface that returns the result of the condition
     * @return Whether or not the message was printed.
     */
    public static boolean logIf(String message, Loggable loggable) {
        boolean wasPrinted = loggable.canLog();
        if(wasPrinted)
            log(message);

        return wasPrinted;
    }

    /**
     * Prints a message to the RIOLog with date time formatting.
     * @param message Message to send
     */
    public static void log(String message) {
        String out = MessageFormat.format(format, getDateTimeFormmatted(), message);
        System.out.println(out);
    }

    /**
     * Formats the current date and time and returns the result.
     *
     * @return Formatted date time
     */
    private static String getDateTimeFormmatted() {
        LocalDateTime date = LocalDateTime.now();
        return date.format(formatter);
    }
}
