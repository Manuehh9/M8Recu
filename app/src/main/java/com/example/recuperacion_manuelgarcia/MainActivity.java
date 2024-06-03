package com.example.recuperacion_manuelgarcia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import android.view.View;
import android.widget.Button;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    String app_id = "a540cd22";
    String app_key = "950122e24a2e53046771f60b8e093e69";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuración de osmdroid
        Configuration.getInstance().load(getApplicationContext(), getSharedPreferences("osmdroid", MODE_PRIVATE));

        setContentView(R.layout.activity_main);

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
                    List<Estacion> estaciones = response.body().getFeatures();

                    for (Estacion estacion : estaciones) {
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
                        }
                    }

                    mapView.invalidate();
                } else {
                    System.err.println("Error en la respuesta: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<GeoJsonResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
