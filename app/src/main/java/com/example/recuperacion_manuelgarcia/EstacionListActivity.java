package com.example.recuperacion_manuelgarcia;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstacionListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EstacionAdapter estacionAdapter;
    private TextView lineSelectionTextView;
    private Button backToMapButton;
    private List<String> selectedLines = new ArrayList<>();
    private boolean[] checkedLines;
    private List<Estacion> estaciones = new ArrayList<>();
    private List<Estacion> filteredEstaciones = new ArrayList<>();

    // ID y clave para la API
    String app_id = "a540cd22";
    String app_key = "950122e24a2e53046771f60b8e093e69";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_estaciones_metro);

        recyclerView = findViewById(R.id.recycler_view_estaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        lineSelectionTextView = findViewById(R.id.lineSelectionTextView);
        backToMapButton = findViewById(R.id.button_back_to_map);

        String[] lineasMetro = getResources().getStringArray(R.array.lineas_metro);
        checkedLines = new boolean[lineasMetro.length];

        // Configurar el diálogo de selección de líneas
        lineSelectionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EstacionListActivity.this);
                builder.setTitle(R.string.seleccionar_lineas);
                builder.setMultiChoiceItems(lineasMetro, checkedLines, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedLines[which] = isChecked;
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedLines.clear();
                        for (int i = 0; i < checkedLines.length; i++) {
                            if (checkedLines[i]) {
                                selectedLines.add(lineasMetro[i]);
                            }
                        }
                        updateRecyclerView();
                    }
                });
                builder.setNegativeButton("Cancelar", null);
                builder.show();
            }
        });

        // Configurar el botón para volver al mapa
        backToMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementa la lógica para volver al mapa
                Intent intent = new Intent(EstacionListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        fetchEstaciones();
    }

    // Método para obtener las estaciones de la API
    private void fetchEstaciones() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<GeoJsonResponse> call = apiService.getEstaciones(app_id, app_key);

        call.enqueue(new Callback<GeoJsonResponse>() {
            @Override
            public void onResponse(Call<GeoJsonResponse> call, Response<GeoJsonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GeoJsonResponse jsonResponse = response.body();
                    List<Estacion> estaciones = jsonResponse.getFeatures();

                    // Depuración: imprimir los datos de la respuesta
                    Log.d("EstacionListActivity", "Datos recibidos del servicio web: " + estaciones.toString());

                    // Asignar los datos a la lista de estaciones y actualizar el RecyclerView
                    updateEstaciones(estaciones);
                } else {
                    Log.e("EstacionListActivity", "Error en la respuesta del servicio web: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<GeoJsonResponse> call, Throwable t) {
                Log.e("EstacionListActivity", "Error al obtener las estaciones: " + t.getMessage());
            }
        });
    }

    // Método para actualizar la lista de estaciones
    private void updateEstaciones(List<Estacion> estaciones) {
        this.estaciones = estaciones;
        updateRecyclerView();
    }

    // Método para actualizar el RecyclerView según las líneas seleccionadas
    private void updateRecyclerView() {
        if (selectedLines.isEmpty()) {
            filteredEstaciones.clear();
            filteredEstaciones.addAll(estaciones);
        } else {
            filteredEstaciones.clear();
            for (Estacion estacion : estaciones) {
                for (String line : selectedLines) {
                    if (estacion.getProperties().getPicto().equalsIgnoreCase(line)) {
                        filteredEstaciones.add(estacion);
                        break;
                    }
                }
            }
        }

        if (estacionAdapter == null) {
            estacionAdapter = new EstacionAdapter(this, filteredEstaciones);
            recyclerView.setAdapter(estacionAdapter);
        } else {
            estacionAdapter.setEstaciones(filteredEstaciones);
        }
    }
}
