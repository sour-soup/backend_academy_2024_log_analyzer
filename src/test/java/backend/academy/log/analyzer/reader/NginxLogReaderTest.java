package backend.academy.log.analyzer.reader;

import backend.academy.log.analyzer.exception.LogReaderException;
import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.parser.LogParser;
import backend.academy.log.analyzer.utils.UrlChecker;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NginxLogReaderTest {
    private final LogParser logParser = mock(LogParser.class);
    private final NginxLogReader logReader = new NginxLogReader(logParser);

    @TempDir
    Path tempDir;

    @SneakyThrows @Test
    @DisplayName("Should read logs from a file and parse each line")
    void readLogs_ShouldReadLogsFromFileAndParseEachLine() {
        // Arrange
        String filename = "log.txt";
        String filePath = tempDir.resolve(filename).toString();
        List<String> logLines = List.of("line1", "line2", "line3");
        List<LogRecord> expectedRecords = logLines.stream().map(this::createLogRecord).toList();

        Files.write(tempDir.resolve(filename), logLines);

        for (int i = 0; i < logLines.size(); i++) {
            when(logParser.parse(logLines.get(i))).thenReturn(expectedRecords.get(i));
        }

        // Act
        Stream<LogRecord> result = logReader.readLogs(filePath);

        // Assert
        assertThat(result.toList()).isEqualTo(expectedRecords);
    }

    @Test
    @DisplayName("Should throw LogReaderException when file cannot be read")
    void read_logs_ShouldThrowLogReaderException_WhenFileIsNotReadable() {
        // Arrange
        String invalidFilePath = tempDir.resolve("invalid-path.txt").toString();

        // Act & Assert
        assertThatThrownBy(() -> logReader.readLogs(invalidFilePath))
            .isInstanceOf(LogReaderException.class)
            .hasMessageContaining("Error while reading file");
    }

    @SneakyThrows
    @Test
    @DisplayName("Should read logs from URL and parse each line")
    void readLogs_ShouldReadLogsFromUrlAndParseEachLine() {
        String url = "https://example.com/log";
        List<String> logLines = List.of("line1", "line2", "line3");
        List<LogRecord> expectedRecords = logLines.stream().map(this::createLogRecord).toList();

        for (int i = 0; i < logLines.size(); i++) {
            when(logParser.parse(logLines.get(i))).thenReturn(expectedRecords.get(i));
        }

        try (MockedStatic<UrlChecker> mockedUrlChecker = Mockito.mockStatic(UrlChecker.class);
             MockedStatic<HttpClient> mockedHttpClient = Mockito.mockStatic(HttpClient.class)) {

            HttpClient mockHttpClient = createMockHttpClient(url, logLines);
            mockedUrlChecker.when(() -> UrlChecker.isUrl(url)).thenReturn(true);
            mockedHttpClient.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);

            // Act
            Stream<LogRecord> result = logReader.readLogs(url);

            // Assert
            assertThat(result.toList()).isEqualTo(expectedRecords);
        }
    }

    @SneakyThrows
    @Test
    @DisplayName("Should throw LogReaderException when URL request fails")
    void readLogs_ShouldThrowLogReaderException_WhenUrlRequestFails() {
        // Arrange
        String url = "https://example.com/log";

        try (MockedStatic<UrlChecker> mockedUrlChecker = Mockito.mockStatic(UrlChecker.class);
             MockedStatic<HttpClient> mockedHttpClient = Mockito.mockStatic(HttpClient.class)) {

            mockedUrlChecker.when(() -> UrlChecker.isUrl(url)).thenReturn(true);

            HttpClient mockClient = mock(HttpClient.class);
            when(mockClient.send(Mockito.any(), Mockito.any())).thenThrow(new IOException("Network error"));
            mockedHttpClient.when(HttpClient::newHttpClient).thenReturn(mockClient);

            // Act & Assert
            assertThatThrownBy(() -> logReader.readLogs(url))
                .isInstanceOf(LogReaderException.class)
                .hasMessageContaining("Error while reading file by url");
        }
    }

    @SneakyThrows
    private HttpClient createMockHttpClient(String url, List<String> logLines) {
        HttpClient mockClient = mock(HttpClient.class);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);

        when(mockResponse.body())
            .thenReturn(String.join("\n", logLines));

        when(mockClient.send(eq(request), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        return mockClient;
    }

    private LogRecord createLogRecord(String remoteAddress) {
        return Instancio.of(LogRecord.class)
            .set(field("remoteAddress"), remoteAddress)
            .create();
    }
}
