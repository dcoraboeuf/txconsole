package net.txconsole.core.support;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public final class TimeUtils {

    private TimeUtils() {
    }

    public static DateTime now() {
        return DateTime.now(DateTimeZone.UTC);
    }
}
