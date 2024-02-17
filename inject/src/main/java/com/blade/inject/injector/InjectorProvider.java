package com.blade.inject.injector;

public interface InjectorProvider {

    default void doInject(Object... source) {
    }
}
