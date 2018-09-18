/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import atchelper.Fase;
import atchelper.Fraseologia;
import atchelper.Vuelo;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mhyst
 */
public class Database {
    
    private static Connection conn = null;
    
    public static Connection getConnection() throws SQLException {
        if (conn != null)
            return conn;
        
        
        DriverManager.registerDriver(new org.sqlite.JDBC());
        conn = DriverManager.getConnection("jdbc:sqlite:atcdata2.db");
        return conn;
    }
    
    public static ArrayList<Vuelo> vuelos(String sql) throws SQLException {
        ArrayList<Vuelo> vs = new ArrayList<Vuelo>();
        
        Statement st = null;
        ResultSet rs = null;
        try {
            st = getConnection().createStatement();
            System.out.println("select * from vuelo"+sql);
            rs = st.executeQuery("select * from vuelo"+sql);
            while (rs.next()) {
                Vuelo v = new Vuelo(rs.getInt("vuelo_id"),
                                    rs.getString("callsign"),
                                    rs.getString("tipo"),
                                    rs.getString("SID"),
                                    rs.getString("STAR"),
                                    rs.getString("climb"),
                                    rs.getString("squawck"),
                                    rs.getInt("fase"),
                                    rs.getBoolean("completo"),
                                    rs.getString("planvuelo"),
                                    rs.getString("origen"),
                                    rs.getString("destino"),
                                    rs.getString("fecha"));
                vs.add(v);
            }
            return vs;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                rs.close();
                st.close();
            } catch (Exception ce) {}
        }
        return null;
            
    }
    
    public static ArrayList<Fraseologia> fraseologia(int fase) throws SQLException {
        ArrayList<Fraseologia> fs = new ArrayList<Fraseologia>();
        
        Statement st = null;
        ResultSet rs = null;
        try {
            st = getConnection().createStatement();
            rs = st.executeQuery("select * from frase where fase = "+fase+" order by frase_id");
            while (rs.next()) {
                Fraseologia f = new Fraseologia(rs.getInt("frase_id"),
                                    rs.getInt("fase"),
                                    rs.getString("castellano"),
                                    rs.getString("ingles"),
                                    rs.getBoolean("paso"));
                fs.add(f);
            }
            return fs;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            rs.close();
            st.close();
        }
        return null;
            
    }
    
    public static ArrayList<Fase> fases(String tipo, String DA) throws SQLException {
        ArrayList<Fase> fs = new ArrayList<Fase>();
        
        Statement st = null;
        ResultSet rs = null;
        try {
            st = getConnection().createStatement();
            rs = st.executeQuery("select * from fase where tipo = '"+tipo+"' and DA = '"+DA+"' order by orden");
            while (rs.next()) {
                Fase f = new Fase(rs.getInt("fase_id"),
                                    rs.getString("nombre"),
                                    rs.getInt("orden"),
                                    rs.getString("tipo"),
                                    rs.getString("DA"));
                fs.add(f);
            }
            return fs;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            rs.close();
            st.close();
        }
        return null;
    }
    
    public static ArrayList<Fase> fasesEx() throws SQLException {
        ArrayList<Fase> fs = new ArrayList<Fase>();
        
        Statement st = null;
        ResultSet rs = null;
        try {
            st = getConnection().createStatement();
            rs = st.executeQuery("select * from fase order by orden");
            while (rs.next()) {
                Fase f = new Fase(rs.getInt("fase_id"),
                                    rs.getString("nombre"),
                                    rs.getInt("orden"),
                                    rs.getString("tipo"),
                                    rs.getString("DA"));
                fs.add(f);
            }
            return fs;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            rs.close();
            st.close();
        }
        return null;
    }
    
    public static String getFase(int id) throws SQLException{
        Statement st = null;
        ResultSet rs = null;
        try {
            st = getConnection().createStatement();
            rs = st.executeQuery("select nombre from fase where fase_id = "+id);
            if (rs.next()) {
                return rs.getString("nombre");
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            rs.close();
            st.close();
        }
        return null;
    }
    
        public static String getFpl(int id) throws SQLException{
        Statement st = null;
        ResultSet rs = null;
        try {
            st = getConnection().createStatement();
            rs = st.executeQuery("select planvuelo from vuelo where vuelo_id = "+id);
            if (rs.next()) {
                return rs.getString("planvuelo");
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            rs.close();
            st.close();
        }
        return null;
    }
    
    public static void updateVuelo(int id, int columna, String data) {
        String[] campos = {"vuelo_id", "callsign", "tipo", "SID", "STAR", "climb", "squawck","fase"};
        Statement st = null;
        try {
            st = getConnection().createStatement();
            System.out.println("update vuelo set "+campos[columna]+" = '"+data+"' where vuelo_id = "+id);
            st.executeUpdate("update vuelo set "+campos[columna]+" = '"+data+"' where vuelo_id = "+id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (st != null)
                    st.close();
            } catch (Exception e2) {
                System.out.println(e2.getMessage());
            }
        }
    }
    
    public static void insertVuelo(Vuelo v) {
        Statement st = null;
        try {
            st = getConnection().createStatement();
            System.out.println("insert into vuelo (callsign, tipo, SID, STAR, climb, squawck, fase, completo, planvuelo, origen, destino, fecha) values('"+
                    v.getCallsign()+"', '"+v.getTipo()+"', '"+v.getSID()+"', '"+v.getSTAR()+"', '"+v.getClimb()+"', '"+v.getSquawck()+"', "+
                    v.getFase()+", "+false+", '"+v.getFpl()+"', '"+v.getOrigen()+"', '"+v.getDestino()+"', CURRENT_TIMESTAMP)");
            st.executeUpdate("insert into vuelo (callsign, tipo, SID, STAR, climb, squawck, fase, completo, planvuelo, origen, destino, fecha) values('"+
                    v.getCallsign()+"', '"+v.getTipo()+"', '"+v.getSID()+"', '"+v.getSTAR()+"', '"+v.getClimb()+"', '"+v.getSquawck()+"', "+
                    v.getFase()+", "+false+", '"+v.getFpl()+"', '"+v.getOrigen()+"', '"+v.getDestino()+"', CURRENT_TIMESTAMP)");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (st != null)
                    st.close();
            } catch (Exception e2) {
                System.out.println(e2.getMessage());
            }
        }
    }    
}
