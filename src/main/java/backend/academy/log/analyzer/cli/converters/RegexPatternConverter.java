package backend.academy.log.analyzer.cli.converters;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexPatternConverter implements IStringConverter<Pattern> {
    @Override
    public Pattern convert(String value) {
        try {
            return Pattern.compile(value);
        } catch (PatternSyntaxException e) {
            throw new ParameterException("Invalid pattern format: " + value, e);
        }
    }
}
