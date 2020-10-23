package com.example.gestorhogar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import entidades.Gasto;

public class AdapterGastos extends RecyclerView.Adapter<AdapterGastos.ViewHolderGastos> {

    ArrayList<Gasto> listaGastos;

    public AdapterGastos(ArrayList<Gasto> listaGastos) {
        this.listaGastos = listaGastos;
    }

    @NonNull
    @Override
    public ViewHolderGastos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gasto, parent,false);
        return new ViewHolderGastos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderGastos holder, int position) {


        holder.etiImporte.setText(listaGastos.get(position).getImporte()+" €");
        holder.etiFecha.setText(listaGastos.get(position).getFecha());
        holder.etiComentario.setText(listaGastos.get(position).getComentario());
        if(position % 2 != 0)
            holder.itemView.setBackgroundColor(Color.parseColor("#dddddd"));

    }

    @Override
    public int getItemCount() {
        return listaGastos.size();
    }

    public class ViewHolderGastos extends RecyclerView.ViewHolder {

        TextView etiImporte, etiFecha, etiComentario;
        public ViewHolderGastos(@NonNull View itemView) {
            super(itemView);

            etiImporte = itemView.findViewById(R.id.itemGasto_importe_textView);
            etiFecha = itemView.findViewById(R.id.itemGasto_fecha_textView);
            etiComentario = itemView.findViewById(R.id.itemGasto_comentario_textView);
        }
    }
}
