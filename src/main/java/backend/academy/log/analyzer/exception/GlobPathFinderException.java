package backend.academy.log.analyzer.exception;

public class GlobPathFinderException extends RuntimeException {
    public GlobPathFinderException(String message) {
        super(message);
    }

    public GlobPathFinderException(String message, Throwable cause) {
        super(message, cause);
    }
}
