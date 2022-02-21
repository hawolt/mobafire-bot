package com.hawolt.action.vote;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class VoteFormData {
    private final StringBuilder builder = new StringBuilder();

    public VoteFormData(Builder b) throws UnsupportedEncodingException {
        for (Map.Entry<String, String> entry : b.map.entrySet()) {
            builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()))
                    .append("&");
        }
        if (builder.length() > 0) builder.setLength(builder.length() - 1);
    }

    public String getFormData() {
        return builder.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, String> map = new HashMap<>();

        public Builder add(String key, String value) {
            map.put(key, value);
            return this;
        }

        public VoteFormData build() throws UnsupportedEncodingException {
            return new VoteFormData(this);
        }
    }
}
