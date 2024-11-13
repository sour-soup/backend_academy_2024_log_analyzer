package backend.academy.log.analyzer.cli;

import backend.academy.log.analyzer.cli.converters.IsoDateTimeConverter;
import backend.academy.log.analyzer.cli.converters.RegexPatternConverter;
import backend.academy.log.analyzer.cli.converters.ReportFormatConverter;
import backend.academy.log.analyzer.model.ReportFormat;
import com.beust.jcommander.Parameter;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
public class LogAnalyzerParameters {
    @Setter
    @Parameter(names = "--path",
        required = true,
        description = "Paths to log files",
        variableArity = true)
    private List<String> paths;

    @Parameter(names = "--from",
        description = "Start date-time in ISO-8601 format (optional)",
        converter = IsoDateTimeConverter.class)
    private OffsetDateTime from;

    @Parameter(names = "--to",
        description = "End date-time in ISO-8601 format (optional)",
        converter = IsoDateTimeConverter.class)
    private OffsetDateTime to;

    @Parameter(names = "--format",
        description = "Output format: markdown or adoc (optional)",
        converter = ReportFormatConverter.class)
    private ReportFormat format = ReportFormat.MARKDOWN;

    @Parameter(names = "--filter-field",
        description = "LogRecord field to filter by (optional)")
    private String filterField;

    @Parameter(names = "--filter-value",
        description = "Regular expression for filtering field (optional)",
        converter = RegexPatternConverter.class)
    private Pattern filterValue;

    @Parameter(names = "--help",
        description = "Display help information",
        help = true)
    private boolean help;
}