package com.example.gtrayectoriasrmr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Configuracion extends AppCompatActivity {

    private EditText et1, et2, et3, et4, et5,et6;
    private Button guardar;
    private Button leer;
    private Button regreso;
    private static final String wmax = "wmax.txt";
    private static final String rllantas = "rllantas.txt";
    private static final String dllantas = "dllantas.txt";
    private static final String vlineal = "vlineal.txt";
    private static final String largo = "largo.txt";
    private static final String ancho = "ancho.txt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        setUpView();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpView() {
        et1 = findViewById(R.id.wmax);
        et2 = findViewById(R.id.rllantas);
        et3 = findViewById(R.id.dllantas);
        et4 = findViewById(R.id.largo);
        et5 = findViewById(R.id.ancho);
        et6 = findViewById(R.id.vlineal);
        guardar = findViewById(R.id.salvar);
        guardar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                savefile();

            }
        });
        leer = findViewById(R.id.read);
        leer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                readfile();
            }
        });


    }


    private void savefile() {


        File fichero1 = new File("/data/user/0/com.example.gtrayectoriasrmr/files/wmax.txt");
        fichero1.delete();
        File fichero2 = new File("/data/user/0/com.example.gtrayectoriasrmr/files/rllantas.txt");
        fichero2.delete();
        File fichero3 = new File("/data/user/0/com.example.gtrayectoriasrmr/files/dllantas.txt");
        fichero3.delete();
        File fichero6 = new File("/data/user/0/com.example.gtrayectoriasrmr/files/vlineal.txt");
        fichero6.delete();
        File fichero4 = new File("/data/user/0/com.example.gtrayectoriasrmr/files/largo.txt");
        fichero4.delete();
        File fichero5 = new File("/data/user/0/com.example.gtrayectoriasrmr/files/ancho.txt");
        fichero5.delete();


        String textoASalvar1 = et1.getText().toString();
        String textoASalvar2 = et2.getText().toString();
        String textoASalvar3 = et3.getText().toString();
        String textoASalvar4 = et4.getText().toString();
        String textoASalvar5 = et5.getText().toString();
        String textoASalvar6 = et6.getText().toString();


        FileOutputStream fileOutputStream = null;

        if (textoASalvar1.length() == 0 && textoASalvar2.length() == 0 && textoASalvar3.length() == 0 && textoASalvar6.length() == 0) {
            Toast.makeText(getBaseContext(), "Debes ingresar todos los parametros ", Toast.LENGTH_LONG).show();
        }
        if (textoASalvar1.length() != 0 && textoASalvar2.length() != 0 && textoASalvar3.length() != 0 && textoASalvar6.length() != 0) {
            try {
                fileOutputStream = openFileOutput(wmax, MODE_APPEND);
                fileOutputStream.write(textoASalvar1.getBytes());
                fileOutputStream = openFileOutput(rllantas, MODE_APPEND);
                fileOutputStream.write(textoASalvar2.getBytes());
                fileOutputStream = openFileOutput(dllantas, MODE_APPEND);
                fileOutputStream.write(textoASalvar3.getBytes());
                fileOutputStream = openFileOutput(vlineal, MODE_APPEND);
                fileOutputStream.write(textoASalvar6.getBytes());
                fileOutputStream = openFileOutput(largo, MODE_APPEND);
                fileOutputStream.write(textoASalvar4.getBytes());
                fileOutputStream = openFileOutput(ancho, MODE_APPEND);
                fileOutputStream.write(textoASalvar5.getBytes());
                Log.d("TAG1", "Informacion wmax guardada en: " + getFilesDir() + "/" + wmax);
                Log.d("TAG2", "Informacion rllantas guardada en: " + getFilesDir() + "/" + rllantas);
                Log.d("TAG3", "Informacion dllantas guardada en: " + getFilesDir() + "/" + dllantas);
                Log.d("TAG3", "Informacion vlineal guardada en: " + getFilesDir() + "/" + vlineal);
                Log.d("TAG4", "Informacion largo guardada en: " + getFilesDir() + "/" + largo);
                Log.d("TAG5", "Informacion ancho guardada en: " + getFilesDir() + "/" + ancho);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            et1.setText("");
            et2.setText("");
            et3.setText("");
            et6.setText("");
            et4.setText("");
            et5.setText("");

            Toast.makeText(getBaseContext(), "Datos Guardados ", Toast.LENGTH_LONG).show();

        }
    }
    private void readfile() {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = openFileInput(wmax);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String lineaTexto;
            StringBuilder stringBuilder = new StringBuilder();
            while ((lineaTexto = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineaTexto).append("\n");
            }
            et1.setText(stringBuilder);
        } catch (Exception e) {

        }
        try {
            fileInputStream = openFileInput(rllantas);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String lineaTexto;
            StringBuilder stringBuilder = new StringBuilder();
            while ((lineaTexto = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineaTexto).append("\n");
            }
            et2.setText(stringBuilder);
        } catch (Exception e) {

        }
        try {
            fileInputStream = openFileInput(dllantas);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String lineaTexto;
            StringBuilder stringBuilder = new StringBuilder();
            while ((lineaTexto = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineaTexto).append("\n");
            }
            et3.setText(stringBuilder);
        } catch (Exception e) {

        }
        try {
            fileInputStream = openFileInput(largo);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String lineaTexto;
            StringBuilder stringBuilder = new StringBuilder();
            while ((lineaTexto = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineaTexto).append("\n");
            }
            et4.setText(stringBuilder);
        } catch (Exception e) {

        }
        try {
            fileInputStream = openFileInput(vlineal);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String lineaTexto;
            StringBuilder stringBuilder = new StringBuilder();
            while ((lineaTexto = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineaTexto).append("\n");
            }
            et6.setText(stringBuilder);
        } catch (Exception e) {

        }
        try {
            fileInputStream = openFileInput(ancho);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String lineaTexto;
            StringBuilder stringBuilder = new StringBuilder();
            while ((lineaTexto = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineaTexto).append("\n");
            }
            et5.setText(stringBuilder);
        } catch (Exception e) {

        }
        finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception e) {

                }
            }
        }
    }



}

