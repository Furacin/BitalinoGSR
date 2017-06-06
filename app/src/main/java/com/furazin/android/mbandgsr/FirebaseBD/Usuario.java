package com.furazin.android.mbandgsr.FirebaseBD;

/**
 * Created by manza on 05/06/2017.
 */

public class Usuario {

    String contraseña;
    String email;

    public Usuario() {

    }

    public Usuario(String contraseña, String email) {

        this.contraseña = contraseña;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getContraseña() {
        return contraseña;
    }
}
