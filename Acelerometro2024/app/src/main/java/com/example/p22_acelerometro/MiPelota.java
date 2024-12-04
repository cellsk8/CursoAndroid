package com.example.p22_acelerometro;

import android.content.Context;
import android.graphics.*;
import android.hardware.*;
import android.view.*;
import android.widget.Toast;

public class MiPelota extends View implements SensorEventListener {

    Paint pincel = new Paint();
    int alto, ancho;
    int tamanio=40;
    int borde=12;
    float ejeX=0,ejeY=0,ejeZ1=0,ejeZ=0;
    String X,Y,Z;
    public MiPelota(Context interfaz){
        super(interfaz);
        SensorManager smAdministrador = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor snsRotation = smAdministrador.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        smAdministrador.registerListener(this, snsRotation, SensorManager.SENSOR_DELAY_FASTEST);
        Display pantalla = ((WindowManager) getContext() .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        ancho = pantalla.getWidth();
        alto=pantalla.getHeight();
    }

    @Override
    public void onSensorChanged(SensorEvent cambio) {
        ejeX-=cambio.values[0];

        X=Float.toString(ejeX);
        if(ejeX<(tamanio+borde)){
            ejeX=(tamanio+borde);
        }
        else if(ejeX > (ancho-(tamanio+borde))){
            ejeX=ancho-(tamanio+borde);
        }

        ejeY+=cambio.values[1];
        Y=Float.toString(ejeY);
        if(ejeY<(tamanio+borde)){
            ejeY=(tamanio+borde);
        }
        else if (ejeY>(alto-tamanio-170)){
            ejeY=alto-tamanio-170;
        }

        ejeZ=cambio.values[2]*2;
        Z=String.format("%.2f",ejeZ);
        invalidate();
    }




    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onDraw(Canvas lienzo){
        pincel.setColor(Color.RED);
        lienzo.drawCircle(ejeX,ejeY, ejeZ+tamanio, pincel);
        pincel.setColor(Color.WHITE);
        pincel.setTextSize(25);
        lienzo.drawText("pelota", ejeX-35,ejeY+3, pincel);
    }
}
