package backend.academy.log.analyzer.reader;

import backend.academy.log.analyzer.exception.LogReaderException;
import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.parser.LogParser;
import backend.academy.log.analyzer.utils.UrlChecker;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.inject.Inject;
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

public class NginxLogReader implements LogReader {
    private final LogParser logParser;

    @Inject
    public NginxLogReader(LogParser logParser) {
        this.logParser = logParser;
    }

    @Override
    public Stream<LogRecord> readLogs(String path) {
        Stream<String> stream;
        if (UrlChecker.isUrl(path)) {
            stream = getStreamFromUrl(path);
        } else {
            stream = getStreamFromFile(path);
        }
        return stream.map(logParser::parse);
    }

    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    private Stream<String> getStreamFromFile(String path) {
        try {
            Path filePath = Paths.get(path).normalize();
            return Files.lines(filePath);
        } catch (IOException | InvalidPathException e) {
            throw new LogReaderException("Error while reading file: " + path, e);
        }
    }

    private Stream<String> getStreamFromUrl(String url) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString()).body().lines();
        } catch (IOException | InterruptedException | IllegalArgumentException e) {
            throw new LogReaderException("Error while reading file by url: " + url, e);
        }
    }
}
