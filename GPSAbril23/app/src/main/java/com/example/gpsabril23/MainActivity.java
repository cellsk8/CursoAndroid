package com.example.gpsabril23;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    Button btUbicacion;
    TextView tvDirec,tvLatitud,tvLongitud;
    EditText edtUbicacion1;
    LocationManager locationManager;
    ProgressBar barra ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Permisos de ubicacion
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        barra=findViewById(R.id.barra);
        barra.setVisibility(View.INVISIBLE);
        btUbicacion = findViewById(R.id.button);
        tvLongitud = findViewById(R.id.txtLongitud);
        tvLatitud=findViewById(R.id.txtLatitud);
        edtUbicacion1=findViewById(R.id.edtUbicacion);
        btUbicacion.setOnClickListener(view -> {

            barra.setVisibility(View.VISIBLE);
            getLocation();
            //barra.setEnabled(false);
            //Toast.makeText(this,"entro al boton",Toast.LENGTH_LONG).show();
        });


    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, MainActivity.this);
    }



    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Toast.makeText(this, ""+location.getLatitude()+","+location.getLongitude(),Toast.LENGTH_LONG).show();
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            //Convertir la longitud y latitud en una direccion de calles
            List<Address>addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            //tvDirec.setText(addresses.get(0).getAddressLine(0));
            edtUbicacion1.setText(addresses.get(0).getAddressLine(0));
            tvLatitud.setText(""+location.getLatitude());
            tvLongitud.setText(""+location.getLongitude());
            barra.setVisibility(View.GONE);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"Hubo un error al obtener la ubicación",Toast.LENGTH_LONG).show();
        }



    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void cerrar(View view){
        //
        // finish();
        //System.exit(0);
        //int p = android.os.Process.myPid();
        //android.os.Process.killProcess(p);
    }
}