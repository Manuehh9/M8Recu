package com.example.recuperacion_manuelgarcia;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstacionListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EstacionAdapter estacionAdapter;
    String app_id = "a540cd22";
    String app_key = "950122e24a2e53046771f60b8e093e69";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_estaciones_metro);

        recyclerView = findViewById(R.id.recycler_view_estaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchEstaciones();
    }

    private void fetchEstaciones() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<GeoJsonResponse> call = apiService.getEstaciones(app_id, app_key);

        call.enqueue(new Callback<GeoJsonResponse>() {
            @Override
            public void onResponse(Call<GeoJsonResponse> call, Response<GeoJsonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Estacion> estaciones = response.body().getFeatures();
                    estacionAdapter = new EstacionAdapter(estaciones);
                    recyclerView.setAdapter(estacionAdapter);
                }
            }

            @Override
            public void onFailure(Call<GeoJsonResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
