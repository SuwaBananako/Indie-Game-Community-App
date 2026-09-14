package com.example.indiegamecommunity.network;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiClient {

    private static ApiService apiService;
    private static TokenProvider tokenProvider;

    public interface TokenProvider {
        String getToken();
    }

    public static void init(final TokenProvider provider) {
        tokenProvider = provider;
        apiService = null;
    }

    private static Retrofit getRetrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor authInterceptor = chain -> {
            okhttp3.Request original = chain.request();
            String token = tokenProvider != null ? tokenProvider.getToken() : null;
            okhttp3.Request request = (token != null && !token.isEmpty())
                ? original.newBuilder().header("Authorization", "Bearer " + token).build()
                : original;
            return chain.proceed(request);
        };

        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

        return new Retrofit.Builder()
            .baseUrl(NetworkConfig.getInstance().getBASE_URL())
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    public static ApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofit().create(ApiService.class);
        }
        return apiService;
    }
}
