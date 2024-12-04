package com.example.p_sensorhuella;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private Button btnAutentificar;
    private Executor executor;
    //private BiometricPrompt biometricPrompt;
    private androidx.biometric.BiometricPrompt biometricPrompt;
    private androidx.biometric.BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnAutentificar=findViewById(R.id.button);

        executor= ContextCompat.getMainExecutor(this);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            biometricPrompt= new androidx.biometric.BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(MainActivity.this,"Error de autenticación"+errString,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(MainActivity.this,"Autenticación correcta",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    Toast.makeText(MainActivity.this,"Autenticación fallida",Toast.LENGTH_SHORT).show();
                }
            });
        }

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Inicio de sesión biométrico")
                .setSubtitle("Coloca tu dedo para continuar")
                .setNegativeButtonText("Cancelar")
                .build();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void autentificar(View view){
        biometricPrompt.authenticate(promptInfo);
    }


}