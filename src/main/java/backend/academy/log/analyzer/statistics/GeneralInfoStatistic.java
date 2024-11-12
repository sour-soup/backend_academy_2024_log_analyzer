package backend.academy.log.analyzer.statistics;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.model.StatisticResult;
import com.datadoghq.sketch.ddsketch.DDSketch;
import com.datadoghq.sketch.ddsketch.DDSketches;
import java.time.OffsetDateTime;
import java.util.List;

public class GeneralInfoStatistic implements Statistic {
    private static final double RELATIVE_ACCURACY = 0.1;
    private static final double PERCENTILE = 0.95;
    private final DDSketch sketch = DDSketches.unboundedDense(RELATIVE_ACCURACY);
    private final List<String> fileNames;
    private final OffsetDateTime startDate;
    private final OffsetDateTime endDate;
    private int countRequests = 0;
    private long totalSize = 0;

    public GeneralInfoStatistic(List<String> fileNames, OffsetDateTime startDate, OffsetDateTime endDate) {
        this.fileNames = fileNames;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public void collect(LogRecord logRecord) {
        ++countRequests;
        totalSize += logRecord.bodyBytesSent();
        sketch.accept(logRecord.bodyBytesSent());
    }

    @Override
    public StatisticResult getResult() {
        String name = "Общая информация";
        List<String> header = List.of("Метрика", "Значение");
        List<List<String>> rows = List.of(
            List.of("Файл(-ы)", fileNames.toString()),
            List.of("Начальная дата", (startDate == null ? "-" : startDate.toString())),
            List.of("Конечная дата", (endDate == null ? "-" : endDate.toString())),
            List.of("Количество запросов", String.valueOf(countRequests)),
            List.of("Средний размер ответа", totalSize / (countRequests == 0 ? 1 : countRequests) + "b"),
            List.of("95-й перцентиль размера ответа", sketch.getValueAtQuantile(PERCENTILE) + "b")
        );
        return new StatisticResult(name, header, rows);
    }
}
