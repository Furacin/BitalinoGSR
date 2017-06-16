package com.furazin.android.mbandgsr.FirebaseBD;

/**
 * Created by manza on 06/06/2017.
 */

public class Experiencia {
    String nombre;
    String apellidos;
    String fecha_nacimiento;
    String sexo;
    String opcion_multimedia;
    String descripcion;
    String terminada;

    public Experiencia() {
    }

    public Experiencia(String nombre, String apellidos, String fecha_nacimiento, String sexo, String opcion_multimedia, String descripcion) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fecha_nacimiento = fecha_nacimiento;
        this.sexo = sexo;
        this.opcion_multimedia = opcion_multimedia;
        this.descripcion = descripcion;
        this.terminada = "no";
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public String getOpcion_multimedia() {
        return opcion_multimedia;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
