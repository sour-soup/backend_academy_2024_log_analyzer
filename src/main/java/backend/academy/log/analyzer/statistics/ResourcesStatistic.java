package backend.academy.log.analyzer.statistics;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.model.StatisticResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcesStatistic implements Statistic {
    private final Map<String, Integer> resourceCounts = new HashMap<>();

    @Override
    public void collect(LogRecord logRecord) {
        resourceCounts.merge(logRecord.request(), 1, Integer::sum);
    }

    @Override
    public StatisticResult getResult() {
        String name = "Запрашиваемые ресурсы";
        List<String> header = List.of("Ресурс", "Количество");
        List<List<String>> rows = resourceCounts.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .map(entry -> List.of(entry.getKey(), entry.getValue().toString()))
            .toList();
        return new StatisticResult(name, header, rows);
    }
}
