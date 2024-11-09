package backend.academy.log.analyzer.statistics.collector;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.model.ReportFormat;
import java.util.stream.Stream;

public interface StatisticsCollector {
    void collect(Stream<LogRecord> logRecordStream);

    String generateReport(ReportFormat format);
}
