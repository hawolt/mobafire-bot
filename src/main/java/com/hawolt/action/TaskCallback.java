package com.hawolt.action;

public interface TaskCallback<T> {
    void onCompletion(T t);

    void onFailure(Throwable t);
}
