package com.example.iem.mapapp.callApi;

import com.example.iem.mapapp.JtsObjectMapper;
import com.example.iem.mapapp.model.Line;
import com.example.iem.mapapp.model.LinesAndStops;
import com.example.iem.mapapp.model.Stop;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by iem on 02/12/2016.
 */

public class ApiRequest {
    private static ApiRequest ourInstance = new ApiRequest();
    private String ip_url ="http://172.31.247.239:8080";


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

    public LinesAndStops getShortestWaybetween(String firstStop, String secondStop) throws IOException {
        String url = "/getShortestWayBetween?start="+firstStop+"&end="+secondStop;
        String response = httpRequest(url);

        ObjectMapper mapper = JtsObjectMapper.JtsObjectMapper();
        LinesAndStops result = new LinesAndStops();
        if(mapper != null && response !=null)
        {
            result = mapper.readValue(response,LinesAndStops.class);
        }

        return result;

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
            System.out.println("OK ? "+urlConnection.getResponseCode());
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
