package com.hawolt.action;

import com.hawolt.http.Client;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;

public class Location {
    private static final String URL = "http://lumtest.com/myip.json";
    private final JSONObject location;

    private Location(JSONObject location) {
        this.location = location;
    }

    public JSONObject getLocation() {
        return location;
    }

    @Override
    public String toString() {
        String country = location.getString("country");
        String ip = location.getString("ip");
        String city = location.getJSONObject("geo").getString("city");
        return String.join(";", country, city, ip);
    }

    public static Location get(Proxy proxy) throws IOException {
        Request request = new Request.Builder()
                .url(URL)
                .get()
                .build();
        Call call = Client.perform(request, proxy);
        try (Response response = call.execute()) {
            try (ResponseBody responseBody = response.body()) {
                if (responseBody == null) throw new IOException("no response body");
                JSONObject object = new JSONObject(responseBody.string());
                return new Location(object);
            }
        }
    }
}
