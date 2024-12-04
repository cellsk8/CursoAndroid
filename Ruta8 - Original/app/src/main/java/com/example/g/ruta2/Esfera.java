package com.example.g.ruta2;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by g on 04/08/2015.
 */
public class Esfera extends Activity {
    LinearLayout mContent;
    signature mSignature;
    Button mClear, mGetSign, mCancel;
    public static String tempDir;
    public String current = null;
    public ArrayList puntosX = new ArrayList();
    public ArrayList puntosY = new ArrayList();
    public ArrayList puntosX1 = new ArrayList();
    public ArrayList puntosY1 = new ArrayList();
    public float maxX = 0;
    public float minX = 0;
    public float maxY = 0;
    public float minY = 0;
    public float longY = 0;
    public float longX = 0;
    public float ajusteX = 0;
    public float ajusteY = 0;
    View mView;
    File mypath;

    private String uniqueId;
    private EditText yourName;
    private int invalida = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ruta_manual);

        tempDir = Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.external_dir) + "/";
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir(getResources().getString(R.string.external_dir), Context.MODE_PRIVATE);

        current = uniqueId + ".png";
        mypath = new File(directory, current);


        mContent = (LinearLayout) findViewById(R.id.linearLayout);
        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) findViewById(R.id.clear);
        mGetSign = (Button) findViewById(R.id.getsign);
        mGetSign.setEnabled(false);
        mCancel = (Button) findViewById(R.id.cancel);

        mView = mContent;

        yourName = (EditText) findViewById(R.id.yourName);

        mClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                mGetSign.setEnabled(false);
            }
        });

        mGetSign.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Saved");

                    if (invalida > 1) {
                        Toast.makeText(getBaseContext(), "Trayectoria no Valida!!\n Ya que está compuesta por: " + invalida + " tramos", Toast.LENGTH_LONG).show();
                        mSignature.clear();
                        invalida =0;
                    } else {
                        mSignature.guardarPuntos();
                        mSignature.clear();
                        mView.setDrawingCacheEnabled(true);
                        //mSignature.save(mView);
                        Bundle b = new Bundle();
                        b.putString("status", "done");
                        Intent intent = new Intent();
                        intent.putExtras(b);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSignature.clear();
                Log.v("log_tag", "Panel Canceled");
                Bundle b = new Bundle();
                b.putString("status", "cancel");
                Intent intent = new Intent();
                intent.putExtras(b);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }

    @Override
    protected void onDestroy() {
        Log.w("GetSignature", "onDestory");
        super.onDestroy();
    }


    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();
        private Bitmap bmp;
        private float x, y;
        private int sWidth;
        private int sHeigth;
        private Bitmap ball;


        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.esfera);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
            x = 0;
            y = 0;
        }


        public void clear() {
            path.reset();
            invalidate();
            puntosX.clear();
            puntosY.clear();
            puntosX1.clear();
            puntosY1.clear();
            x = 0;
            y = 0;
        }


        public void guardarPuntos() {
            String sPuntosX = "";
            String sPuntosX1 = "";
            String sPuntosY = "";
            String sPuntosY1 = "";
            String stiempos = "";
            float maximo = 0, maxY2 = 0, minY2 = 0;
            float[] x_Array = new float[puntosX.size()];
            float[] y_Array = new float[puntosY.size()];

            for (int i = 0; i < puntosX.size(); i++) {
                if (i == 0) {
                    minX = maxX = (float) puntosX.get(i);
                    minY2 = maxY2 = (float) puntosY.get(i);
                    sPuntosX = "x," + puntosX.get(i);
                    sPuntosY = "" + puntosY.get(i);
                    x_Array[i] = (float) puntosX.get(i);

                } else {
                    sPuntosX = sPuntosX + "," + puntosX.get(i);
                    sPuntosY = sPuntosY + "" + puntosY.get(i);
                    x_Array[i] = (float) puntosX.get(i);
                    //Encontrar minimo de X
                    if (minX > x_Array[i]) {
                        minX = x_Array[i];
                    }
                    //Encontrar el maximo de X
                    if (maxX < x_Array[i]) {
                        maxX = x_Array[i];
                    }
                    //Encontrar minimo de Y
                    if (minY2 > (float) puntosY.get(i)) {
                        minY2 = (float) puntosY.get(i);
                    }
                    //Encontrar el maximo de Y
                    if (maxY2 < (float) puntosY.get(i)) {
                        maxY2 = (float) puntosY.get(i);
                    }
                }

            }

            for (int j = 0; j < puntosX1.size(); j++) {

                if (j == 0) {
                    sPuntosX1 = "" + puntosX1.get(j);
                    sPuntosY1 = "" + puntosY1.get(j);

                } else {
                    sPuntosX1 = sPuntosX1 + " -" + puntosX1.get(j);
                    sPuntosY1 = sPuntosY1 + "- " + puntosY1.get(j);
                }
            }


            //Volteaar los puntos del eje Y
            for (int j = 0; j < puntosY.size(); j++) {
                if (j == 0) {
                    sPuntosY = "y," + (-1 * ((float) puntosY.get(j)) + maxY2);
                    y_Array[j] = (float) puntosY.get(j);
                    minY = maxY = (float) puntosY.get(j);

                } else {
                    sPuntosY = sPuntosY + "," + (-1 * ((float) puntosY.get(j)) + maxY2);
                    y_Array[j] = (float) puntosY.get(j);
                    //Encontrar minimo de Y
                    if (minY > y_Array[j]) {
                        minY = y_Array[j];
                    }
                    //Encontrar el maximo de Y
                    if (maxY < y_Array[j]) {
                        maxY = y_Array[j];
                    }

                }
            }

            //REDUCIR la trayectoria
            if ((maxX / 1000) > 1.0 || (maxY / 1000) > 1.0) {

                minX = maxX = x_Array[0] / 1000;
                minY = maxY = y_Array[0] / 1000;
                for (int i = 0; i < x_Array.length; i++) {
                    x_Array[i] = x_Array[i] / 1000;

                    //Encontrar minimo de X
                    if (minX > x_Array[i]) {
                        minX = x_Array[i];
                    }
                    //Encontrar el maximo de X
                    if (maxX < x_Array[i]) {
                        maxX = x_Array[i];
                    }

                }

                for (int i = 0; i < y_Array.length; i++) {
                    y_Array[i] = y_Array[i] / 1000;
                    //Encontrar minimo de Y
                    if (minY > y_Array[i]) {
                        minY = y_Array[i];
                    }
                    //Encontrar el maximo de Y
                    if (maxY < y_Array[i]) {
                        maxY = y_Array[i];
                    }
                }
            } else if ((maxX / 100) > 1.0 || (maxY / 100) > 1.0) {
                minX = maxX = x_Array[0] / 100;
                minY = maxY = y_Array[0] / 100;
                for (int i = 0; i < x_Array.length; i++) {
                    x_Array[i] = x_Array[i] / 100;
                    //Encontrar minimo de X
                    if (minX > x_Array[i]) {
                        minX = x_Array[i];
                    }
                    //Encontrar el maximo de X
                    if (maxX < x_Array[i]) {
                        maxX = x_Array[i];
                    }
                }

                for (int i = 0; i < y_Array.length; i++) {
                    y_Array[i] = y_Array[i] / 100;
                    //Encontrar minimo de Y
                    if (minY > y_Array[i]) {
                        minY = y_Array[i];
                    }
                    //Encontrar el maximo de Y
                    if (maxY < y_Array[i]) {
                        maxY = y_Array[i];
                    }
                }
            } else {
                Toast.makeText(getBaseContext(), "Dimensiones no compatibles", Toast.LENGTH_LONG).show();
            }

            //Ajustar Trayectoria al area de trabajo 1.6 x 1.6 m
            //Calcular la Longitud de X,Y
            longX = maxX - minX;
            longY = maxY - minY;

            if ((longX - 1.6) < 1.0) {
                ajusteX = (float) (1.0 - (longX - 1.6));
            } else {
                ajusteX = 1 / (float) (longX / 1.6);
            }

            if ((longY - 1.6) < 1) {
                ajusteY = (float) (1 - (longY - 1.6));
            } else {
                ajusteY = 1 / (float) (longY / 1.6);
            }

            //Actualizar X,Y con el ajuste
            sPuntosX1 = "x";
            sPuntosY1 = "y";
            for (int i = 0; i < x_Array.length; i++) {
                x_Array[i] = x_Array[i] * ajusteX;
                sPuntosX1 = sPuntosX1 + "," + x_Array[i];
            }

            for (int i = 0; i < y_Array.length; i++) {
                y_Array[i] = y_Array[i] * ajusteY;
                sPuntosY1 = sPuntosY1 + "," + y_Array[i];
            }

            sPuntosY = sPuntosY1;
            sPuntosX = sPuntosX1;

            //Toast.makeText(getBaseContext(), "Nx" + sPuntosX1.length(), Toast.LENGTH_LONG).show();

            /******************************************************
             * Calcular la distancia total de la trayectoria
             ******************************************************/
            float distancia,tiempo;
            int k;
            distancia=0;
            k=0;

            for (int i = 0; i < x_Array.length-1; i++) {
                distancia=distancia+(float)Math.sqrt( Math.pow((x_Array[i+1]-x_Array[i]),2)+ Math.pow((y_Array[i+1]-y_Array[i]),2));
            }

            tiempo=distancia/(float)0.3;


            /******************************************************
             * Quitar puntos con una distancia menor a 1cm
             ******************************************************/
            distancia=0;
            for (int i = 0; i < x_Array.length-1; i++) {
                distancia=(float)Math.sqrt( Math.pow((x_Array[i+1]-x_Array[i]),2)+ Math.pow((y_Array[i+1]-y_Array[i]),2));
                if(distancia<0.01)
                    i=i+1;
                else{
                    k=k+1;
                }
            }

            float[] nx = new float[k];
            float[] ny = new float[k];
            k=0;
            distancia=0;
            for (int i = 0; i < x_Array.length-1; i++) {
                distancia=(float)Math.sqrt( Math.pow((x_Array[i+1]-x_Array[i]),2)+ Math.pow((y_Array[i+1]-y_Array[i]),2));
                if(distancia<0.01)
                    i=i+1;
                else{
                    nx[k]=x_Array[i];
                    ny[k]=y_Array[i];
                    k=k+1;
                }
            }

            /******************************************************
             * Agregar puntos si la distancia es mayor a 2cm entre puntos
             ******************************************************/
            distancia=0;
            k=0;
            //Encontrar el tamaño del vector final para (x,y)
            for (int i = 0; i < nx.length-1; i++) {
                distancia=(float)Math.sqrt( Math.pow((nx[i+1]-nx[i]),2)+ Math.pow((ny[i+1]-ny[i]),2));
                if(distancia>0.02){
                    k=k+1;
                    k=k+1;
                }
                else{
                    k=k+1;
                }
            }


            //Crear los vectores con el valor encontrado anteriormente
            float[] fx = new float[k];
            float[] fy = new float[k];
            //Toast.makeText(getBaseContext(), "Tamaño K" + k, Toast.LENGTH_LONG).show();
            //Toast.makeText(getBaseContext(), "Tamaño fx" + fx.length, Toast.LENGTH_LONG).show();
            /*for (int i = 0; i < fx.length-1; i++) {
                Toast.makeText(getBaseContext(), "Tamaño fx" + i + ":"+ fx[i], Toast.LENGTH_SHORT).show();
            }*/

            //Guardar la trayectoria final
            distancia=0;
            k=0;
            for (int i = 0; i < nx.length-1; i++) {
                distancia=(float)Math.sqrt( Math.pow((nx[i+1]-nx[i]),2)+ Math.pow((ny[i+1]-ny[i]),2));
                if(distancia>0.02){
                    fx[k]=nx[i];
                    fy[k]=ny[i];
                    k=k+1;
                    fx[k]=(nx[i+1]+nx[i])/2;
                    fy[k]=(ny[i+1]+ny[i])/2;
                    k=k+1;
                }
                else{
                    fx[k]=nx[i];
                    fy[k]=ny[i];
                    k=k+1;
                }
            }

            /******************************************************
             * Guardas los tiempos
             ******************************************************/
            distancia=0;
            float[] tiempos = new float[fx.length];

            //Encontrar el tamaño del vector final para (x,y)
            for (int i = 0; i < fx.length-1; i++) {
                distancia=(float)Math.sqrt( Math.pow((fx[i+1]-fx[i]),2)+ Math.pow((fy[i+1]-fy[i]),2));
                if(i==0){
                    tiempos[i]=distancia/(float)0.3;
                    //Toast.makeText(getBaseContext(), "Tiempo " + i+": "+ tiempos[i], Toast.LENGTH_LONG).show();
                }
                else{
                    tiempos[i]=tiempos[i-1]+distancia/(float)0.3;
                }
            }

            /******************************************************
             * Guardas los nuevos valores de XY en un string
             ******************************************************/
            sPuntosX1 = "x";
            sPuntosY1 = "y";
            stiempos = "t";

            for (int i = 0; i < fx.length; i++) {
                sPuntosX1 = sPuntosX1 + "," + fx[i];
                sPuntosY1 = sPuntosY1 + "," + fy[i];
            }

            for (int i = 0; i < tiempos.length-1; i++) {
                stiempos = stiempos + "," + tiempos[i];
            }
            stiempos=stiempos+",";

            /*Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse("mailto:"));
            String to = "insqui2000@gmail.com";
            //String[] cc = copias;
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
            //emailIntent.putExtra(Intent.EXTRA_CC, cc);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Puntos Trayectoria");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "PUNTOS X: " + sPuntosX + "\n" + "maxX: " + maxX + "\n" + "minX: " + minX + "\n" + "new X: " + sPuntosX1 + "\n" + "new Y: " + sPuntosY1 + "\n" + "PUNTOS Y: " + sPuntosY + "\n" + "Maximo:" + maximo + "\n" + "maxY: " + maxY + "\n" + "minY: " + minY + "\n" + "tiempos: " + stiempos+"tiempo:"+tiempo);
            emailIntent.setType("message/rfc822");
            startActivity(Intent.createChooser(emailIntent, "Email "));*/


            /*Guardar los puntos XY en un archivo Interno*/
            String filename = "myfile";
            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                //outputStream.write("x,".getBytes());
                outputStream.write(sPuntosX1.getBytes());
                outputStream.write(";".getBytes());
                outputStream.write(sPuntosY1.getBytes());
                outputStream.write(";".getBytes());
                outputStream.write(stiempos.getBytes());
                outputStream.write(",".getBytes());
                outputStream.close();
                //Toast.makeText(getBaseContext(), "x" + sPuntosX.length(), Toast.LENGTH_LONG).show();
                Toast.makeText(getBaseContext(), "Trayectoria Guardada!", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSizeChanged(int a, int b, int c, int d) {
            sWidth = getWidth() / 15;
            sHeigth = getHeight() / 10;
            ball = Bitmap.createScaledBitmap(bmp, sWidth, sHeigth, true);
        }


        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
            canvas.drawBitmap(ball, x, y, null);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    invalida = invalida + 1;
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();


                    for (int i = 0; i < historySize - 1; i++) {
                        float historicalX = event.getHistoricalX(i);
                        puntosX.add(historicalX);
                        x = historicalX - (sWidth / 2);
                        float historicalY = event.getHistoricalY(i);
                        puntosY.add(historicalY);
                        y = historicalY - (sHeigth / 2);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }

                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate();

            lastTouchX = eventX;
            lastTouchY = eventY;


            return true;
        }

        private void debug(String string) {

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}

