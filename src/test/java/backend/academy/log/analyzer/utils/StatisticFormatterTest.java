package backend.academy.log.analyzer.utils;

import backend.academy.log.analyzer.model.StatisticResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class StatisticFormatterTest {
    @Test
    @DisplayName("Should format StatisticResult to Markdown format")
    void formatToMarkdown_CorrectlyFormatsToMarkdown() {
        // Arrange
        StatisticResult statisticResult = new StatisticResult(
            "Test Statistic",
            List.of("Column1", "Column2"),
            List.of(List.of("Value1", "Value2"), List.of("Value3", "Value4"))
        );

        String expectedMarkdown = "#### Test Statistic" + System.lineSeparator() +
                                  "| Column1 | Column2 |" + System.lineSeparator() +
                                  "| --- | --- |" + System.lineSeparator() +
                                  "| Value1 | Value2 |" + System.lineSeparator() +
                                  "| Value3 | Value4 |" + System.lineSeparator();

        // Act
        String actualMarkdown = StatisticFormatter.formatToMarkdown(statisticResult);

        // Assert
        assertThat(actualMarkdown).isEqualTo(expectedMarkdown);
    }

    @Test
    @DisplayName("Should format StatisticResult to AsciiDoc format")
    void formatToAdoc_CorrectlyFormatsToAsciiDoc() {
        // Arrange
        StatisticResult statisticResult = new StatisticResult(
            "Test Statistic",
            List.of("Column1", "Column2"),
            List.of(List.of("Value1", "Value2"), List.of("Value3", "Value4"))
        );

        String expectedAsciiDoc = "==== Test Statistic" + System.lineSeparator() +
                                  System.lineSeparator() +
                                  "|===" + System.lineSeparator() +
                                  "| Column1 | Column2" + System.lineSeparator() +
                                  "| Value1 | Value2" + System.lineSeparator() +
                                  "| Value3 | Value4" + System.lineSeparator() +
                                  "|===" + System.lineSeparator();

        // Act
        String actualAsciiDoc = StatisticFormatter.formatToAdoc(statisticResult);

        // Assert
        assertThat(actualAsciiDoc).isEqualTo(expectedAsciiDoc);
    }
}
