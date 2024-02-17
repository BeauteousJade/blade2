package com.jade.blade2;

import androidx.annotation.Nullable;

import com.blade.annotation.Module;
import com.blade.annotation.Provides;
import com.blade.inject.fetcher.Fetcher;

@Module
public class TestFetcherProvider2 extends TestFetcherProvider {

    @Provides("string1")
    public String string1 = "string1";

    private TestFetcherProvider2Fetcher fetcher;

    @Nullable
    @Override
    public Fetcher<?> getFetcher() {
        ensureFetcher();
        return fetcher;
    }

    private void ensureFetcher() {
        if (fetcher == null) {
            Fetcher<?> parentFetcher = super.getFetcher();
            fetcher = new TestFetcherProvider2Fetcher(parentFetcher);
        }
    }
}
