package com.example.recuperacion_manuelgarcia;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Clase ApiClient para configurar Retrofit
public class ApiClient {
    // URL base del API
    private static final String BASE_URL = "https://api.tmb.cat/v1/";
    // Instancia de Retrofit
    private static Retrofit retrofit = null;

    // Método para obtener la instancia de Retrofit
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Construcción de Retrofit con la URL base y el convertidor Gson
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
