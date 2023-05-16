package com.example.medicheck;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class RecordatoriosFragment extends Fragment {

    View view;

    ListView listView_lunes, listView_martes, listView_miercoles, listView_jueves, listView_viernes, listView_sabado, listView_domingo, listView_lunes2;
    String day, month, year, currentDate;
    int day_name;
    TextView lunes, martes, miercoles, jueves, viernes, sabado, domingo, lunes2;

    String userID;
    CollectionReference collectionReference;
    com.google.firebase.firestore.Query query;
    ListAdapter adapter;

    TextView titulo_lista_recordatorio, tipo_lista_recordatorio, fecha_lista_recordatorio;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_recordatorios, container, false);


        lunes = view.findViewById(R.id.lunes);
        martes = view.findViewById(R.id.martes);
        miercoles = view.findViewById(R.id.miercoles);
        jueves = view.findViewById(R.id.jueves);
        viernes = view.findViewById(R.id.viernes);
        sabado = view.findViewById(R.id.sabado);
        domingo = view.findViewById(R.id.domingo);
        lunes2 = view.findViewById(R.id.lunes2);

        listView_lunes = view.findViewById(R.id.listview_lunes);
        listView_martes = view.findViewById(R.id.listview_martes);
        listView_miercoles = view.findViewById(R.id.listview_miercoles);
        listView_jueves = view.findViewById(R.id.listview_jueves);
        listView_viernes = view.findViewById(R.id.listview_viernes);
        listView_sabado = view.findViewById(R.id.listview_sabado);
        listView_domingo = view.findViewById(R.id.listview_domingo);
        listView_lunes2 = view.findViewById(R.id.listview_lunes2);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        Calendar c = Calendar.getInstance();
        day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        month = String.valueOf(c.get(Calendar.MONTH) + 1);
        year = String.valueOf(c.get(Calendar.YEAR));
        day_name = c.get(Calendar.DAY_OF_WEEK);
        System.out.println(day_name);

        int comienzo = c.get(Calendar.DAY_OF_MONTH);

        switch (day_name){
            case Calendar.MONDAY:
                comienzo = c.get(Calendar.DAY_OF_MONTH);
                break;
            case Calendar.TUESDAY:
                comienzo = c.get(Calendar.DAY_OF_MONTH) - 1;
                break;
            case Calendar.WEDNESDAY:
                comienzo = c.get(Calendar.DAY_OF_MONTH) - 2;
                break;
            case Calendar.THURSDAY:
                comienzo = c.get(Calendar.DAY_OF_MONTH) - 3;
                break;
            case Calendar.FRIDAY:
                comienzo = c.get(Calendar.DAY_OF_MONTH) - 4;
                break;
            case Calendar.SATURDAY:
                comienzo = c.get(Calendar.DAY_OF_MONTH) - 5;
                break;
            case Calendar.SUNDAY:
                comienzo = c.get(Calendar.DAY_OF_MONTH) - 6;
                break;
            default:
                break;
        }

        lunes.setText("Lunes " + comienzo );
        getDayReminders(comienzo + "/" + month + "/" + year, listView_lunes);

        martes.setText("Martes " + (comienzo + 1) );
        getDayReminders( (comienzo + 1) + "/" + month + "/" + year, listView_martes);

        miercoles.setText("Miércoles " + (comienzo + 2)  );
        getDayReminders( (comienzo + 2)  + "/" + month + "/" + year, listView_miercoles);

        jueves.setText("Jueves " + (comienzo + 3)  );
        getDayReminders( (comienzo + 1)  + "/" + month + "/" + year, listView_jueves);

        viernes.setText("Viernes " + (comienzo + 4) );
        getDayReminders( (comienzo + 4)  + "/" + month + "/" + year, listView_viernes);

        sabado.setText("Sábado " + (comienzo + 5) );
        getDayReminders( (comienzo + 5)  + "/" + month + "/" + year, listView_sabado);

        domingo.setText("Domingo " + (comienzo + 6) );
        getDayReminders( (comienzo + 6)  + "/" + month + "/" + year, listView_domingo);

        lunes2.setText("Lunes " + (comienzo + 7) );
        getDayReminders( (comienzo + 7)  + "/" + month + "/" + year, listView_lunes2);



        return view;
    }

    public void getDayReminders(String currentDate, ListView listView_recordatorios){
        List<Recordatorios_Item> items = new ArrayList<>();

        collectionReference = firebaseFirestore.collection("Users").document(userID).collection("reminders");
        query = collectionReference.whereEqualTo("fecha", currentDate);
        System.out.println("Fecha actual: " + currentDate);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> lista = task.getResult().getDocuments();
                    System.out.println("Lista obtenida: " + lista);
                    if(!lista.isEmpty()) {
                        int i;
                        for (i = 0; i < lista.size(); i++) {
                            String fecha_hora = lista.get(i).get("fecha").toString() + "-" + lista.get(i).get("hora").toString();
                            Recordatorios_Item añadir = new Recordatorios_Item(lista.get(i).get("titulo").toString(),
                                    lista.get(i).get("tipo").toString(), fecha_hora);
                            System.out.println(añadir.getTitulo());
                            System.out.println(añadir.getTipo_recordatorio());
                            System.out.println(añadir.getFecha_hora());
                            items.add(añadir);
                            }
                    }
                    adapter = new Recordatorios_Adapter(view.getContext(), R.layout.list_item_recordatorios, items);
                    listView_recordatorios.setAdapter(adapter);
                    setListViewHeightBasedOnChildren(listView_recordatorios);
                }
            }
        });
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}

