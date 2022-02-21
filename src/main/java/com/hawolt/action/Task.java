package com.hawolt.action;

import com.hawolt.http.ResponseWrapper;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings(value = "all")
public abstract class Task<T> implements Runnable {

    private final Function<ResponseWrapper, T> function;

    protected Supplier<String> target;
    protected TaskCallback callback;
    protected boolean complete;

    public Task(TaskCallback callback, Supplier<String> target, Function<ResponseWrapper, T> function) {
        this.callback = callback;
        this.function = function;
        this.target = target;
    }

    public void run() {
        try {
            executeTask();
        } catch (Throwable t) {
            if (!(t instanceof InterruptedIOException)) {
                callback.onFailure(t);
            }
        } finally {
            complete = true;
            if (isSuccessful()) {
                callback.onCompletion(function.apply(get()));
            }
        }
    }

    public boolean isComplete() {
        return complete;
    }

    public abstract ResponseWrapper get();

    public abstract void executeTask() throws IOException;

    public abstract boolean isSuccessful();

}
