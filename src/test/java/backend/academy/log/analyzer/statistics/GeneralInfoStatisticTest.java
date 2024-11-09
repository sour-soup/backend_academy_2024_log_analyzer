package backend.academy.log.analyzer.statistics;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.model.StatisticResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.util.List;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class GeneralInfoStatisticTest {
    @Test
    @DisplayName("Should calculate correct average response size and 95th percentile")
    void getResult_CorrectlyCalculatesAverageAndPercentile() {
        // Arrange
        List<String> fileNames = List.of("file1", "file2");
        OffsetDateTime startDate = OffsetDateTime.parse("2024-11-01T00:00:00+00:00");
        OffsetDateTime endDate = OffsetDateTime.parse("2024-11-02T00:00:00+00:00");
        GeneralInfoStatistic statistic = new GeneralInfoStatistic(fileNames, startDate, endDate);

        statistic.collect(new LogRecord(
            "192.168.0.1", "user1", OffsetDateTime.now(), "GET /test", 200, 512, "https://example.com", "Mozilla/5.0"
        ));
        statistic.collect(new LogRecord(
            "192.168.0.2", "user2", OffsetDateTime.now(), "GET /test", 200, 2048, "https://example.com", "Mozilla/5.0"
        ));
        statistic.collect(new LogRecord(
            "192.168.0.3", "user3", OffsetDateTime.now(), "GET /test", 200, 512, "https://example.com", "Mozilla/5.0"
        ));

        // Act
        StatisticResult result = statistic.getResult();

        // Assert
        assertThat(result.rows())
            .extracting(List::getFirst)
            .containsExactly("Файл(-ы)", "Начальная дата", "Конечная дата",
                "Количество запросов", "Средний размер ответа", "95-й персентиль размера ответа");

        assertThat(result.rows())
            .filteredOn(row -> row.getFirst().equals("Средний размер ответа"))
            .extracting(row -> row.get(1))
            .containsExactly("1024b");

        assertThat(result.rows())
            .filteredOn(row -> row.getFirst().equals("95-й персентиль размера ответа"))
            .extracting(row -> row.get(1))
            .containsExactly("2048b");
    }

    @Test
    @DisplayName("Should handle empty response sizes")
    void getResult_HandlesEmptyResponseSizes() {
        // Arrange
        List<String> fileNames = List.of("file1");
        OffsetDateTime startDate = OffsetDateTime.parse("2024-11-01T00:00:00+00:00");
        OffsetDateTime endDate = OffsetDateTime.parse("2024-11-02T00:00:00+00:00");
        GeneralInfoStatistic statistic = new GeneralInfoStatistic(fileNames, startDate, endDate);

        // Act
        StatisticResult result = statistic.getResult();

        // Assert
        assertThat(result.rows())
            .filteredOn(row -> row.getFirst().equals("Количество запросов"))
            .extracting(row -> row.get(1))
            .containsExactly("0");
    }

    @Test
    @DisplayName("Should handle null start and end dates")
    void getResult_HandlesNullDates() {
        // Arrange
        List<String> fileNames = List.of("file1");
        GeneralInfoStatistic statistic = new GeneralInfoStatistic(fileNames, null, null);

        statistic.collect(new LogRecord(
            "192.168.0.1", "user1", OffsetDateTime.now(), "GET /test", 200, 1024, "https://example.com", "Mozilla/5.0"
        ));

        // Act
        StatisticResult result = statistic.getResult();

        // Assert
        assertThat(result.rows())
            .filteredOn(row -> row.getFirst().equals("Начальная дата"))
            .extracting(row -> row.get(1))
            .containsExactly("-");

        assertThat(result.rows())
            .filteredOn(row -> row.getFirst().equals("Конечная дата"))
            .extracting(row -> row.get(1))
            .containsExactly("-");
    }
}
