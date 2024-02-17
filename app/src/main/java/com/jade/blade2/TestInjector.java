package com.jade.blade2;

import com.blade.annotation.Inject;
import com.blade.inject.injector.Injector;
import com.blade.inject.injector.InjectorProvider;

public class TestInjector implements InjectorProvider {
    @Inject
    String a;
    private Injector<TestInjector> injector;


    @Override
    public void doInject(Object... source) {
        ensureInjector();
        injector.inject(this, source);
    }

    private void ensureInjector() {
        if (injector == null) {
            injector = new TestInjectorInjector();
        }
    }
}
