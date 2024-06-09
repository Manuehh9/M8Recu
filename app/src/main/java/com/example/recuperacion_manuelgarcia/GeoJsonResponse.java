package com.example.recuperacion_manuelgarcia;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// Clase modelo para la respuesta del API
public class GeoJsonResponse {
    @SerializedName("type")
    private String type;

    @SerializedName("features")
    private List<Estacion> features;

    // Getters y setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Estacion> getFeatures() {
        return features;
    }

    public void setFeatures(List<Estacion> features) {
        this.features = features;
    }
}
