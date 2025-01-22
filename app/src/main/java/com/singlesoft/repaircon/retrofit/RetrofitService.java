package com.singlesoft.repaircon.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitService {
    private Retrofit retrofit;
    private Retrofit retrofitNoauth;

    //private final String url = "http://192.168.0.12:8090";
    private final String url = "https://efaf-191-7-206-129.ngrok-free.app";
    public RetrofitService(String token) {
        if(token.equals("")){
            initRetrofitNoAuth();
        }else {
            initRetrofit("Bearer "+token);
        }
    }
    private void initRetrofitNoAuth(){
        retrofitNoauth = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }
    private void initRetrofit(final String authToken){
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        Request newRequest = originalRequest.newBuilder()
                                .header("Authorization", authToken)
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        /*
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(client)
                .build();

         */
    }

    public Retrofit getRetrofit(){
        return retrofit;
    }
    public Retrofit getRetrofitNoauth(){
        return retrofitNoauth;
    }
}