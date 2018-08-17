package com.furazin.android.mbandgsr.FirebaseBD;

import java.util.ArrayList;

/**
 * Created by manza on 05/06/2017.
 */

public class Usuario {

    ArrayList<Experiencia> experiencias;
    String contraseña;
    String email;

    public Usuario() {

    }

    public Usuario(ArrayList<Experiencia> experiencias, String contraseña, String email) {
        this.experiencias = experiencias;
        this.contraseña = contraseña;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getContraseña() {
        return contraseña;
    }

//    public void setContraseña(String contraseña) {
//        this.contraseña = contraseña;
//    }

//    public void setEmail(String email) {
//        this.email = email;
//    }

    public ArrayList<Experiencia> getExperiencias() {
        return experiencias;
    }

//    public void setExperiencias(ArrayList<Experiencia> experiencias) {
//        this.experiencias = experiencias;
//    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
