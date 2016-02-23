package com.ebaykorea.escrow.replymybox.rest;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by cacao on 2016. 2. 23..
 */
public class RestServiceGenerator {
    public static final String API_BASE_URL = "http://ec2-52-79-92-220.ap-northeast-2.compute.amazonaws.com:3000/api/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }

}
