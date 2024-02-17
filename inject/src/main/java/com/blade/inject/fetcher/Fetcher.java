package com.blade.inject.fetcher;

public interface Fetcher<T> {

    void init(T t);

    boolean isInit();

    <U> U fetch(String name);
}
