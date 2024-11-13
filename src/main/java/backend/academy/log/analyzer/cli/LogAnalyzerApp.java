package backend.academy.log.analyzer.cli;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.reader.LogReader;
import backend.academy.log.analyzer.statistics.collector.StatisticsCollector;
import backend.academy.log.analyzer.utils.GlobPathFinder;
import backend.academy.log.analyzer.utils.UrlChecker;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LogAnalyzerApp {
    private final LogAnalyzerParameters parameters;
    private final LogReader reader;
    private final StatisticsCollector collector;

    public LogAnalyzerApp(LogAnalyzerParameters parameters, LogReader reader, StatisticsCollector collector) {
        this.parameters = parameters;
        this.reader = reader;
        this.collector = collector;
    }

    public String run() {
        parameters.paths(getAllPaths(parameters.paths()));

        Stream<LogRecord> logRecordStream = parameters.paths().stream()
            .flatMap(reader::readLogs);

        Stream<LogRecord> filteredStream = applyFilters(logRecordStream);

        collector.collect(filteredStream);

        return collector.generateReport(parameters.format());
    }

    private Stream<LogRecord> applyFilters(Stream<LogRecord> logRecordStream) {
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

    private String getFieldValue(LogRecord logRecord, String fieldName) {
        return switch (fieldName) {
            case "remoteAddress" -> logRecord.remoteAddress();
            case "remoteUser" -> logRecord.remoteUser();
            case "request" -> logRecord.request();
            case "httpReferer" -> logRecord.httpReferer();
            case "httpUserAgent" -> logRecord.httpUserAgent();
            default -> null;
        };
    }

    private static List<String> getAllPaths(List<String> paths) {
        return paths.stream()
            .flatMap(path -> {
                if (UrlChecker.isUrl(path)) {
                    return Stream.of(path);
                } else {
                    return GlobPathFinder.globPath(path).stream();
                }
            }).toList();
    }
}
