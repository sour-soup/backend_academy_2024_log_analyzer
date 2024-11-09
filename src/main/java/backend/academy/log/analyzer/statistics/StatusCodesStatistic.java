package backend.academy.log.analyzer.statistics;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.model.StatisticResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusCodesStatistic implements Statistic {
    private final Map<Integer, Integer> statusCounts = new HashMap<>();

    @Override
    public void collect(LogRecord logRecord) {
        statusCounts.merge(logRecord.status(), 1, Integer::sum);
    }

    @Override
    public StatisticResult getResult() {
        String name = "Коды ответа";
        List<String> header = List.of("Код", "Количество");
        List<List<String>> rows = statusCounts.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .map(entry -> List.of(String.valueOf(entry.getKey()), String.valueOf(entry.getValue())))
            .toList();
        return new StatisticResult(name, header, rows);
    }
}
