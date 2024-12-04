package com.example.p20_archivosexternos;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private EditText etNombre,etInformacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etNombre=findViewById(R.id.txt_nombre);
        etInformacion=findViewById(R.id.txt_informacion);

    }

    public void guardar(View view){
        String nombre=etNombre.getText().toString();
        String contenido=etInformacion.getText().toString();

        try{
            File tarjetaSD = Environment.getExternalStorageDirectory();
            Toast.makeText(this, "Ruta"+tarjetaSD.getPath(), Toast.LENGTH_SHORT).show();
            File rutaArchivo=new File(tarjetaSD.getPath(),nombre);
            OutputStreamWriter crearArchivo=new OutputStreamWriter(openFileOutput(nombre,MODE_PRIVATE));
            crearArchivo.write(contenido);
            crearArchivo.flush();
            crearArchivo.close();
            Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show();
            etNombre.setText("");
            etInformacion.setText("");
        }
        catch (IOException e){
            Toast.makeText(this, "No se pudo guardar", Toast.LENGTH_SHORT).show();
        }
    }

    public void consultar(View view){
        String nombre=etNombre.getText().toString();
        try{
            File tarjetaSD=Environment.getExternalStorageDirectory();
            File rutaArchivo=new File(tarjetaSD.getPath(),nombre);
            InputStreamReader abrirArchivo=new InputStreamReader(openFileInput(nombre));
            BufferedReader leerArchivo=new BufferedReader(abrirArchivo);
            String linea=leerArchivo.readLine();
            String contenidoGuardado="";
            while (linea!=null){
                contenidoGuardado=contenidoGuardado+linea+"\n";
                linea=leerArchivo.readLine();
            }
            leerArchivo.close();
            abrirArchivo.close();
            etInformacion.setText(contenidoGuardado);
        }catch (IOException e){
            Toast.makeText(this, "Error al leer el archivo"+e, Toast.LENGTH_SHORT).show();
        }
    }
}