package com.fund.common.api;

/**
 * Shared HTTP response envelope. Business-specific error codes are introduced in later phases.
 */
public record Result<T>(boolean success, T data, String message) {

    public static <T> Result<T> success(T data) {
        return new Result<>(true, data, "OK");
    }

    public static Result<Void> ok() {
        return success(null);
    }

    public static <T> Result<T> failure(String message) {
        return new Result<>(false, null, message);
    }
}
