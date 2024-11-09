package backend.academy.log.analyzer.utils;

import org.apache.commons.validator.routines.UrlValidator;

public final class UrlChecker {
    private static final UrlValidator URL_VALIDATOR = new UrlValidator();

    private UrlChecker() {
    }

    public static boolean isUrl(final String url) {
        return URL_VALIDATOR.isValid(url);
    }
}
