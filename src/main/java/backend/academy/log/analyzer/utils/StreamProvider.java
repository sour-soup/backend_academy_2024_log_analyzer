package backend.academy.log.analyzer.utils;

import backend.academy.log.analyzer.exception.StreamProviderException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;

@Log4j2
public final class StreamProvider {
    private StreamProvider() {
    }

    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    public static Stream<String> getStreamFromFile(String path) {
        try {
            Path filePath = Paths.get(path).normalize();
            return Files.lines(filePath);
        } catch (IOException | InvalidPathException e) {
            String message = "Error while reading file: " + path;
            log.error(message, e);
            throw new StreamProviderException(message, e);
        }
    }

    public static Stream<String> getStreamFromUrl(String url) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString()).body().lines();
        } catch (IOException | InterruptedException | IllegalArgumentException e) {
            String message = "Error while reading file by url: " + url;
            log.error(message, e);
            throw new StreamProviderException(message, e);
        }
    }
}
