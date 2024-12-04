package com.example.p20_basesdatos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText et_codigo, et_descripcion, et_precio;
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

        et_codigo = (EditText) findViewById(R.id.et_codigo);
        et_descripcion = (EditText) findViewById(R.id.et_descripcion);
        et_precio = (EditText) findViewById(R.id.et_precio);
    }

    public void registrar(View view){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase BaseDeDatos = admin.getWritableDatabase();

        String codigo = et_codigo.getText().toString();
        String descripcion = et_descripcion.getText().toString();
        String precio = et_precio.getText().toString();

        if (!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()) {
            ContentValues registro = new ContentValues();

            registro.put("codigo", codigo);
            registro.put("descripcion", descripcion);
            registro.put("precio", precio);

            BaseDeDatos.insert("articulos", null, registro);

            BaseDeDatos.close();
            et_codigo.setText("");
            et_descripcion.setText("");
            et_precio.setText("");

            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    public void Buscar(View view){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase BaseDeDatabase = admin.getWritableDatabase();

        String codigo = et_codigo.getText().toString();

        if(!codigo.isEmpty()){
            Cursor fila = BaseDeDatabase.rawQuery
                    ("select descripcion, precio from articulos where codigo =" + codigo, null);

            if(fila.moveToFirst()){
                et_descripcion.setText(fila.getString(0));
                et_precio.setText(fila.getString(1));
                BaseDeDatabase.close();
            } else {
                Toast.makeText(this,"No existe el artículo", Toast.LENGTH_SHORT).show();
                BaseDeDatabase.close();
            }

        } else {
            Toast.makeText(this, "Debes introducir el código del artículo", Toast.LENGTH_SHORT).show();
        }
    }

    //Método para eliminar un producto o Artículo
    public void Eliminar(View view){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper
                (this, "administracion", null, 1);
        SQLiteDatabase BaseDatabase = admin.getWritableDatabase();

        String codigo = et_codigo.getText().toString();

        if(!codigo.isEmpty()){

            int cantidad = BaseDatabase.delete("articulos", "codigo=" + codigo, null);
            BaseDatabase.close();

            et_codigo.setText("");
            et_descripcion.setText("");
            et_precio.setText("");

            if(cantidad == 1){
                Toast.makeText(this, "Artículo eliminado exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "El artículo no existe", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Debes de introducir el código del artículo", Toast.LENGTH_SHORT).show();
        }
    }

    //Método para modificar un artículo o producto
    public void Modificar(View view){
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        SQLiteDatabase BaseDatabase = admin.getWritableDatabase();

        String codigo = et_codigo.getText().toString();
        String descripcion = et_descripcion.getText().toString();
        String precio = et_precio.getText().toString();

        if(!codigo.isEmpty() && !descripcion.isEmpty() && !precio.isEmpty()){

            ContentValues registro = new ContentValues();
            registro.put("codigo", codigo);
            registro.put("descripcion", descripcion);
            registro.put("precio", precio);

            int cantidad = BaseDatabase.update("articulos", registro, "codigo=" + codigo, null);
            BaseDatabase.close();

            if(cantidad == 1){
                Toast.makeText(this, "Artículo modificado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "El artículo no existe", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Debes llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }


}