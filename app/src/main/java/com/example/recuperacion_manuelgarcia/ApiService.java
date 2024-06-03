package com.example.recuperacion_manuelgarcia;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("transit/estacions")
    Call<GeoJsonResponse> getEstaciones(@Query("app_id") String appId, @Query("app_key") String appKey);
}
