package com.example.gtrayectoriasrmr;

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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gtrayectoriasrmr.MobileAnarchy.JoystickMovedListener;
import com.example.gtrayectoriasrmr.MobileAnarchy.JoystickView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;

public class Generar_TrayectoriaMC extends AppCompatActivity {
    private static final String wmax = "wmax.txt";
    private static final String rllantas = "rllantas.txt";
    private static final String dllantas = "dllantas.txt";
    private static final String vlineal = "vlineal.txt";
    private static final String largo = "largo.txt";
    private static final String acho = "ancho.txt";
    private static final long RATE = 5;
    //private static final double radio = 15;
    private static double radio = 0;
   // private static final double longitud = 38.0;
   private static double longitud = 0;

   LinearLayout mContent;

    private static double rad;

    private static double longi;
    private static double anchoe;
    private static double altoe;
    private static float velocidadlineal;
    private static String en1 = null;
    private static String en2 = null;

    public static int num=0;

    private static float wd2 = 0;
    private static float wi2 = 0;
   // LinearLayout Cpizarron;
    signature mSignature;
    Button Clear, Save , Cancel;
    public static String direccion;
    public boolean inicio=true;
    public String current = null;
    public String lineaTexto = "";
    public String nwax = "";
    public String nrad = "";
    public String nlong = "";
    public String vlineal2 = "";
    public String nancho = "";
    public String nalto = "";
    public ArrayList puntosX = new ArrayList();
    public ArrayList puntosY = new ArrayList();
    public ArrayList puntoswd = new ArrayList();
    public ArrayList puntoswi = new ArrayList();


    public ArrayList puntosX1 = new ArrayList();
    public ArrayList puntosY1 = new ArrayList();
    public ArrayList maspuntosX = new ArrayList();
    public ArrayList maspuntosY = new ArrayList();
    public double maxX = 0;
    public double minX = 0;
    public double maxY = 0;
    public double minY = 0;
    public double longY = 0;
    public double longX = 0;
    public double ajusteX = 0;
    public double ajusteY = 0;
    public String name;


   //converti a flotantes
   float ancho = 0;
    float alto = 0;
    int iancho = 0;
    int ialto = 0;
    float anchoDer=0;
    float anchoIzq=0;
    float altoSup=0;
    float altoBajo=0;
    private Bitmap mBitmap;
    private Bitmap bmp;
    private Bitmap walle;
    View mView;
    File mypath;
    public int last_wd=0,   last_wi=0;
    public Drawable imagen;

    private String dats;
    private EditText renombre;

    TextView txtX, txtY;
    JoystickView joystick;
    JoystickView joystick2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_trayectoria_mc);
        /*Se asigna variables a los apartasdos de hasta abajo que son los joystick y los look*/
        txtX = (TextView) findViewById(R.id.TextViewX);
        txtY = (TextView) findViewById(R.id.TextViewY);
        joystick = (JoystickView) findViewById(R.id.joystickView);
        joystick2 = (JoystickView) findViewById(R.id.joystickView2);

        /*El listener estara en modo escucha para los joystick*/
        joystick.setOnJostickMovedListener(_listener);
        joystick2.setOnJostickMovedListener(_listener2);
        /*Se asigna la ruta en la que se guardaran en el telefono */
        direccion = Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.external_dir) + "/";
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir(getResources().getString(R.string.external_dir), Context.MODE_PRIVATE);
        leerarchivo();
        if (wd2==0.0f&&wi2==0.0f&&radio==0.0f&&longitud==0.0f&&altoe==0.0f&&anchoe==0.0f)
        {
            Toast.makeText(getBaseContext(), "Ingresar los valores a trabajar en configuracion ", Toast.LENGTH_LONG).show();
        }
        /*llama a la funcion*/
        prepareDirectory();
        /*se le asigna los diferentes datos al nombre que llevara la imagen*/
        dats = getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random();
        current = dats + ".png";
        mypath = new File(directory, current);

        /*Asignar variables y restricciones a los demas componentes*/
        mContent = (LinearLayout) findViewById(R.id.linearLayout);
        mSignature = new signature(this, null);
        mSignature.setBackgroundColor(Color.WHITE);
        /*Añade la estructura dentro dle layaout */
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Clear = (Button) findViewById(R.id.limp);
        Save = (Button) findViewById(R.id.save);
        Save.setEnabled(false);
        Cancel = (Button) findViewById(R.id.cancel);
        mView = mContent;
        /*Nombre que el usuario le quiera poner se guardara aqui*/
       // renombre = (EditText) findViewById(R.id.nomtrayect);
        renombre = findViewById(R.id.nomtrayect);
        name = renombre.getText().toString();
        /*Funcion del boton que limpiara el pizarron*/
        Clear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Cleared");
                mSignature.clear();
                /*El boton guardar estara desabilitado hasta que haya algo*/
                Save.setEnabled(false);
            }
        });
        /*Funcion del boton save que guardara los puntos ademas de la imagen*/
        Save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Saved");
                boolean error = captureSignature();
                if (!error) {
                    /*Llama a la funcion guardar puntos*/
                    mSignature.guardarPuntos();
                    mView.setDrawingCacheEnabled(true);
                    /*Manda a ambas al save*/
                    mSignature.save(mView);
                    Bundle b = new Bundle();
                    b.putString("status", "done");
                    Intent intent = new Intent();
                    intent.putExtras(b);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        /*Funcion del botor cancelar y te regresa al inicio*/
        Cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("log_tag", "Panel Canceled");
                Bundle b = new Bundle();
                b.putString("status", "cancel");
                Intent intent = new Intent();
                intent.putExtras(b);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
     //  leerarchivo();


    }

    //llamar a funcion
  public void leerarchivo() {

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = openFileInput(wmax);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String lineaTexto;
            StringBuilder stringBuilder = new StringBuilder();
            while ((lineaTexto = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineaTexto).append("\n");
                //en1=lineaTexto;
                nwax=lineaTexto;
                //wd2 = new Double(lineaTexto).doubleValue();
                //wi2 = new Double(lineaTexto).doubleValue();
            }
            wd2= Float.parseFloat(nwax);
            wi2= Float.parseFloat(nwax);

            //Toast.makeText(this, "El valor es: "+ lineaTexto, Toast.LENGTH_LONG).show();
        } catch (Exception e) {

        }

        try {
            fileInputStream = openFileInput(rllantas);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
           // String lineaTexto;
            StringBuilder stringBuilder = new StringBuilder();
            while ((lineaTexto = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineaTexto).append("\n");
                nrad=lineaTexto;
            }
            radio = Double.parseDouble(nrad);
        } catch (Exception e) {

        }

        try {
            fileInputStream = openFileInput(dllantas);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            //   String lineaTexto;
            StringBuilder stringBuilder = new StringBuilder();
            while ((lineaTexto = bufferedReader.readLine()) != null) {
                stringBuilder.append(lineaTexto).append("\n");
                nlong = lineaTexto;
            }
        longitud= Double.parseDouble(nlong);
        } catch (Exception e) {

        }
      try {
          fileInputStream = openFileInput(vlineal);
          InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
          BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
          //   String lineaTexto;
          StringBuilder stringBuilder = new StringBuilder();
          while ((lineaTexto = bufferedReader.readLine()) != null) {
              stringBuilder.append(lineaTexto).append("\n");
              vlineal2 = lineaTexto;
          }
          velocidadlineal= Float.parseFloat(vlineal2);
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
                    nalto=lineaTexto;
                }
                altoe =  Double.parseDouble(nalto);
                altoe = altoe - .40;
            } catch (Exception e) {

            }
            try {
                fileInputStream = openFileInput(acho);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String lineaTexto;
                StringBuilder stringBuilder = new StringBuilder();
                while ((lineaTexto = bufferedReader.readLine()) != null) {
                    stringBuilder.append(lineaTexto).append("\n");
                    nancho=lineaTexto;
                }
                anchoe =  Double.parseDouble(nancho);
                anchoe = altoe - .40;
               // anchoe = anchoe + 4;
                //ancho = (int) Double.parseDouble(lineaTexto);
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


    private JoystickMovedListener _listener = new JoystickMovedListener() {



        @Override
        public void OnMoved(int pan, int tilt) {
            /*Boton guardar se activa*/
            Save.setEnabled(true);
            /*Valores al mover el joystick Y y los mostrara en el look de y*/
            if(tilt>0){//wi
                tilt=-tilt;
            }
            else{
                tilt=-tilt;

            }

            txtY.setText("WD "+Integer.toString(tilt));
            last_wi=tilt;

        }


        @Override
        /*Jostick suelto*/
        public void OnReleased() {
            txtX.setText("released");
            txtY.setText("released");
            last_wi=0;
        }

        public void OnReturnedToCenter() {
            /*Se mostrara lo siguiente cuando esten en el centro*/
            txtX.setText("Stop");
            txtY.setText("Stop");
            last_wi=0;
        };
    };

    private JoystickMovedListener _listener2 = new JoystickMovedListener() {

        @Override
        public void OnMoved(int pan, int tilt) {

            Save.setEnabled(true);
            /*Valores al mover el joystick Y y los mostrara en el look de x*/
            if(tilt>0){//wi
                tilt=-tilt;
            }
            else{
                tilt=-tilt;
            }

            txtX.setText("WI "+Integer.toString(tilt));
            last_wd=tilt;

        }


        @Override
        public void OnReleased() {
            txtX.setText("released");
            txtY.setText("released");
            last_wd=0;
        }

        public void OnReturnedToCenter() {
            txtX.setText("Stop");
            txtY.setText("Stop");
            last_wd=0;
        };
    };

    /*Se cierra la app*/
    @Override
    protected void onDestroy() {
        Log.w("GetSignature", "onDestory");
        super.onDestroy();
    }

    private boolean captureSignature() {

        boolean error = false;
        String errorMessage = "";

        /*Condicion para llenar el editext llamado nombretrayec*/
        if (renombre.getText().toString().equalsIgnoreCase("")) {
            errorMessage = errorMessage + "Por Favor ingresa el \n nombre de la Trayectoria!! \n";
            error = true;
        }

        if (error) {
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            /*Posicion del mensaje*/
            toast.setGravity(Gravity.TOP, 105, 50);
            toast.show();
        }

        return error;
    }

    /*Obtener los datos del dia*/
    private String getTodaysDate() {

        final Calendar c = Calendar.getInstance();
        int todaysDate = (c.get(Calendar.YEAR) * 10000) +
                ((c.get(Calendar.MONTH) + 1) * 100) +
                (c.get(Calendar.DAY_OF_MONTH));
        Log.w("DATE:", String.valueOf(todaysDate));
        return (String.valueOf(todaysDate));

    }
    /*Obtener tiempo del dia*/
    private String getCurrentTime() {

        final Calendar c = Calendar.getInstance();
        int currentTime = (c.get(Calendar.HOUR_OF_DAY) * 10000) +
                (c.get(Calendar.MINUTE) * 100) +
                (c.get(Calendar.SECOND));
        Log.w("TIME:", String.valueOf(currentTime));
        return (String.valueOf(currentTime));

    }

    /*Prepara el espacio donde sera almacenado*/
    private boolean prepareDirectory() {
        try {
            /*Llama a la funcion makedirs*/
            if (makedirs()) {
                return true;
            } else {
                return false;
            }
        }/*fallo en la tarjeta, no es reconocida*/
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not initiate File System.. Is Sdcard mounted properly?", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean makedirs() {
        /*Verifica que la ruta sea correcta*/
        File tempdir = new File(direccion);
        if (!tempdir.exists())
            tempdir.mkdirs();

        if (tempdir.isDirectory()) {
            File[] files = tempdir.listFiles();
            for (File file : files) {
                /*error al eliminar*/
                if (!file.delete()) {
                    System.out.println("Failed to delete " + file);
                }
            }
        }
        return (tempdir.isDirectory());
    }

    public class signature extends View {
        private static final float STROKE_WIDTH = 10f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();
        public double x=100.0;
        public double y=100.0;
        public double phi=0.0;
        public double anterior_wd=0, anterior_wi=0;

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();
        /*Parametros al pintar*/
        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            bmp = BitmapFactory.decodeResource(getResources(), R.drawable.dif2);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);

           // imagen = context.getResources().getDrawable(R.drawable.dif2);

        }
        /*Lo que realizara al apretar el boton de guardar*/
        public void save(View v) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (mBitmap == null) {
                /*Cuando se cumpla la condicion mbitmap los almacenara en una imagen vectorial todos los siguientes parametros*/
                mBitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
                //;

            }
            Canvas canvas = new Canvas(mBitmap);


            try {
                FileOutputStream mFileOutStream = new FileOutputStream(mypath);

                v.draw(canvas);
                /*Convierte la imagen vectorial a PNG y la guarda*/
                mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();
                String url = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "title", null);
                Log.v("log_tag", "url: " + url);
                //In case you want to delete the file
                //boolean deleted = mypath.delete();
                //Log.v("log_tag","deleted: " + mypath.toString() + deleted);
                //If you want to convert the image to string use base64 converter

            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
        }
        /*Funcion que limpiara el pizarron borrando los puntos y el camino*/
        public void clear() {
            path.reset();
            invalidate();
            x=100.0;
            y=100.0;
            phi=0.0;
            puntosX.clear();
            puntosY.clear();
            puntoswd.clear();
            puntoswi.clear();
        }

        public void trayectoria(float wd, float wi) {
            String mensaje="wd";
            mensaje= mensaje+ String.valueOf(wd);
            mensaje=mensaje+"-wi:"+wi;
            String lineaTexto= "";

            //wd=wd/6;
            //wi=wi/6;
            //wd=10;
            //wi=0;


/*
                wd2= ((wd*wd2)/10);
                wi2=  ((wi*wi2)/10);

*/
           // Toast.makeText(this,"El archivo es: ",Toast.LENGTH_LONG).show();

            path.moveTo(Float.valueOf(String.valueOf(x)),Float.valueOf(String.valueOf(y)));
            //for (int i=0;i<100;i++){
            wd=(wd*wd2)/10;
            wi=(wi*wi2)/10;

            x = x + ((((wd + wi) * radio) / 2) * Math.cos(phi)) * 0.02;
            y = y + ((((wd + wi) * radio) / 2) * Math.sin(phi)) * 0.02;
            phi = phi + ((((wd - wi) * radio) / (2 * longitud))) * 0.02;
            int dx = 2;
            int dy = -2;

            if(y + dy < 0) {
                dy = -dy;
            }

             if (x< anchoDer)
            {
                x=anchoDer;
                // Save.setEnabled(false);
            }
            else if (x >= anchoIzq) {
                x=anchoIzq;
                // Save.setEnabled(false);
            }
            if (y< altoSup)
            {
                y=altoSup;
                //Save.setEnabled(false);
            }
            else if (y >= altoBajo) {
               y=altoBajo;
                // Save.setEnabled(false);
            }

            puntosX.add(x);
            puntosY.add(y);
            puntoswd.add(wd);
            puntoswi.add(wi);

            path.lineTo(Float.valueOf(String.valueOf(x)), Float.valueOf(String.valueOf(y)));
            //}
            inicio=false;
            //anterior_wd=wd;
            //anterior_wi=wi;

            invalidate();
        }



        public void guardarPuntos() {
            String sPuntosX = "";
            String sPuntosX1 = "";
            String sPuntoswd = "";
            String sPuntoswi = "";
            String stiempos = "";
            String sMasPuntosX = "";
            String sMasPuntosY = "";
            String sPuntosY = "";
            String sPuntosY1 = "";
            double maximo = 0, maxY2 = 0, minY2 = 0;
            double[] x_Array = new double[puntosX.size()];
            double[] y_Array = new double[puntosY.size()];


            for (int i = 0; i < puntosX.size(); i++) {
                if (i == 0) {
                    minX = maxX = (double) puntosX.get(i);
                    minY2 = maxY2 = (double) puntosY.get(i);
                    sPuntosX = "x," + puntosX.get(i);
                    sPuntosY = "" + puntosY.get(i);
                    x_Array[i] = (double) puntosX.get(i);

                } else {
                    sPuntosX = sPuntosX + "," + puntosX.get(i);
                    sPuntosY = sPuntosY + "" + puntosY.get(i);
                    x_Array[i] = (double) puntosX.get(i);
                    //Encontrar minimo de X
                    if (minX > x_Array[i]) {
                        minX = x_Array[i];
                    }
                    //Encontrar el maximo de X
                    if (maxX < x_Array[i]) {
                        maxX = x_Array[i];
                    }
                    //Encontrar minimo de Y
                    if (minY2 > (double) puntosY.get(i)) {
                        minY2 = (double) puntosY.get(i);
                    }
                    //Encontrar el maximo de Y
                    if (maxY2 < (double) puntosY.get(i)) {
                        maxY2 = (double) puntosY.get(i);
                    }
                }

            }

            for (int j = 0; j < puntoswd.size(); j++) {

                if (j == 0) {
                    sPuntosX1 = "" + puntoswd.get(j);
                    sPuntosY1 = "" + puntoswi.get(j);

                } else {
                    sPuntosX1 = sPuntosX1 + " -" + puntoswd.get(j);
                    sPuntosY1 = sPuntosY1 + "- " + puntoswi.get(j);
                }
            }


            //Volteaar los puntos del eje Y
            for (int j = 0; j < puntosY.size(); j++) {
                if (j == 0) {
                    sPuntosY = "y," + (-1 * ((double) puntosY.get(j)) + maxY2);
                    y_Array[j] = (double) puntosY.get(j);
                    minY = maxY = (double) puntosY.get(j);

                } else {
                    sPuntosY = sPuntosY + "," + (-1 * ((double) puntosY.get(j)) + maxY2);
                    y_Array[j] = (double) puntosY.get(j);
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
            //Toast.makeText(getBaseContext(), "xmax"+maxX, Toast.LENGTH_LONG).show();
           // Toast.makeText(getBaseContext(), "Ymax"+maxY, Toast.LENGTH_LONG).show();
            Toast.makeText(getBaseContext(), "maxX "+maxX, Toast.LENGTH_LONG).show();
            Toast.makeText(getBaseContext(), "minX "+minX, Toast.LENGTH_LONG).show();
            Toast.makeText(getBaseContext(), "maxY "+maxY, Toast.LENGTH_LONG).show();
            Toast.makeText(getBaseContext(), "minY "+minY, Toast.LENGTH_LONG).show();
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
            longX = maxX - minX;
            longY = maxY - minY;

            if ((longX - anchoe) < 1.0) {
                ajusteX = (float) (1.0 - (longX - anchoe));
            } else {
                ajusteX = 1 / (float) (longX / anchoe);
            }

            if ((longY - altoe) < 1) {
                ajusteY = (float) (1 - (longY - altoe));
            } else {
                ajusteY = 1 / (float) (longY / altoe);
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

            tiempo=distancia/(float)velocidadlineal;


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
                    nx[k]= (float) x_Array[i];
                    ny[k]= (float) y_Array[i];
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
                    tiempos[i]=distancia/(float)velocidadlineal;
                    //Toast.makeText(getBaseContext(), "Tiempo " + i+": "+ tiempos[i], Toast.LENGTH_LONG).show();
                }
                else{
                    tiempos[i]=tiempos[i-1]+distancia/(float)velocidadlineal;
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

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse("mailto:"));
            String to = "josuahdzglez@gmail.com";
            //String[] cc = copias;
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
            //emailIntent.putExtra(Intent.EXTRA_CC, cc);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Puntos Trayectoria");
            //emailIntent.putExtra(Intent.EXTRA_TEXT, "PUNTOS X: " + sPuntosX + "\n" + "PUNTOS Y: " + sPuntosY + "\n" + "Maximo:" + maximo + "\n" + "\n" + "WD:" + sPuntoswd + "\n" + "WI:" + sPuntoswi + "\n" + "W max : " + nwax + "\n" + "Radio de las llantas : " + radio + "\n" + "Diestrancia / llantas: " + longitud + "\n" + "Ancho del espacio de trabajo: " + anchoe + "\n" + "Alto del espacio del trabajo: " + altoe + "\n"+"new x: " + sPuntosX1 +"\n"+ "new y: " + sPuntosY1);
           // emailIntent.putExtra(Intent.EXTRA_TEXT, "PUNTOS X: " + sPuntosX + "\n" + "maxX: " + maxX + "\n" + "minX: " + minX + "\n" + "new X: " + sPuntosX1 + "\n" + "new Y: " + sPuntosY1 + "\n" + "PUNTOS Y: " + sPuntosY + "\n" + "Maximo:" + maximo + "\n" + "maxY: " + maxY + "\n" + "minY: " + minY + "\n" + "tiempos: " + stiempos+"tiempo:"+tiempo+ "\n" + "WD:" + sPuntoswd + "\n" + "WI:" + sPuntoswi + "\n" + "W max : " + nwax + "\n" + "Radio de las llantas : " + radio + "\n"+ "Velocidad lineal : " + velocidadlineal + "\n" + "Diestrancia / llantas: " + longitud + "\n" + "Ancho del espacio de trabajo: " + anchoe + "\n" + "Alto del espacio del trabajo: "+ altoe);
            emailIntent.putExtra(Intent.EXTRA_TEXT, sPuntosX1 + ";" + sPuntosY1 + ";" + stiempos);

            emailIntent.setType("message/rfc822");
            startActivity(Intent.createChooser(emailIntent, "Email "));

            /*Guardar los puntos XY en un archivo Interno*/
            File fichero1 = new File("/data/user/0/com.example.gtrayectoriasrmr/files/myfile.txt");
           // File fichero1 = new File("/data/user/0/com.example.gtrayectoriasrmr/files/"+name+".txt");
            fichero1.delete();
            String filename = "myfile.txt";
            //String filename = name+".txt";
            FileOutputStream outputStream;
            try {

               // outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream = openFileOutput(filename, MODE_APPEND);
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
                Log.d("TAG1", "Informacion de la trayectoria guardada en: " + getFilesDir() + "/" + filename);
            } catch (Exception e) {
                e.printStackTrace();
            }


            /*try {
                FileOutputStream fos = openFileOutput("textFile.txt", MODE_APPEND);
                OutputStreamWriter osw = new OutputStreamWriter(fos);

                // Escribimos el String en el archivo
                osw.write(sPuntosX);
                osw.flush();
                osw.close();

                // Mostramos que se ha guardado
                // Toast.makeText(getBaseContext(), "Guardado", Toast.LENGTH_SHORT).show();

            } catch (IOException ex) {
                ex.printStackTrace();
            }*/
        }


        @Override
        protected void onDraw(Canvas canvas) {

            ancho = canvas.getWidth();
            alto = canvas.getHeight();
         /*  anchoDer= (float) (ancho-(ancho*.960));
            anchoIzq=(float) (ancho-(ancho*.100));;
            altoSup= (float) (alto-(alto*.980));;
            altoBajo=(float) (alto-(alto*.05));;*/

            anchoDer= (float) (ancho-(ancho*.960));
            anchoIzq=(float) (ancho-(ancho*.35));;
            altoSup= (float) (alto-(alto*.980));;
            altoBajo=(float) (alto-(alto*.5));;
             Paint pt = new Paint();
            pt.setColor(Color.RED);
            pt.setStyle(Paint.Style.STROKE);
            pt.setStrokeWidth(10);
            pt.setAntiAlias(true);
            //implementar variables para asignar a canvas draw

            canvas.drawRect(anchoDer,altoSup,anchoIzq,altoBajo,pt);



            Paint pc = new Paint();
            pc.setColor(Color.BLUE);
            pc.setStyle(Paint.Style.FILL);
            pc.setStrokeWidth(10);
            pc.setAntiAlias(true);

           // canvas.drawCircle(80,80,30,pc);
            iancho = canvas.getWidth()/40;
            ialto = canvas.getHeight()/35;
            walle = Bitmap.createScaledBitmap(bmp, iancho, ialto, true);
            //imagen.setBounds(50,50,100,100);
            //imagen.draw(canvas);

            // Cpizarron.
            //canvas.setBitmap(mBitmap);
            update();
            canvas.drawPath(path, paint);
            canvas.drawBitmap(walle, (float) x, (float) y, null);
            postInvalidateDelayed(RATE);
        }

        private void update() {
            //last_wd=last_wd+1;
            //last_wi=last_wi+1;
            trayectoria(last_wd, last_wi);

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
          /* if (eventX>= anchoDer)
            {
                eventX=anchoDer;
               // Save.setEnabled(false);
            }
            else if (eventX < anchoIzq) {
                eventX=anchoIzq;
               // Save.setEnabled(false);
            }
            if (eventY>= altoSup)
            {
                eventY=altoSup;
                //Save.setEnabled(false);
            }
            else if (eventY < altoBajo) {
                eventY=altoBajo;
               // Save.setEnabled(false);
            }*/
            /*
            if (eventX== anchoDer)
            {
                eventX=anchoDer;
                // Save.setEnabled(false);
            }
            else if (eventX == anchoIzq) {
                eventX=anchoIzq;
                // Save.setEnabled(false);
            }
            if (eventY == altoSup)
            {
                eventY=altoSup;
                //Save.setEnabled(false);
            }
            else if (eventY == altoBajo) {
                eventY=altoBajo;
                // Save.setEnabled(false);
            }*/
            Save.setEnabled(true);


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return false;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();


                    for (int i = 0; i < historySize - 1; i++) {
                        float historicalX = event.getHistoricalX(i);
                        //puntosX.add(historicalX);
                        float historicalY = event.getHistoricalY(i);
/*
                        if (historicalX== anchoDer)
                        {
                            eventX=anchoDer;
                            // Save.setEnabled(false);
                        }
                        else if (historicalX == anchoIzq) {
                            eventX=anchoIzq;
                            // Save.setEnabled(false);
                        }
                        if (historicalY== altoSup)
                        {
                            historicalY =altoSup;
                            //Save.setEnabled(false);
                        }
                        else if (historicalY == altoBajo) {
                            historicalY=altoBajo;
                            // Save.setEnabled(false);
                        }*/
                        //puntosY.add(historicalY);

                        //float Xmed = (event.getHistoricalX(i) + event.getHistoricalX(i)) / 2;
                        //float Ymed = ((event.getHistoricalY(i) + event.getHistoricalY(i)) / 2);

                        //Ymed = Ymed + 0.5f;

                        expandDirtyRect(historicalX, historicalY);
                        //paint.setColor(Color.BLACK);
                        path.lineTo(historicalX, historicalY);
                        //path.quadTo(Xmed,Ymed,event.getHistoricalX(i+1), event.getHistoricalY(i+1));
                    }
                    //paint.setColor(Color.BLUE);
                    //path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            /*invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));*/
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

          /*
            if (historicalX>= anchoDer)
            {
                historicalX=anchoDer;
                // Save.setEnabled(false);
            }
            else if (historicalX <= anchoIzq) {
                historicalX=anchoIzq;
                // Save.setEnabled(false);
            }
            if (historicalY >= altoSup)
            {
                historicalY=altoSup;
                //Save.setEnabled(false);
            }
            if (historicalY<= altoBajo) {
                historicalY = altoBajo;
                // Save.setEnabled(false);
            }*/
        }


        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);

            dirtyRect.right = Math.max(lastTouchX, eventX);


            dirtyRect.top = Math.min(lastTouchY, eventY);

            dirtyRect.bottom = Math.max(lastTouchY, eventY);



        }
    }
}