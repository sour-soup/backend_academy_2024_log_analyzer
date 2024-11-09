package backend.academy.log.analyzer.parser;

import backend.academy.log.analyzer.exception.LogParserException;
import backend.academy.log.analyzer.model.LogRecord;
import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;
import static backend.academy.log.analyzer.utils.LogDateTimeFormatter.LOG_DATE_TIME_FORMATTER;

@Log4j2
public class NginxLogParser implements LogParser {
    private static final Pattern LOG_PATTERN = Pattern.compile("""
        ^(?<remoteAddress>\\S+) - (?<remoteUser>\\S+) \\[(?<dateTime>.+?)] "(?<request>.+?)" \
        (?<status>\\d{3}) (?<bodyBytesSent>\\d+) "(?<httpReferer>.*?)" "(?<httpUserAgent>.*?)"$""");

    public LogRecord parse(String logLine) {
        Matcher matcher = LOG_PATTERN.matcher(logLine);

        if (!matcher.matches()) {
            String message = "Invalid log line format: " + logLine;
            log.error(message);
            throw new LogParserException(message);
        }

        String remoteAddress = matcher.group("remoteAddress");
        String remoteUser = matcher.group("remoteUser");
        String dateTimeString = matcher.group("dateTime");
        String request = matcher.group("request");
        int status = Integer.parseInt(matcher.group("status"));
        int bodyBytesSent = Integer.parseInt(matcher.group("bodyBytesSent"));
        String httpReferer = matcher.group("httpReferer");
        String httpUserAgent = matcher.group("httpUserAgent");

        OffsetDateTime dateTime = OffsetDateTime.parse(dateTimeString, LOG_DATE_TIME_FORMATTER);

        return new LogRecord(
            remoteAddress,
            remoteUser,
            dateTime,
            request,
            status,
            bodyBytesSent,
            httpReferer,
            httpUserAgent
        );
    }
}
