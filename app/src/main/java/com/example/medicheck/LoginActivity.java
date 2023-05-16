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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText usuarioCorreo, usuarioPassw;
    TextView olvido, nuevoUsuario;
    MaterialButton inicioSesion;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuarioCorreo= findViewById(R.id.usuarioCorreo);
        usuarioPassw= findViewById(R.id.usuarioPassw);
        olvido= findViewById(R.id.olvido);
        nuevoUsuario= findViewById(R.id.nuevoUsuario);
        inicioSesion= findViewById(R.id.inicioSesion);

        firebaseAuth = FirebaseAuth.getInstance();

        nuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        inicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        olvido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassw.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void signUp(){
        Intent intent= new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    public void validate() {
        String correo = usuarioCorreo.getText().toString().trim();
        String contras = usuarioPassw.getText().toString().trim();
        if(correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
            usuarioCorreo.setError("Correo no válido");
            return;
        } else{
            usuarioCorreo.setError(null);
        }

        if(contras.isEmpty()){
            usuarioPassw.setError("Escriba la contraseña");
            return;
        } else{
            usuarioPassw.setError(null);
        }

        login(correo, contras);
    }

    public void login(String correo, String contras){

        firebaseAuth.signInWithEmailAndPassword(correo, contras)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email", correo);
                            editor.putString("password", contras);
                            editor.apply();
                            Intent intent= new Intent(LoginActivity.this, MainMenu.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Email o contraseña incorrectos", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

}