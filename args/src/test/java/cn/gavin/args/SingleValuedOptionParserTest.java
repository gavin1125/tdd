package cn.gavin.args;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.function.Function;

import static cn.gavin.args.BooleanOptionParserTest.option;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SingleValuedOptionParserTest {
    @Test //sad path
    public void should_not_accept_extra_argument_for_single_valued_option() {
        TooManyArgumentsException e = assertThrows(TooManyArgumentsException.class,
                () -> new SingleValuedOptionParser<>(0, Integer::parseInt).parse(Arrays.asList("-p", "8088", "8081"), option("p")));

        assertEquals("p", e.getOption());
    }

    @ParameterizedTest //sad path
    @ValueSource(strings = {"-p -l", "-p"})
    public void should_not_accept_insufficient_argument_for_single_valued_option(String arguments) {
        InsufficientArgumentsException e = assertThrows(InsufficientArgumentsException.class,
                () -> new SingleValuedOptionParser<>(0, Integer::parseInt).parse(Arrays.asList(arguments.split(" ")), option("p")));

        assertEquals("p", e.getOption());
    }

    @Test // default value
    public void should_set_default_value_for_single_valued_option() {
        Function<String, Object> whatever = it -> null;
        Object defaultValue = new Object();
        assertEquals(defaultValue, new SingleValuedOptionParser<>(defaultValue, whatever).parse(Arrays.asList(), option("p")));
    }

    @Test // happy path
    public void should_parse_value_if_flag_present() {
        Object parsed = new Object();
        Function<String, Object> parse = it -> parsed;
        Object whatever = new Object();
        assertEquals(parsed, new SingleValuedOptionParser<>(whatever, parse).parse(Arrays.asList("-p", "8080"), option("p")));
    }
}
