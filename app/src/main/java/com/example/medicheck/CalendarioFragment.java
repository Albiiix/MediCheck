package com.example.medicheck;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class CalendarioFragment extends Fragment{


    View view;

    CalendarView calendarView;
    String selectedDay, selectedMonth, selectedYear;
    ListView listView_calendar;
    ArrayAdapter<String> adapter;

    Button añadir_recordatorio;

    String userID;
    CollectionReference collectionReference;
    com.google.firebase.firestore.Query query;
    List<String> items;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_calendario, container, false);

        Calendar c = Calendar.getInstance();
        selectedDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        selectedMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
        selectedYear = String.valueOf(c.get(Calendar.YEAR));

        listView_calendar = view.findViewById(R.id.listview_calendar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        getReminders();

        añadir_recordatorio = view.findViewById(R.id.añadir_recordatorio);
        añadir_recordatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Reminder.class);
                intent.putExtra("day", selectedDay);
                intent.putExtra("month", selectedMonth);
                intent.putExtra("year", selectedYear);
                startActivity(intent);
            }
        });

        calendarView = view.findViewById(R.id.calendar_view);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                c.set(i,i1,i2);
                calendarView.setDate(c.getTimeInMillis());
                selectedDay = String.valueOf(i2);
                selectedMonth = String.valueOf(i1+1);
                selectedYear = String.valueOf(i);
                getReminders();
            }
        });

        listView_calendar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Intent intent = new Intent(view.getContext(), Recordatorio_detail.class);
                startActivity(intent);
            }
        });

        return view; // Devuelve la vista inflada en "view"
    }


    public void getReminders(){
        String currentDate = selectedDay + "/" + selectedMonth + "/" + selectedYear;
        System.out.println("CurrentDate: "+ currentDate);

        items = new ArrayList<>();

        collectionReference = firebaseFirestore.collection("Users").document(userID).collection("reminders");
        query = collectionReference.whereEqualTo("fecha", currentDate);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> lista = task.getResult().getDocuments();
                    if(!lista.isEmpty()) {
                        int i;
                        for (i = 0; i < lista.size(); i++) {
                            items.add(lista.get(i).get("titulo").toString());
                        }
                    }
                    System.out.println("Items: " + items);
                    adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_calendar, items);
                    listView_calendar.setAdapter(adapter);
                }
            }
        });
    }


}