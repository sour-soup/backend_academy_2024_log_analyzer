package backend.academy.log.analyzer.statistics;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.model.StatisticResult;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.instancio.Select.field;

class ResponseSizeStatisticTest {
    @Test
    @DisplayName("Should correctly count requests for each size range")
    void collect_CorrectlyCountsRequestsForEachSizeRange() {
        // Arrange
        ResponseSizeStatistic statistic = new ResponseSizeStatistic();

        statistic.collect(createLogRecord(512));   // < 1KB
        statistic.collect(createLogRecord(1024));  // 1KB - 10KB
        statistic.collect(createLogRecord(10240)); // 10KB - 100KB
        statistic.collect(createLogRecord(204800)); // 100KB - 1MB
        statistic.collect(createLogRecord(2048000)); // > 1MB

        // Act
        StatisticResult result = statistic.getResult();

        // Assert
        assertThat(result.rows())
            .containsExactly(
                List.of("< 1KB", "1"),
                List.of("1KB - 10KB", "1"),
                List.of("10KB - 100KB", "1"),
                List.of("100KB - 1MB", "1"),
                List.of("> 1MB", "1")
            );
    }

    @Test
    @DisplayName("Should handle when no records are collected")
    void collect_HandlesNoRecordsCollected() {
        // Arrange
        ResponseSizeStatistic statistic = new ResponseSizeStatistic();

        // Act
        StatisticResult result = statistic.getResult();

        // Assert
        assertThat(result.rows())
            .containsExactly(
                List.of("< 1KB", "0"),
                List.of("1KB - 10KB", "0"),
                List.of("10KB - 100KB", "0"),
                List.of("100KB - 1MB", "0"),
                List.of("> 1MB", "0")
            );
    }

    private LogRecord createLogRecord(int bodyBytesSent) {
        return Instancio.of(LogRecord.class)
            .set(field("bodyBytesSent"), bodyBytesSent)
            .create();
    }
}
