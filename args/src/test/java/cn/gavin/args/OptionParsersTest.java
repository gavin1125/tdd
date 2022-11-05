package cn.gavin.args;

import cn.gavin.args.exceptions.IllegalValueException;
import cn.gavin.args.exceptions.InsufficientArgumentsException;
import cn.gavin.args.exceptions.TooManyArgumentsException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.function.Function;

import static cn.gavin.args.OptionParsersTest.BooleanOptionParserTest.option;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class OptionParsersTest {

    @Nested
    class UnaryOptionParserTest {
        @Test //sad path
        public void should_not_accept_extra_argument_for_single_valued_option() {
            TooManyArgumentsException e = assertThrows(TooManyArgumentsException.class,
                    () -> OptionParsers.unary(0, Integer::parseInt).parse(Arrays.asList("-p", "8088", "8081"), option("p")));

            assertEquals("p", e.getOption());
        }

        @ParameterizedTest //sad path
        @ValueSource(strings = {"-p -l", "-p"})
        public void should_not_accept_insufficient_argument_for_single_valued_option(String arguments) {
            InsufficientArgumentsException e = assertThrows(InsufficientArgumentsException.class,
                    () -> OptionParsers.unary(0, Integer::parseInt).parse(Arrays.asList(arguments.split(" ")), option("p")));

            assertEquals("p", e.getOption());
        }

        @Test // default value
        public void should_set_default_value_for_single_valued_option() {
            Function<String, Object> whatever = it -> null;
            Object defaultValue = new Object();
            assertEquals(defaultValue, OptionParsers.unary(defaultValue, whatever).parse(Arrays.asList(), option("p")));
        }

        @Test // happy path
        public void should_parse_value_if_flag_present() {
            Object parsed = new Object();
            Function<String, Object> parse = it -> parsed;
            Object whatever = new Object();
            assertEquals(parsed, OptionParsers.unary(whatever, parse).parse(Arrays.asList("-p", "8080"), option("p")));
        }

        @Test
        public void should_raise_exception_if_illegal_value_format() {
            Object whatever = new Object();
            Function<String, Object> parse = it -> {
                throw new RuntimeException();
            };

            IllegalValueException e = assertThrows(IllegalValueException.class, () -> OptionParsers.unary(whatever, parse).parse(Arrays.asList("-p", "8080"), option("p")));
            assertEquals("p", e.getOption());
            assertEquals("8080", e.getValue());
        }
    }

    @Nested
    class BooleanOptionParserTest {
        @Test //Sad Path
        public void should_not_accept_extra_argument_for_boolean_option() {
            TooManyArgumentsException e = assertThrows(TooManyArgumentsException.class,
                    () -> OptionParsers.bool().parse(asList("-l", "t"), option("l")));

            assertEquals("l", e.getOption());
        }

        @Test //Default Value
        public void should_set_default_value_to_false_if_option_not_present() {
            assertFalse(OptionParsers.bool().parse(asList(), option("l")));
        }

        @Test //Happy Path
        public void should_set_default_value_to_true_if_option_present() {
            assertTrue(OptionParsers.bool().parse(asList("-l"), option("l")));
        }

        static Option option(String value) {
            return new Option() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return Option.class;
                }

                @Override
                public String value() {
                    return value;
                }
            };
        }
    }
}
