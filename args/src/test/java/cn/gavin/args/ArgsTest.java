package cn.gavin.args;

import cn.gavin.args.exceptions.IllegalOptionException;
import cn.gavin.args.exceptions.UnsupportedOptionTypeException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ArgsTest {
    @Test
    public void should_parse_multi_options() {
        MultiOptions options = Args.parse(MultiOptions.class, "-l", "-p", "8080", "-d", "/usr/logs");
        assertTrue(options.logging());
        assertEquals(8080, options.port());
        assertEquals("/usr/logs", options.directory());
    }

    record MultiOptions(@Option("l") boolean logging, @Option("p") int port, @Option("d") String directory) {
    }

    @Test
    public void should_throw_illegal_option_exception_if_annotation_not_present() {
        IllegalOptionException e = assertThrows(IllegalOptionException.class, () -> Args.parse(OptionWithoutAnnotation.class, "-l", "-p", "8080", "-d", "/usr/logs"));

        assertEquals("port", e.getParameter());
    }

    record OptionWithoutAnnotation(@Option("l") boolean logging, int port, @Option("d") String directory) {
    }

    @Test
    public void should_raise_exception_if_type_not_supported() {
        UnsupportedOptionTypeException e = assertThrows(UnsupportedOptionTypeException.class,
                () -> Args.parse(OptionsWithUnsupportedType.class, "-l", "abc"));

        assertEquals("l", e.getOption());
        assertEquals(Object.class, e.getType());
    }

    record OptionsWithUnsupportedType(@Option("l") Object logging) {
    }

    //-g this is a list -d 1 2 -3 5
    @Test
    public void should_example_2() {
        ListOptions options = Args.parse(ListOptions.class, "-g", "this", "is", "a", "list", "-d", "1", "2", "-3", "5");

        assertArrayEquals(new String[]{"this", "is", "a", "list"}, options.group());
        assertArrayEquals(new Integer[]{1, 2, -3, 5}, options.decimals());
    }

    record ListOptions(@Option("g") String[] group, @Option("d") Integer[] decimals) {
    }

    @Test
    public void should_parse_options_if_option_parser_provided() {
        OptionParser boolParser = Mockito.mock(OptionParser.class);
        OptionParser intParser = Mockito.mock(OptionParser.class);
        OptionParser stringParser = Mockito.mock(OptionParser.class);
        Mockito.when(boolParser.parse(Mockito.any(), Mockito.any())).thenReturn(true);
        Mockito.when(intParser.parse(Mockito.any(), Mockito.any())).thenReturn(1000);
        Mockito.when(stringParser.parse(Mockito.any(), Mockito.any())).thenReturn("parsed");

        Args<MultiOptions> args = new Args<>(MultiOptions.class, Map.of(boolean.class, boolParser, int.class, intParser, String.class, stringParser));

        MultiOptions options = args.parse("-l", "-p", "8080", "-d", "/usr/logs");

        assertTrue(options.logging());
        assertEquals(1000, options.port());
        assertEquals("parsed", options.directory());
    }
}
