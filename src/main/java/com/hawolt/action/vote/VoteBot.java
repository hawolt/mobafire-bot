package com.hawolt.action.vote;

import com.hawolt.Logger;
import com.hawolt.SynchronizedInteger;
import com.hawolt.action.Bot;
import com.hawolt.action.TaskType;
import com.hawolt.action.vote.impl.GenerateVote;
import com.hawolt.http.ResponseWrapper;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public class VoteBot extends Bot<JSONObject> {

    private final Function<ResponseWrapper, JSONObject> function = response -> new JSONObject(response.getBody());
    private final SynchronizedInteger current = new SynchronizedInteger();
    private final Supplier<String> supplier;
    private final double amount;
    private final String target;
    private final boolean up;
    private long lastSuccess;


    public VoteBot(boolean up, int amount, final String target, int max) {
        super(TaskType.VOTE, max);
        this.target = target;
        this.amount = amount;
        this.up = up;
        this.supplier = () -> target;
    }

    public static VoteBot init(boolean up, int amount, String target, int max) {
        return new VoteBot(up, amount, target, max);
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
        do {
            try {
                long secondsSinceVote = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastSuccess);
                if (secondsSinceVote >= TimeUnit.MINUTES.toSeconds(max)) {
                    GenerateVote vote = new GenerateVote<>(up, this, supplier, function);
                    vote.run();
                } else {
                    long minimumSleepSeconds = 120 - secondsSinceVote;
                    if (minimumSleepSeconds < 1) minimumSleepSeconds = 1;
                    long randomDuration = (long) (RANDOM.nextDouble() * RANDOM.nextInt(max * 60 * 1000));
                    long sleep = TimeUnit.SECONDS.toMillis(minimumSleepSeconds) + randomDuration;
                    Thread.sleep(sleep > 0 ? sleep : 120000);
                }
            } catch (InterruptedException e) {

            }
        } while (!isDone());
    }

    @Override
    public void onCompletion(JSONObject object) {
        lastSuccess = System.currentTimeMillis();
        current.increment();
    }

    public void onFailure(Throwable t) {
        Logger.error(t);
    }
}
