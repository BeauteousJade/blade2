package com.jade.blade2;

import com.blade.annotation.Inject;
import com.blade.inject.injector.Injector;

public class TestInjector2 extends TestInjector {

    @Inject
    String b;

    private Injector<TestInjector> injector;

    @Override
    public void doInject(Object... source) {
        super.doInject(source);
        ensureInjector();
        injector.inject(this, source);
    }


    private void ensureInjector() {
        if (injector == null) {
            injector = new TestInjectorInjector();
        }
    }
}
