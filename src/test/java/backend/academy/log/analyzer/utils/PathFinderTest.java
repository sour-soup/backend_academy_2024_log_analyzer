package backend.academy.log.analyzer.utils;

import backend.academy.log.analyzer.exception.PathFinderException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class PathFinderTest {

    @TempDir
    Path tempDir;

    @SneakyThrows
    @Test
    @DisplayName("getAllPaths: Should return both URL and expanded file paths")
    void getAllPaths_ShouldReturnCorrectPaths() {
        // Arrange
        Path file1 = Files.createFile(tempDir.resolve("file1.log"));
        Path file2 = Files.createFile(tempDir.resolve("file2.log"));

        String separator = FileSystems.getDefault().getSeparator();
        String globPattern = tempDir + separator + "*.log";

        List<String> inputPaths = List.of(
            "https://example.com",
            globPattern
        );

        // Act
        List<String> result = PathFinder.getAllPaths(inputPaths);

        // Assert
        assertThat(result).containsExactlyInAnyOrder(
            "https://example.com",
            file1.toString(),
            file2.toString()
        );
    }


    @Test
    @DisplayName("globPath: Should return the same path if no glob characters are present")
    void globPath_ShouldReturnSamePath_IfNoGlobCharacters() {
        // Arrange
        String inputPath = tempDir.resolve("file.log").toString();

        // Act
        List<String> result = PathFinder.globPath(inputPath);

        // Assert
        assertThat(result).containsExactly(inputPath);
    }

    @SneakyThrows
    @Test
    @DisplayName("globPath: Should correctly expand glob pattern")
    void globPath_ShouldExpandGlobPattern() {
        // Arrange
        Path file1 = Files.createFile(tempDir.resolve("file1.log"));
        Path file2 = Files.createFile(tempDir.resolve("file2.log"));

        String separator = FileSystems.getDefault().getSeparator();
        String globPattern = tempDir.toAbsolutePath() + separator + "*.log";

        // Act
        List<String> result = PathFinder.globPath(globPattern);

        // Assert
        assertThat(result).containsExactlyInAnyOrder(file1.toString(), file2.toString());
    }

    @SneakyThrows
    @Test
    @DisplayName("globPath: Should correctly expand recursive glob pattern with **")
    void globPath_ShouldExpandRecursiveGlobPattern() {
        // Arrange
        Path subDir1 = Files.createDirectories(tempDir.resolve("subdir1"));
        Path subDir2 = Files.createDirectories(tempDir.resolve("subdir1/subdir2"));

        Path file1 = Files.createFile(tempDir.resolve("file1.log"));
        Path file2 = Files.createFile(subDir1.resolve("file2.log"));
        Path file3 = Files.createFile(subDir2.resolve("file3.log"));
        Path unrelatedFile = Files.createFile(tempDir.resolve("unrelated.txt"));

        String separator = FileSystems.getDefault().getSeparator();
        String globPattern = tempDir + separator + "**.log";

        // Act
        List<String> result = PathFinder.globPath(globPattern);

        // Assert
        assertThat(result).containsExactlyInAnyOrder(
            file1.toString(),
            file2.toString(),
            file3.toString()
        ).doesNotContain(unrelatedFile.toString());
    }

    @Test
    @DisplayName("globPath: Should throw exception if base directory does not exist")
    void globPath_ShouldThrowException_IfBaseDirectoryDoesNotExist() {
        // Arrange
        String separator = FileSystems.getDefault().getSeparator();
        String globPattern = tempDir + separator + "nonexistent-dir" + separator + "*.log";

        // Act & Assert
        assertThatThrownBy(() -> PathFinder.globPath(globPattern))
            .isInstanceOf(PathFinderException.class)
            .hasMessageContaining("Error while searching for files matching the glob pattern");
    }
}
