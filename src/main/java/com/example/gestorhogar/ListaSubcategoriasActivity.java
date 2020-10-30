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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import entidades.Subcategoria;
import utilidades.Utilidades;

public class ListaSubcategoriasActivity extends AppCompatActivity implements AdapterSubcategorias.SubcategoriaListener{

    public static final String INTENT_CATEGORIA = "11";
    public static final String INTENT_SUBCATEGORIA = "22";
    public int refrescar = 0;

    TextView añadeSubcategoria;
    ArrayList<Subcategoria> listaSubcategorias;
    ArrayList<Subcategoria> subcategoriasSeleccionadas = new ArrayList<>();
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
        añadeSubcategoria = findViewById(R.id.listaSubcategoriasAct_default_tv);
        conn = new ConexionSQLiteHelper(this, "bd_subcategorias", null, 1);



        recyclerSubcategorias = findViewById(R.id.listaSubcategorias_recyclerSubcategorias_recyclerView);
        recyclerSubcategorias.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        categoriaSeleccionada = intent.getStringExtra(ListaCategoriasActivity.EXTRA_CATEGORIA);
        llenarSubcategorias(categoriaSeleccionada);
        if(listaSubcategorias.size() == 0){
            añadeSubcategoria.setVisibility(View.VISIBLE);
        }


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
        Cursor cursor = db.rawQuery("SELECT * FROM subcategorias WHERE categoria = ? ORDER BY subcategoria ASC", new String[]{categoria});
        Log.e("Subcategoria", "Entro en el metodo llenar subcategorias");

        //RECORRE LA TABLA SUBCATEGORIAS Y PARA EN CADA REGISTRO
        if(cursor.moveToFirst()){
            Log.e("Subcategoria", "Entro en el cursor");

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
        }

        Cursor cursorGastoSinSubcategoria = db.rawQuery("SELECT * FROM gastos WHERE categoria = ? AND subcategoria = ? ORDER BY subcategoria ASC", new String[]{categoria, ""});
        if(cursorGastoSinSubcategoria.moveToFirst()){
            double importeTotalSinSubcategoria = 0 ;

            do{
                importeTotalSinSubcategoria += cursorGastoSinSubcategoria.getDouble(3);
                gastoTotal++;
                Log.e("SINSUBCATEGORIA", "EXISTE UN GASTO SIN SUBCATEGORIA");
            }while(cursorGastoSinSubcategoria.moveToNext());
            listaSubcategorias.add(new Subcategoria(-1,"Gastos sin subcategoria",importeTotalSinSubcategoria,gastoTotal));
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
        if(comprobarArray(listaSubcategorias.get(position))){
//            Log.e("PRUEBA", "El objeto esta en la lista");



        }else{
//            Log.e("PRUEBA", "El objeto no esta en la lista");

            subcategoriasSeleccionadas.add(listaSubcategorias.get(position));

        }
        for(int i = 0; i < subcategoriasSeleccionadas.size(); i++ ){
            Log.e("PRUEBA", "obj "+ subcategoriasSeleccionadas.get(i).getSubcategoria());
            Log.e("PRUEBA", "obj 2: "+ subcategoriasSeleccionadas.size());
        }
    }

    public boolean comprobarArray(Subcategoria obj){
        boolean existe = false;

        for(int i  = 0; i < subcategoriasSeleccionadas.size(); i++){
            if(subcategoriasSeleccionadas.get(i).getId() == obj.getId()){
                Log.e("PRUEBA", "Encontrado el objeto en la lista");
                subcategoriasSeleccionadas.remove(i);
                existe = true;
            }
        }

        return existe;
    }

    public void borrarSubcategoria(){
        SQLiteDatabase db = conn.getWritableDatabase();

        for(int i = 0; i<subcategoriasSeleccionadas.size(); i++){
            db.delete(Utilidades.TABLA_SUBCATEGORIA,Utilidades.CAMPO_ID+"=?",new String[]{subcategoriasSeleccionadas.get(i).getId()+""});
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


}

//TODO: Desglosar gastos sin subcategoria