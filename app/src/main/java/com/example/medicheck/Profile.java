package com.example.medicheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    DatePickerDialog datePickerDialog;
    TextInputEditText profile_nombre, profile_email;
    Button profile_nacimiento, guardar_cambios_profile;
    TextView profile_name;

    String userID;
    DocumentReference documentReference;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_email = findViewById(R.id.profile_email);
        profile_nombre = findViewById(R.id.profile_nombre);
        profile_nacimiento = findViewById(R.id.profile_nacimiento);
        profile_name = findViewById(R.id.profile_name);
        guardar_cambios_profile = findViewById(R.id.guardar_cambios_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userID = firebaseAuth.getCurrentUser().getUid();
        documentReference = firebaseFirestore.collection("Users").document(userID);


        drawerLayout= findViewById(R.id.drawer_layout);
        navigationView= findViewById(R.id.nav_view_2);

        toggle= new ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        if(savedInstanceState == null){
            navigationView.setCheckedItem(R.id.menu_profile);
        }

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);


        firebaseFirestore.collection("Users").document(userID).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if(documentSnapshot.exists()){
                                        profile_name.setText(documentSnapshot.get("name").toString());
                                        profile_email.setText(documentSnapshot.get("email").toString());
                                        profile_nombre.setText(documentSnapshot.get("name").toString());
                                        if(!documentSnapshot.get("fecha_nacimiento").toString().equals("Unknown")){
                                            profile_nacimiento.setText(documentSnapshot.get("fecha_nacimiento").toString());
                                        }
                                    } else{
                                        System.out.println("No such document");
                                    }
                                }else{
                                    System.out.println("Failed");
                                }
                            }
                        });

        initDatePicker();
        profile_nacimiento.setText(getTodayDate());
        profile_nacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(view);
            }
        });

        guardar_cambios_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                documentReference = firebaseFirestore.collection("Users").document(userID);
                Map<String, Object> user = new HashMap<>();
                user.put("name", profile_nombre.getText().toString());
                user.put("email", profile_email.getText().toString());
                user.put("fecha_nacimiento", profile_nacimiento.getText().toString());
                documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Profile.this, "Cambios guardados", Toast.LENGTH_LONG).show();
                            recreate();
                        } else{
                            Toast.makeText(Profile.this, "Cambios no guardados", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month += 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_logout:
                logout();
                break;
            case R.id.menu_settings:
                Intent intent = new Intent(Profile.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.main_menu:
                intent = new Intent(Profile.this, MainMenu.class);
                startActivity(intent);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1 += 1;
                String date = makeDateString(i2, i1, i);
                profile_nacimiento.setText(date);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if(month == 1) return "ENE";
        if(month == 2) return "FEB";
        if(month == 3) return "MAR";
        if(month == 4) return "ABR";
        if(month == 5) return "MAY";
        if(month == 6) return "JUN";
        if(month == 7) return "JUL";
        if(month == 8) return "AGO";
        if(month == 9) return "SEP";
        if(month == 10) return "OCT";
        if(month == 11) return "NOV";
        else return "DIC";
    }

    private void openDatePicker(View view){

        datePickerDialog.show();
    }

    public void logout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        builder.setTitle("Cerrar sesión");
        builder.setMessage("¿Seguro que quieres cerrar sesión?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", "");
                editor.putString("password", "");
                editor.apply();
                firebaseAuth.signOut();
                Toast.makeText(Profile.this, "Sesión cerrada", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Profile.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();

    }
}