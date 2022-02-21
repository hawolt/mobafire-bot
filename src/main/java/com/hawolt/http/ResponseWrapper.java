package com.hawolt.http;

public class ResponseWrapper {

    private final String body;
    private final int code;

    private ResponseWrapper(int code, String body) {
        this.code = code;
        this.body = body;
    }

    public static ResponseWrapper create(int code, String string) {
        return new ResponseWrapper(code, string);
    }

    public int getCode() {
        return code;
    }

    public String getBody() {
        return body;
    }
}
