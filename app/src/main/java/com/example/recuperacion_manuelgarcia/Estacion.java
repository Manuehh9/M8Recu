package com.example.recuperacion_manuelgarcia;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// Clase modelo para las estaciones de metro
public class Estacion {
    @SerializedName("type")
    private String type;

    @SerializedName("id")
    private String id;

    @SerializedName("geometry")
    private Geometry geometry;

    @SerializedName("properties")
    private Properties properties;

    // Getters y setters

    public String getId() {
        return id;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public Properties getProperties() {
        return properties;
    }

    public static class Geometry {
        @SerializedName("type")
        private String type;

        @SerializedName("coordinates")
        private List<Double> coordinates;

        // Getters y setters

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Double> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Double> coordinates) {
            this.coordinates = coordinates;
        }
    }

    public static class Properties {
        @SerializedName("ID_ESTACIO")
        private int idEstacio;

        @SerializedName("CODI_GRUP_ESTACIO")
        private int codiGrupEstacio;

        @SerializedName("NOM_ESTACIO")
        private String nomEstacio;

        @SerializedName("PICTO")
        private String picto;

        @SerializedName("DATA")
        private String data;

        // Getters y setters

        public int getIdEstacio() {
            return idEstacio;
        }

        public void setIdEstacio(int idEstacio) {
            this.idEstacio = idEstacio;
        }

        public int getCodiGrupEstacio() {
            return codiGrupEstacio;
        }

        public void setCodiGrupEstacio(int codiGrupEstacio) {
            this.codiGrupEstacio = codiGrupEstacio;
        }

        public String getNomEstacio() {
            return nomEstacio;
        }

        public void setNomEstacio(String nomEstacio) {
            this.nomEstacio = nomEstacio;
        }

        public String getPicto() {
            return picto;
        }

        public void setPicto(String picto) {
            this.picto = picto;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
