package backend.academy.log.analyzer.cli.converters;

import backend.academy.log.analyzer.model.ReportFormat;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import java.util.Arrays;

public class ReportFormatConverter implements IStringConverter<ReportFormat> {
    @Override
    public ReportFormat convert(String value) {
        try {
            return ReportFormat.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ParameterException(
                "Invalid format. Allowed values are: " + Arrays.toString(ReportFormat.values()), e);
        }
    }
}
