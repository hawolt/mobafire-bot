package com.hawolt.action.view.impl;

import com.hawolt.action.Task;
import com.hawolt.action.TaskCallback;
import com.hawolt.http.Client;
import com.hawolt.http.ResponseWrapper;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenerateView<T> extends Task<T> {

    private ResponseWrapper response;

    public GenerateView(TaskCallback callback, Supplier<String> target, Function<ResponseWrapper, T> function) {
        super(callback, target, function);
    }

    @Override
    public ResponseWrapper get() {
        return response;
    }

    public boolean isSuccessful() {
        return response.getCode() == 200;
    }

    public void executeTask() throws IOException {
        Request request = new Request.Builder()
                .url(target.get())
                .addHeader("User-Agent", Client.USER_AGENT)
                .build();
        Call call = Client.perform(request, Client.RESIDENTIAL_PROXY, Client.ROTATING_AUTHENTICATION);
        try (Response response = call.execute()) {
            try (ResponseBody body = response.body()) {
                if (body == null) throw new IOException("no response body");
                this.response = ResponseWrapper.create(response.code(), body.string());
            }
        }
    }
}
