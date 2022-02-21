package com.hawolt.action.vote.impl;

import com.hawolt.AntiCaptcha;
import com.hawolt.Controller;
import com.hawolt.Cookie;
import com.hawolt.action.Location;
import com.hawolt.action.Task;
import com.hawolt.action.TaskCallback;
import com.hawolt.error.AntiCaptchaException;
import com.hawolt.http.Client;
import com.hawolt.http.ResponseWrapper;
import com.hawolt.task.CaptchaResult;
import com.hawolt.task.CaptchaTask;
import com.hawolt.task.TaskBuilder;
import com.hawolt.task.TaskType;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenerateVote<T> extends Task<T> {
    private ResponseWrapper response;
    private boolean up;

    public GenerateVote(boolean up, TaskCallback callback, Supplier<String> target, Function<ResponseWrapper, T> function) {
        super(callback, target, function);
        this.up = up;
    }

    @Override
    public ResponseWrapper get() {
        return response;
    }

    public boolean isSuccessful() {
        return response != null && response.getCode() == 200 && isSuccess(response.getBody());
    }

    private boolean isSuccess(String body) {
        JSONObject object = new JSONObject(body);
        return object.has("success") && object.getBoolean("success");
    }

    public void executeTask() throws IOException {
        TaskBuilder builder = Controller.ANTI_CAPTCHA.buildTask(TaskType.RecaptchaV2TaskProxyless, target.get(), Controller.MOBAFIRE_SITEKEY);
        final String destination = target.get();
        String[] split = destination.split("-");
        final String id = split[split.length - 1];
        final Proxy proxy = Client.generateSticky();
        try {
            final String cookie = Cookie.get(proxy, target);
            if (cookie == null) throw new IOException("empty cookie");
            CaptchaTask task = builder.create();
            CaptchaResult result = task.complete();
            AntiCaptcha.balance.subtract(result.getCost());
            RequestBody body = new FormBody.Builder()
                    .addEncoded("relation_type", "Build")
                    .addEncoded("relation_id", id)
                    .addEncoded("score", up ? "1" : "-1")
                    .addEncoded("deselect", "false")
                    .addEncoded("g-recaptcha-response", result.getSolution())
                    .addEncoded("ignoreDVF", "false")
                    .build();
            Request request = new Request.Builder()
                    .url("https://www.mobafire.com/ajax/vote")
                    .addHeader("Cookie", cookie)
                    .post(body)
                    .build();
            Call call = Client.perform(request, proxy);
            try (Response response = call.execute()) {
                try (ResponseBody responseBody = response.body()) {
                    if (responseBody == null) throw new IOException("no response body");
                    this.response = ResponseWrapper.create(response.code(), responseBody.string());
                }
            }
        } catch (AntiCaptchaException e) {
            callback.onFailure(e);
        }
    }
}
