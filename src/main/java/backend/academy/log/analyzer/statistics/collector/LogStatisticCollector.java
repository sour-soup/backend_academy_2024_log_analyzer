package backend.academy.log.analyzer.statistics.collector;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.model.ReportFormat;
import backend.academy.log.analyzer.model.StatisticResult;
import backend.academy.log.analyzer.statistics.Statistic;
import backend.academy.log.analyzer.utils.StatisticFormatter;
import java.util.List;
import java.util.stream.Stream;

public class LogStatisticCollector implements StatisticsCollector {
    private final List<Statistic> statistics;

    public LogStatisticCollector(List<Statistic> statistics) {
        this.statistics = statistics;
    }

    @Override
    public void collect(Stream<LogRecord> logRecords) {
        logRecords.forEach(logRecord -> statistics.forEach(statistic -> statistic.collect(logRecord)));
    }

    @Override
    public String generateReport(ReportFormat format) {
        StringBuilder report = new StringBuilder();
        for (Statistic statistic : statistics) {
            StatisticResult result = statistic.getResult();
            report.append(
                switch (format) {
                    case MARKDOWN -> StatisticFormatter.formatToMarkdown(result);
                    case ADOC -> StatisticFormatter.formatToAdoc(result);
                }
            );
            report.append(System.lineSeparator());
        }
        return report.toString();
    }
}
