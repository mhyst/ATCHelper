/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atchelper;

/**
 *
 * @author mhyst
 */
public class Fase {
    private int id;
    private String nombre;
    private int orden;
    private String tipo;
    private String DA;

    public Fase(int id, String nombre, int orden, String tipo, String DA) {
        this.id = id;
        this.nombre = nombre;
        this.orden = orden;
        this.tipo = tipo;
        this.DA = DA;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDA() {
        return DA;
    }

    public void setDA(String DA) {
        this.DA = DA;
    }

    public String toString() {
        return nombre;
    }
    
}
