package backend.academy.log.analyzer.statistics;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.model.StatisticResult;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.instancio.Select.field;

class HourStatisticTest {
    @Test
    @DisplayName("Should correctly count requests per hour")
    void collect_CorrectlyCountsRequestsPerHour() {
        // Arrange
        HourStatistic statistic = new HourStatistic();

        statistic.collect(createLogRecord(10));
        statistic.collect(createLogRecord(10));
        statistic.collect(createLogRecord(11));
        statistic.collect(createLogRecord(11));
        statistic.collect(createLogRecord(12));

        // Act
        StatisticResult result = statistic.getResult();

        // Assert
        assertThat(result.rows())
            .containsExactly(
                List.of("10", "2"),
                List.of("11", "2"),
                List.of("12", "1")
            );
    }

    @Test
    @DisplayName("Should handle when no records are collected")
    void collect_HandlesNoRecordsCollected() {
        // Arrange
        HourStatistic statistic = new HourStatistic();

        // Act
        StatisticResult result = statistic.getResult();

        // Assert
        assertThat(result.rows())
            .isEmpty();
    }

    private LogRecord createLogRecord(int hour) {
        return Instancio.of(LogRecord.class)
            .set(field("dateTime"), OffsetDateTime.of(2024, 11, 9, hour, 0, 0, 0, ZoneOffset.UTC))
            .create();
    }
}
