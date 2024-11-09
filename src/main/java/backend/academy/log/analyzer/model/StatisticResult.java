package backend.academy.log.analyzer.model;

import java.util.List;

public record StatisticResult(String name, List<String> header, List<List<String>> rows) {
}
