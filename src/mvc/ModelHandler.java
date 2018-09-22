/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mvc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author mhyst
 */
public class ModelHandler {
    
    private final HashMap<String, Model> map;
    private Vista vista;
    
    public ModelHandler() {
        map = new HashMap<>();
    }
    
    public void setVista(Vista v) {
        vista = v;
    }
    
    public Model get(String key) {
        return map.get(key);
    }
    
    public Model put(String key, Model m) {
        vista.notifyAdd(m);
        return map.put(key, m);
    }
    
    public void remove(String key) {
        Model m = map.remove(key);
        vista.notifyRemove(m);
    }    
    
    public Set<String> getKeys() {
        return map.keySet();
    }
}
