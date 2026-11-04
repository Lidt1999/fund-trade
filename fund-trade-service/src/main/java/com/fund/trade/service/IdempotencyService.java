package com.fund.trade.service;

@FunctionalInterface
public interface IdempotencyService {
    boolean tryAcquire(String key);

    default void release(String key) {
    }
}
