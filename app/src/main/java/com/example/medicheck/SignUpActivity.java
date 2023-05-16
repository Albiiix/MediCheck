package com.example.medicheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    TextView nuevoUsuario2;
    TextInputEditText nombre, email, passw, confirmPassw;
    MaterialButton registrar;
    String userID;
    DocumentReference documentReference;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nuevoUsuario2 = findViewById(R.id.nuevoUsuario2);
        nombre= findViewById(R.id.nombre);
        email= findViewById(R.id.email);
        passw= findViewById(R.id.passw);
        confirmPassw= findViewById(R.id.confirmPassw);
        registrar= findViewById(R.id.registrar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        nuevoUsuario2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }

    @Override
    public void onBackPressed() {
        back();
    }

    public void back(){
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void validate(){
        String correo = email.getText().toString().trim();
        String contras = passw.getText().toString().trim();
        String confirm = confirmPassw.getText().toString().trim();

        if(correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()){

            email.setError("Correo no válido");
            return;
        } else{
            email.setError(null);
        }

        if(contras.isEmpty()){
            passw.setError("Escriba una contraseña");
            return;
        } else if(contras.length() < 8){
            passw.setError("Al menos ocho caracteres");
            return;
        }else{
            passw.setError(null);
        }

        if(!confirm.equals(contras)){
            confirmPassw.setError("Contraseñas diferentes");
            return;
        } else{
            confirmPassw.setError(null);
        }

        registrar(correo, contras);
    }

    public void registrar(String correo, String contras){

        firebaseAuth.createUserWithEmailAndPassword(correo, contras)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    userID = firebaseAuth.getCurrentUser().getUid();
                    documentReference = firebaseFirestore.collection("Users").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    String getNombre = nombre.getText().toString().trim();
                    user.put("name", getNombre);
                    user.put("email", correo);
                    user.put("fecha_nacimiento", "Unknown");
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(SignUpActivity.this, "Usuario registrado", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this, "Usuario no registrado", Toast.LENGTH_LONG).show();
                            firebaseAuth.getCurrentUser().delete();
                        }
                    });
                } else{
                    Toast.makeText(SignUpActivity.this, "Fallo al registrarse", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}