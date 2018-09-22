/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mvc;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author mhyst
 */
public abstract class Model {
    private int id;
    private String[] fields;
    private ArrayList<Vista> vistas;
    private final HashMap<String, Integer> vistaID;
    
    public Model(String[] fields) {
        this.fields = fields;
        vistas = new ArrayList<>();
        vistaID = new HashMap<>();
    }
    
    public String[] getFields() {
        return fields;
    }
    
    private int getFieldId(String field) {
        int i;
        for (i = 0; i < fields.length && !fields[i].equalsIgnoreCase(field); i++);
        if (i == fields.length)
            return -1;
        return i;
    }
    
    public Object get(String field) {
        int id = getFieldId(field);
        if (id == -1) {
            return null;
        }
        Object[] values = getRow();
        return values[id];
    }
    
    public Object get(int id) {
        return getRow()[id];
//        if (id > 0 && id < values.length)
//            return values[id];
//        return null;
    }
    
    public void set(int id, Object value) {
        Object[] values = getRow();
        if (id >= 0 && id < values.length)
            values[id] = value;
        setRow(values);
        notifyUpdate();
    }
    
    public void set(String field, Object value) {
        Object[] values = getRow();
        int id = getFieldId(field);
        if (id > 0 && id < values.length)
            values[id] = value;
        setRow(values);
        notifyUpdate();
    }
    
    public abstract Object[] getRow();
    public abstract void setRow(Object[] row);
    
    public void notifyUpdate() {
        for (Vista v : vistas) {
            v.receiveUpdate(this);
        }
    }
    
    public void addVista(Vista v, int id) {
        vistas.add(v);
        vistaID.put(v.getNombre(), id);
    }
    
    public int getVistaId(Vista v) {
        return vistaID.get(v);
    }
    
    public void removeVista(Vista v) {
        vistas.remove(v);
        vistaID.remove(v.getNombre());
    }
}
