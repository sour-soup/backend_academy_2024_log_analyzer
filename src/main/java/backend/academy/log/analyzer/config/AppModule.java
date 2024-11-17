package backend.academy.log.analyzer.config;

import backend.academy.log.analyzer.cli.LogAnalyzerApp;
import backend.academy.log.analyzer.cli.LogAnalyzerParameters;
import backend.academy.log.analyzer.parser.LogParser;
import backend.academy.log.analyzer.parser.NginxLogParser;
import backend.academy.log.analyzer.reader.LogReader;
import backend.academy.log.analyzer.reader.NginxLogReader;
import backend.academy.log.analyzer.statistics.GeneralInfoStatistic;
import backend.academy.log.analyzer.statistics.HourStatistic;
import backend.academy.log.analyzer.statistics.ResourcesStatistic;
import backend.academy.log.analyzer.statistics.ResponseSizeStatistic;
import backend.academy.log.analyzer.statistics.StatusCodesStatistic;
import backend.academy.log.analyzer.statistics.collector.LogStatisticsCollector;
import backend.academy.log.analyzer.statistics.collector.StatisticsCollector;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import java.util.List;

public class AppModule extends AbstractModule {
    private final LogAnalyzerParameters parameters;

    public AppModule(final LogAnalyzerParameters parameters) {
        this.parameters = parameters;
    }

    @Override
    protected void configure() {
        bind(LogParser.class).to(NginxLogParser.class);
        bind(LogReader.class).to(NginxLogReader.class);
    }

    @Provides
    public StatisticsCollector provideStatisticsCollector() {
        return new LogStatisticsCollector(List.of(
            new GeneralInfoStatistic(parameters.paths(), parameters.from(), parameters.to()),
            new ResourcesStatistic(),
            new StatusCodesStatistic(),
            new ResponseSizeStatistic(),
            new HourStatistic()
        ));
    }

    @Provides
    public LogAnalyzerApp provideLogAnalyzerApp(LogReader logReader, StatisticsCollector statisticsCollector) {
        return new LogAnalyzerApp(parameters, logReader, statisticsCollector);
    }
}
