package com.example.iem.mapapp.callApi;

import com.example.iem.mapapp.JtsObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import app.model.Line;
import app.model.Stop;

/**
 * Created by iem on 02/12/2016.
 */

public class ApiRequest {
    private static ApiRequest ourInstance = new ApiRequest();
    private String ip_url ="http://172.16.1.159:8080";


    public static ApiRequest getInstance() {
        return ourInstance;
    }

    private ApiRequest() {

    }



    public String[] getLinesNames(){
        String url = ip_url + "/getlinesname";
        try {
            String response = httpRequest(url);
            ObjectMapper mapper = JtsObjectMapper.JtsObjectMapper();
            List<String> nameLines = mapper.readValue(response,List.class);
            String retour[] = new String[nameLines.size()];
            for(int i = 0 ; i < nameLines.size(); i++){
                retour[i] = nameLines.get(i);
            }
            return retour;
        }catch (Exception e){
            System.out.println("ERROR: "+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getLinesName(){
        String url = ip_url + "/getlinesname";
        try {
            String response = httpRequest(url);
            ObjectMapper mapper = JtsObjectMapper.JtsObjectMapper();
            List<String> nameLines = mapper.readValue(response,List.class);
            return nameLines;
        }catch (Exception e){
            System.out.println("ERROR: "+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    public Line getLinesByNames(String name){
        String url ="/getLinesByName?name=" + name;
        try {
            String response = httpRequest(url);
            ObjectMapper mapper = JtsObjectMapper.JtsObjectMapper();
            Line line  = mapper.readValue(response,Line.class);
            return line;
        }catch (Exception e){
            System.out.println("ERROR: "+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Stop> getStopsAroundMe(int nbStops, float latitude, float longitude){
        String url = "/getStopsAroudMe?";
        try {
            String response = httpRequest(url);
            ObjectMapper mapper = JtsObjectMapper.JtsObjectMapper();
            List<Stop> stopList = mapper.readValue(response,List.class);
            return stopList;
        }catch (Exception e){
            System.out.println("ERROR: "+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public String getlinesAndStops(){
        String url = "/getlinesandstops";
        String response = httpRequest(url);
        return response;
    }


    public String httpRequest(String url){
        BufferedReader in;
        System.out.println("URL: "+ip_url+"||"+url);
        try {


            URL urlGetNetwork = new URL(ip_url + url);
            HttpURLConnection urlConnection = (HttpURLConnection) urlGetNetwork.openConnection();
            String readLine="";
            String response="";
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));

                while ((readLine = in.readLine()) != null) {
                    response+=readLine;
                }
                System.out.println("Réponse: "+response);
                in.close();
                return response;
            }
        }catch (Exception e){
            System.out.println("ERROR: "+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
