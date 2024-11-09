package backend.academy.log.analyzer.statistics;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.model.StatisticResult;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.instancio.Select.field;

class StatusCodesStatisticTest {
    @Test
    @DisplayName("Should collect status codes and provide correct result")
    void collect_CorrectlyCollectsStatusCodes() {
        StatusCodesStatistic statistic = new StatusCodesStatistic();

        // Arrange
        LogRecord logRecord1 = createLogRecord(200);
        LogRecord logRecord2 = createLogRecord(404);
        LogRecord logRecord3 = createLogRecord(200);

        // Act
        statistic.collect(logRecord1);
        statistic.collect(logRecord2);
        statistic.collect(logRecord3);

        StatisticResult result = statistic.getResult();

        // Assert
        assertThat(result.rows())
            .containsExactly(
                List.of("200", "2"),
                List.of("404", "1")
            );
    }

    @Test
    @DisplayName("Should handle when no records are collected")
    void collect_HandlesNoRecordsCollected() {
        // Arrange
        StatusCodesStatistic statistic = new StatusCodesStatistic();

        // Act
        StatisticResult result = statistic.getResult();

        // Assert
        assertThat(result.rows())
            .isEmpty();
    }

    private LogRecord createLogRecord(int status) {
        return Instancio.of(LogRecord.class)
            .set(field("status"), status)
            .create();
    }
}
