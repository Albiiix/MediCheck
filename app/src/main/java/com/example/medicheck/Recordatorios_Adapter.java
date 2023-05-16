package com.example.medicheck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.List;

public class Recordatorios_Adapter extends ArrayAdapter<Recordatorios_Item> {

    private List<Recordatorios_Item> lista;
    private Context context;
    private int resourceLayout;

    public Recordatorios_Adapter(@NonNull Context context, int resource, List<Recordatorios_Item> objects) {
        super(context, resource, objects);
        this.lista = objects;
        this.context = context;
        this.resourceLayout = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null) {
            view = LayoutInflater.from(context).inflate(resourceLayout, null);
        }
        Recordatorios_Item recordatorios_item = lista.get(position);

        TextView titulo_lista_recordatorios = view.findViewById(R.id.titulo_lista_recordatorio);
        titulo_lista_recordatorios.setText(recordatorios_item.getTitulo());

        TextView tipo_lista_recordatorios = view.findViewById(R.id.tipo_lista_recordatorio);
        tipo_lista_recordatorios.setText(recordatorios_item.getTipo_recordatorio());

        TextView fecha_lista_recordatorios = view.findViewById(R.id.fecha_lista_recordatorio);
        fecha_lista_recordatorios.setText(recordatorios_item.getFecha_hora());


        return view;

    }
}
