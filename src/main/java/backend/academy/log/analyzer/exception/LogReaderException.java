package backend.academy.log.analyzer.exception;

public class LogReaderException extends RuntimeException {
    public LogReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
