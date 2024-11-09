package backend.academy.log.analyzer.statistics;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.model.StatisticResult;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HourStatistic implements Statistic {
    private final Map<Integer, Integer> hourlyCounts = new HashMap<>();

    @Override
    public void collect(LogRecord logRecord) {
        int hour = logRecord.dateTime().toLocalDateTime().getHour();
        hourlyCounts.merge(hour, 1, Integer::sum);
    }

    @Override
    public StatisticResult getResult() {
        String name = "Количество запросов по часам";

        List<String> header = List.of("Час", "Количество запросов");
        List<List<String>> rows = hourlyCounts.entrySet().stream()
            .sorted(Comparator.comparingInt(Map.Entry::getKey))
            .map(entry -> List.of(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())))
            .toList();

        return new StatisticResult(name, header, rows);
    }
}
