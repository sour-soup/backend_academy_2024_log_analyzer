package backend.academy.log.analyzer.utils;

import backend.academy.log.analyzer.cli.LogAnalyzerParameters;
import backend.academy.log.analyzer.model.LogRecord;
import java.time.OffsetDateTime;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class FilterLogRecordUtils {
    private FilterLogRecordUtils() {
    }

    public static Stream<LogRecord> applyFilters(LogAnalyzerParameters parameters, Stream<LogRecord> logRecordStream) {
        OffsetDateTime from = parameters.from();
        OffsetDateTime to = parameters.to();
        String filterField = parameters.filterField();
        Pattern filterValue = parameters.filterValue();

        Stream<LogRecord> filteredStream = logRecordStream;
        if (from != null) {
            filteredStream = filteredStream.filter(log -> !log.dateTime().isBefore(from));
        }
        if (to != null) {
            filteredStream = filteredStream.filter(log -> !log.dateTime().isAfter(to));
        }
        if (filterField != null && filterValue != null) {
            filteredStream = filteredStream.filter(log -> {
                String fieldValue = getFieldValue(log, filterField);
                return fieldValue != null && filterValue.matcher(fieldValue).matches();
            });
        }

        return filteredStream;
    }

    private static String getFieldValue(LogRecord logRecord, String fieldName) {
        return switch (fieldName) {
            case "remoteAddress" -> logRecord.remoteAddress();
            case "remoteUser" -> logRecord.remoteUser();
            case "request" -> logRecord.request();
            case "httpReferer" -> logRecord.httpReferer();
            case "httpUserAgent" -> logRecord.httpUserAgent();
            default -> null;
        };
    }
}
