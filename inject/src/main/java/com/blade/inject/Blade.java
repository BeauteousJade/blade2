package com.blade.inject;

import com.blade.inject.injector.InjectorProvider;

public class Blade {

    public static <T> void inject(T target, Object... source) {
        if (target instanceof InjectorProvider) {
            ((InjectorProvider) target).doInject(source);
        }
    }
}
