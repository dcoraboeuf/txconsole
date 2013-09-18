package net.txconsole.backend.dao.impl;

import net.txconsole.core.support.SimpleMessage;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Locale;

public final class SQLUtils {

    public static final String PARAMETERS_SEPARATOR = "||||";

    private SQLUtils() {
    }

    public static String dateToDB(LocalDate date) {
        return date.toString();
    }

    public static LocalDate dateFromDB(String str) {
        return LocalDate.parse(str);
    }

    public static String timeToDB(LocalTime time) {
        return time.toString("HH:mm");
    }

    public static LocalTime timeFromDB(String str) {
        return LocalTime.parse(str);
    }

    public static Timestamp toTimestamp(DateTime dateTime) {
        return new Timestamp(dateTime.getMillis());
    }

    public static DateTime getDateTime(ResultSet rs, String columnName) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnName);
        return getDateTime(timestamp);
    }

    public static DateTime getDateTime(Timestamp timestamp) {
        return timestamp != null ? new DateTime(timestamp.getTime(), DateTimeZone.UTC) : null;
    }

    public static <E extends Enum<E>> E getEnum(Class<E> enumClass, ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        if (value == null) {
            return null;
        } else {
            return Enum.valueOf(enumClass, value);
        }
    }

    public static Locale toLocale(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        if (StringUtils.isBlank(value)) {
            return Locale.ENGLISH;
        } else {
            return Locale.forLanguageTag(value);
        }
    }

    public static SimpleMessage getMessage(ResultSet rs, String codeColumn, String parametersColumn) throws SQLException {
        String code = rs.getString(codeColumn);
        if (StringUtils.isNotBlank(code)) {
            String parametersString = rs.getString(parametersColumn);
            String[] parameters = StringUtils.split(parametersString, PARAMETERS_SEPARATOR);
            return new SimpleMessage(code, parameters);
        } else {
            return null;
        }
    }

    public static String getMessageCode(SimpleMessage message) {
        if (message == null) {
            return null;
        } else {
            return message.getCode();
        }
    }

    public static String getMessageParameters(SimpleMessage message) {
        if (message == null) {
            return null;
        } else {
            String[] parameters = message.getParameters();
            if (parameters == null) {
                return null;
            } else {
                return StringUtils.join(parameters, PARAMETERS_SEPARATOR);
            }
        }
    }
}
