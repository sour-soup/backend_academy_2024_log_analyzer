package backend.academy.log.analyzer.statistics;

import backend.academy.log.analyzer.model.LogRecord;
import backend.academy.log.analyzer.model.StatisticResult;

public interface Statistic {
    void collect(LogRecord logRecord);

    StatisticResult getResult();
}
