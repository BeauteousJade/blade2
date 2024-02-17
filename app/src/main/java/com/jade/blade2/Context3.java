package com.jade.blade2;

import com.blade.annotation.Module;
import com.blade.annotation.Provides;

@Module(isRoot = false)
public class Context3 extends MainActivity.Context {

    @Provides("longValue1")
    public long longValue = 4L;
}
