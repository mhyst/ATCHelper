/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mvc;

import ivao.IvaoServer;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author mhyst
 */
public class Vista {
    private String nombre;
    private final String[] fields;
    private final int[] modelReciprocity;
    private final ModelHandler mh;
    private ArrayList<Model> models;
    private JTable tbl;
    private DefaultTableModel tblModel;
    
    private String currentFilters;
    
    public Vista(String nombre, String[] fields, int[] reciprocity, ModelHandler mh, JTable tbl) {
        this.nombre = nombre;
        this.fields = fields;
        this.modelReciprocity = reciprocity;
        this.mh = mh;
        models = new ArrayList<>();
        this.tbl = tbl;
        currentFilters = "";
        this.tblModel = (DefaultTableModel) tbl.getModel();
    }
    
    public void setFilters(String filters) {
        currentFilters = filters;
    }

    public String getNombre() {
        return nombre;
    }
    
    //TODO: Revisar funcionalidad
    private boolean isSelected(Model m) {
        Object[] row = m.getRow();
        
        if (currentFilters == null || currentFilters.trim().length() == 0)
            return true;
        
        String[] filters = currentFilters.split(",");
        boolean pass = false;
        for (String filter :  filters) {
            if (filter.trim().length() == 0) continue;
            String[] fdata = filter.split("=");
            int id = Integer.parseInt(fdata[0]);
            String value = fdata[1];
            if (id < 0) {
                pass = pass || IvaoServer.isInDistance((IvaoServer.FlightData) m, Double.parseDouble(value));
            } else {
                if (row[id].toString().equalsIgnoreCase(value)) {
                    pass = pass || true;
                }
            }
        }
        return pass;
    }
    
    private void clearTable() {
        //tbl.removeAll();
        while(tbl.getRowCount() > 0)
            tblModel.removeRow(0);
    }
    
    public void loadFromModelHandler() {
        Set<String> keys = mh.getKeys();
        
        clear();
        for(String key : keys) {
            Model m = mh.get(key);
            if (isSelected(m))
                add(m);
        }
    }
    
    public void clear() {
        clearTable();
        models.clear();
    }
    
    public void add(Model m) {
        models.add(m);
        tblModel.addRow(m.getRow());
        int id = tblModel.getRowCount()-1;
        m.addVista(this, id);
    }
    
    public void remove(int id) {
        Model m = models.remove(id);
        m.removeVista(this);
        tblModel.removeRow(id);
        tblModel.fireTableRowsDeleted(id, id);
    }
    
    public void remove(Model m) {
        int id = models.indexOf(m);
        models.remove(m);
        m.removeVista(this);
        
        tblModel.removeRow(id);
        tblModel.fireTableRowsDeleted(id, id);
    }
    
    public String[] getFields() {
        return fields;
    }
    
    public ArrayList<Model> getModels() {
        return models;
    }
    
    private int getFieldId(String field) {
        int i;
        for (i = 0; i < fields.length && !fields[i].equalsIgnoreCase(field); i++);
        if (i == fields.length)
            return -1;
        return i;
    }
    
    public int getReciprocity(int id) {
        return modelReciprocity[id];
    }
    
    public Object getValue(String field, int id) {
        int fid = getFieldId(field);
        int mid = getReciprocity(fid);
        
        return models.get(id).get(mid);
    }
    
    public void setValue(String field, int id, Object value) {
        int fid = getFieldId(field);
        int mid = getReciprocity(fid);
        
        models.get(id).set(mid, value);
    }
    
    public Object getValue(int vid, int rowid) {
        int mid = getReciprocity(vid);
        
        return models.get(rowid).get(mid);
    }
    
    public Object[] getRow(int rowid) {
        Object[] row =  models.get(rowid).getRow();
        Object[] vrow = new Object[fields.length];
        for (int i = 0; i < row.length; i++)
            vrow[i] = row[modelReciprocity[i]];
        
        return vrow;
    }
    
    public void setRow(int id, Object[] row) {
        Model m = models.get(id);
        Object[] orow = m.getRow();
        for (int i = 0; i < fields.length; i++) {
            int mid = modelReciprocity[i];
            orow[mid] = row[i];
        }
        m.setRow(orow);
    }
    
    public void notifyAdd(Model m) {
        if (!models.contains(m) && isSelected(m))
            add(m);
    }
    
    public void notifyRemove(Model m) {
        if (models.contains(m)) {
            remove(m);
        }
    }
    
    public void shouldBeRemoved(Model m) {
        if (models.contains(m) && !isSelected(m)) {
            remove(m);
        }
    }
    
    public void receiveUpdate(Model m) {
        boolean modified = false;
        int id = models.indexOf(m);
        //System.out.println("Callsign: "+((ivao.IvaoServer.FlightData) m).getCallsign()+":"+id);
        
        if (id == -1) {
            notifyAdd(m);
            return;
        }
        Object[] row = m.getRow();
        for (int i = 0; i < modelReciprocity.length; i++) {
            Object o = tblModel.getValueAt(id, i);
            if (row[modelReciprocity[i]] != o) {
                tblModel.setValueAt(row[modelReciprocity[i]], id, i);
                //tblModel.fireTableCellUpdated(id, i);
                modified = true;
            }
        }
        
        if (modified)  {
            shouldBeRemoved(m);
        
            java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                tblModel.fireTableDataChanged();
            }});
        }
                        
    }
}
