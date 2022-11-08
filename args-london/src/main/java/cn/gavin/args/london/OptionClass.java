package cn.gavin.args.london;

public interface OptionClass<T> {

    String[] getOptionNames();

    Class getOptionType(String name);

    T create(Object[] value);
}
