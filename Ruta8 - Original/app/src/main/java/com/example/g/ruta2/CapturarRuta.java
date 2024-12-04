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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.g.ruta2.MobileAnarchy.JoystickMovedListener;
import com.example.g.ruta2.MobileAnarchy.JoystickView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by g on 04/08/2015.
 */
public class CapturarRuta extends Activity {
    private static final long RATE = 5;
    private static final double radio = 15;
    private static final double longitud = 38.0;
    LinearLayout mContent;
    signature mSignature;
    Button mClear, mGetSign, mCancel;
    public static String tempDir;
    public boolean inicio=true;
    public String current = null;
    public ArrayList puntosX = new ArrayList();
    public ArrayList puntosY = new ArrayList();
    public ArrayList puntoswd = new ArrayList();
    public ArrayList puntoswi = new ArrayList();


    int ancho = 0;
    int alto = 0;

    private Bitmap mBitmap;
    View mView;
    File mypath;
    public int last_wd=0,   last_wi=0;

    private String uniqueId;
    private EditText yourName;

    TextView txtX, txtY;
    JoystickView joystick;
    JoystickView joystick2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_ruta);

        txtX = (TextView)findViewById(R.id.TextViewX);
        txtY = (TextView)findViewById(R.id.TextViewY);
        joystick = (JoystickView)findViewById(R.id.joystickView);
        joystick2 = (JoystickView)findViewById(R.id.joystickView2);

        joystick.setOnJostickMovedListener(_listener);
        joystick2.setOnJostickMovedListener(_listener2);

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

    private JoystickMovedListener _listener = new JoystickMovedListener() {



        @Override
        public void OnMoved(int pan, int tilt) {

            mGetSign.setEnabled(true);

            if(tilt>0){//wi
                tilt=0;
            }
            else{
                tilt=-tilt;
            }

            txtY.setText("WI "+Integer.toString(tilt));
            last_wi=tilt;
        }


        @Override
        public void OnReleased() {
            txtX.setText("released");
            txtY.setText("released");
            last_wi=0;
        }

        public void OnReturnedToCenter() {
            txtX.setText("stoppedX");
            txtY.setText("stoppedY");
            last_wi=0;
        };
    };

    private JoystickMovedListener _listener2 = new JoystickMovedListener() {

        @Override
        public void OnMoved(int pan, int tilt) {

            mGetSign.setEnabled(true);

            if(tilt>0){//wi
                tilt=0;
            }
            else{
                tilt=-tilt;
            }

            txtX.setText("WD "+Integer.toString(tilt));
            last_wd=tilt;

        }


        @Override
        public void OnReleased() {
            txtX.setText("released");
            txtY.setText("released");
            last_wd=0;
        }

        public void OnReturnedToCenter() {
            txtX.setText("stoppedX");
            txtY.setText("stoppedY");
            last_wd=0;
        };
    };


    @Override
    protected void onDestroy() {
        Log.w("GetSignature", "onDestory");
        super.onDestroy();
    }

    private boolean captureSignature() {

        boolean error = false;
        String errorMessage = "";


        if (yourName.getText().toString().equalsIgnoreCase("")) {
            errorMessage = errorMessage + "Por Favor ingresa el \n nombre de la Trayectoria!! \n";
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
        private static final float STROKE_WIDTH = 10f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();
        public double x=300.0;
        public double y=300.0;
        public double phi=0.0;
        public double anterior_wd=0, anterior_wi=0;

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
            puntoswd.clear();
            puntoswi.clear();
        }

        public void trayectoria(float wd, float wi) {
            String mensaje="wd";
            mensaje= mensaje+ String.valueOf(wd);
            mensaje=mensaje+"-wi:"+wi;

            //wd=wd/6;
            //wi=wi/6;
            //wd=10;
            //wi=0;


            path.moveTo(Float.valueOf(String.valueOf(x)),Float.valueOf(String.valueOf(y)));
            //for (int i=0;i<100;i++){

                x = x + ((((wd + wi) * radio) / 2) * Math.cos(phi)) * 0.01;
                y = y + ((((wd + wi) * radio) / 2) * Math.sin(phi)) * 0.01;
                phi = phi + ((((wd - wi) * radio) / (2 * longitud))) * 0.01;

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
            String sPuntoswd = "";
            String sPuntoswi = "";

            String sMasPuntosX = "";
            String sMasPuntosY = "";
            String sPuntosY = "";

            double primero = 0, maximo = 0, segundo = 0;
            //this.generarMasPuntos();

            for (int i = 0; i < puntosX.size(); i++) {
                if (i == 0) {

                    sPuntosX = "" + puntosX.get(i);
                    sPuntosY = "" + puntosY.get(i);
                    primero = (double) puntosY.get(i);

                } else {
                    sPuntosX = sPuntosX + " " + puntosX.get(i);
                    sPuntosY = sPuntosY + " " + puntosY.get(i);
                    segundo = (double) puntosY.get(i);
                    if (segundo < primero) {
                        maximo = primero;
                    } else {
                        maximo = segundo;
                    }
                    primero = maximo;

                }

            }

            maximo=0;
            primero=0;
            segundo=0;

            for (int i = 0; i < puntoswd.size(); i++) {
                if (i == 0) {

                    sPuntoswd = "" + puntoswd.get(i);
                    sPuntoswi = "" + puntoswi.get(i);
                    //primero = (double) puntoswi.get(i);

                } else {
                    sPuntoswd = sPuntoswd + " " + puntoswd.get(i);
                    sPuntoswi = sPuntoswi + " " + puntoswi.get(i);
                    //segundo = (double) puntoswi.get(i);
                    if (segundo < primero) {
                        maximo = primero;
                    } else {
                        maximo = segundo;
                    }
                    primero = maximo;

                }

            }



            //Volteaar los puntos del eje Y
            for (int j = 0; j < puntosY.size(); j++) {
                if (j == 0) {
                    sPuntosY = "" + (-1*((double)puntosY.get(j))+maximo);

                } else {
                    sPuntosY = sPuntosY + " " + (-1*((double)puntosY.get(j))+maximo);

                }
            }


            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse("mailto:"));
            String to = "insqui2000@gmail.com";
            //String[] cc = copias;
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
            //emailIntent.putExtra(Intent.EXTRA_CC, cc);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Puntos Trayectoria");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "PUNTOS X: " + sPuntosX + "\n"  + "PUNTOS Y: " + sPuntosY + "\n" + "Maximo:" + maximo + "\n" + "\n" + "WD:" + sPuntoswd + "\n" + "WI:" + sPuntoswi + "\n"  );
            emailIntent.setType("message/rfc822");
            startActivity(Intent.createChooser(emailIntent, "Email "));

            try {
                FileOutputStream fos = openFileOutput("textFile.txt", MODE_APPEND);
                OutputStreamWriter osw = new OutputStreamWriter(fos);

                // Escribimos el String en el archivo
                osw.write(sPuntosX);
                osw.flush();
                osw.close();

                // Mostramos que se ha guardado
                 //Toast.makeText(getBaseContext(), "Guardado" + sPuntosX, Toast.LENGTH_SHORT).show();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


        @Override
        protected void onDraw(Canvas canvas) {

            ancho = canvas.getWidth();
            alto = canvas.getHeight();
            update();
            canvas.drawPath(path, paint);
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
                        //puntosX.add(historicalX);
                        float historicalY = event.getHistoricalY(i);
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
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}

