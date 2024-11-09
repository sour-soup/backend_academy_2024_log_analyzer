package backend.academy.log.analyzer.reader;

import backend.academy.log.analyzer.model.LogRecord;
import java.util.stream.Stream;

public interface LogReader {
    Stream<LogRecord> readLogs(String path);
}
