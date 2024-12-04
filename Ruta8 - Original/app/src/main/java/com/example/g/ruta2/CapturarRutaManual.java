package com.example.g.ruta2;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Created by g on 04/08/2015.
 */
public class CapturarRutaManual extends Activity {
    LinearLayout mContent;
    signature mSignature;
    Button mClear, mGetSign, mCancel;
    public static String tempDir;
    public int count = 1;
    public String current = null;
    public ArrayList puntosX = new ArrayList();
    public ArrayList puntosY = new ArrayList();
    public ArrayList puntosX1 = new ArrayList();
    public ArrayList puntosY1 = new ArrayList();
    public ArrayList maspuntosX = new ArrayList();
    public ArrayList maspuntosY = new ArrayList();
    public float maxX = 0;
    public float minX = 0;
    public float maxY = 0;
    public float minY = 0;
    public float longY = 0;
    public float longX = 0;
    public float ajusteX = 0;
    public float ajusteY = 0;
    private Bitmap mBitmap;
    View mView;
    File mypath;

    private String uniqueId;
    private EditText yourName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_ruta_manual);

        tempDir = Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.external_dir) + "/";
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir(getResources().getString(R.string.external_dir), Context.MODE_PRIVATE);

        prepareDirectory();
        uniqueId = getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random();
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
                boolean error = captureSignature();
                if (!error) {
                    mSignature.guardarPuntos();
                    mView.setDrawingCacheEnabled(true);
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

        mCancel.setOnClickListener(new View.OnClickListener() {
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


    }

    @Override
    protected void onDestroy() {
        Log.w("GetSignature", "onDestory");
        super.onDestroy();
    }

    private boolean captureSignature() {

        boolean error = false;
        String errorMessage = "";


        if (yourName.getText().toString().equalsIgnoreCase("")) {
            errorMessage = errorMessage + "Por Favor Ingresa tu Nombre\n";
            error = true;
        }

        if (error) {
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 105, 50);
            toast.show();
        }

        return error;
    }

    private String getTodaysDate() {

        final Calendar c = Calendar.getInstance();
        int todaysDate = (c.get(Calendar.YEAR) * 10000) +
                ((c.get(Calendar.MONTH) + 1) * 100) +
                (c.get(Calendar.DAY_OF_MONTH));
        Log.w("DATE:", String.valueOf(todaysDate));
        return (String.valueOf(todaysDate));

    }

    private String getCurrentTime() {

        final Calendar c = Calendar.getInstance();
        int currentTime = (c.get(Calendar.HOUR_OF_DAY) * 10000) +
                (c.get(Calendar.MINUTE) * 100) +
                (c.get(Calendar.SECOND));
        Log.w("TIME:", String.valueOf(currentTime));
        return (String.valueOf(currentTime));

    }


    private boolean prepareDirectory() {
        try {
            if (makedirs()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not initiate File System.. Is Sdcard mounted properly?", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean makedirs() {
        File tempdir = new File(tempDir);
        if (!tempdir.exists())
            tempdir.mkdirs();

        if (tempdir.isDirectory()) {
            File[] files = tempdir.listFiles();
            for (File file : files) {
                if (!file.delete()) {
                    System.out.println("Failed to delete " + file);
                }
            }
        }
        return (tempdir.isDirectory());
    }

    public class signature extends View {
        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());
            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
                //;
            }
            Canvas canvas = new Canvas(mBitmap);
            try {
                FileOutputStream mFileOutStream = new FileOutputStream(mypath);

                v.draw(canvas);
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

        public void clear() {
            path.reset();
            invalidate();
            puntosX.clear();
            puntosY.clear();
        }



        public void generarMasPuntos() {
            float primero = 0, segundo = 0,primeroy=0, segundoy=0;


            for (int i = 0; i < puntosX.size() - 1; i++) {
                primero = (float) puntosX.get(i);
                segundo = (float) puntosX.get(i + 1);

                if (primero == segundo) {
                    maspuntosX.add(primero);
                    this.repetir(primero, true);
                    //Toast.makeText(getBaseContext(), "Entro al primero\n"+String.valueOf(primero), Toast.LENGTH_SHORT).show();

                } else if (primero < segundo) {
                    this.incrementar(primero, segundo, true);
                    //Toast.makeText(getBaseContext(), "Entro al segundo\n"+String.valueOf(primero), Toast.LENGTH_SHORT).show();
                } else {
                    this.incrementar(primero, segundo, false);
                    // Toast.makeText(getBaseContext(), "Entro a la RESTA\n"+primero+"\n"+segundo, Toast.LENGTH_LONG).show();
                }

            }

            for (int i = 0; i < puntosY.size() - 1; i++) {
                primeroy = (float) puntosY.get(i);
                segundoy = (float) puntosY.get(i + 1);

                if (primero == segundo) {
                    maspuntosY.add(primero);
                    this.repetir(primero, false);
                    //Toast.makeText(getBaseContext(), "Entro al primero\n"+String.valueOf(primero), Toast.LENGTH_SHORT).show();

                } else if (primero < segundo) {
                    this.incrementarY(primeroy, segundoy, true);
                    //Toast.makeText(getBaseContext(), "Entro al segundo\n"+String.valueOf(primero), Toast.LENGTH_SHORT).show();
                } else {
                    this.incrementarY(primeroy, segundoy, false);
                    // Toast.makeText(getBaseContext(), "Entro a la RESTA\n"+primero+"\n"+segundo, Toast.LENGTH_LONG).show();
                }

            }


        }

        public void repetir(float inicial, boolean x) {

            for (int i = 0; i < 10; i++) {

                if(x){
                    maspuntosX.add(inicial);
                }
                else{
                    maspuntosY.add(inicial);
                }
            }

        }

        public void incrementar(float inicial, float ultimo, boolean sumar) {

            BigDecimal value = new BigDecimal(inicial);
            BigDecimal value2 = new BigDecimal(ultimo);

            value = value.setScale(1, RoundingMode.HALF_EVEN); // here the value is correct (625.30)
            inicial= value.floatValue();

            value2 = value2.setScale(1, RoundingMode.HALF_EVEN); // here the value is correct (625.30)
            ultimo= value2.floatValue();

            //Toast.makeText(getBaseContext(),"Valor de incial\r\n"+inicial, Toast.LENGTH_SHORT).show();


            float contador = 0f;
            //Si sumar=true es incremental, sino es decremental
            if (sumar) {
                contador = inicial;
                do {
                    maspuntosX.add(contador);
                    contador += 0.1f;
                } while (contador < ultimo);

            } else {
                contador = inicial;
                do {
                    maspuntosX.add(contador);
                    contador =contador-0.1f;
                    // Toast.makeText(getBaseContext(), String.valueOf(contador), Toast.LENGTH_SHORT).show();
                } while (contador > ultimo);
            }
            //Toast.makeText(getBaseContext(), String.valueOf(contador), Toast.LENGTH_SHORT).show();

        }

        public void incrementarY(float inicial, float ultimo, boolean sumar) {

            BigDecimal value = new BigDecimal(inicial);
            BigDecimal value2 = new BigDecimal(ultimo);

            value = value.setScale(1, RoundingMode.HALF_EVEN); // here the value is correct (625.30)
            inicial= value.floatValue();

            value2 = value2.setScale(1, RoundingMode.HALF_EVEN); // here the value is correct (625.30)
            ultimo= value2.floatValue();

            //Toast.makeText(getBaseContext(),"Valor de incial\r\n"+inicial, Toast.LENGTH_SHORT).show();


            float contador = 0f;
            //Si sumar=true es incremental, sino es decremental
            if (sumar) {
                contador = inicial;
                do {
                    maspuntosY.add(contador);
                    contador += 0.1f;
                } while (contador < ultimo);

            } else {
                contador = inicial;
                do {
                    maspuntosY.add(contador);
                    contador =contador-0.1f;
                    // Toast.makeText(getBaseContext(), String.valueOf(contador), Toast.LENGTH_SHORT).show();
                } while (contador > ultimo);
            }
            //Toast.makeText(getBaseContext(), String.valueOf(contador), Toast.LENGTH_SHORT).show();

        }

        public void guardarPuntos() {
            String sPuntosX = "";
            String sPuntosX1 = "";
            String sMasPuntosX = "";
            String sMasPuntosY = "";
            String sPuntosY = "";
            String sPuntosY1 = "";
            float primero = 0, maximo = 0, segundo = 0, maxY2=0, minY2=0;
            float[] x_Array =new float[puntosX.size()];
            float[] y_Array =new float[puntosY.size()];
            //this.generarMasPuntos();



            for (int i = 0; i < puntosX.size(); i++) {
                if (i == 0) {
                    minX=maxX = (float)puntosX.get(i);
                    minY2=maxY2 = (float)puntosY.get(i);
                    sPuntosX = "x," + puntosX.get(i);
                    sPuntosY = "" + puntosY.get(i);
                    primero = (float) puntosY.get(i);
                    x_Array[i]= (float) puntosX.get(i);

                } else {
                    sPuntosX = sPuntosX + "," + puntosX.get(i);
                    sPuntosY = sPuntosY + "" + puntosY.get(i);
                    x_Array[i]= (float) puntosX.get(i);
                    //Encontrar minimo de X
                    if(minX > x_Array[i])
                    {
                        minX=x_Array[i];
                    }
                    //Encontrar el maximo de X
                    if(maxX < x_Array[i])
                    {
                        maxX=x_Array[i];
                    }
                    //Encontrar minimo de Y
                    if(minY2 > (float)puntosY.get(i))
                    {
                        minY2=(float)puntosY.get(i);
                    }
                    //Encontrar el maximo de Y
                    if(maxY2 < (float)puntosY.get(i))
                    {
                        maxY2=(float)puntosY.get(i);
                    }
                }

            }
            //Volteaar los puntos del eje Y
            for (int j = 0; j < puntosY.size(); j++) {
                if (j == 0) {
                    sPuntosY = "y," + (-1*((float)puntosY.get(j))+maxY2);
                    y_Array[j]= (-1*((float)puntosY.get(j))+maxY2);
                    minY=maxY = (float)puntosY.get(j);

                } else {
                    sPuntosY = sPuntosY + "," + (-1*((float)puntosY.get(j))+maxY2);
                    y_Array[j]= (-1*((float)puntosY.get(j))+maxY2);
                    //Encontrar minimo de Y
                    if(minY > y_Array[j])
                    {
                        minY=y_Array[j];
                    }
                    //Encontrar el maximo de Y
                    if(maxY < y_Array[j])
                    {
                        maxY=y_Array[j];
                    }

                }
            }

            for (int j = 0; j < puntosX1.size(); j++) {

                if (j == 0) {
                    sPuntosX1 = "" + puntosX1.get(j);
                    //sPuntosY1 = "" + puntosY1.get(j);
                    sPuntosY1 = "" + y_Array[j];

                } else {
                    sPuntosX1 = sPuntosX1 + " -" + puntosX1.get(j);
                    //sPuntosY1 = sPuntosY1 + "- " + puntosY1.get(j);
                    sPuntosY1 = "" + y_Array[j];
                }
            }



            for (int j = 0; j < maspuntosX.size(); j++) {
                if (j == 0) {
                    sMasPuntosX = "" + maspuntosX.get(j);
                    //sMasPuntosY=""+maspuntosY.get(j);
                } else {
                    sMasPuntosX = sMasPuntosX + " " + maspuntosX.get(j);
                    //sPuntosY=sPuntosY+" "+maspuntosY.get(j);
                }
            }

            for (int j = 0; j < maspuntosY.size(); j++) {
                if (j == 0) {
                    sMasPuntosY = "" + maspuntosY.get(j);
                    //sMasPuntosY=""+maspuntosY.get(j);
                } else {
                    sMasPuntosY = sMasPuntosY + " " + maspuntosY.get(j);
                    //sPuntosY=sPuntosY+" "+maspuntosY.get(j);
                }
            }

            // Toast.makeText(getBaseContext(), "+= " + sPuntosX, Toast.LENGTH_LONG).show();



            //REDUCIR la trayectoria

            if ((maxX/1000)>1.0 || (maxY/1000)>1.0 ) {

                minX=maxX = x_Array[0]/1000;
                minY=maxY = y_Array[0]/1000;
                for (int i=0; i < x_Array.length; i++) {
                    x_Array[i]=x_Array[i]/1000;

                    //Encontrar minimo de X
                    if(minX > x_Array[i])
                    {
                        minX=x_Array[i];
                    }
                    //Encontrar el maximo de X
                    if(maxX < x_Array[i])
                    {
                        maxX=x_Array[i];
                    }

                }

                for (int i=0; i < y_Array.length; i++) {
                    y_Array[i]=y_Array[i]/1000;
                    //Encontrar minimo de Y
                    if(minY > y_Array[i])
                    {
                        minY=y_Array[i];
                    }
                    //Encontrar el maximo de Y
                    if(maxY < y_Array[i])
                    {
                        maxY=y_Array[i];
                    }
                }
            }
            else if((maxX/100)>1.0 || (maxY/100)>1.0 ){
                minX=maxX = x_Array[0]/100;
                minY=maxY = y_Array[0]/100;
                for (int i=0; i < x_Array.length; i++) {
                    x_Array[i]=x_Array[i]/100;
                    //Encontrar minimo de X
                    if(minX > x_Array[i])
                    {
                        minX=x_Array[i];
                    }
                    //Encontrar el maximo de X
                    if(maxX < x_Array[i])
                    {
                        maxX=x_Array[i];
                    }
                }

                for (int i=0; i < y_Array.length; i++) {
                    y_Array[i]=y_Array[i]/100;
                    //Encontrar minimo de Y
                    if(minY > y_Array[i])
                    {
                        minY=y_Array[i];
                    }
                    //Encontrar el maximo de Y
                    if(maxY < y_Array[i])
                    {
                        maxY=y_Array[i];
                    }
                }
            }
            else{
                Toast.makeText(getBaseContext(), "Dimensiones no compatibles", Toast.LENGTH_LONG).show();
            }

            //Ajustar Trayectoria al area de trabajo 1.6 x 1.6 m
            //Calcular la Longitud de X,Y
            longX=maxX-minX;
            longY=maxY-minY;

            if((longX-1.6)<1.0){
                ajusteX=(float)(1.0-(longX-1.6));
            }
            else {
                ajusteX= 1/(float)(longX/1.6);
            }

            if((longY-1.6)<1){
                ajusteY=(float)(1-(longY-1.6));
            }
            else {
                ajusteY=1/(float)(longY/1.6);
            }

            //Actualizar X,Y con el ajuste
            sPuntosX1 ="x";
            sPuntosY1 ="y";
            for (int i=0; i < x_Array.length; i++) {
                x_Array[i]=x_Array[i]*ajusteX;
                sPuntosX1 = sPuntosX1 + "," + x_Array[i];
            }

            for (int i=0; i < y_Array.length; i++) {
                y_Array[i]=y_Array[i]*ajusteY;
                sPuntosY1 = sPuntosY1 + "," + y_Array[i];
            }


            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse("mailto:"));
            String to = "insqui2000@gmail.com";
            //String[] cc = copias;
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
            //emailIntent.putExtra(Intent.EXTRA_CC, cc);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Puntos Trayectoria");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "PUNTOS X: " + sPuntosX + "\n" + "maxX: " + maxX + "\n" + "minX: " + minX + "\n" + "new X: " + sPuntosX1 + "\n" + "new Y: " + sPuntosY1 + "\n" + "PUNTOS Y: " + sPuntosY + "\n" + "Maximo:" + maximo + "\n" +  "maxY: " + maxY + "\n" + "minY: " + minY);
            emailIntent.setType("message/rfc822");
            startActivity(Intent.createChooser(emailIntent, "Email "));

            /*Guardar punto XY en un archivo Interno*/
            String filename = "myfile";
            //String string = "Hola Mundo desde archivo interno!!";
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(sPuntosX1.getBytes());
                outputStream.write(",\n".getBytes());
                outputStream.write(sPuntosY1.getBytes());
                outputStream.write(",\n".getBytes());
                outputStream.close();
                Toast.makeText(getBaseContext(), "Trayectoria Guardada!" + puntosX.size(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
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
            }
        }


        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
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
                        float historicalY = event.getHistoricalY(i);
                        puntosY.add(historicalY);

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
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}

