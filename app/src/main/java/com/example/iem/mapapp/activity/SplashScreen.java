package com.example.iem.mapapp.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.iem.mapapp.MapsActivity;
import com.example.iem.mapapp.R;
import com.example.iem.mapapp.callApi.ApiRequest;
import com.example.iem.mapapp.utils.Constante;

import java.util.List;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new MyAsyncTask().execute();
    }


    private class MyAsyncTask extends AsyncTask{

        ApiRequest apiRequest = ApiRequest.getInstance();
        String namesLines[];

        @Override
        protected String doInBackground(Object[] params) {
          //  namesLines = apiRequest.getLinesNames();
            //System.out.println("RETOUR: " + namesLines.toString());
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            Intent intent = new Intent(SplashScreen.this, MapsActivity.class);
            intent.putExtra(Constante.INTENT_LIST_NAME_LINES,namesLines);
            startActivity(intent);
            finish();
        }


    }
}
