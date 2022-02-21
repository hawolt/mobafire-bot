package com.hawolt.http;

import com.hawolt.Controller;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Client {
    private static final OkHttpClient NO_PROXY = new OkHttpClient();

    private static final SecureRandom RANDOM = new SecureRandom();

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";

    public static final Authentication ROTATING_AUTHENTICATION = new Authentication(
            Controller.CONFIG.get("bright-data-static-username"),
            Controller.CONFIG.get("bright-data-static-password")
    );

    public static final Proxy RESIDENTIAL_PROXY = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
            Controller.CONFIG.get("bright-data-residential-hostname"),
            Integer.parseInt(Controller.CONFIG.get("bright-data-residential-port"))
    ));

    private static OkHttpClient getClient(Proxy proxy, Authentication authentication) {
        if (proxy == null) return NO_PROXY;
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .connectTimeout(15L, TimeUnit.SECONDS)
                .writeTimeout(15L, TimeUnit.SECONDS)
                .readTimeout(15L, TimeUnit.SECONDS)
                .callTimeout(15L, TimeUnit.SECONDS)
                .proxy(proxy);
        return authentication == null ? builder.build() : builder.proxyAuthenticator(authentication).build();
    }

    public static final ProxyGateway[] PROXY_GATEWAYS = ProxyGateway.values();

    public static Proxy generateSticky() {
        ProxyGateway gateway = PROXY_GATEWAYS[RANDOM.nextInt(PROXY_GATEWAYS.length)];
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(gateway.getCode() + ".proxiware.com", gateway.getStart() + RANDOM.nextInt(gateway.getAmount() + 1)));
    }

    public static Call perform(Request request, Proxy proxy) {
        return perform(request, proxy, null);
    }

    public static Call perform(Request request, Proxy proxy, Authentication authentication) {
        return getClient(proxy, authentication).newCall(request);
    }

}
