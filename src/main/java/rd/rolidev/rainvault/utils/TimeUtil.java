package rd.rolidev.rainvault.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static String formatTimestamp(long timestamp) {
        return DATE_FORMAT.format(new Date(timestamp));
    }

    public static String formatDuration(long milliseconds) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);

        if (days > 0) {
            return days + "д " + (hours % 24) + "ч";
        } else if (hours > 0) {
            return hours + "ч " + (minutes % 60) + "м";
        } else if (minutes > 0) {
            return minutes + "м " + (seconds % 60) + "с";
        } else {
            return seconds + "с";
        }
    }

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public static boolean isExpired(long timestamp, long duration, TimeUnit unit) {
        long expirationTime = timestamp + unit.toMillis(duration);
        return System.currentTimeMillis() > expirationTime;
    }
}
