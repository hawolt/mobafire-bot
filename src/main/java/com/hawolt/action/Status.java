package com.hawolt.action;

public interface Status {
    int getGoal();
    int getCurrent();
    double getPercentProgress();
    boolean isDone();
    String getTarget();
}
