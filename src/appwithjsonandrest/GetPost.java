/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appwithjsonandrest;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import org.json.*;

/**
 *
 * @author samue
 */
public class GetPost {

    static StringBuilder GetDataJson(String urlIn)
    {
        String line;
        StringBuilder result = new StringBuilder();

        try
        {
            URL url = new URL(urlIn);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //On utilise le verbe GET pour une recherche par ID
            con.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));

            while ((line = rd.readLine()) != null)
            {
                result.append(line);
                System.out.println(line);
            }
            rd.close();
            return result;

            //System.out.println("Le JSON des détails:" + result);
        }catch (Exception e)
        {
            System.out.println("Error in  getting details : " + e);
        }

        return result;
    }
    static int POSTImageJson(String base64Img, String choix, String timestamp, String dateheure,String urlIn)
    {
        String line;
        StringBuilder result = new StringBuilder();

        try
        {
            JSONObject json = new JSONObject();
            json.put("image", base64Img);
            json.put("choix", choix);
            json.put("timestamp", timestamp);
            json.put("dateheure", dateheure);
            String urlParameters = json.toString();
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8 );
            int postDataLength = postData.length;

            URL url = new URL(urlIn);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST"); //Verbe POST
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = rd.readLine()) != null)
            {
                result.append(line);
                System.out.println(line);
            }
            rd.close();

            System.out.println("Réponse:" + result);
            return 0;

        }catch (Exception e)
        {
            System.out.println("Error in  getting details : " + e);
            return -1;
        }
    }
}
