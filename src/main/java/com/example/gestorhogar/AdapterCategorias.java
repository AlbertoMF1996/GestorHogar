package com.example.gestorhogar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterCategorias extends RecyclerView.Adapter<AdapterCategorias.ViewHolderCategorias> {

    ArrayList<String> listCategorias;
    private CategoriaListener mCategoriaListener;

    public AdapterCategorias(ArrayList<String> listCategorias, CategoriaListener categoriaListener) {
        this.listCategorias = listCategorias;
        this.mCategoriaListener = categoriaListener;
    }
    @NonNull
    @Override
    public ViewHolderCategorias onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria,parent,false);
        return new ViewHolderCategorias(view, mCategoriaListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCategorias holder, int position) {

        holder.asignarDatos(listCategorias.get(position));
    }

    @Override
    public int getItemCount() {
        return listCategorias.size();
    }

    public class ViewHolderCategorias extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView categoria;
        CategoriaListener categoriaListener;
        public ViewHolderCategorias(@NonNull View itemView, CategoriaListener categoriaListener) {
            super(itemView);

            this.categoriaListener = categoriaListener;
            categoria = itemView.findViewById(R.id.itemCategoria_textoCategoria_textView);

            itemView.setOnClickListener(this);
        }

        public void asignarDatos(String s) {
            categoria.setText(s);
        }

        @Override
        public void onClick(View v) {
            categoriaListener.onCategoriaListener(getAdapterPosition());
        }
    }

    public interface CategoriaListener{
        void onCategoriaListener(int position);
    }
}
