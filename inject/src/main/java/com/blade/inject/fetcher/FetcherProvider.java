package com.blade.inject.fetcher;

import androidx.annotation.Nullable;

public interface FetcherProvider {

    @Nullable
    default Fetcher<?> getFetcher() {
        return null;
    }
}
