/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

/**
 *
 * @author mhyst
 */
public class HttpAirportInfo {

    public static String query(String icao) {
        System.out.println("Petición aeropuerto: ["+icao+"]");
        HttpURLConnection connection = null;
        String address = "http://www.airport-data.com/api/ap_info.json?icao="+icao;

        try {
            //Create connection
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //connection.setRequestProperty("Content-Type", 
            //    "application/x-www-form-urlencoded");

            //connection.setRequestProperty("Content-Length", 
            //    Integer.toString(urlParameters.getBytes().length));
            //connection.setRequestProperty("Content-Language", "en-US");  

            connection.setUseCaches(false);
            //connection.setDoOutput(true);


            /*DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);*/


            if (connection.getResponseCode() != 200) {
                return "Error: Se excedió el número de peticiones del servidor";
            }
            //Get Response  
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
              response.append(line);
              response.append('\r');
            }
            rd.close();


            String json = response.toString();
            String[] jsonData = json.split(",");
            String name = jsonData[2].split(":")[1];
            String airportname = null;
            if (name == null || name.trim().length() == 0 || name.equalsIgnoreCase("null"))
                return null;
            
            String country = jsonData[4].split(":")[1];
            country = country.substring(1, country.length()-1);

            airportname = name.substring(1,name.length()-1);
            Properties p = new Properties();
            p.load(new StringReader("key="+airportname));
            airportname = p.getProperty("key");

            return airportname+" - ("+country+")";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
          if (connection != null) {
            connection.disconnect();
          }
        }
    }
    
    public static void main(String[] args) {
        String name = HttpAirportInfo.query("EDDL");
        System.out.println(name);
    }
     
}
