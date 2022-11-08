package cn.gavin.args;

import cn.gavin.args.exceptions.IllegalOptionException;
import cn.gavin.args.exceptions.UnsupportedOptionTypeException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static cn.gavin.args.OptionParsers.*;

public class Args<T> {
    private static Map<Class<?>, OptionParser> PARSERS = Map.of(
            boolean.class, bool(),
            int.class, unary(0, Integer::parseInt),
            String.class, unary("", String::valueOf),
            String[].class, OptionParsers.list(String[]::new, String::valueOf),
            Integer[].class, OptionParsers.list(Integer[]::new, Integer::parseInt));

    public static <T> T parse(Class<T> optionsClass, String... args) {
        return new Args<>(optionsClass, PARSERS).parse(args);
    }

    private Class<T> optionsClass;
    private Map<Class<?>, OptionParser> parsers;

    public Args(Class<T> optionsClass, Map<Class<?>, OptionParser> parsers) {
        this.optionsClass = optionsClass;
        this.parsers = parsers;
    }

    public T parse(String... args) {
        try {
            List<String> arguments = Arrays.asList(args);
            Constructor<?> constructor = optionsClass.getDeclaredConstructors()[0];

            Object[] values = Arrays.stream(constructor.getParameters()).map(it -> parseOption(arguments, it)).toArray();

            return (T) constructor.newInstance(values);
        } catch (IllegalOptionException | UnsupportedOptionTypeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object parseOption(List<String> arguments, Parameter parameter) {
        if (!parameter.isAnnotationPresent(Option.class)) throw new IllegalOptionException(parameter.getName());
        Option option = parameter.getAnnotation(Option.class);
        Class<?> type = parameter.getType();
        if (!parsers.containsKey(type)) throw new UnsupportedOptionTypeException(option.value(), type);
        return parsers.get(type).parse(arguments, option);
    }
}
