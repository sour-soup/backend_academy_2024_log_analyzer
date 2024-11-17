package backend.academy.log.analyzer.utils;

import backend.academy.log.analyzer.cli.LogAnalyzerParameters;
import backend.academy.log.analyzer.model.LogRecord;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

class FilterLogRecordUtilsTest {

    private LogRecord createLogRecord(
        OffsetDateTime dateTime,
        String remoteAddress
    ) {
        return Instancio.of(LogRecord.class)
            .set(field("dateTime"), dateTime)
            .set(field("remoteAddress"), remoteAddress)
            .create();
    }

    private final List<LogRecord> logs = List.of(
        createLogRecord(
            OffsetDateTime.of(2024, 11, 10, 10, 0, 0, 0, ZoneOffset.UTC),
            "192.168.0.1"
        ),
        createLogRecord(
            OffsetDateTime.of(2024, 11, 11, 15, 0, 0, 0, ZoneOffset.UTC),
            "192.168.0.2"
        ),
        createLogRecord(
            OffsetDateTime.of(2024, 11, 12, 20, 0, 0, 0, ZoneOffset.UTC),
            "10.0.0.1"
        )
    );

    @Test
    @DisplayName("Should filter logs by 'from' date")
    void applyFilters_FilterByFromDate_ShouldReturnFilteredLogs() {
        // Arrange
        LogAnalyzerParameters parameters = new LogAnalyzerParameters(
            List.of(),
            OffsetDateTime.of(2024, 11, 11, 0, 0, 0, 0, ZoneOffset.UTC),
            null,
            null,
            null,
            null,
            false
        );

        // Act
        Stream<LogRecord> result = FilterLogRecordUtils.applyFilters(parameters, logs.stream());

        // Assert
        assertThat(result).containsExactly(logs.get(1), logs.get(2));
    }

    @Test
    @DisplayName("Should filter logs by 'to' date")
    void applyFilters_FilterByToDate_ShouldReturnFilteredLogs() {
        // Arrange
        LogAnalyzerParameters parameters = new LogAnalyzerParameters(
            List.of(),
            null,
            OffsetDateTime.of(2024, 11, 11, 0, 0, 0, 0, ZoneOffset.UTC),
            null,
            null,
            null,
            false
        );

        // Act
        Stream<LogRecord> result = FilterLogRecordUtils.applyFilters(parameters, logs.stream());

        // Assert
        assertThat(result).containsExactly(logs.getFirst());
    }

    @Test
    @DisplayName("Should filter logs by 'filter-field' and 'filter-value'")
    void applyFilters_FilterByFieldAndValue_ShouldReturnFilteredLogs() {
        // Arrange
        LogAnalyzerParameters parameters = new LogAnalyzerParameters(
            null,
            null,
            null,
            null,
            "remoteAddress",
            Pattern.compile("192\\.168\\.0\\.\\d+"),
            false
        );

        // Act
        Stream<LogRecord> result = FilterLogRecordUtils.applyFilters(parameters, logs.stream());

        // Assert
        assertThat(result).containsExactly(logs.get(0), logs.get(1));
    }

    @Test
    @DisplayName("Should return all logs when no filters are applied")
    void applyFilters_NoFilters_ShouldReturnAllLogs() {
        // Arrange
        LogAnalyzerParameters parameters = new LogAnalyzerParameters(
            null, null, null, null, null, null, false
        );

        // Act
        Stream<LogRecord> result = FilterLogRecordUtils.applyFilters(parameters, logs.stream());

        // Assert
        assertThat(result).containsExactlyElementsOf(logs);
    }
}

