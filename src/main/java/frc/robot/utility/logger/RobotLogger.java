package frc.robot.utility.logger;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class RobotLogger {
    private static final String format = "[{0}]: {1}";
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("HH:mm:ss:SSS")
                    .withLocale(Locale.getDefault())
                    .withZone(ZoneId.systemDefault());

    public interface Loggable {
        boolean canLog();
    }

    private static ArrayList<String> alreadyPrinted = new ArrayList<>();
    private static HashMap<String, String> lastPrint = new HashMap<>();

    public static void reset() {
        alreadyPrinted.clear();
        lastPrint.clear();
    }

    public static void logOnce(String message) {
        if(logIf(message, () -> !alreadyPrinted.contains(message)))
            alreadyPrinted.add(message);
    }

    public static void logOnChange(String id, String message) {
        if(!lastPrint.containsKey(id)) {
            log(message);
            lastPrint.put(id, message);
            return;
        }

        logIf(message, () -> !lastPrint.get(id).equalsIgnoreCase(message));
        lastPrint.put(id, message);
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
        LocalDateTime date = LocalDateTime.now();
        return date.format(formatter);
    }
}
