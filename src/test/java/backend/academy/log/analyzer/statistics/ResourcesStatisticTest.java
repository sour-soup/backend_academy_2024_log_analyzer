package backend.academy.log.analyzer.statistics;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.model.StatisticResult;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.instancio.Select.field;

class ResourcesStatisticTest {
    @Test
    @DisplayName("Should correctly count the number of requests for each resource")
    void getResult_CorrectlyCountsRequestsForEachResource() {
        // Arrange
        ResourcesStatistic statistic = new ResourcesStatistic();

        statistic.collect(createLogRecord("/home"));
        statistic.collect(createLogRecord("/about"));
        statistic.collect(createLogRecord("/home"));
        statistic.collect(createLogRecord("/contact"));

        // Act
        StatisticResult result = statistic.getResult();

        // Assert
        assertThat(result.rows())
            .containsExactly(
                List.of("/home", "2"),
                List.of("/about", "1"),
                List.of("/contact", "1")
            );
    }

    @Test
    @DisplayName("Should handle empty resource counts")
    void getResult_HandlesEmptyResourceCounts() {
        // Arrange
        ResourcesStatistic statistic = new ResourcesStatistic();

        // Act
        StatisticResult result = statistic.getResult();

        // Assert
        assertThat(result.rows()).isEmpty();
    }

    private LogRecord createLogRecord(String resource) {
        return Instancio.of(LogRecord.class)
            .set(field("request"), resource)
            .create();
    }
}
