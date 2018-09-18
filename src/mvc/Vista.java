/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mvc;

import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author mhyst
 */
public abstract class Vista {
    private final String[] fields;
    private final int[] modelReciprocity;
    private final ModelHandler mh;
    private ArrayList<Model> models;
    
    public Vista(String[] fields, int[] reciprocity, ModelHandler mh) {
        this.fields = fields;
        this.modelReciprocity = reciprocity;
        this.mh = mh;
        models = new ArrayList<>();
    }
    
    public void loadFromModelHandler() {
        Set<String> keys = mh.getKeys();
        
        clear();
        for(String key : keys) {
            add(mh.get(key));
        }
    }
    
    private boolean isSelected(Model m, String sFilters) {
        Object[] row = m.getRow();
        
        String[] filters = sFilters.split(",");
        boolean pass = false;
        for (String filter :  filters) {
            String[] fdata = filter.split("=");
            int id = Integer.parseInt(fdata[0]);
            String value = fdata[1];
            
            if (row[id] == value) {
                pass = pass || true;
            }
        }
        return pass;
    }
    
    public void loadFromModelHandler(String filters) {
        Set<String> keys = mh.getKeys();
        
        clear();
        for(String key : keys) {
            Model m = mh.get(key);
            if (isSelected(m, filters))
                add(m);
        }
    }
    
    public void clear() {
        models.clear();
    }
    
    public void add(Model m) {
        models.add(m);
    }
    
    public void remove(int id) {
        models.remove(id);
    }
    
    public void remove(Model m) {
        models.remove(m);
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
    
    //public Object set(String field, Object value);
    
//    public void setRow(Object[] row);
//    public void updateModel(Model m);
//    public void refreshView(Model m);
//    public void filter(String field, Object value);
}
