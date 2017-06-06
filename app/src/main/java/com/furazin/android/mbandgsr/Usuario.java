package com.furazin.android.mbandgsr;

import java.util.ArrayList;

/**
 * Created by manza on 05/06/2017.
 */

public class Usuario {

    ArrayList<String> experiencias;
    String contraseña;
    String email;

    public Usuario() {

    }

    public Usuario(ArrayList<String> experiencias, String contraseña, String email) {
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

    public ArrayList<String> getExperiencias() {
        return experiencias;
    }
}
