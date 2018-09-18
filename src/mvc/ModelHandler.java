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
public abstract class ModelHandler {
    
    private final HashMap<String, Model> map;
    
    public ModelHandler(Model m) {
        map = new HashMap<>();
    }
    
    public Model get(String key) {
        return map.get(key);
    }
    
    public Model put(String key, Model m) {
        return map.put(key, m);
    }
    
    public void remove(String key) {
        map.remove(key);
    }    
    
    public Set<String> getKeys() {
        return map.keySet();
    }
}
