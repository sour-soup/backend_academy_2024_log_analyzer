package backend.academy.log.analyzer.cli;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.reader.LogReader;
import backend.academy.log.analyzer.statistics.collector.StatisticsCollector;
import backend.academy.log.analyzer.utils.GlobPathFinder;
import backend.academy.log.analyzer.utils.UrlChecker;
import java.util.List;
import java.util.stream.Stream;
import static backend.academy.log.analyzer.utils.FilterLogRecordUtils.applyFilters;

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

        Stream<LogRecord> filteredStream = applyFilters(parameters, logRecordStream);

        collector.collect(filteredStream);

        return collector.generateReport(parameters.format());
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
