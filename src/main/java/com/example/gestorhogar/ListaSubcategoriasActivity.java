package com.example.gestorhogar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import entidades.Subcategoria;
import utilidades.Utilidades;

public class ListaSubcategoriasActivity extends AppCompatActivity implements AdapterSubcategorias.SubcategoriaListener{

    public static final String INTENT_CATEGORIA = "11";
    public static final String INTENT_SUBCATEGORIA = "22";
    public int refrescar = 0;

    ArrayList<Subcategoria> listaSubcategorias;
    ArrayList<Integer> subcategoriasSeleccionadas = new ArrayList<>();
    RecyclerView recyclerSubcategorias;
    String categoriaSeleccionada;
    Button eliminar;

    ConexionSQLiteHelper conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_subcategorias);

        listaSubcategorias = new ArrayList<>();
        eliminar = findViewById(R.id.listSubcategoriasAct_eliminar_btn);
        conn = new ConexionSQLiteHelper(this, "bd_subcategorias", null, 1);


        recyclerSubcategorias = findViewById(R.id.listaSubcategorias_recyclerSubcategorias_recyclerView);
        recyclerSubcategorias.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        categoriaSeleccionada = intent.getStringExtra(ListaCategoriasActivity.EXTRA_CATEGORIA);
        llenarSubcategorias(categoriaSeleccionada);


        AdapterSubcategorias adapterSubcategorias = new AdapterSubcategorias(listaSubcategorias, this);
        recyclerSubcategorias.setAdapter(adapterSubcategorias);

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoEliminar();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(refrescar>0){
            refrescar = 0;
            recreate();
        }
    }

    private void llenarSubcategorias(String categoria) {
        int id;
        String subcategoria;
        double importeTotal = 0;
        int gastoTotal=0;
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this, "bd_subcategorias", null, 1);
        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM subcategorias WHERE  categoria = ? ORDER BY subcategoria ASC", new String[]{categoria});

        if(cursor.moveToFirst()){  //RECORRE LA TABLA SUBCATEGORIAS Y PARA EN CADA REGISTRO
            do{
                Log.e("Gasto", "ID: "+cursor.getString(0)+" Subcategoria: "+cursor.getString(1)+" Categoria: "+cursor.getString(2));
                id = cursor.getInt(0);
                subcategoria = cursor.getString(1);

                Cursor cursorGastos = db.rawQuery("SELECT * FROM gastos WHERE subcategoria = ?", new String[]{subcategoria});
                if(cursorGastos.moveToFirst()){
                    do {
                        importeTotal += cursorGastos.getDouble(3);
                        gastoTotal++;
                    }while(cursorGastos.moveToNext());
                }
                listaSubcategorias.add(new Subcategoria(id, subcategoria,importeTotal,gastoTotal));
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
        refrescar++;

        Intent intent = new Intent(this, ListaGastosActivity.class);
        String categoria = categoriaSeleccionada;
        String subcategoria = listaSubcategorias.get(position).getSubcategoria();

        intent.putExtra(INTENT_CATEGORIA, categoria);
        intent.putExtra(INTENT_SUBCATEGORIA, subcategoria);
        startActivity(intent);
    }

    @Override
    public void onSubcategoriaLongClick(int position) {
        if(comprobarArray(position)){

        }else{
            subcategoriasSeleccionadas.add(position);

        }
    }

    public void dialogoEliminar(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(ListaSubcategoriasActivity.this);
        alerta.setTitle("Eliminar");
        alerta.setMessage("Si acepta se eliminará las subcategorias selecionadas")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        borrarSubcategoria();
                        recreate();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alerta.create().show();

    }

    public boolean comprobarArray(int valor){
        boolean existe = false;
        for(int i = 0; i < subcategoriasSeleccionadas.size(); i++){
            if(subcategoriasSeleccionadas.get(i) == valor) {
                subcategoriasSeleccionadas.remove(i);
                existe = true;
            }
        }

        return existe;
    }

    public void borrarSubcategoria(){
        SQLiteDatabase db = conn.getWritableDatabase();

        for(int i = 0; i<subcategoriasSeleccionadas.size(); i++){
            Log.e("Lista", "Id"+ listaSubcategorias.get(i).getId()+" Subcategoria "+listaSubcategorias.get(i).getSubcategoria());
            db.delete(Utilidades.TABLA_SUBCATEGORIA,Utilidades.CAMPO_ID+"=?",new String[]{listaSubcategorias.get(i).getId()+""});
        }

    }
}