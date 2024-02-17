package com.jade.blade2;

import androidx.annotation.Nullable;

import com.blade.annotation.Module;
import com.blade.annotation.Provides;
import com.blade.inject.fetcher.Fetcher;
import com.blade.inject.fetcher.FetcherProvider;

@Module
public class TestFetcherProvider implements FetcherProvider {

    @Provides("string")
    public String string = "string";

    private TestFetcherProviderFetcher fetcher;

    @Nullable
    @Override
    public Fetcher<?> getFetcher() {
        ensureFetcher();
        return fetcher;
    }

    private void ensureFetcher() {
        if (fetcher == null) {
            Fetcher<?> parentFetcher = FetcherProvider.super.getFetcher();
            fetcher = new TestFetcherProviderFetcher(parentFetcher);
        }
    }
}
