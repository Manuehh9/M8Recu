package com.example.recuperacion_manuelgarcia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recuperacion_manuelgarcia.Estacion;
import com.example.recuperacion_manuelgarcia.R;

import java.util.List;

public class EstacionAdapter extends RecyclerView.Adapter<EstacionAdapter.ViewHolder> {
    private List<Estacion> estaciones;

    public EstacionAdapter(List<Estacion> estaciones) {
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
        holder.direccionTextView.setText(estacion.getId()); // Asumiendo que la dirección no está disponible en tu modelo. Puedes ajustarlo según tus necesidades.
        holder.lineasTextView.setText("Líneas: " + estacion.getProperties().getPicto());
    }

    @Override
    public int getItemCount() {
        return estaciones.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
