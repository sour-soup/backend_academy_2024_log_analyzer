package backend.academy.log.analyzer.cli.converters;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class IsoDateTimeConverter implements IStringConverter<OffsetDateTime> {
    @Override
    public OffsetDateTime convert(String value) {
        try {
            return OffsetDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new ParameterException(
                "Invalid date format: " + value + ". Expected ISO-8601 format (example: 2024-11-11T12:41:10Z).", e);
        }
    }
}
