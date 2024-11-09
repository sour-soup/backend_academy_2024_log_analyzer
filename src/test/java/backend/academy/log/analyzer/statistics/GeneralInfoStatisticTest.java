package backend.academy.log.analyzer.statistics;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.model.StatisticResult;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.instancio.Select.field;

class GeneralInfoStatisticTest {
    @Test
    @DisplayName("Should calculate correct average response size and 95th percentile")
    void getResult_CorrectlyCalculatesAverageAndPercentile() {
        // Arrange
        List<String> fileNames = List.of("file1", "file2");
        GeneralInfoStatistic statistic = new GeneralInfoStatistic(fileNames, null, null);

        statistic.collect(createLogRecord(512));
        statistic.collect(createLogRecord(2048));
        statistic.collect(createLogRecord(512));

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
        GeneralInfoStatistic statistic = new GeneralInfoStatistic(fileNames, null, null);

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

        statistic.collect(createLogRecord(1024));

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

    private LogRecord createLogRecord(int bodyBytesSent) {
        return Instancio.of(LogRecord.class)
            .set(field("bodyBytesSent"), bodyBytesSent)
            .create();
    }
}
