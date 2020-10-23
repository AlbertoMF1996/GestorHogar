package com.example.gestorhogar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import entidades.Subcategoria;

public class ListaSubcategoriasActivity extends AppCompatActivity implements AdapterSubcategorias.SubcategoriaListener{

    public static final String INTENT_CATEGORIA = "11";
    public static final String INTENT_SUBCATEGORIA = "22";

    ArrayList<Subcategoria> listaSubcategorias;
    RecyclerView recyclerSubcategorias;
    String categoriaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_subcategorias);

        listaSubcategorias = new ArrayList<>();
        recyclerSubcategorias = findViewById(R.id.listaSubcategorias_recyclerSubcategorias_recyclerView);
        recyclerSubcategorias.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        categoriaSeleccionada = intent.getStringExtra(ListaCategoriasActivity.EXTRA_CATEGORIA);
        llenarSubcategorias(categoriaSeleccionada);


        AdapterSubcategorias adapterSubcategorias = new AdapterSubcategorias(listaSubcategorias, this);
        recyclerSubcategorias.setAdapter(adapterSubcategorias);

    }

    private void llenarSubcategorias(String categoria) {
        String subcategoria;
        double importeTotal = 0;
        int gastoTotal=0;
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this, "bd_subcategorias", null, 1);
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM subcategorias WHERE  categoria = ? ORDER BY subcategoria ASC", new String[]{categoria});

        if(cursor.moveToFirst()){  //RECORRE LA TABLA SUBCATEGORIAS Y PARA EN CADA REGISTRO
            do{
                Log.e("Gasto", "ID: "+cursor.getString(0)+" Subcategoria: "+cursor.getString(1)+" Categoria: "+cursor.getString(2));
                subcategoria = cursor.getString(1);

                Cursor cursorGastos = db.rawQuery("SELECT * FROM gastos WHERE subcategoria = ?", new String[]{subcategoria});
                if(cursorGastos.moveToFirst()){
                    do {
                        importeTotal += cursorGastos.getDouble(3);
                        gastoTotal++;
                    }while(cursorGastos.moveToNext());
                }
                listaSubcategorias.add(new Subcategoria(subcategoria,importeTotal,gastoTotal));
                importeTotal = 0;
                gastoTotal = 0;
            } while (cursor.moveToNext());
        }else{
            Toast.makeText(getApplicationContext(),"La categoria "+categoriaSeleccionada+" no tiene subcategorias",Toast.LENGTH_SHORT);
            Log.e("Error","entro");
        }
    }

    @Override
    public void onSubcategoriaListener(int position) {
        Log.e("LISTENER", "funciona "+ listaSubcategorias.get(position).getSubcategoria());
        Intent intent = new Intent(this, ListaGastosActivity.class);
        String categoria = categoriaSeleccionada;
        String subcategoria = listaSubcategorias.get(position).getSubcategoria();

        intent.putExtra(INTENT_CATEGORIA, categoria);
        intent.putExtra(INTENT_SUBCATEGORIA, subcategoria);
        startActivity(intent);
    }
}