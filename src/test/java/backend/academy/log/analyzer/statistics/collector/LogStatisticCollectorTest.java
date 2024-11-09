package backend.academy.log.analyzer.statistics.collector;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.model.ReportFormat;
import backend.academy.log.analyzer.model.StatisticResult;
import backend.academy.log.analyzer.statistics.Statistic;
import backend.academy.log.analyzer.utils.StatisticFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Stream;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LogStatisticCollectorTest {
    private Statistic mockStatistic1;
    private Statistic mockStatistic2;
    private StatisticsCollector collector;

    @BeforeEach
    void setUp() {
        mockStatistic1 = mock(Statistic.class);
        mockStatistic2 = mock(Statistic.class);
        collector = new LogStatisticCollector(List.of(mockStatistic1, mockStatistic2));
    }

    @Test
    @DisplayName("Should correctly collect statistics from log records")
    void collect_shouldCorrectlyCollectStatistics() {
        // Arrange
        LogRecord logRecord1 = mock(LogRecord.class);
        LogRecord logRecord2 = mock(LogRecord.class);
        Stream<LogRecord> logRecordStream = Stream.of(logRecord1, logRecord2);

        // Act
        collector.collect(logRecordStream);

        // Assert
        verify(mockStatistic1, times(1)).collect(logRecord1);
        verify(mockStatistic1, times(1)).collect(logRecord2);
        verify(mockStatistic2, times(1)).collect(logRecord1);
        verify(mockStatistic2, times(1)).collect(logRecord2);
    }

    @Test
    @DisplayName("Should generate report in Markdown format")
    void generateReport_GeneratesMarkdownReport() {
        // Arrange
        StatisticResult result1 = new StatisticResult(
            "Test Statistic 1",
            List.of("Header1", "Header2"),
            List.of(List.of("Value1", "Value2")));
        StatisticResult result2 = new StatisticResult(
            "Test Statistic 2",
            List.of("HeaderA", "HeaderB"),
            List.of(List.of("ValueA", "ValueB")));

        when(mockStatistic1.getResult()).thenReturn(result1);
        when(mockStatistic2.getResult()).thenReturn(result2);

        String expectedReport = StatisticFormatter.formatToMarkdown(result1) + System.lineSeparator()
                                + StatisticFormatter.formatToMarkdown(result2) + System.lineSeparator();

        // Act
        String report = collector.generateReport(ReportFormat.MARKDOWN);

        // Assert
        assertThat(report).isEqualTo(expectedReport);
    }

    @Test
    @DisplayName("Should generate report in AsciiDoc format")
    void generateReport_GeneratesAsciiDocReport() {
        // Arrange
        StatisticResult result1 = new StatisticResult("Test Statistic 1", List.of("Header1", "Header2"),
            List.of(List.of("Value1", "Value2")));
        StatisticResult result2 = new StatisticResult("Test Statistic 2", List.of("HeaderA", "HeaderB"),
            List.of(List.of("ValueA", "ValueB")));

        when(mockStatistic1.getResult()).thenReturn(result1);
        when(mockStatistic2.getResult()).thenReturn(result2);

        String expectedReport = StatisticFormatter.formatToAdoc(result1) + System.lineSeparator()
                                + StatisticFormatter.formatToAdoc(result2) + System.lineSeparator();

        // Act
        String report = collector.generateReport(ReportFormat.ADOC);

        // Assert
        assertThat(report).isEqualTo(expectedReport);
    }
}
