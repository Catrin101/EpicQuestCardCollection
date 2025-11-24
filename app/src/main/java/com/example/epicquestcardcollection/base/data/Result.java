package com.example.epicquestcardcollection.base.data;

public class Result<T> {
    private final T data;
    private final Exception error;

    private Result(T data, Exception error) {
        this.data = data;
        this.error = error;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data, null);
    }

    public static <T> Result<T> error(Exception error) {
        return new Result<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public T getData() {
        return data;
    }

    public Exception getError() {
        return error;
    }
}
