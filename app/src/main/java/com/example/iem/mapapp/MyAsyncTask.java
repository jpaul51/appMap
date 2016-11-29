package com.example.iem.mapapp;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jonas on 28/11/2016.
 */

public class MyAsyncTask extends AsyncTask {

    private String rep=null;
    @Override
    protected String doInBackground(Object[] params) {
        request();
        System.out.println("RETOUR: "+rep);
        return rep;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    private  void request(){
        String url ="http://178.62.77.239:8080/getlinesandstops";
        BufferedReader in;
        try {
            URL urlGetNetwork = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) urlGetNetwork.openConnection();
            String readLine="";
            String response="";
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));

                while ((readLine = in.readLine()) != null) {
                   response+=readLine;
                }
                rep=response;
                System.out.println("Réponse: "+response);
                in.close();

            }
        }catch (Exception e){
           System.out.println("ERROR: "+e.getMessage());
            e.printStackTrace();
        }
    }
}
