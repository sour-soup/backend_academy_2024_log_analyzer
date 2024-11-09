package backend.academy.log.analyzer.model;

import java.time.OffsetDateTime;
import static backend.academy.log.analyzer.utils.LogDateTimeFormatter.LOG_DATE_TIME_FORMATTER;

public record LogRecord(
    String remoteAddress,
    String remoteUser,
    OffsetDateTime dateTime,
    String request,
    int status,
    int bodyBytesSent,
    String httpReferer,
    String httpUserAgent
) {

    public String toLogLine() {
        return "%s - %s [%s] \"%s\" %d %d \"%s\" \"%s\"".formatted(
            remoteAddress,
            remoteUser,
            dateTime.format(LOG_DATE_TIME_FORMATTER),
            request,
            status,
            bodyBytesSent,
            httpReferer,
            httpUserAgent
        );
    }
}
