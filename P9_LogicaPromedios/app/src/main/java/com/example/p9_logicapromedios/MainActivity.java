package com.example.p9_logicapromedios;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private EditText et_SO,et_PM,et_Filosofia;
    private TextView tv_Estatus;

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
        et_Filosofia=findViewById(R.id.txt_Filosofia);
        et_PM=findViewById(R.id.txt_PM);
        et_SO=findViewById(R.id.txt_SO);
        tv_Estatus=findViewById(R.id.tv_estatus);
    }
    public void estatus(View view){
        String SistemasOperativos = et_SO.getText().toString();
        String ProgramacionMovil = et_PM.getText().toString();
        String Filosofia = et_Filosofia.getText().toString();
        int SistemasO_int = Integer.parseInt(SistemasOperativos);
        int ProgramacionM_int = Integer.parseInt(ProgramacionMovil);
        int Filosofia_int = Integer.parseInt(Filosofia);
        int promedio = (SistemasO_int + ProgramacionM_int + Filosofia_int) / 3;
        if(promedio >= 6){
            tv_Estatus.setText("Estatus Aprobado con " + promedio);
        } else if(promedio <= 5){
            tv_Estatus.setText("Estatus Reprobado con " + promedio);
        }
    }
}

