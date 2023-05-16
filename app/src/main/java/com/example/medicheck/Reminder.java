package com.example.medicheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Reminder extends AppCompatActivity {

    String[] items = {"Cita médica", "Inicio tratamiento", "Fin tratamiento", "Caducidad medicamento", "Tomar medicamento"};

    AutoCompleteTextView tipo_reminder;
    ArrayAdapter<String> adapter;
    Button guardar_reminder;
    String selectedDay, selectedMonth, selectedYear;
    String selectedHour, selectedMinute;
    ImageView back_reminder;

    int day, month, year;
    int hour, minute;

    TextInputEditText titulo_reminder, fecha_reminder, hora_reminder;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    String userID;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        Intent intent = getIntent();
        selectedDay = intent.getStringExtra("day");
        selectedMonth = intent.getStringExtra("month");
        selectedYear = intent.getStringExtra("year");

        titulo_reminder = findViewById(R.id.titulo_reminder);
        fecha_reminder = findViewById(R.id.fecha_reminder);
        fecha_reminder.setText(selectedDay + "/" + selectedMonth + "/" + selectedYear);
        fecha_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.MONTH);
                year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Reminder.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                fecha_reminder.setText(i2 + "/" + (i1+1) + "/" + i);
                                selectedDay = String.valueOf(i2);
                                selectedMonth = String.valueOf(i1);
                                selectedYear = String.valueOf(i);
                            }
                        }
                        , day, month, year);
                datePickerDialog.show();
            }
        });

        hora_reminder = findViewById(R.id.hora_reminder);
        hora_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(Reminder.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                if(i1 == 0 || i1 == 1 || i1 == 2 || i1 == 3 || i1 == 4 || i1 == 5
                                || i1 == 6 || i1 == 7 || i1 == 8 || i1 == 9){
                                    hora_reminder.setText(i + ":0" + i1);
                                }else {
                                    hora_reminder.setText(i + ":" + i1);
                                }
                                selectedHour = String.valueOf(i);
                                selectedMinute = String.valueOf(i1);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        tipo_reminder = findViewById(R.id.tipo_reminder);
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, items);
        tipo_reminder.setAdapter(adapter);
        tipo_reminder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        guardar_reminder = findViewById(R.id.guardar_reminder);
        guardar_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validar()) {
                    userID = firebaseAuth.getCurrentUser().getUid();
                    String guardarComo = selectedYear + selectedMonth + selectedDay + "-" + titulo_reminder.getText().toString().trim();
                    documentReference = firebaseFirestore.collection("Users").document(userID).collection("reminders").document(guardarComo);
                    Map<String, Object> reminder = new HashMap<>();
                    reminder.put("titulo", titulo_reminder.getText().toString().trim());
                    reminder.put("fecha", fecha_reminder.getText().toString());
                    reminder.put("tipo", tipo_reminder.getText().toString());
                    reminder.put("hora", hora_reminder.getText().toString());
                    documentReference.set(reminder).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Reminder.this, "Guardado", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Reminder.this, "No guardado", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    Toast.makeText(Reminder.this, "Error al guardar",Toast.LENGTH_LONG).show();
                }
            }
        });

        back_reminder = findViewById(R.id.back_reminder);
        back_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(Reminder.this, CalendarioFragment.class);
                //startActivity(intent);
                finish();
            }
        });
    }

    private boolean validar() {
        String tituloR = titulo_reminder.getText().toString().trim();
        String fechaR = fecha_reminder.getText().toString().trim();
        String horaR = hora_reminder.getText().toString().trim();

        if(tituloR.isEmpty()){
            titulo_reminder.setError("Necesita titulo");
            return false;
        } else{
            titulo_reminder.setError(null);
        }

        if(fechaR.isEmpty()){
            fecha_reminder.setError("Fecha necesaria");
            return false;
        }else{
            fecha_reminder.setError(null);
        }

        if(horaR.isEmpty()){
            hora_reminder.setText("9:00"); //hora por defecto
        }
        return true;
    }
}