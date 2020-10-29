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
    ArrayList<Integer> gastosSeleccionados = new ArrayList<>();
    private  GastoListener mGastoListener;

    public AdapterGastos(ArrayList<Gasto> listaGastos, GastoListener gastoListener) {
        this.listaGastos = listaGastos;
        this.mGastoListener = gastoListener;
    }

    @NonNull
    @Override
    public ViewHolderGastos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gasto, parent,false);
        return new ViewHolderGastos(view, mGastoListener);
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

    public class ViewHolderGastos extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView etiImporte, etiFecha, etiComentario;
        GastoListener gastoListener;

        public ViewHolderGastos(@NonNull View itemView, GastoListener gastoListener) {
            super(itemView);
            this.gastoListener = gastoListener;

            etiImporte = itemView.findViewById(R.id.itemGasto_importe_textView);
            etiFecha = itemView.findViewById(R.id.itemGasto_fecha_textView);
            etiComentario = itemView.findViewById(R.id.itemGasto_comentario_textView);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            gastoListener.onGastoLongClick(getAdapterPosition());
            if(comprobarArray(getAdapterPosition())){
                v.setBackgroundColor(Color.parseColor("#666666"));
            }else{
                gastosSeleccionados.add(getAdapterPosition());
                v.setBackgroundColor(Color.parseColor("#ff00ff"));
            }

            return false;
        }

        public boolean comprobarArray(int valor){
            boolean existe = false;
            for(int i = 0; i < gastosSeleccionados.size(); i++){
                if(gastosSeleccionados.get(i) == valor) {
                    gastosSeleccionados.remove(i);
                    existe = true;
                }
            }

            return existe;
        }
    }

    public interface GastoListener{
        void onGastoLongClick(int position);
    }
}
