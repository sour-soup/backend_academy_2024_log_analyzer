package backend.academy.log.analyzer.reader;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.parser.LogParser;
import backend.academy.log.analyzer.utils.StreamProvider;
import backend.academy.log.analyzer.utils.UrlChecker;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class NginxLogReaderTest {
    private final LogParser logParser = mock(LogParser.class);
    private final NginxLogReader logReader = new NginxLogReader(logParser);

    @Test
    @DisplayName("Should read logs from a file and parse each line")
    void readLogs_ShouldReadLogsFromFileAndParseEachLine() {
        // Arrange
        String filePath = "path/to/log";
        List<String> logLines = List.of("line1", "line2", "line3");
        List<LogRecord> expectedRecords = logLines.stream().map(logParser::parse).toList();

        try (MockedStatic<StreamProvider> mockedFileReader = mockStatic(StreamProvider.class)) {
            mockedFileReader.when(() -> StreamProvider.getStreamFromFile(filePath))
                .thenReturn(logLines.stream());

            for (int i = 0; i < logLines.size(); i++) {
                when(logParser.parse(logLines.get(i))).thenReturn(expectedRecords.get(i));
            }

            // Act
            Stream<LogRecord> result = logReader.readLogs(filePath);

            // Assert
            assertThat(result.toList()).isEqualTo(expectedRecords);
        }
    }

    @Test
    @DisplayName("Should read logs from URL and parse each line")
    void readLogs_ShouldReadLogsFromUrlAndParseEachLine() {
        // Arrange
        String url = "https://example.com/log";
        List<String> logLines = List.of("line1", "line2", "line3");
        List<LogRecord> expectedRecords = logLines.stream().map(logParser::parse).toList();

        try (MockedStatic<UrlChecker> mockedUrlChecker = mockStatic(UrlChecker.class);
             MockedStatic<StreamProvider> mockedFileReader = mockStatic(StreamProvider.class)) {

            mockedUrlChecker.when(() -> UrlChecker.isUrl(url)).thenReturn(true);
            mockedFileReader.when(() -> StreamProvider.getStreamFromUrl(url)).thenReturn(logLines.stream());

            for (int i = 0; i < logLines.size(); i++) {
                when(logParser.parse(logLines.get(i))).thenReturn(expectedRecords.get(i));
            }

            // Act
            Stream<LogRecord> result = logReader.readLogs(url);

            // Assert
            assertThat(result.toList()).isEqualTo(expectedRecords);
        }
    }
}
