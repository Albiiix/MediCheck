package com.example.medicheck;

public class User_Data {

    private String name;
    private String email;
    private String fecha_nacimiento;

    public User_Data(String name, String email, String fecha_nacimiento){
        this.email = email;
        this.name = name;
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
