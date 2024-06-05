package com.example.recuperacion_manuelgarcia;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    String app_id = "a540cd22";
    String app_key = "950122e24a2e53046771f60b8e093e69";
    private TextView lineSelectionTextView;
    private List<String> selectedLines = new ArrayList<>();
    private List<Estacion> estaciones = new ArrayList<>();
    private boolean[] checkedLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuración de osmdroid
        Configuration.getInstance().load(getApplicationContext(), getSharedPreferences("osmdroid", MODE_PRIVATE));

        setContentView(R.layout.activity_main);

        // Configurar el Toolbar como la barra de acción
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Crear el objeto MapView
        mapView = findViewById(R.id.mapView);

        // Configurar el mapa
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Centrar el mapa en Barcelona con zoom
        GeoPoint startPoint = new GeoPoint(41.3851, 2.1734);
        mapView.getController().setCenter(startPoint);
        mapView.getController().setZoom(16);

        // Inicializar el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Configurar el botón para centrar el mapa en la ubicación actual del usuario
        Button centerButton = findViewById(R.id.centerButton);
        centerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Solicitar permiso de ubicación
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                } else {
                    centerMapOnUserLocation();
                }
            }
        });

        // Configurar el botón para mostrar la lista de estaciones
        Button listButton = findViewById(R.id.listButton);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EstacionListActivity.class);
                startActivity(intent);
            }
        });

        // Inicializar el TextView para la selección de líneas
        lineSelectionTextView = findViewById(R.id.lineSelectionTextView);

        // Obtener las líneas de metro del array de recursos
        String[] lineasMetro = getResources().getStringArray(R.array.lineas_metro);
        checkedLines = new boolean[lineasMetro.length];

        // Configurar el diálogo para la selección de líneas de metro
        lineSelectionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                        updateMap();
                    }
                });
                builder.setNegativeButton("Cancelar", null);
                builder.show();
            }
        });

        // Llamar a la función para obtener estaciones
        fetchEstaciones();
    }

    private void centerMapOnUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mapView.getController().setCenter(userLocation);
                    mapView.getController().setZoom(16);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                centerMapOnUserLocation();
            }
        }
    }

    private void fetchEstaciones() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<GeoJsonResponse> call = apiService.getEstaciones(app_id, app_key);

        call.enqueue(new Callback<GeoJsonResponse>() {
            @Override
            public void onResponse(Call<GeoJsonResponse> call, Response<GeoJsonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    estaciones = response.body().getFeatures();
                    updateMap(); // Actualizar el mapa con todas las estaciones obtenidas
                }
            }

            @Override
            public void onFailure(Call<GeoJsonResponse> call, Throwable t) {
                // Manejar errores en la llamada a la API
            }
        });
    }

    private void updateMap() {
        // Limpiar los marcadores actuales
        mapView.getOverlays().clear();

        Map<String, List<GeoPoint>> lineStationsMap = new HashMap<>();

        for (Estacion estacion : estaciones) {
            if (selectedLines.isEmpty() || selectedLines.contains(estacion.getProperties().getPicto())) {
                List<Double> coordinates = estacion.getGeometry().getCoordinates();
                if (coordinates != null && coordinates.size() >= 2) {
                    double latitude = coordinates.get(1);
                    double longitude = coordinates.get(0);

                    GeoPoint point = new GeoPoint(latitude, longitude);
                    Marker marker = new Marker(mapView);
                    marker.setPosition(point);
                    marker.setTitle(estacion.getProperties().getNomEstacio());
                    marker.setSnippet("Líneas: " + estacion.getProperties().getPicto());
                    marker.setOnMarkerClickListener((marker1, mapView) -> {
                        marker1.showInfoWindow();
                        return true;
                    });
                    mapView.getOverlays().add(marker);

                    // Agregar puntos a la lista correspondiente a la línea
                    String line = estacion.getProperties().getPicto();
                    if (!lineStationsMap.containsKey(line)) {
                        lineStationsMap.put(line, new ArrayList<>());
                    }
                    lineStationsMap.get(line).add(point);
                }
            }
        }

        // Dibujar las polylines para cada línea
        for (Map.Entry<String, List<GeoPoint>> entry : lineStationsMap.entrySet()) {
            List<GeoPoint> points = entry.getValue();
            if (points.size() > 1) {
                Polyline polyline = new Polyline();
                polyline.setPoints(points);
                polyline.getOutlinePaint().setColor(getLineColor(entry.getKey()));
                mapView.getOverlays().add(polyline);
            }
        }

        mapView.invalidate(); // Refrescar el mapa para mostrar los nuevos marcadores y polylines
    }

    private int getLineColor(String line) {
        switch (line) {
            case "L1":
                return getResources().getColor(R.color.line1);
            case "L2":
                return getResources().getColor(R.color.line2);
            case "L3":
                return getResources().getColor(R.color.line3);
            case "L4":
                return getResources().getColor(R.color.line4);
            case "L5":
                return getResources().getColor(R.color.line5);
            case "L6":
                return getResources().getColor(R.color.line6);
            default:
                return getResources().getColor(R.color.defaultLine);
        }
    }
}
