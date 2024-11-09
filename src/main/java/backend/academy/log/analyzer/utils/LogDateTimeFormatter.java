package backend.academy.log.analyzer.utils;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class LogDateTimeFormatter {
    public static final DateTimeFormatter LOG_DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    private LogDateTimeFormatter() {
    }
}
