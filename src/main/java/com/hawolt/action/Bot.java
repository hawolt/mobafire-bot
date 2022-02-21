package com.hawolt.action;

import java.security.SecureRandom;

public abstract class Bot<T> implements Runnable, Status, TaskCallback<T> {

    protected final static SecureRandom RANDOM = new SecureRandom();

    protected TaskType taskType;
    protected int max;

    public Bot(TaskType taskType, int max) {
        this.taskType = taskType;
        this.max = max;
    }

    public TaskType getTaskType() {
        return taskType;
    }
}
