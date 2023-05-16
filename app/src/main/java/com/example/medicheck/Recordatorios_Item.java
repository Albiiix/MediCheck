package com.example.medicheck;

public class Recordatorios_Item {

    String titulo, tipo_recordatorio, fecha_hora;

    public Recordatorios_Item(String titulo, String tipo_recordatorio, String fecha_hora){

        this.titulo = titulo;
        this.fecha_hora = fecha_hora;
        this.tipo_recordatorio = tipo_recordatorio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo_recordatorio() {
        return tipo_recordatorio;
    }

    public void setTipo_recordatorio(String tipo_recordatorio) {
        this.tipo_recordatorio = tipo_recordatorio;
    }

    public String getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(String fecha_hora) {
        this.fecha_hora = fecha_hora;
    }
}
