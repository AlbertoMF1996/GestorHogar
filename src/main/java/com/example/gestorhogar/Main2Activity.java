package com.example.gestorhogar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.gestorhogar.MainActivity.MENSAJE_CATEGORIA;
import static com.example.gestorhogar.MainActivity.MENSAJE_CATEGORIA_SELECCIONADA;
import static com.example.gestorhogar.MainActivity.MENSAJE_SUBCATEGORIA;

public class Main2Activity extends AppCompatActivity {

    Button aceptar, cancelar;
    TextView tv_nuevaCategoria, tv_titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        aceptar = findViewById(R.id.actDialogo_aceptar_btn);
        cancelar = findViewById(R.id.actDialogo_cancelar_btn);
        tv_nuevaCategoria = findViewById(R.id.actDialogo_nuevoElemento_txt);
        tv_titulo = findViewById(R.id.actDialogo_titulo_string);

        aceptar.setEnabled(false);

        String titulo = getIntent().getStringExtra("titulo");

        if(titulo.equals("categoria")){
            tv_titulo.setText(titulo);
        }else{
            tv_titulo.setText(titulo+": "+getIntent().getStringExtra("categoria"));
        }


        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getIntent().getStringExtra("opcion").equals("1")) {
                    Intent intent = new Intent();
                    String nuevaCategoria = tv_nuevaCategoria.getText().toString();
                    intent.putExtra(MENSAJE_CATEGORIA, nuevaCategoria);
                    setResult(RESULT_OK, intent);
                }else if(getIntent().getStringExtra("opcion").equals("2")){
                    /*MANDAR DE VUELTA LA CATEGORIA Y LA NUEVA SUBCATEGORIA PARA CREAR UN ARRAY*/
                    Intent intent = new Intent();
                    String nuevaSubcategoria = tv_nuevaCategoria.getText().toString();
                    intent.putExtra(MENSAJE_SUBCATEGORIA, nuevaSubcategoria);
                    intent.putExtra(MENSAJE_CATEGORIA_SELECCIONADA, getIntent().getStringExtra("categoria"));
                    setResult(RESULT_OK, intent);
                }

                finish();
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_nuevaCategoria.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count>0){
                    aceptar.setEnabled(true);
                }else{
                    aceptar.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


}
