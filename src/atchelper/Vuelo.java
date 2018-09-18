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
public class Vuelo {
    private int id;
    private String callsign;
    private String tipo;
    private String SID;
    private String STAR;
    private String climb;
    private String squawck;
    private int fase;
    private boolean completo;
    private String fpl;
    private String origen;
    private String destino;
    private String fecha;

    public Vuelo(int id, String callsign, String tipo, String SID, String STAR, String climb, String squawck, int fase, boolean completo, String fpl, String origen, String destino, String fecha) {
        this.id = id;
        this.callsign = callsign;
        this.tipo = tipo;
        this.SID = SID;
        this.STAR = STAR;
        this.climb = climb;
        this.squawck = squawck;
        this.fase = fase;
        this.completo = completo;
        this.fpl = fpl;
        this.origen = origen;
        this.destino = destino;
        this.fecha = fecha;
        
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public String getSID() {
        return SID;
    }

    public void setSID(String SID) {
        this.SID = SID;
    }

    public String getSTAR() {
        return STAR;
    }

    public void setSTAR(String STAR) {
        this.STAR = STAR;
    }

    public String getClimb() {
        return climb;
    }

    public void setClimb(String climb) {
        this.climb = climb;
    }

    public String getSquawck() {
        return squawck;
    }

    public void setSquawck(String squawck) {
        this.squawck = squawck;
    }

    public int getFase() {
        return fase;
    }

    public void setFase(int fase) {
        this.fase = fase;
    }

    public boolean isCompleto() {
        return completo;
    }

    public void setCompleto(boolean completo) {
        this.completo = completo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFpl() {
        return fpl;
    }

    public void setFpl(String fpl) {
        this.fpl = fpl;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    
    
    
}
