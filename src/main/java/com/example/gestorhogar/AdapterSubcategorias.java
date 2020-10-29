package com.example.gestorhogar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import entidades.Subcategoria;

public class AdapterSubcategorias extends RecyclerView.Adapter<AdapterSubcategorias.ViewHolderSubcategorias> {

    ArrayList<Subcategoria> listaSubcategorias;
    ArrayList<Integer> subcategoriaSeleccionada = new ArrayList<>();
    private SubcategoriaListener mSubcategoriaListener;

    public AdapterSubcategorias(ArrayList<Subcategoria> listaSubcategorias, SubcategoriaListener subcategoriaListener) {
        this.listaSubcategorias = listaSubcategorias;
        this.mSubcategoriaListener = subcategoriaListener;
    }

    @NonNull
    @Override
    public ViewHolderSubcategorias onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subcategoria, parent,false);
        return new ViewHolderSubcategorias(view, mSubcategoriaListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSubcategorias holder, int position) {

        holder.etiSubcategoria.setText(listaSubcategorias.get(position).getSubcategoria());
        holder.etiImportesTotales.setText(listaSubcategorias.get(position).getImporteTotal()+" €");
        holder.etiGastosTotales.setText( "Total registros: "+listaSubcategorias.get(position).getGastosTotal());

    }

    @Override
    public int getItemCount() {
        return listaSubcategorias.size();
    }

    public class ViewHolderSubcategorias extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView etiSubcategoria, etiGastosTotales, etiImportesTotales;
        SubcategoriaListener subcategoriaListener;
        public ViewHolderSubcategorias(@NonNull View itemView, SubcategoriaListener subcategoriaListener) {
            super(itemView);

            this.subcategoriaListener = subcategoriaListener;

            etiSubcategoria = itemView.findViewById(R.id.itemSubcategoria_subcategoria_textView);
            etiGastosTotales = itemView.findViewById(R.id.itemSubcategoria_gastosTotales_textView);
            etiImportesTotales = itemView.findViewById(R.id.itemSubcategoria_importeTotal_textView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            subcategoriaListener.onSubcategoriaListener(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            subcategoriaListener.onSubcategoriaLongClick(getAdapterPosition());
            if(comprobarArray(getAdapterPosition())){
                v.setBackgroundColor(Color.parseColor("#666666"));
            }else{
                subcategoriaSeleccionada.add(getAdapterPosition());
                v.setBackgroundColor(Color.parseColor("#ff00ff"));
            }
            return true;
        }
    }
    public boolean comprobarArray(int valor){
        boolean existe = false;
        for(int i = 0; i < subcategoriaSeleccionada.size(); i++){
            if(subcategoriaSeleccionada.get(i) == valor) {
                subcategoriaSeleccionada.remove(i);
                existe = true;
            }
        }

        return existe;
    }

    public interface SubcategoriaListener{
        void onSubcategoriaListener(int position);
        void onSubcategoriaLongClick(int position);
    }
}
