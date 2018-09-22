/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ivao;

import atchelper.ATCHelperFrame;
import atchelper.Vuelo;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author mhyst
 */
public class IvaoServer implements Runnable {
    
    private static SocksProxy sp;
    private ATCHelperFrame frm;
    private String inbuff;
    private String outbuff;
    private HashMap<String, Metar> metar;
    //private HashMap<String, FlightData> aviones;
    private mvc.ModelHandler aviones;
    private Stack<String> commands;
    private Thread t;
    private int idMetar;
    private int idAvion;
    private BufferedWriter bout;
    private long boutid;
    private String dependencia;
    private mvc.Vista vista;
    private static double longitud = 0;
    private static double latitud = 0;
    
    private static final int CMD_METAR = 0;
    private static final int CMD_FLIGHT_MOVES = 1;
    private static final int CMD_FLIGHT_STOPS = 2;
    private static final int CMD_FLIGHT_PLAN = 3;
    private static final int CMD_FLIGHT_DISCONNECTS = 4;
    private static final int CMD_INIT = 5;
    private static final int CMD_ASSUME = 6;
    private static final int CMD_RELEASE = 7;
    private static final int CMD_QUERY = 8;
    private static final int CMD_TM = 9;
    private static final int CMD_ATC_EXIT = 10;
    private static final int CMD_FLIGHT_CONNECTS = 11;
    private static final int CMD_FLIGHT_APPEARS = 12;
    private static final int CMD_INIT_POS = 13;
    
    public IvaoServer(ATCHelperFrame frm) {
        idMetar = 0;
        idAvion = 0;
        dependencia="";
        commands = new Stack();
        metar = new HashMap<String, Metar>();
        //aviones = new HashMap<String, FlightData>();
        aviones = new mvc.ModelHandler();
        String[] fields = {"callsign","squawk","longitud","latitud","altitud","velocidad","origen","destino","piloto","vid","asum"};
        int[] reciprocidadVista = {0,1,2,3,4,5,6,7,8,9,10};
        vista = new mvc.Vista("Aviones",fields,reciprocidadVista,aviones,frm.getTableAviones());
        aviones.setVista(vista);
        inbuff = "";
        outbuff = "";
        String[] connData = {"185.34.216.31", "6809", "6809"};
        try {
            SocksProxy sp = new SocksProxy(connData,this);
        } catch (IOException ex) {
            Logger.getLogger(ATCHelperFrame.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        this.frm = frm;
        boutid = 0;
        try {
            bout = new BufferedWriter(new FileWriter("output.txt"));
        } catch (IOException ex) {
            Logger.getLogger(IvaoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        t = new Thread(this);
        t.start();
    }
    
    public void close(){
        try {
            bout.close();
        } catch (IOException ex) {
            Logger.getLogger(IvaoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setFilter(String filter) {
        vista.setFilters(filter);
    }
    
    public void refresh() {
        vista.loadFromModelHandler();
    }
    
    public String getFpl(String callsign) {
        FlightData fd = (FlightData) aviones.get(callsign);
        
        return fd.getFpl();            
    }
    
    public int getIdCommand(String command) {
        String[] COMMANDS = {"&DSERVER", "@N:", "@S:", "$FP", "#DP", "!SSERVER", "=A", "=R", "$CR", "#TM", "#DA", "#AP", "=C", "%"};
        /**
         * &DSERVER - Información METAR
         * @N:      Seguimiento de aviones en movimiento
         * @S:      Seguimiento de aviones estáticas
         * $FP      Recepción del plan de vuelo
         * #DP      Un avión se ha desconectado
         * !SSERVER Dependencia propia abierta
         * =A       Tráfico asumido
         * =R       Tráfico liberado
         * $CR      Recepción de nombre y vid de pilotos
         * #TM"     Mensajes unicom
         * #DA      Un controlador se desconectó
         * #AP      Un piloto se conectó
         * =C       Un nuevo avión ha aparecido en el radar 
         */
        //#APPUA737:SERVER:573479::3:B:16:Jose Prudenza ICAO
        
        int i;
        for(i = 0; i < COMMANDS.length && !command.startsWith(COMMANDS[i]); i++);
        
        if (i == COMMANDS.length) {
            //System.out.println("Comando ["+command+"] no encontrado");
            return -1;
        } else {
            return i;
        }
    }
    
    public String getDependencia() {
        return dependencia;
    }
    
    public void parse(String msg) {
        // Intentamos escribir cada comando al log output.txt
        try {
            bout.write(""+boutid+"\t"+msg+"\r\n");
            boutid++;
        } catch (Exception e) {}
        
        // Escribimos también el comando en la consola visual del programa
        frm.getOutput().append(msg+"\n");
        
        // Si se trata de algo que va del cliente al servidor lo descartamos
        // al menos por ahora
        //if (msg.startsWith(">"))
        //    return;
        
        // Extraemos el comando completo y lo dividimos en sus partes
        String command = msg.substring(2);
        //System.out.println("CMD: "+command);
        String[] parms = command.split(":");
        
        // Obtenemos el modelo de la tabla de aviones
        //DefaultTableModel taviones = (DefaultTableModel) frm.getTableAviones().getModel();
        
        // Variables de datos
        String callsign;
        String dependenciaAsumido;
        FlightData fd;
        
        // Averiguamos de qué comando se trata
        int ID = getIdCommand(command);
        
        // Tratamos cada comando
        switch(ID) {
            case CMD_METAR:
                // Esta parte tiene como finalidad obtener el metar o metars de 
                // los aeropuertos de los que lo solicita el ivac
                DefaultListModel<String> lm = (DefaultListModel<String>) frm.getListMetar().getModel();
                int pos = parms[3].indexOf(" ");
                String airport = parms[3].substring(0, pos);
                String smetar = parms[3].substring(pos+1);
                Metar m = metar.get(airport);
                if (m == null) {
                    metar.put(airport, new Metar(idMetar, smetar));
                    lm.add(idMetar, airport+" - "+smetar);
                    idMetar++;
                } else {
                    if(!smetar.equals(m.getMetar())) {
                        m.setMetar(smetar);
                        lm.setElementAt(airport+" - "+smetar, m.getId());
                    }
                }
                //System.out.println("METAR: "+airport+" "+smetar);
                break;
            case CMD_FLIGHT_MOVES:
            case CMD_FLIGHT_STOPS:
                //DefaultTableModel taviones = (DefaultTableModel) frm.getTableAviones().getModel();
                callsign = parms[1];
                fd = (FlightData) aviones.get(callsign);
                if (fd == null) {
                    fd = new FlightData(idAvion, callsign, parms[2],
                                        Integer.parseInt(parms[3]),
                                        Double.parseDouble(parms[4]),Double.parseDouble(parms[5]),
                                        Integer.parseInt(parms[6]),Integer.parseInt(parms[7]),
                                        parms[8],Integer.parseInt(parms[9]));
                    aviones.put(callsign, fd);
                    //taviones.addRow(fd.getRow());
                    //if (command.startsWith("@S:"))
                    //    frm.getTableAviones().set
                    //fd.setId(taviones.getRowCount()-1);
                    //idAvion++ ;
                } else {
                    fd.setSq(parms[2]);
                    fd.setN1(Integer.parseInt(parms[3]));
                    fd.setLongitud(Double.parseDouble(parms[4]));
                    fd.setLatitud(Double.parseDouble(parms[5]));
                    fd.setHeight(Integer.parseInt(parms[6]));
                    fd.setSpeed(Integer.parseInt(parms[7]));
                    /*if (taviones.getRowCount() <= fd.getId())
                        return;*/
                    fd.setN2(parms[8]);
                    fd.setN3(Integer.parseInt(parms[9]));
                    vista.receiveUpdate(fd);
                    /*taviones.setValueAt(fd.getSq(),fd.getId(),1);
                    taviones.setValueAt(fd.getLongitud(),fd.getId(),2);
                    taviones.setValueAt(fd.getLatitud(),fd.getId(),3);
                    taviones.setValueAt(fd.getHeight(),fd.getId(),4);
                    taviones.setValueAt(fd.getSpeed(),fd.getId(),5);*/
                    //taviones.setValueAt(fd.getNombrePiloto(),fd.getId(),6);
                }
                break;
            case CMD_FLIGHT_PLAN:
                callsign = parms[0].substring(3);
                fd = (FlightData) aviones.get(callsign);
                if (fd != null) {
                    fd.setFpl(command);
                    vista.receiveUpdate(fd);
                    //taviones.setValueAt(fd.getOrigin(),fd.getId(),6);
                    //String airportName = http.HttpAirportInfo.query(fd.getDestination());
                    //if (airportName != null) {
                    //       taviones.setValueAt(fd.getDestination()+"("+airportName+")",fd.getId(),7);
                    //} else {
                    //    taviones.setValueAt(fd.getDestination(),fd.getId(),7);
                    //}
                }
                break;
            case CMD_FLIGHT_DISCONNECTS:
                if (parms.length > 2)
                    return;
                /*if (!command.equals("#DP"+dependencia+":")) {
                    return;
                }*/
                callsign = parms[0].substring(3);
                fd = (FlightData) aviones.get(callsign);
                if (fd != null) {
                    System.out.println("Avion "+fd.getCallsign()+" eliminado: "+command);
                    //taviones.removeRow(fd.getId());
                    //taviones.fireTableRowsDeleted(fd.getId(), fd.getId());
                    aviones.remove(callsign);
                    int i = 0;
                    //while (i < taviones.getRowCount()) {
                    //for(int i = 0; i < taviones.getRowCount(); i++) {
                        /*try {
                            callsign = (String) taviones.getValueAt(i, 0);
                            fd = aviones.get(callsign);
                            if (fd != null) {
                                fd.setId(i);
                            }
                            i++;
                        } catch (Exception e) {}
                    }*/
                }
                break;
            case CMD_INIT:
                if (dependencia.length() == 0) {
                    dependencia = parms[1];
                    System.out.println("Dependencia iniciada: "+dependencia);
                }
                break;
            case CMD_ASSUME:
                callsign = parms[1];
                dependenciaAsumido = parms[0].substring(2);
                fd = (FlightData) aviones.get(callsign);
                if (fd != null) {
                    fd.setAssumed(true, dependenciaAsumido);
                    vista.receiveUpdate(fd);
                    //taviones.setValueAt(fd.getDependenciaAsumido(), fd.getId(), 10);
                }
                break;
            case CMD_RELEASE:
                callsign = parms[1];
                dependenciaAsumido = parms[0].substring(2);
                fd = (FlightData) aviones.get(callsign);
                if (fd != null) {
                    fd.setAssumed(false, "");
                    vista.receiveUpdate(fd);
                    //taviones.setValueAt(fd.getDependenciaAsumido(), fd.getId(), 10);
                    if (dependenciaAsumido.equals(dependencia)) {
                        database.Database.insertVuelo(fd.getVuelo());
                    }
                }
                break;
            case CMD_QUERY:
                callsign = parms[0].substring(3);
                if (callsign.length() > 7) {
                    DefaultTableModel latc = (DefaultTableModel) frm.getTableATC().getModel();
                    Object[] obj = new Object[2];
                    obj[0] = callsign;
                    obj[1] = parms[3];
                    latc.addRow(obj);
                } else {
                    fd = (FlightData) aviones.get(callsign);
                    if (fd == null)
                        return;
                    if (msg.contains(":RN:")) {
                        fd.setNombrePiloto(parms[3]);
                        //taviones.setValueAt(fd.getNombrePiloto(),fd.getId(),8);
                    } else if (msg.contains(":RV:")) {
                        fd.setVid(parms[3]);
                        //taviones.setValueAt(fd.getVid(),fd.getId(),9);
                    }
                    vista.receiveUpdate(fd);
                }
                break;
            case CMD_ATC_EXIT:
                callsign = parms[0].substring(3);
                if (callsign.length() > 7) {
                    DefaultTableModel latc = (DefaultTableModel) frm.getTableATC().getModel();
                    for(int i = 0; i < latc.getRowCount(); i++) {
                        String atc = (String) latc.getValueAt(i, 0);
                        if (atc.startsWith(callsign)) {
                            latc.removeRow(i);
                            break;
                        }
                    }
                }
            case CMD_INIT_POS:
                if(longitud == 0 && latitud == 0) {
                    String dep = parms[0].substring(1);
                    if (dep.equals(dependencia)) {
                        longitud = Double.parseDouble(parms[5]);
                        latitud = Double.parseDouble(parms[6]);
                        System.out.println("Posicion:"+longitud+", "+latitud);
                    }
                }
                break;
//            case CMD_FLIGHT_CONNECTS:
//                callsign = parms[0].substring(3);
//                fd = aviones.get(callsign);
//                if (fd == null) {
//                    fd = new FlightData(idAvion, callsign, "", 0,0.0,0.0,0,0,"",0);
//                    fd.setVid(parms[2]);
//                    fd.setNombrePiloto(parms[7]);
//                    aviones.put(callsign, fd);
//                    taviones.addRow(fd.getRow());
//                    //if (command.startsWith("@S:"))
//                    //    frm.getTableAviones().set
//                    fd.setId(taviones.getRowCount()-1);                    
//                }
//                break;
        }
    }
    
    public FlightData getAvion(String callsign) {
       return (FlightData) aviones.get(callsign);
    }
    
    public void in(int data) {
        char c = (char) data;
        if (c == '\r') {
            inbuff = inbuff.trim();
            if (inbuff.length() > 0) {
                //parse(inbuff);
                //frm.getOutput().append("< "+inbuff+"\n");
                synchronized(commands) {
                    commands.push("< "+inbuff);
                    commands.notifyAll();
                }
            }
            inbuff = "";
        }
        if (c != '\n' || c != '\r')
            inbuff += c;
    }
    
    public void out(int data) {
        char c = (char) data;
        if (c == '\r') {
            outbuff = outbuff.trim();
            if (outbuff.length() > 0) {
                //frm.getOutput().append("> "+outbuff+"\n");
                synchronized(commands) {
                    commands.push("> "+outbuff);
                    commands.notifyAll();
                }
            }
            outbuff = "";
        }
        if (c != '\n' || c != '\r')
            outbuff += c;    
    }    
    
    public void incoming(String msg) {
        
        //System.out.println("> "+msg);
        frm.getOutput().append("< "+msg);
    }
    
    public void outgoing(String msg) {
        //System.out.println("< "+msg);
        frm.getOutput().append("> "+msg);
    }

    @Override
    public void run() {
        synchronized(commands) {
            while(true) {
                while(commands.size() == 0) {
                    //System.out.println("wait");
                    try {
                        commands.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(IvaoServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //System.out.println("pop");
                parse(commands.remove(0));
            }
        }
    }
    
    public class Metar {
        private int id;
        private String metar;
        
        public Metar(int id, String metar) {
            this.id = id;
            this.metar = metar;
        }
        
        public int getId() {
            return id;
        }
        
        public String getMetar() {
            return metar;
        }
        
        public void setMetar(String metar) {
            this.metar = metar;
        }
    }
    
    public class FlightData extends mvc.Model {
        private int id;
        private String callsign;
        private String sq;
        private int n1;
        private double longitud;
        private double latitud;
        private int height;
        private int speed;
        private String n2;
        private int n3;
        private String nombrePiloto;
        private String vid;
        private String fpl;
        private String origin;
        private String destination;
        private boolean assumed;
        private String dependenciaAsumido;
        
        public FlightData(int id, String callsign, String sq, int n1,
                          double longitud, double latitud, int height,
                          int speed, String n2, int n3) {
            super(null);
            this.id = id;
            this.callsign = callsign;
            this.sq = sq;
            this.n1 = n1;
            this.longitud = longitud;
            this.latitud = latitud;
            this.height = height;
            this.speed = speed;
            this.n2 = n2;
            this.n3 = n3;
            this.assumed = false;
            nombrePiloto = "";
            vid = "";
            fpl = "";
            dependenciaAsumido = "";
        }

        public boolean isAssumed() {
            return assumed;
        }

        public void setAssumed(boolean assumed, String dependencia) {
            this.assumed = assumed;
            this.dependenciaAsumido = dependencia;
        }
        
        public String getDependenciaAsumido() {
            return dependenciaAsumido;
        }
        
        public String getOrigin() {
            if (fpl.length() == 0)
                return "---";
            return origin;
        }
        
        public String getDestination() {
            if (fpl.length() == 0)
                return "---";
            return destination;
        }
        
        public Object[] getRow() {
            Object[] row = new Object[11];
            
            row[0] = callsign;
            row[1] = sq;
            row[2] = longitud;
            row[3] = latitud;
            row[4] = height;
            row[5] = speed;
            row[6] = getOrigin();
            row[7] = getDestination();
            row[8] = nombrePiloto;
            row[9] = vid;
            row[10] = dependenciaAsumido;
            
            return row;
        }
        
        public Vuelo getVuelo() {
            //String origen, String destino, String fecha
            Vuelo v = new Vuelo(0,callsign,"","","","",sq,1,false,fpl,origin,destination,"");
            
            return v;
        }
        
        public String toString() {
            return callsign+" | "+sq+" | "+longitud+" | "+latitud+" | "+height+" | "+speed+" | "+nombrePiloto;
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

        public String getSq() {
            return sq;
        }

        public void setSq(String sq) {
            this.sq = sq;
        }

        public int getN1() {
            return n1;
        }

        public void setN1(int n1) {
            this.n1 = n1;
        }

        public double getLongitud() {
            return longitud;
        }

        public void setLongitud(double longitud) {
            this.longitud = longitud;
        }

        public double getLatitud() {
            return latitud;
        }

        public void setLatitud(double latitud) {
            this.latitud = latitud;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

        public String getN2() {
            return n2;
        }

        public void setN2(String n2) {
            this.n2 = n2;
        }

        public int getN3() {
            return n3;
        }

        public void setN3(int n3) {
            this.n3 = n3;
        }

        public String getNombrePiloto() {
            return nombrePiloto;
        }

        public void setNombrePiloto(String nombrePiloto) {
            this.nombrePiloto = nombrePiloto;
        }

        public String getVid() {
            return vid;
        }

        public void setVid(String vid) {
            this.vid = vid;
        }

        public String getFpl() {
            return fpl;
        }

        public void setFpl(String fpl) {
            this.fpl = fpl;
            String[] parms = fpl.split(":");
            origin = parms[5];
            destination = parms[9];
        }

        @Override
        public void setRow(Object[] row) {
            /**
             *             Object[] row = new Object[11];
            
            row[0] = callsign;
            row[1] = sq;
            row[2] = longitud;
            row[3] = latitud;
            row[4] = height;
            row[5] = speed;
            row[6] = getOrigin();
            row[7] = getDestination();
            row[8] = nombrePiloto;
            row[9] = vid;
            row[10] = dependenciaAsumido;
             */
            
            callsign = (String) row[0];
            sq = (String) row[1];
            longitud = (Double) row[2];
            latitud = (Double) row[3];
            height = (int) row[4];
            speed = (int) row[5];
            nombrePiloto = (String) row[8];
            vid = (String) row[9];
            dependenciaAsumido = (String) row[10];
        }
    }
    
    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * 
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public static double distance(double lat1, double lat2, double lon1,
            double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }    
    
    public static boolean isInDistance(FlightData f, double dist) {
        if (longitud == 0 && latitud == 0)
            return true;
        
        double d = distance(latitud,f.getLatitud(),longitud,f.getLongitud(),0,f.getHeight()/3.2808);
        
        return d <= dist*1000;
    }
    
}
