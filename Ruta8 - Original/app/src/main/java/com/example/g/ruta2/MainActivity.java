package com.example.g.ruta2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends ActionBarActivity {

    public static final int SIGNATURE_ACTIVITY = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button esferaButton = (Button) findViewById(R.id.esferaButton);
        esferaButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Esfera.class);
                startActivityForResult(intent,SIGNATURE_ACTIVITY);

            }
        });

        Button getSignature = (Button) findViewById(R.id.signature);
        getSignature.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CapturarRutaManual.class);
                startActivityForResult(intent,SIGNATURE_ACTIVITY);

            }
        });


        Button getRuta = (Button) findViewById(R.id.modCinematico);
        getRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CapturarRuta.class);
                startActivityForResult(intent,SIGNATURE_ACTIVITY);

            }
        });

        Button conexionBluetooth = (Button) findViewById(R.id.conexionBluetooth);
        conexionBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ConexionBluetooth.class);
                startActivityForResult(intent,SIGNATURE_ACTIVITY);

            }
        });

    }



}