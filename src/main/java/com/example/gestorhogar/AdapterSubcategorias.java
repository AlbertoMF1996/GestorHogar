package com.example.gestorhogar;

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

    public class ViewHolderSubcategorias extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView etiSubcategoria, etiGastosTotales, etiImportesTotales;
        SubcategoriaListener subcategoriaListener;
        public ViewHolderSubcategorias(@NonNull View itemView, SubcategoriaListener subcategoriaListener) {
            super(itemView);

            this.subcategoriaListener = subcategoriaListener;

            etiSubcategoria = itemView.findViewById(R.id.itemSubcategoria_subcategoria_textView);
            etiGastosTotales = itemView.findViewById(R.id.itemSubcategoria_gastosTotales_textView);
            etiImportesTotales = itemView.findViewById(R.id.itemSubcategoria_importeTotal_textView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            subcategoriaListener.onSubcategoriaListener(getAdapterPosition());
        }
    }

    public interface SubcategoriaListener{
        void onSubcategoriaListener(int position);
    }
}
