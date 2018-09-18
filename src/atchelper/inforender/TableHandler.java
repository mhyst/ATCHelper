/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atchelper.inforender;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author mhyst
 */
public class TableHandler {
    JTable table;
    DefaultTableModel model;
    String[] fields;
    
    public TableHandler(JTable table, String[] fields) {
        this.table = table;
        this.fields = fields;
        model = (DefaultTableModel) table.getModel();
    }
    
    public void clear() {
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
    }
    
    public int add(Object[] row) {
        model.addRow(row);
        return model.getRowCount()-1;
    }
    
    public void remove(int id) {
        model.removeRow(id);
    }
    
    public Object get(int r, int c) {
        return model.getValueAt(r, c);
    }
    
    public void set(int r, int c, Object value) {
        model.setValueAt(c, r, c);
    }
    
    public int getFieldId(String field) {
        int i;
        for(i = 0; i < fields.length && !fields[i].equalsIgnoreCase(field); i++);
        if (i < fields.length)
            return i;
        else
            return -1;
    }
    
    public Object get(int r, String field) {
        int id = getFieldId(field);
        if (id == -1)
            return null;
        return get(r, id);
    }
    
    public void set(int r, String field, Object value) {
        int id = getFieldId(field);
        if (-1 != id) {
            set(r, id, value);
        } else {
            return;
        }
    }
}
