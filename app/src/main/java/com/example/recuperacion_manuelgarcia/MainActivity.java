package com.example.recuperacion_manuelgarcia;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Configuración de osmdroid
        Configuration.getInstance().load(getApplicationContext(), getSharedPreferences("osmdroid", MODE_PRIVATE));

        setContentView(R.layout.activity_main);

        // Crear el objeto MapView
        mapView = (MapView) findViewById(R.id.mapView);

        // Configurar el mapa
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Centrar el mapa en Barcelona con zoom
        GeoPoint startPoint = new GeoPoint(41.3851, 2.1734);
        mapView.getController().setCenter(startPoint);
        mapView.getController().setZoom(16);
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
}
