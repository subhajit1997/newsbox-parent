package org.newsbox;
import okhttp3.*;

import java.io.IOException;


public class HttpResponse {
    public static String okHttpResponse(String newsAPIURL)  {
        Request request = new Request.Builder()
                .url(newsAPIURL)
                .build();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .followRedirects(false)
                .build();

        Call call = client.newCall(request);

        String jsonResponse = null;
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                jsonResponse = response.body().string();
            } else {
                System.out.println("Failed to fetch data from the NewsAPI. Error code: " + response.code());
            }
        } catch (IOException e) {
            System.out.println("An error occurred while fetching data: " + e.getMessage());
        }
        return jsonResponse;

    }

}
