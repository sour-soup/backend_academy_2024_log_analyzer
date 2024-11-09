package backend.academy.log.analyzer.reader;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.parser.LogParser;
import backend.academy.log.analyzer.utils.StreamProvider;
import backend.academy.log.analyzer.utils.UrlChecker;
import java.util.stream.Stream;

public class NginxLogReader implements LogReader {
    private final LogParser logParser;

    public NginxLogReader(LogParser logParser) {
        this.logParser = logParser;
    }

    @Override
    public Stream<LogRecord> readLogs(String path) {
        Stream<String> stream;
        if (UrlChecker.isUrl(path)) {
            stream = StreamProvider.getStreamFromUrl(path);
        } else {
            stream = StreamProvider.getStreamFromFile(path);
        }
        return stream.map(logParser::parse);
    }
}
