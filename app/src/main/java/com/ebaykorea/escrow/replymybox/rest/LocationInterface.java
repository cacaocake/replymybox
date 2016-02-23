package com.ebaykorea.escrow.replymybox.rest;

import com.ebaykorea.escrow.replymybox.model.LocationModel;
import com.ebaykorea.escrow.replymybox.model.LocationModelRetrofit;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by cacao on 2016. 2. 19..
 */
public interface LocationInterface {
    @GET("location")
    Call<List<LocationModelRetrofit>> listRepos(
            @QueryMap Map<String, String> options);
}
