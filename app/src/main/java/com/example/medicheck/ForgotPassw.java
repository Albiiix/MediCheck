package com.example.medicheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ForgotPassw extends AppCompatActivity {

    MaterialButton recuperar;
    TextInputEditText usuarioCorreoRecu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_passw);

        recuperar= findViewById(R.id.recuperar);
        usuarioCorreoRecu= findViewById(R.id.usuarioCorreoRecu);

        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent= new Intent(ForgotPassw.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void validate(){
        String correo= usuarioCorreoRecu.getText().toString().trim();

        if(correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            usuarioCorreoRecu.setError("Correo no válido");
            return;
        }

        mensaje(correo);
    }

    public void mensaje(String correo){

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String email = correo;

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ForgotPassw.this, "Correo enviado", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgotPassw.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ForgotPassw.this, "Correo inválido", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}