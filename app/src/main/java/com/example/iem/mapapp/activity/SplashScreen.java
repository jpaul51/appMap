package com.example.iem.mapapp.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.iem.mapapp.MapsActivity;
import com.example.iem.mapapp.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new MyAsyncTask().execute();
    }


    private class MyAsyncTask extends AsyncTask{
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
            Intent intent = new Intent(SplashScreen.this, MapsActivity.class);
            startActivity(intent);
            finish();
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
}
