package backend.academy.log.analyzer.cli;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.reader.LogReader;
import backend.academy.log.analyzer.statistics.collector.StatisticsCollector;
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

        Stream<LogRecord> logRecordStream = parameters.paths().stream()
            .flatMap(reader::readLogs);

        Stream<LogRecord> filteredStream = applyFilters(parameters, logRecordStream);

        collector.collect(filteredStream);

        return collector.generateReport(parameters.format());
    }

}
