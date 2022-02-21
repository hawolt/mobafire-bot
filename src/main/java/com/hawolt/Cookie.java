package com.hawolt;

import com.hawolt.http.Client;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.Proxy;
import java.util.function.Supplier;

public class Cookie {

    public static String get(Proxy proxy, Supplier<String> target) throws IOException {
        Request request = new Request.Builder()
                .url(target.get())
                .addHeader("User-Agent", Client.USER_AGENT)
                .build();
        Call call = Client.perform(request, proxy);
        StringBuilder builder = new StringBuilder();
        try (Response response = call.execute()) {
            for (String cookie : response.headers("Set-Cookie")) {
                builder.append(cookie.split(";")[0]).append("; ");
            }
        }
        if (builder.length() > 0) builder.setLength(builder.length() - 2);
        return builder.length() == 0 ? null : builder.toString();
    }
}
