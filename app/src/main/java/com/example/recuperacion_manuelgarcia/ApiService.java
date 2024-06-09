package com.example.recuperacion_manuelgarcia;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// Interfaz ApiService para definir las llamadas a la API
public interface ApiService {
    // Método GET para obtener las estaciones de metro
    @GET("transit/estacions")
    Call<GeoJsonResponse> getEstaciones(@Query("app_id") String appId, @Query("app_key") String appKey);
}