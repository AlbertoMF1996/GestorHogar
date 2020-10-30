package com.example.gestorhogar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import entidades.Gasto;
import entidades.Subcategoria;
import utilidades.Utilidades;

public class MainActivity extends AppCompatActivity /*implements Main2Activity.FinalizoCuadroDialogo*/{


    private static final int REQUEST_CODE_CATEGORIA = 222;
    public static final String MENSAJE_CATEGORIA = "categoria";
    private static final int REQUEST_CODE_SUBCATEGORIA = 223;
    public static final String MENSAJE_SUBCATEGORIA = "subcategoria";
    public static final String MENSAJE_CATEGORIA_SELECCIONADA = "catSeleccionada";
    public int refrescar = 0;
    ConexionSQLiteHelper conn;

    Spinner categoria, subCategoria;
    Context contexto;
    EditText cuantia, comentario;
    TextView fechaEt;

    Button guardar,resumen, addCategoria, addSubcategoria;
    ArrayList<String> arrayCategorias;
    ArrayList<String> arraySubcategorias;
    public static final String DIRECTORIO_APP = "Gestor Hogar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categoria = findViewById(R.id.act1_categoria_spn);
        subCategoria = findViewById(R.id.act1_subCategoria_spn);
        guardar = findViewById(R.id.act1_guardar_btn);
        resumen = findViewById(R.id.act1_resumen_btn);
        addCategoria = findViewById(R.id.act1_addCategoria_btn);
        addSubcategoria = findViewById(R.id.act1_addSubcategoria_btn);
        cuantia = findViewById(R.id.act1_cuantia_numText);
        fechaEt = findViewById(R.id.act1_fecha_editText);
        comentario = findViewById(R.id.act1_comentario_editText);
        contexto = this;

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(calendar.YEAR);
        final int month = calendar.get(calendar.MONTH);
        final int day = calendar.get(calendar.DAY_OF_MONTH);


        conn = new ConexionSQLiteHelper(this, "bd_subcategorias", null, 1);
        comprobarPermisos();

        cargarCategoria();

        guardar.setEnabled(false);


        final ArrayAdapter<String> spinnerCategoria = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item, arrayCategorias);
        spinnerCategoria.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        categoria.setAdapter(spinnerCategoria);

        if(categoria.getSelectedItem() !=null) {
            cargarSubcategoria(categoria.getSelectedItem().toString());
        }else{
            cargarSubcategoria("null");
        }

        ArrayAdapter<String> spinnerSubcategoria = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item, arraySubcategorias);
        spinnerSubcategoria.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        subCategoria.setAdapter(spinnerSubcategoria);

        cuantia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("") || categoria.getSelectedItem() == null){
                    guardar.setEnabled(false);

                }else{
                    guardar.setEnabled(true);

                }
            }
        });


        categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarSubcategoria(arrayCategorias.get(position));
                ArrayAdapter<String> spinnerSubcategoria = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item, arraySubcategorias);
                spinnerSubcategoria.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                subCategoria.setAdapter(spinnerSubcategoria);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        fechaEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        String date = day+"/"+month+"/"+year;
                        fechaEt.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });



        addCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                i.putExtra("titulo", "Categoría");
                i.putExtra("opcion", "1");
                startActivityForResult(i ,REQUEST_CODE_CATEGORIA);
            }
        });
        addSubcategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                i.putExtra("titulo", "Subcategoría");
                i.putExtra("categoria", categoria.getSelectedItem().toString());
                i.putExtra("opcion", "2");
                startActivityForResult(i ,REQUEST_CODE_SUBCATEGORIA);
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String fechaTexto;
                String comentarioTexto;
                String subcategoriaFinal;

                Log.e("FECHA", "valor campo fecha: "+fechaEt.getText().toString().length());
                Log.e("FECHA", "Dia: "+day+" mes: "+month+" año:"+year);
                String mesFinal = String.valueOf(month + 1);
                if(fechaEt.getText().toString().length()==0){
                    fechaTexto = day+"/"+mesFinal+"/"+year;
                }else{
                    fechaTexto = fechaEt.getText().toString();
                }

                DecimalFormat decimalFormat = new DecimalFormat("#.00");

                double primero = Double.parseDouble(cuantia.getText().toString());
                String segundo = decimalFormat.format(primero);


                if(segundo.contains(",")){
                    Log.e("DECIMAL", "estoy en el movil");
                    segundo = segundo.replace(",",".");
                    Log.e("DECIMAL", segundo+"");

                }else{
                    Log.e("DECIMAL", "estoy en el emulador");
                }

                double importeFinal = Double.parseDouble(segundo);

                comentarioTexto = comentario.getText().toString();

                if(subCategoria.getSelectedItemPosition() == 0){
                    subcategoriaFinal = "";
                    Log.e("PRUEBA", "entro en la pòsicion 0");
                }else{
                    subcategoriaFinal = subCategoria.getSelectedItem().toString();
                }


                Gasto nuevoGasto = new Gasto (categoria.getSelectedItem().toString(),subcategoriaFinal, importeFinal, fechaTexto, comentarioTexto);

                guardarGasto(nuevoGasto);
            }
        });

        resumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refrescar++;
                Intent intent = new Intent(getApplicationContext(), ListaCategoriasActivity.class);
                startActivity(intent);
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

    private void comprobarPermisos() {
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED){
            createFolder();
        }else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 100 && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            createFolder();
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void createFolder(){

        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(Environment.getExternalStorageDirectory(),DIRECTORIO_APP);
        Log.e("Path", ""+file);
        Log.e("Path", ""+baseDir);

        if(!file.exists()){
            file.mkdirs();
            if(!file.isDirectory()){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                String sMessage = "Message : failed to create directory"+ "\n Path: "+ Environment.getExternalStorageDirectory()+ " \n mkdirs: "+ file.mkdirs();
                builder.setMessage(sMessage);
                builder.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch(requestCode){
            case REQUEST_CODE_CATEGORIA:
                if(resultCode == RESULT_OK){
                    arrayCategorias.add(data.getStringExtra(MENSAJE_CATEGORIA));
                    guardarCategoria();
                }else if(resultCode == RESULT_CANCELED){
                    Toast.makeText(getApplicationContext(),"Operación cancelada",Toast.LENGTH_SHORT).show();

                }
                break;

            case REQUEST_CODE_SUBCATEGORIA:
                if(resultCode == RESULT_OK){
                    // MENSAJE_SUBCATEGORIA tiene la subcategoria escrita | MENSAJE_CATEGORIA_SELECCIONADA tiene la categoria seleccionada de vuelta
                    // Crear un shared preferences de subcategorias
                    // Crear un array al comienzo donde se actualicen las subcategorias en función de la categoria seleccionada (onChangeListener)
                    Subcategoria nuevaSubcategoria = new Subcategoria(data.getStringExtra(MENSAJE_SUBCATEGORIA), data.getStringExtra(MENSAJE_CATEGORIA_SELECCIONADA));

                    guardarSubcategoria(nuevaSubcategoria);

                }
        }
    }


    private void guardarCategoria() {
        SharedPreferences sharedPreferences = getSharedPreferences("categorias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayCategorias);
        editor.putString("lista categorias", json);
        editor.apply();
        recreate();
    }

    private void guardarSubcategoria(Subcategoria subcategoria){
        SQLiteDatabase db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Utilidades.CAMPO_SUBCATEGORIA, subcategoria.getSubcategoria());
        values.put(Utilidades.CAMPO_CATEGORIA, subcategoria.getCategoria());

        db.insert(Utilidades.TABLA_SUBCATEGORIA, Utilidades.CAMPO_ID, values);

        db.close();
        recreate();

    }

    private void guardarGasto(Gasto gasto){
        SQLiteDatabase db = conn.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Utilidades.CAMPO_CATEGORIA, gasto.getCategoria());
        values.put(Utilidades.CAMPO_SUBCATEGORIA, gasto.getSubcategoria());
        values.put(Utilidades.CAMPO_IMPORTE, gasto.getImporte());
        values.put(Utilidades.CAMPO_FECHA, gasto.getFecha());
        values.put(Utilidades.CAMPO_COMENTARIO, gasto.getComentario());


        Long idResultante = db.insert(Utilidades.TABLA_GASTOS, Utilidades.CAMPO_ID, values);
        Toast.makeText(getApplicationContext(), "Id registro: "+idResultante, Toast.LENGTH_SHORT).show();
        Log.e("RESULTADO", idResultante+"");


        db.close();
        cuantia.setText("");
        comentario.setText("");
        fechaEt.setText("");


    }

    private void cargarCategoria(){
        SharedPreferences sharedPreferences = getSharedPreferences("categorias", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("lista categorias", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        arrayCategorias = gson.fromJson(json, type);


        if(arrayCategorias == null){
            arrayCategorias = new ArrayList<>();
            resumen.setEnabled(false);
        }else{
            Collections.sort(arrayCategorias);
        }
    }

    private void cargarSubcategoria(String categoriaSeleccionada){
        arraySubcategorias = new ArrayList<>();

        SQLiteDatabase db = conn.getReadableDatabase();
        String [] campos = {Utilidades.CAMPO_SUBCATEGORIA, Utilidades.CAMPO_CATEGORIA};
        String selection = Utilidades.CAMPO_CATEGORIA + " = ?";
        String [] selectionArgs = {categoriaSeleccionada};

        try{
            Cursor cursor = db.query(Utilidades.TABLA_SUBCATEGORIA, campos, selection, selectionArgs,null,null,null);
            ArrayList subcategoriasGuardadas = new ArrayList<>();
            while(cursor.moveToNext()){
                String nombreSubcategoria = cursor.getString(0);
                subcategoriasGuardadas.add(nombreSubcategoria);
            }
            arraySubcategorias = subcategoriasGuardadas;
            Collections.sort(arraySubcategorias);
            arraySubcategorias.add(0, "Sin subcategoria");
            cursor.close();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error al mostrar", Toast.LENGTH_SHORT).show();
            Log.e("ERROR", ""+e);
        }
//        if(arraySubcategorias == null){
//            arraySubcategorias = new ArrayList<>();
//        }
    }
    //TODO: Conectar entre dispositivos
    //TODO: la puta estética



}
