package frc.robot.utility.logger;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class RobotLogger {
    private static final String format = "[{0}]: {1}";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SS");

    private static ArrayList<String> alreadyPrinted = new ArrayList<>();

    public interface Loggable {
        boolean canLog();
    }

    public static void reset() {
        alreadyPrinted.clear();
    }

    public static void logOnce(String message) {
        if(logIf(message, () -> !alreadyPrinted.contains(message)))
            alreadyPrinted.add(message);
    }

    public static boolean logIf(String message, Loggable loggable) {
        boolean out = loggable.canLog();
        if(out)
            log(message);

        return out;
    }

    public static void log(String message) {
        String out = MessageFormat.format(format, getDateTimeFormmatted(), message);
        System.out.println(out);
    }

    private static String getDateTimeFormmatted() {
        LocalDate date = LocalDate.now();
        return date.format(formatter);
    }
}
