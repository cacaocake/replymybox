package com.ebaykorea.escrow.replymybox.rest;

import com.ebaykorea.escrow.replymybox.model.DeliveryModel;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by cacao on 2016. 2. 19..
 */
public interface DeliveryInterface {
    @GET("delivery")
    Call<List<DeliveryModel>> listRepos(
            @QueryMap Map<String, String> options);
}
