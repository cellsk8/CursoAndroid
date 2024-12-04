package com.example.g.ruta2;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Cellsk8 on 23/07/2015.
 */
public class EnvioDatosBluetooth extends ActionBarActivity {

    Button btnDis;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    FileInputStream fin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(ConexionBluetooth.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the ledControl
        setContentView(R.layout.layout_evio_ruta);

        //call the widgtes
        btnDis = (Button) findViewById(R.id.button4);

        new ConnectBT().execute(); //Call the class to connect

        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Disconnect(); //close connection
            }
        });

        //BOTON RECTA
        Button recta = (Button) findViewById(R.id.buttonRecta);
        recta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    /*fin = openFileInput("myfile");
                    int c;
                    String temp = "";
                    while ((c = fin.read()) != -1) {
                        temp = temp + Character.toString((char) c);
                    }

                    //string temp contains all the data of the file.
                    fin.close();*/
                    //Enviar los datos por Bluetooth
                    if (btSocket != null) {
                        try {
                            btSocket.getOutputStream().write("R,7,\n".toString().getBytes());
                        } catch (IOException e) {
                            msg("Error al enviar recta por Bluetooth");
                        }
                    }

                    Toast.makeText(getBaseContext(), "Datos enviados! ", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //BOTON CIRCULO
        Button circulo = (Button) findViewById(R.id.buttonCirculo);
        circulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    /*fin = openFileInput("myfile");
                    int c;
                    String temp = "";
                    while ((c = fin.read()) != -1) {
                        temp = temp + Character.toString((char) c);
                    }

                    //string temp contains all the data of the file.
                    fin.close();*/
                    //Enviar los datos por Bluetooth
                    if (btSocket != null) {
                        try {
                            btSocket.getOutputStream().write("C,12,\n".toString().getBytes());
                        } catch (IOException e) {
                            msg("Error al enviar recta por Bluetooth");
                        }
                    }

                    Toast.makeText(getBaseContext(), "Datos enviados! ", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        Button mostarDatos = (Button) findViewById(R.id.mostrarDatos);
        mostarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    fin = openFileInput("myfile");
                    int c;
                    String temp = "";
                    while ((c = fin.read()) != -1) {
                        temp = temp + Character.toString((char) c);
                    }

                    //string temp contains all the data of the file.
                    fin.close();
                    //Enviar los datos por Bluetooth
                    if (btSocket != null) {
                        try {
                            btSocket.getOutputStream().write(temp.getBytes());
                        } catch (IOException e) {
                            msg("Error al enviar datos por Bluetooth");
                        }
                    }

                    Toast.makeText(getBaseContext(), "Datos enviados: "+ temp, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void Disconnect() {
        if (btSocket != null) //If the btSocket is busy
        {
            try {
                btSocket.getOutputStream().write("D,1,\n".toString().getBytes());
                btSocket.close(); //close connection
            } catch (IOException e) {
                msg("Error");
            }
        }
        finish(); //return to the first layout

    }


    // fast way to call Toast
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(EnvioDatosBluetooth.this, "Conectando...", "Epere porfavor!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Conexión Fallida. Intenta de nuevo.");
                finish();
            } else {
                msg("Conectado");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
