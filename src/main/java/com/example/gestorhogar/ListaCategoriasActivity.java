package com.example.gestorhogar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.TypedArrayUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

import entidades.Gasto;

import static com.example.gestorhogar.MainActivity.DIRECTORIO_APP;

public class ListaCategoriasActivity extends AppCompatActivity implements AdapterCategorias.CategoriaListener {

    public static final String EXTRA_CATEGORIA = "111";

    ArrayList<String> listCategorias;
    public ArrayList<Integer> categoriasSeleccionadas = new ArrayList<>();
    RecyclerView recycler;
    Button guardarFichero, eliminar;
    TextView añadeCategorias;

    Calendar calendar = Calendar.getInstance();
    int año = calendar.get(calendar.YEAR);
    int month = calendar.get(calendar.MONTH);
    int dia = calendar.get(calendar.DAY_OF_MONTH);

    String mesFinal = String.valueOf(month +1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_categorias);

        cargarCategoria();


        añadeCategorias = findViewById(R.id.listaCategoriasAct_default_tv);
        guardarFichero = findViewById(R.id.listaCategoriaAct_guardar_btn);
        eliminar = findViewById(R.id.listaCategoriasAct_eliminar_btn);
        recycler = findViewById(R.id.listaCategoriaAct_listado_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        if(listCategorias.size()== 0){
            añadeCategorias.setVisibility(View.VISIBLE);
        }

        AdapterCategorias adapter = new AdapterCategorias(listCategorias, this);
        recycler.setAdapter(adapter);

        guardarFichero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoCSV();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoEliminar();
            }
        });

    }


    private void escribirFicheroCSV() {
        double totalSuma = 0;
        if(checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this, "bd_subcategorias", null, 1);
            SQLiteDatabase db = conn.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM gastos ORDER BY fecha DESC",null);
            File textFile = new File(Environment.getExternalStorageDirectory()+"/"+DIRECTORIO_APP, dia+"_"+mesFinal+"_"+año+".csv");
            Toast.makeText(this, "Permiso dado", Toast.LENGTH_SHORT).show();
            try {
                FileOutputStream fos = new FileOutputStream(textFile);
                if(cursor.moveToFirst()){
                    fos.write("Categoria;Subcategoria;Importe;Fecha;Comentario".getBytes());
                    do{
                        String categoria = cursor.getString(1);
                        String subcategoria = cursor.getString(2);
                        double importe = cursor.getDouble(3);
                        String fecha = cursor.getString(4);
                        String comentario = cursor.getString(5);


                        Log.e("total", "Total: "+totalSuma);
                        fos.write(("\n"+categoria+ ";"+subcategoria+ ";"+importe+";"+fecha+";"+comentario).getBytes(StandardCharsets.ISO_8859_1));
                    } while (cursor.moveToNext());
                }else{
                    Log.e("Gasto", "No encuentra gasto");
                }

                fos.close();
                Toast.makeText(this, "Exito al escribir", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Permiso no dado", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }


    private void cargarCategoria(){
        SharedPreferences sharedPreferences = getSharedPreferences("categorias", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("lista categorias", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        listCategorias = gson.fromJson(json, type);
        Log.e("Lista",listCategorias+"");
        Collections.sort(listCategorias);

        if(listCategorias == null){
            listCategorias = new ArrayList<>();
        }
    }

    @Override
    public void onCategoriaListener(int position) {
        Log.e("CLICK", "funciona "+listCategorias.get(position));
        Intent intent = new Intent(this, ListaSubcategoriasActivity.class);
        String categoria = listCategorias.get(position);
        intent.putExtra(EXTRA_CATEGORIA, categoria);
        startActivity(intent);
    }

    @Override
    public void onCategoriaLongClick(int position) {
        if(comprobarArray(position)){

        }else{
            categoriasSeleccionadas.add(position);

        }
    }

    public void dialogoCSV(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(ListaCategoriasActivity.this);
        alerta.setTitle("Guardar gastos");
        alerta.setMessage("Si acepta se guardará un archivo excel y borrará los gastos de la aplicación")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        escribirFicheroCSV();
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

    public void dialogoEliminar(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(ListaCategoriasActivity.this);
        alerta.setTitle("Eliminar");
        alerta.setMessage("Si acepta se eliminará las categorias selecionadas")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        borrarCategoria();
                        guardarCategoria();
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
        for(int i = 0; i < categoriasSeleccionadas.size(); i++){
            if(categoriasSeleccionadas.get(i) == valor) {
                categoriasSeleccionadas.remove(i);
                existe = true;
            }
        }

        return existe;
    }

    public void borrarCategoria(){
        Collections.sort(categoriasSeleccionadas);
        Log.e("Categoria", "Categorias ordenadas "+categoriasSeleccionadas);
        Collections.reverse(categoriasSeleccionadas);
        Log.e("Categoria", "Categorias reverse "+categoriasSeleccionadas);

        for(int i = 0; i<categoriasSeleccionadas.size(); i++){
            int indiceBorrar = categoriasSeleccionadas.get(i);
            listCategorias.remove(indiceBorrar);
        }

        Log.e("Categoria", "Categorias final "+listCategorias);
    }

    private void guardarCategoria() {
        SharedPreferences sharedPreferences = getSharedPreferences("categorias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listCategorias);
        editor.putString("lista categorias", json);
        editor.apply();
        finish();
    }

}
