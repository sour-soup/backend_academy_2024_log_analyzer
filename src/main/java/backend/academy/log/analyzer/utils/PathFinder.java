package backend.academy.log.analyzer.utils;

import backend.academy.log.analyzer.exception.PathFinderException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public final class PathFinder {
    private PathFinder() {
    }

    public static List<String> getAllPaths(List<String> paths) {
        return paths.stream()
            .flatMap(path -> {
                if (UrlChecker.isUrl(path)) {
                    return Stream.of(path);
                } else {
                    return PathFinder.globPath(path).stream();
                }
            }).toList();
    }

    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    public static List<String> globPath(String path) {
        if (!containsGlobCharacters(path)) {
            return List.of(path);
        }

        String baseDirectory = extractBaseDirectory(path);
        Path basePath = Paths.get(baseDirectory);
        String globPattern = path.substring(baseDirectory.length());

        return findMatchingPaths(basePath, globPattern);
    }

    private static List<String> findMatchingPaths(Path basePath, String globPattern) {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
        try (Stream<Path> stream = Files.walk(basePath)) {
            return stream
                .filter(path -> matcher.matches(basePath.relativize(path)))
                .map(Path::toString)
                .toList();
        } catch (IOException e) {
            throw new PathFinderException("Error while searching for files matching the glob pattern", e);
        }
    }

    private static String extractBaseDirectory(String path) {
        int index = findFirstGlobCharacterIndex(path);
        int lastSeparatorIndex = path.substring(0, index).lastIndexOf(FileSystems.getDefault().getSeparator());
        return lastSeparatorIndex == -1 ? "" : path.substring(0, lastSeparatorIndex + 1);
    }

    private static int findFirstGlobCharacterIndex(String path) {
        for (int i = 0; i < path.length(); i++) {
            char ch = path.charAt(i);
            if ("*?{}[]".indexOf(ch) >= 0) {
                return i;
            }
        }
        return path.length();
    }

    private static boolean containsGlobCharacters(String path) {
        return findFirstGlobCharacterIndex(path) != path.length();
    }
}
