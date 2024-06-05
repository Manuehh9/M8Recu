package com.example.recuperacion_manuelgarcia;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstacionListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EstacionAdapter estacionAdapter;
    private TextView lineSelectionTextView;
    private List<String> selectedLines = new ArrayList<>();
    private boolean[] checkedLines;
    private List<Estacion> estaciones = new ArrayList<>();

    private List<Estacion> filteredEstaciones = new ArrayList<>(); // Definir filteredEstaciones como una lista de Estacion

    String app_id = "a540cd22";
    String app_key = "950122e24a2e53046771f60b8e093e69";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_estaciones_metro);

        recyclerView = findViewById(R.id.recycler_view_estaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        lineSelectionTextView = findViewById(R.id.lineSelectionTextView);

        String[] lineasMetro = getResources().getStringArray(R.array.lineas_metro);
        checkedLines = new boolean[lineasMetro.length];
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

        fetchEstaciones();
    }

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

    private void updateEstaciones(List<Estacion> estaciones) {
        this.estaciones.clear(); // Limpiar la lista actual
        this.estaciones.addAll(estaciones); // Agregar las nuevas estaciones
        updateRecyclerView(); // Actualizar el RecyclerView con las nuevas estaciones
    }

    private void updateRecyclerView() {
        filteredEstaciones.clear(); // Limpiar la lista de estaciones filtradas

        for (Estacion estacion : estaciones) {
            if (selectedLines.isEmpty() || selectedLines.contains(estacion.getProperties().getPicto())) {
                filteredEstaciones.add(estacion); // Agregar la estación si no hay líneas seleccionadas o si la estación pertenece a una línea seleccionada
            }
        }

        // Depuración: imprimir el número de estaciones antes y después del filtrado
        Log.d("EstacionListActivity", "Estaciones antes del filtrado: " + estaciones.size());
        Log.d("EstacionListActivity", "Estaciones después del filtrado: " + filteredEstaciones.size());

        if (estacionAdapter == null) {
            estacionAdapter = new EstacionAdapter(filteredEstaciones); // Crear el adaptador si es nulo
            recyclerView.setAdapter(estacionAdapter); // Establecer el adaptador en el RecyclerView
        } else {
            estacionAdapter.setEstaciones(filteredEstaciones); // Actualizar los datos del adaptador
            estacionAdapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
        }
    }


}
