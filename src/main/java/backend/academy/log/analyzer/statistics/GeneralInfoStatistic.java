package backend.academy.log.analyzer.statistics;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.model.StatisticResult;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneralInfoStatistic implements Statistic {
    private static final double PERCENTILE = 0.95;
    private final List<Integer> responseSizes = new ArrayList<>();
    private final List<String> fileNames;
    private final OffsetDateTime startDate;
    private final OffsetDateTime endDate;

    public GeneralInfoStatistic(List<String> fileNames, OffsetDateTime startDate, OffsetDateTime endDate) {
        this.fileNames = fileNames;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void collect(LogRecord logRecord) {
        responseSizes.add(logRecord.bodyBytesSent());
    }

    @Override
    public StatisticResult getResult() {
        Collections.sort(responseSizes);
        int countRequests = responseSizes.size();
        long totalSize = responseSizes.stream().mapToLong(i -> i).sum();
        int index = (int) Math.ceil(responseSizes.size() * PERCENTILE) - 1;

        String name = "Общая информация";
        List<String> header = List.of("Метрика", "Значение");
        List<List<String>> rows = List.of(
            List.of("Файл(-ы)", fileNames.toString()),
            List.of("Начальная дата", (startDate == null ? "-" : startDate.toString())),
            List.of("Конечная дата", (endDate == null ? "-" : endDate.toString())),
            List.of("Количество запросов", String.valueOf(countRequests)),
            List.of("Средний размер ответа", totalSize / (countRequests == 0 ? 1 : countRequests) + "b"),
            List.of("95-й персентиль размера ответа", (countRequests == 0 ? 0 : responseSizes.get(index)) + "b")
        );
        return new StatisticResult(name, header, rows);
    }
}
