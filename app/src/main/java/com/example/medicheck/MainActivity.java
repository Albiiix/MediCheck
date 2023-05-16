package com.example.medicheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    LottieAnimationView imagen;
    TextView titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagen = findViewById(R.id.imageView);
        titulo = findViewById(R.id.textView);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        Thread thread = new Thread(){

          public void run(){
              try {
                  sleep(4000);
              }catch(Exception e){
                  e.printStackTrace();
              }finally {
                  FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                  SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                  String email = sharedPreferences.getString("email", "");
                  String password = sharedPreferences.getString("password", "");
                  if(!email.equals("") && !password.equals("")){
                      firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                          @Override
                          public void onComplete(@NonNull Task<AuthResult> task) {
                              if(task.isSuccessful()){
                                  Intent intent = new Intent(MainActivity.this, MainMenu.class);
                                  startActivity(intent);
                              } else{
                                  Intent intent= new Intent(MainActivity.this, LoginActivity.class);
                                  startActivity(intent);
                              }
                          }
                      });

                  } else{
                      Intent intent= new Intent(MainActivity.this, LoginActivity.class);
                      startActivity(intent);
                  }
                  finish();
              }
          }
        }; thread.start();
    }

}