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

    public Experiencia(String nombre, String apellidos, String fecha_nacimiento, String sexo, String opcion_multimedia, String descripcion, String terminada) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fecha_nacimiento = fecha_nacimiento;
        this.sexo = sexo;
        this.opcion_multimedia = opcion_multimedia;
        this.descripcion = descripcion;
        this.terminada = terminada;
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

    public String getTerminada() {
        return terminada;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public void setOpcion_multimedia(String opcion_multimedia) {
        this.opcion_multimedia = opcion_multimedia;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setTerminada(String terminada) {
        this.terminada = terminada;
    }

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
