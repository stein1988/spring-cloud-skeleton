package com.lonbon.cloud.base.repository;

import com.easy.query.core.proxy.AbstractProxyEntity;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.easy.query.core.proxy.fetcher.AbstractFetcher;

@FunctionalInterface
public interface FetcherProvider<
        TProxy extends AbstractProxyEntity<TProxy, T>,
        T extends ProxyEntityAvailable<T, TProxy>,
        TChain extends AbstractFetcher<TProxy, T, TChain>
        > {
    TChain apply(TProxy proxy);
}