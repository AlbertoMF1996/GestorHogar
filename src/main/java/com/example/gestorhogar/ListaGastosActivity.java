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
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import entidades.Gasto;
import utilidades.Utilidades;

public class ListaGastosActivity extends AppCompatActivity implements AdapterGastos.GastoListener{

    ArrayList<Gasto> listaGastos;
    ArrayList<Integer> gastosSeleccionados = new ArrayList<>();
    TextView añadeGastos;
    RecyclerView recyclerGasto;
    String categoriaSeleccionada, subcategoriaSeleccionada;
    Button eliminar;
    ConexionSQLiteHelper conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_gastos);

        añadeGastos = findViewById(R.id.listaGastos_default_tv);
        listaGastos = new ArrayList<>();
        recyclerGasto = findViewById(R.id.listaGastos_gastos_recyclerView);
        eliminar = findViewById(R.id.listaGastosAct_eliminar_btn);
        conn = new ConexionSQLiteHelper(this, "bd_subcategorias", null, 1);
        recyclerGasto.setLayoutManager(new LinearLayoutManager(this));
        Intent intent = getIntent();
        categoriaSeleccionada = intent.getStringExtra(ListaSubcategoriasActivity.INTENT_CATEGORIA);
        subcategoriaSeleccionada = intent.getStringExtra(ListaSubcategoriasActivity.INTENT_SUBCATEGORIA);

        llenarGastos(subcategoriaSeleccionada, categoriaSeleccionada);

        if(listaGastos.size() == 0){
            añadeGastos.setVisibility(View.VISIBLE);
        }

        AdapterGastos adapterGastos = new AdapterGastos(listaGastos, this);
        recyclerGasto.setAdapter(adapterGastos);

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gastosSeleccionados.size() != 0){
                    dialogoEliminar();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Manten en un gasto para seleccionarlo", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER|Gravity.CENTER_HORIZONTAL,0,10);
                    toast.show();
                }
            }
        });

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
                listaGastos.add(new Gasto(cursor.getInt(0), categoriaSeleccionada, subcategoriaSeleccionada, cursor.getDouble(3),cursor.getString(4), cursor.getString(5)));
            } while (cursor.moveToNext());
        }else{
            Log.e("Gasto", "No encuentra gasto");
        }

    }

    @Override
    public void onGastoLongClick(int position) {
        if(comprobarArray(position)){

        }else{
            gastosSeleccionados.add(position);

        }
    }

    public void dialogoEliminar(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(ListaGastosActivity.this);
        alerta.setTitle("Eliminar");
        alerta.setMessage("Si acepta se eliminará los gastos selecionados")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        borrarGasto();
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
        for(int i = 0; i < gastosSeleccionados.size(); i++){
            if(gastosSeleccionados.get(i) == valor) {
                gastosSeleccionados.remove(i);
                existe = true;
            }
        }

        return existe;
    }

    public void borrarGasto(){
        SQLiteDatabase db = conn.getWritableDatabase();

        for(int i = 0; i<gastosSeleccionados.size(); i++){
            Log.e("Lista", "Id"+ listaGastos.get(i).getId()+" Subcategoria "+listaGastos.get(i).getSubcategoria());
            db.delete(Utilidades.TABLA_GASTOS,Utilidades.CAMPO_ID+"=?",new String[]{listaGastos.get(i).getId()+""});
        }

    }
}