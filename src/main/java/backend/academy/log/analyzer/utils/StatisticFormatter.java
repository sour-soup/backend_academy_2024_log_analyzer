package backend.academy.log.analyzer.utils;

import backend.academy.log.analyzer.model.StatisticResult;
import java.util.List;

public final class StatisticFormatter {
    private StatisticFormatter() {
    }

    @SuppressWarnings("MultipleStringLiterals")
    public static String formatToMarkdown(StatisticResult statisticResult) {
        String endLine = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append("#### ").append(statisticResult.name()).append(endLine);
        sb.append("| ").append(String.join(" | ", statisticResult.header())).append(" |").append(endLine);
        sb.append('|').append(" --- |".repeat(statisticResult.header().size())).append(endLine);

        for (List<String> row : statisticResult.rows()) {
            sb.append("| ").append(String.join(" | ", row)).append(" |").append(endLine);
        }

        return sb.toString();
    }

    @SuppressWarnings("MultipleStringLiterals")
    public static String formatToAdoc(StatisticResult statisticResult) {
        String endLine = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append("==== ").append(statisticResult.name()).append(endLine).append(endLine);
        sb.append("|===").append(endLine);

        sb.append("| ").append(String.join(" | ", statisticResult.header())).append(endLine);

        for (List<String> row : statisticResult.rows()) {
            sb.append("| ").append(String.join(" | ", row)).append(endLine);
        }

        sb.append("|===").append(endLine);

        return sb.toString();
    }
}
