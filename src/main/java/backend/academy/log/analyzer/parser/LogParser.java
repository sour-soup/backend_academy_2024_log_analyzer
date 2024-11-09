package backend.academy.log.analyzer.parser;

import backend.academy.log.analyzer.model.LogRecord;

public interface LogParser {
    LogRecord parse(String line);
}
