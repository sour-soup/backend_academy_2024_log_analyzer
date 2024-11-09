package backend.academy.log.analyzer.utils;

import backend.academy.log.analyzer.exception.StreamProviderException;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class StreamProviderTest {
    @TempDir
    Path tempDirectory;

    @SneakyThrows
    @Test
    @DisplayName("Should return stream of lines when file exists")
    void getStreamFromFile_ShouldReturnStreamOfLines_WhenFileExists() {
        // Arrange
        Path tempFile = Files.createFile(tempDirectory.resolve("testFile.txt"));
        List<String> expectedLines = List.of("line1", "line2", "line3");
        Files.write(tempFile, expectedLines);

        // Act & Assert
        try (Stream<String> stream = StreamProvider.getStreamFromFile(tempFile.toString())) {
            List<String> actualLines = stream.toList();
            assertThat(actualLines).isEqualTo(expectedLines);
        }
    }

    @Test
    @DisplayName("Should throw FileReaderException when file does not exist")
    void getStreamFromFile_ShouldThrowFileReaderException_WhenFileDoesNotExist() {
        // Arrange
        String nonExistentPath = tempDirectory.resolve("nonexistent.txt").toString();

        // Act & Assert
        assertThatThrownBy(() -> StreamProvider.getStreamFromFile(nonExistentPath))
            .isInstanceOf(StreamProviderException.class)
            .hasMessageContaining("Error while reading file");
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return stream of lines when HTTP response is successful")
    void getStreamFromUrl_ShouldReturnStreamOfLines_WhenResponseIsSuccessful() {
        // Arrange
        String url = "https://example.com";
        List<String> expectedLines = List.of("line1", "line2", "line3");

        HttpClient mockClient = mock(HttpClient.class);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);

        when(mockResponse.body())
            .thenReturn(String.join("\n", expectedLines));

        when(mockClient.send(eq(request), eq(HttpResponse.BodyHandlers.ofString())))
            .thenReturn(mockResponse);

        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            mockedHttpClient.when(HttpClient::newHttpClient).thenReturn(mockClient);

            // Act
            try (Stream<String> stream = StreamProvider.getStreamFromUrl(url)) {
                List<String> actualLines = stream.toList();

                // Assert
                assertThat(actualLines).isEqualTo(expectedLines);
            }
        }
    }

    @Test
    @DisplayName("Should throw FileReaderException when HTTP request fails")
    void getStreamFromUrl_ShouldThrowFileReaderException_WhenRequestFails() throws Exception {
        // Arrange
        String url = "https://example.com";
        HttpClient mockClient = mock(HttpClient.class);
        when(mockClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
            .thenThrow(new IOException("Network error"));

        try (MockedStatic<HttpClient> mockedHttpClient = mockStatic(HttpClient.class)) {
            mockedHttpClient.when(HttpClient::newHttpClient).thenReturn(mockClient);

            // Act & Assert
            assertThatThrownBy(() -> StreamProvider.getStreamFromUrl(url))
                .isInstanceOf(StreamProviderException.class)
                .hasMessageContaining("Error while reading file by url");
        }
    }
}
