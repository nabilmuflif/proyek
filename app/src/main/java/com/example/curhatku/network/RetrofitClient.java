package com.example.curhatku.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

/**
 * RetrofitClient - Singleton class untuk konfigurasi Retrofit
 * Mengatur HTTP client, logging, dan base URL untuk API calls
 */
public class RetrofitClient {
    private static final String QUOTE_BASE_URL = "https://zenquotes.io/";

    private static Retrofit quoteRetrofit = null;

    // Timeout configuration
    private static final int CONNECT_TIMEOUT = 30;
    private static final int READ_TIMEOUT = 30;
    private static final int WRITE_TIMEOUT = 30;


    public static Retrofit getQuoteInstance() {
        if (quoteRetrofit == null) {
            quoteRetrofit = createRetrofit(QUOTE_BASE_URL); // Menggunakan QUOTE_BASE_URL yang baru didefinisikan di sini
        }
        return quoteRetrofit;
    }

    private static Retrofit createRetrofit(String baseUrl) {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new HeaderInterceptor())
                .retryOnConnectionFailure(true);

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    private static class HeaderInterceptor implements okhttp3.Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws java.io.IOException {
            okhttp3.Request original = chain.request();
            okhttp3.Request.Builder requestBuilder = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("User-Agent", "CurhatKu-Android/1.0")
                    .method(original.method(), original.body());
            okhttp3.Request request = requestBuilder.build();
            return chain.proceed(request);
        }
    }

    public static void resetInstance() {
        quoteRetrofit = null;
    }

    public static QuoteApiService getQuoteApiService() {
        return getQuoteInstance().create(QuoteApiService.class);
    }
}