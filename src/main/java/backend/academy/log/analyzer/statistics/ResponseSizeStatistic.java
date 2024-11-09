package backend.academy.log.analyzer.statistics;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.model.StatisticResult;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResponseSizeStatistic implements Statistic {
    private static final List<String> RANGES_NAME =
        List.of("< 1KB", "1KB - 10KB", "10KB - 100KB", "100KB - 1MB", "> 1MB");
    private static final List<Integer> RANGES_VALUES =
        List.of(1024, 10 * 1024, 100 * 1024, 1024 * 1024);

    private final Map<String, Integer> sizeDistribution = new LinkedHashMap<>();

    @Override
    public void collect(LogRecord logRecord) {
        for (int i = 0; i < RANGES_VALUES.size(); i++) {
            if (logRecord.bodyBytesSent() < RANGES_VALUES.get(i)) {
                sizeDistribution.merge(RANGES_NAME.get(i), 1, Integer::sum);
                return;
            }
        }
        sizeDistribution.merge(RANGES_NAME.getLast(), 1, Integer::sum);
    }

    @Override
    public StatisticResult getResult() {
        String name = "Распределение по размеру ответа";
        List<String> header = List.of("Диапазон размера", "Количество запросов");
        List<List<String>> rows = RANGES_NAME.stream()
            .map(range -> List.of(range, String.valueOf(sizeDistribution.getOrDefault(range, 0))))
            .toList();

        return new StatisticResult(name, header, rows);
    }
}
