package com.hawolt.http;

import okhttp3.*;

public class Authentication implements Authenticator {
    private final String username, password;

    public Authentication(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Request authenticate(Route route, Response response) {
        String credential = Credentials.basic(username, password);
        return response.request().newBuilder()
                .header("Proxy-Authorization", credential)
                .build();
    }

}
