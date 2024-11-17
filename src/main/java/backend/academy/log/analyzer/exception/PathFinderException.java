package backend.academy.log.analyzer.exception;

public class PathFinderException extends RuntimeException {
    public PathFinderException(String message) {
        super(message);
    }

    public PathFinderException(String message, Throwable cause) {
        super(message, cause);
    }
}
