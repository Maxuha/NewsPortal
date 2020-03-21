package ua.edu.sumdu.j2ee.zykov.util;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Network {
    private static OkHttpClient client = new OkHttpClient();

    public static String getResponse(String url, String token, Map<String, String> parameters) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .header("X-Api-Key", token)
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static InputStream getImageInputStream(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().byteStream();
    }
}
