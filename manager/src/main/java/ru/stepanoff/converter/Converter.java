package ru.stepanoff.converter;

public interface Converter<T, U> {
    U convert(T t);
}
