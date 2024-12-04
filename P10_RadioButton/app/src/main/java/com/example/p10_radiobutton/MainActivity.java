package com.example.p10_radiobutton;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private EditText et_Num1, et_Num2;
    private TextView tv_resultado;
    private RadioButton rb_Suma, rb_Resta;


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
        et_Num1 = findViewById(R.id.et_Num1);
        et_Num2 = findViewById(R.id.et_Num2);
        tv_resultado = (TextView)findViewById(R.id.tv_resultado);
        rb_Suma = (RadioButton)findViewById(R.id.rb_sumar);
        rb_Resta = (RadioButton)findViewById(R.id.rb_restar);

    }

    //Método para el botón calcular
    public void Calcular(View view){
        String valor1_String = et_Num1.getText().toString();
        String valor2_Sgtring = et_Num2.getText().toString();

        int valor1_int = Integer.parseInt(valor1_String);
        int valor2_int = Integer.parseInt(valor2_Sgtring);

        if(rb_Suma.isChecked() == true){
            int suma = valor1_int + valor2_int;
            String resultado = String.valueOf(suma);
            tv_resultado.setText(resultado);
        } else if(rb_Resta.isChecked() == true){
            int resta = valor1_int - valor2_int;
            String resultado = String.valueOf(resta);
            tv_resultado.setText(resultado);
        }
    }

}