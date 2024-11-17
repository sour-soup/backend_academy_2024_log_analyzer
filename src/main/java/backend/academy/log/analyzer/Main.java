package backend.academy.log.analyzer;

import backend.academy.log.analyzer.cli.LogAnalyzerApp;
import backend.academy.log.analyzer.cli.LogAnalyzerParameters;
import backend.academy.log.analyzer.config.AppModule;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.experimental.UtilityClass;
import static backend.academy.log.analyzer.utils.PathFinder.getAllPaths;

@SuppressWarnings("RegexpSinglelineJava")
@UtilityClass
public class Main {
    public static void main(String[] args) {
        LogAnalyzerParameters parameters = parseCommandLineArgs(args);

        try {
            Injector injector = Guice.createInjector(new AppModule(parameters));

            parameters.paths(getAllPaths(parameters.paths()));

            LogAnalyzerApp app = injector.getInstance(LogAnalyzerApp.class);
            String report = app.run();
            System.out.println(report);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static LogAnalyzerParameters parseCommandLineArgs(String[] args) {
        LogAnalyzerParameters parameters = new LogAnalyzerParameters();
        JCommander jCommander = JCommander.newBuilder()
            .addObject(parameters)
            .build();

        try {
            jCommander.parse(args);
            if (parameters.help()) {
                jCommander.usage();
                System.exit(0);
            }
        } catch (ParameterException e) {
            jCommander.usage();
            throw new IllegalArgumentException("Invalid arguments: " + e.getMessage(), e);
        }

        return parameters;
    }
}
