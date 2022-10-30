package cn.gavin.args;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArgsTest {
    //-l -p 8080 -d /usr/logs
    //[-l], [-p, 8080], [-d, /usr/logs]
    //{-l:[], -p:[8080], -d:[/usr/logs]}
    // Single Option:
    // TODO:       - bool -l
    // TODO:       - int -p 8080
    // TODO:       - string -d /usr/logs
    // TODO: multi options: -l -p 8080 -d /user/logs
    // sad path:
    // TODO:       - bool -l t / -l t f
    // TODO:       - int -p / -p 8080 8081
    // TODO:       - string -d / -d /usr/logs /usr/vars
    // default value:
    // TODO:      - bool false
    // TODO:       - int 0
    // TODO:       - string ""

    @Test
    @Disabled
    public void should_example_1() {
        Options options = Args.parse(Options.class, "-l", "-p", "8080", "-d", "/usr/logs");
        assertTrue(options.logging());
        assertEquals(8080, options.port());
        assertEquals("/usr/logs", options.directory());
    }

    //-g this is a list -d 1 2 -3 5
    @Test
    @Disabled
    public void should_example_2() {
        ListOptions options = Args.parse(ListOptions.class, "-g", "this", "is", "a", "list", "-d", "1", "2", "-3", "5");

        assertArrayEquals(new String[]{"this", "is", "a", "list"}, options.group());
        assertArrayEquals(new int[]{1, 2, -3, 5}, options.decimals());
    }

    record Options(@Option("l") boolean logging, @Option("p") int port, @Option("d") String directory) {
    }

    record ListOptions(@Option("g") String[] group, @Option("d") int[] decimals) {
    }
}
