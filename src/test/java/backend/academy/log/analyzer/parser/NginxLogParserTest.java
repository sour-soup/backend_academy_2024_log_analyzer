package backend.academy.log.analyzer.parser;

import backend.academy.log.analyzer.exception.LogParserException;
import backend.academy.log.analyzer.model.LogRecord;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class NginxLogParserTest {
    private final NginxLogParser parser = new NginxLogParser();

    @Test
    @DisplayName("Should parse log line correctly")
    void parse_ShouldReturnLogRecord_WhenLogLineIsValid() {
        // Arrange
        LogRecord expectedLogRecord = new LogRecord(
            "127.0.0.1",
            "user",
            OffsetDateTime.parse("2023-12-12T18:30:12+00:00"),
            "GET /index.html HTTP/1.1",
            200,
            1234,
            "https://example.com",
            "Mozilla/5.0"
        );

        String logLine = expectedLogRecord.toLogLine();

        // Act
        LogRecord actualLogRecord = parser.parse(logLine);

        // Assert
        assertThat(actualLogRecord).isEqualTo(expectedLogRecord);
    }

    @Test
    @DisplayName("Should throw LogParserError when log line is invalid")
    void parse_ShouldThrowLogParserError_WhenLogLineIsInvalid() {
        // Arrange
        String invalidLogLine = "invalid_log_line";

        // Act & Assert
        assertThatThrownBy(() -> parser.parse(invalidLogLine))
            .isInstanceOf(LogParserException.class)
            .hasMessageContaining("Invalid log line format");
    }
}
