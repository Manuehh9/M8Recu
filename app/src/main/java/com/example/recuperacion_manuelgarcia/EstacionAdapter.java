package com.example.recuperacion_manuelgarcia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Adaptador para mostrar las estaciones en un RecyclerView
public class EstacionAdapter extends RecyclerView.Adapter<EstacionAdapter.ViewHolder> {
    private List<Estacion> estaciones;
    private Context context;

    // Constructor del adaptador
    public EstacionAdapter(Context context, List<Estacion> estaciones) {
        this.context = context;
        this.estaciones = estaciones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_estacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Estacion estacion = estaciones.get(position);
        holder.nombreTextView.setText(estacion.getProperties().getNomEstacio());
        holder.direccionTextView.setText(estacion.getId());
        holder.lineasTextView.setText("Líneas: " + estacion.getProperties().getPicto());

        // Establecer el color de fondo según la línea de metro
        int color = getLineColor(estacion.getProperties().getPicto());
        holder.itemView.setBackgroundColor(color);
    }

    // Método para obtener el color según la línea
    private int getLineColor(String line) {
        switch (line) {
            case "L1":
                return ContextCompat.getColor(context, R.color.line1);
            case "L2":
                return ContextCompat.getColor(context, R.color.line2);
            case "L3":
                return ContextCompat.getColor(context, R.color.line3);
            case "L4":
                return ContextCompat.getColor(context, R.color.line4);
            case "L5":
                return ContextCompat.getColor(context, R.color.line5);
            case "L6":
                return ContextCompat.getColor(context, R.color.line6);
            default:
                return ContextCompat.getColor(context, R.color.defaultLine);
        }
    }

    // Método para actualizar la lista de estaciones
    public void setEstaciones(List<Estacion> estaciones) {
        this.estaciones = estaciones;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return estaciones.size();
    }

    // ViewHolder para el RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreTextView;
        public TextView direccionTextView;
        public TextView lineasTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
            direccionTextView = itemView.findViewById(R.id.direccionTextView);
            lineasTextView = itemView.findViewById(R.id.lineasTextView);
        }
    }
}
