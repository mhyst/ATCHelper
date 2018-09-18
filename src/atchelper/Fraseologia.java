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
public class Fraseologia {
    private int id;
    private int fase;
    private String castellano;
    private String ingles;
    private boolean paso;

    public Fraseologia(int id, int fase, String castellano, String ingles, boolean paso) {
        this.id = id;
        this.fase = fase;
        this.castellano = castellano;
        this.ingles = ingles;
        this.paso = paso;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFase() {
        return fase;
    }

    public void setFase(int fase) {
        this.fase = fase;
    }

    public String getCastellano() {
        return castellano;
    }

    public void setCastellano(String castellano) {
        this.castellano = castellano;
    }

    public String getIngles() {
        return ingles;
    }

    public void setIngles(String ingles) {
        this.ingles = ingles;
    }

    public boolean isPaso() {
        return paso;
    }

    public void setPaso(boolean paso) {
        this.paso = paso;
    }
    
    
}
