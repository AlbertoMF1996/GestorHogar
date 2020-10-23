package com.example.gestorhogar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import entidades.Gasto;

public class ListaGastosActivity extends AppCompatActivity {

    ArrayList<Gasto> listaGastos;
    RecyclerView recyclerGasto;
    String categoriaSeleccionada, subcategoriaSeleccionada;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_gastos);

        listaGastos = new ArrayList<>();
        recyclerGasto = findViewById(R.id.listaGastos_gastos_recyclerView);
        recyclerGasto.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        categoriaSeleccionada = intent.getStringExtra(ListaSubcategoriasActivity.INTENT_CATEGORIA);
        subcategoriaSeleccionada = intent.getStringExtra(ListaSubcategoriasActivity.INTENT_SUBCATEGORIA);

        llenarGastos(subcategoriaSeleccionada, categoriaSeleccionada);

        AdapterGastos adapterGastos = new AdapterGastos(listaGastos);
        recyclerGasto.setAdapter(adapterGastos);

    }

    private void llenarGastos(String subcategoria, String categoria) {

        Log.e("VALORES", "La categoria es: "+categoria);
        Log.e("VALORES", "La subcategoria  es: "+subcategoria);
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this, "bd_subcategorias", null, 1);
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM gastos WHERE subcategoria = ? AND categoria = ? ORDER BY fecha DESC",new String[]{subcategoria, categoria});


        if(cursor.moveToFirst()){
            do{
                Log.e("GASTO", cursor.getString(0));
                Log.e("GASTO", cursor.getString(1));
                Log.e("GASTO", cursor.getString(2));
                Log.e("GASTO", cursor.getString(3));
                Log.e("GASTO", cursor.getString(4));
                Log.e("GASTO", cursor.getString(5));
                listaGastos.add(new Gasto(categoriaSeleccionada, subcategoriaSeleccionada, cursor.getDouble(3),cursor.getString(4), cursor.getString(5)));
            } while (cursor.moveToNext());
        }else{
            Log.e("Gasto", "No encuentra gasto");
        }

    }
}