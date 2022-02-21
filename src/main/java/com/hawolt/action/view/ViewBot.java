package com.hawolt.action.view;

import com.hawolt.Logger;
import com.hawolt.SynchronizedInteger;
import com.hawolt.action.Bot;
import com.hawolt.action.TaskType;
import com.hawolt.action.view.impl.GenerateView;
import com.hawolt.http.ResponseWrapper;

import java.io.InterruptedIOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class ViewBot extends Bot<Integer> {
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    private final Function<ResponseWrapper, Integer> function = ResponseWrapper::getCode;
    private final SynchronizedInteger current = new SynchronizedInteger();
    private final Supplier<String> supplier;
    private final double amount;
    private final String target;


    public ViewBot(int amount, final String target, int max) {
        super(TaskType.VIEW, max);
        this.target = target;
        this.amount = amount;
        this.supplier = () -> target;
    }

    public static ViewBot init(int amount, String target, int max) {
        return new ViewBot(amount, target, max);
    }

    @Override
    public int getGoal() {
        return (int) amount;
    }

    @Override
    public int getCurrent() {
        return current.get();
    }


    public double getPercentProgress() {
        return (current.get() / amount) * 100D;
    }

    @Override
    public boolean isDone() {
        return current.get() == amount;
    }

    @Override
    public String getTarget() {
        return target;
    }

    public void run() {
        long delayForView = 0;
        for (int i = 0; i < amount; i++) {
            if (max > 0) delayForView += ((RANDOM.nextInt(max) + 1) * (RANDOM.nextDouble() * 1000L));
            GenerateView<Integer> view = new GenerateView<>(this, supplier, function);
            service.schedule(view, delayForView, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void onCompletion(Integer integer) {
        current.increment();
        if (isDone()) {
            service.shutdown();
        }
    }

    public void onFailure(Throwable t) {
        if (!(t instanceof InterruptedIOException)) Logger.error(t);
    }
}
