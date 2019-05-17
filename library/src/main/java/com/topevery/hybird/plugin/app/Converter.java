package com.topevery.hybird.plugin.app;

/**
 * @author wujie
 */
public interface Converter<T> {
    T convert(String source);
}
