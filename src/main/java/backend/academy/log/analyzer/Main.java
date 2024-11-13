package backend.academy.log.analyzer;

import backend.academy.log.analyzer.cli.LogAnalyzerApp;
import backend.academy.log.analyzer.cli.LogAnalyzerParameters;
import backend.academy.log.analyzer.exception.GlobPathFinderException;
import backend.academy.log.analyzer.exception.LogParserException;
import backend.academy.log.analyzer.exception.StreamProviderException;
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
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import java.util.List;
import lombok.experimental.UtilityClass;

@SuppressWarnings("RegexpSinglelineJava")
@UtilityClass
public class Main {
    public static void main(String[] args) {
        LogAnalyzerParameters parameters = new LogAnalyzerParameters();
        JCommander jCommander = JCommander.newBuilder()
            .addObject(parameters)
            .build();
        try {
            jCommander.parse(args);

            if (parameters.help()) {
                jCommander.usage();
                return;
            }

            LogParser parser = new NginxLogParser();
            LogReader reader = new NginxLogReader(parser);
            StatisticsCollector collector = createStatisticsCollector(parameters);

            LogAnalyzerApp analyzer = new LogAnalyzerApp(parameters, reader, collector);

            String report = analyzer.run();
            System.out.println(report);
        } catch (ParameterException | GlobPathFinderException | LogParserException | StreamProviderException e) {
            System.err.println(e.getMessage());
            jCommander.usage();
        }
    }

    private StatisticsCollector createStatisticsCollector(LogAnalyzerParameters parameters) {
        return new LogStatisticsCollector(List.of(
            new GeneralInfoStatistic(parameters.paths(), parameters.from(), parameters.to()),
            new ResourcesStatistic(),
            new StatusCodesStatistic(),
            new ResponseSizeStatistic(),
            new HourStatistic()
        ));
    }
}
