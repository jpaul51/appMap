package com.example.iem.mapapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.iem.mapapp.callApi.ApiRequest;
import com.example.iem.mapapp.interfaces.CallbackButtonClick;
import com.example.iem.mapapp.listener.ButtonCliqueListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.wallet.firstparty.GetBuyFlowInitializationTokenResponse;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.vividsolutions.jts.geom.LineString;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import app.model.Line;
import app.model.LinesAndStops;
import app.model.Schedule;
import app.model.Stop;

import static com.example.iem.mapapp.R.id.map;
import static com.example.iem.mapapp.R.id.toolbarChoixLigne;


public class MapsActivity extends AbstractMapActivity
        implements NavigationView.OnNavigationItemSelectedListener , OnMapReadyCallback,OnInfoWindowClickListener,
        CallbackButtonClick {

    //maps
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    //toolbar
    private Toolbar toolbarMain;
    private Toolbar toolbarChoixLigne;
    private Toolbar toolbarRechercheTrajet;

    //bottom bar
    private BottomBar bottomBar;

    //Boutons dans la top bar pour les lignes;
    private Button btLigne1;
    private Button btLigne2;
    private Button btLigne3;
    private Button btLigne4;
    private Button btLigne5;
    private Button btLigne6;
    private Button btLigne7;
    private Button btLigne8;

    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private LinesAndStops linesAndStops;
    private ApiRequest apiRequest;
    private Boolean needsInit = false;
    private List<ButtonCliqueListener> listeners;
    private List<Integer> listLineDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (readyToGo()) {
            setContentView(R.layout.activity_main_v2);

            initUIComponent();

            toolbarMain.setTitle("TUB");
            setSupportActionBar(toolbarMain);

            /*exemple d'implementation
            String colorDefault = "#5500FF";
            ((GradientDrawable)btLigne1.getBackground()).setColor(Color.parseColor(colorDefault));*/
            listeners = new ArrayList<>();
            listLineDisplay = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                listeners.add(new ButtonCliqueListener(this,i));
            }
            btLigne1.setOnClickListener(listeners.get(0));
            btLigne2.setOnClickListener(listeners.get(1));
            btLigne3.setOnClickListener(listeners.get(2));
            btLigne4.setOnClickListener(listeners.get(3));
            btLigne5.setOnClickListener(listeners.get(4));
            btLigne6.setOnClickListener(listeners.get(5));
            btLigne7.setOnClickListener(listeners.get(6));
            btLigne8.setOnClickListener(listeners.get(7));


            bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelected(@IdRes int tabId) {
                    switch (tabId){
                        case R.id.tab_ligne:
                            toolbarChoixLigne.setVisibility(View.VISIBLE);
                            toolbarRechercheTrajet.setVisibility(View.GONE);
                            break;
                        case R.id.tab_direction:
                            toolbarChoixLigne.setVisibility(View.GONE);
                            toolbarRechercheTrajet.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            });


            apiRequest = ApiRequest.getInstance();


            menuListInit();


            if (savedInstanceState == null) {
                needsInit = true;
            }
            mapFragment.getMapAsync(this);
        }
    }

    private void initUIComponent(){
        //Top bar
        toolbarMain = (Toolbar) findViewById(R.id.toolbar);
        toolbarChoixLigne = (Toolbar) findViewById(R.id.toolbarChoixLigne);
        toolbarRechercheTrajet = (Toolbar) findViewById(R.id.toolbarRechercheTrajet);

        //Bottom bar

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        //bt Ligne 2nd Top bar
        btLigne1 = (Button) findViewById(R.id.bt_ligne1);
        btLigne2 = (Button) findViewById(R.id.bt_ligne2);
        btLigne3 = (Button) findViewById(R.id.bt_ligne3);
        btLigne4 = (Button) findViewById(R.id.bt_ligne4);
        btLigne5 = (Button) findViewById(R.id.bt_ligne5);
        btLigne6 = (Button) findViewById(R.id.bt_ligne6);
        btLigne7 = (Button) findViewById(R.id.bt_ligne7);
        btLigne8 = (Button) findViewById(R.id.bt_ligne8);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
    }



    Thread loadNetwork = new Thread(new Runnable() {
        @Override
        public void run() {
            String lines=null;

            linesAndStops=null;

            lines = apiRequest.getlinesAndStops();

            ObjectMapper mapper = JtsObjectMapper.JtsObjectMapper();
            try {
                linesAndStops  = mapper.readValue(lines,LinesAndStops.class);

            } catch (IOException e) {
                e.printStackTrace();
            }

            /*ArrayList<LatLng> linesPoints = new ArrayList<>();
            LineString aLine=null;
            for(Line aMultiLine : linesAndStops.getLines()) {
                for (int i=0; i < aMultiLine.getLines().getNumGeometries();i++ ) {
                    aLine = (LineString) aMultiLine.getLines().getGeometryN(i);
                    final PolylineOptions polyOptions = new PolylineOptions().color(Color.parseColor(aMultiLine.getColor()))
                            ;
                    for (int coordinatesIndex = 0; coordinatesIndex < aLine.getCoordinates().length; coordinatesIndex++) {
                        // linesPoints.add();
                        polyOptions.add(new LatLng(aLine.getCoordinates()[coordinatesIndex].y,aLine.getCoordinates()[coordinatesIndex].x));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMap.addPolyline(polyOptions);
                        }
                    });

                }
            }*/

            LocalDateTime localDateTime = new LocalDateTime();

            /*for (Stop aStop: linesAndStops.getStops())
            {
                //building test data to remove
                ArrayList<Schedule> testSchedules= new ArrayList<>();
                Schedule s1 = new Schedule();
                String dateExemple ="09:15";
                String dateExemple2 ="17:45";

                DateTimeFormatter format = DateTimeFormat.forPattern("HH:mm");
                DateTime extractedTime = format.parseDateTime(dateExemple);
                DateTime extractedTime2 = format.parseDateTime(dateExemple2);

                ArrayList<DateTime> dates = new ArrayList<>();
                dates.add(extractedTime);
                dates.add(extractedTime2);

                s1.setSchedules(dates);
                ArrayList<Schedule> stopSchedules = new ArrayList<>();
                stopSchedules.add(s1);
                aStop.setSchedules(stopSchedules);

                //End building
                final MarkerOptions opt = new MarkerOptions()
                        .position(new LatLng(aStop.getPoint().getX(), aStop.getPoint().getY()))
                        .title(aStop.getLabel());

                String oldSnippet="";
                for(DateTime time : aStop.getSchedules().get(0).getSchedules()) {
                    if (opt.getSnippet()!=null)
                        oldSnippet= opt.getSnippet();
                    if(time.getHourOfDay() == localDateTime.getHourOfDay()  ) {
                        if (time.getMinuteOfHour() > localDateTime.getMinuteOfHour()) {
                            opt.snippet(oldSnippet + time.getHourOfDay() + ":" + time.getMinuteOfHour() + "\n");
                        }
                    } else if(time.getHourOfDay() > localDateTime.getHourOfDay()  ) {
                        opt.snippet(oldSnippet + time.getHourOfDay() + ":" + time.getMinuteOfHour() + "\n");
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMap.addMarker(opt);
                    }
                });

            }*/
        }
    });

    /*private void loadNetwork(){
        String lines=null;

        linesAndStops=null;

        lines = apiRequest.getlinesAndStops();

        ObjectMapper mapper = JtsObjectMapper.JtsObjectMapper();
        try {
            linesAndStops  = mapper.readValue(lines,LinesAndStops.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<LatLng> linesPoints = new ArrayList<>();
        LineString aLine=null;
        for(Line aMultiLine : linesAndStops.getLines()) {
            for (int i=0; i < aMultiLine.getLines().getNumGeometries();i++ ) {
                aLine = (LineString) aMultiLine.getLines().getGeometryN(i);
                PolylineOptions polyOptions = new PolylineOptions().color(Color.parseColor(aMultiLine.getColor()))
                        ;
                for (int coordinatesIndex = 0; coordinatesIndex < aLine.getCoordinates().length; coordinatesIndex++) {
                    // linesPoints.add();
                    polyOptions.add(new LatLng(aLine.getCoordinates()[coordinatesIndex].y,aLine.getCoordinates()[coordinatesIndex].x));
                }


                mMap.addPolyline(polyOptions);
            }
        }

        LocalDateTime localDateTime = new LocalDateTime();

        for (Stop aStop: linesAndStops.getStops())
        {
             //building test data to remove
            ArrayList<Schedule> testSchedules= new ArrayList<>();
            Schedule s1 = new Schedule();
            String dateExemple ="09:15";
            String dateExemple2 ="17:45";

            DateTimeFormatter format = DateTimeFormat.forPattern("HH:mm");
            DateTime extractedTime = format.parseDateTime(dateExemple);
            DateTime extractedTime2 = format.parseDateTime(dateExemple2);

            ArrayList<DateTime> dates = new ArrayList<>();
            dates.add(extractedTime);
            dates.add(extractedTime2);

            s1.setSchedules(dates);
            ArrayList<Schedule> stopSchedules = new ArrayList<>();
            stopSchedules.add(s1);
            aStop.setSchedules(stopSchedules);

            //End building
            MarkerOptions opt = new MarkerOptions()
                    .position(new LatLng(aStop.getPoint().getX(), aStop.getPoint().getY()))
                    .title(aStop.getLabel());

            String oldSnippet="";
            for(DateTime time : aStop.getSchedules().get(0).getSchedules()) {
                if (opt.getSnippet()!=null)
                    oldSnippet= opt.getSnippet();
                if(time.getHourOfDay() == localDateTime.getHourOfDay()  ) {
                    if (time.getMinuteOfHour() > localDateTime.getMinuteOfHour()) {
                        opt.snippet(oldSnippet + time.getHourOfDay() + ":" + time.getMinuteOfHour() + "\n");
                    }
                } else if(time.getHourOfDay() > localDateTime.getHourOfDay()  ) {
                    opt.snippet(oldSnippet + time.getHourOfDay() + ":" + time.getMinuteOfHour() + "\n");
                }
            }
            mMap.addMarker(opt);
        }
    }*/

    private void menuListInit(){
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        listDataHeader.add("Lignes");
        listDataChild.put(listDataHeader.get(0),apiRequest.getLinesName());

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();


        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);*/
        return true;
    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        loadNetwork.start();
        mMap = googleMap;
        checkPermission();
    }





    private String getFileName(String file){


        String fileName = "";

        int i = file.lastIndexOf('.');
        if (i > 0) {
            fileName = file.substring(0, i );
        }
        return fileName;


    }


    private InputStream openRawFileByName(String inputname)
    {
        InputStream ins =null;

        ins = getResources().openRawResource(
                getResources().getIdentifier(inputname,
                        "raw", getPackageName()));

        return ins;
    }



    private void checkPermission(){

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                System.out.println("REQUEST PERMISSIONS");
                ActivityCompat.requestPermissions(this,
                        new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        if (ContextCompat.checkSelfPermission(this,  android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            moveButtonLocation();
            Log.d("SUCCESS","Permission de localisation");
        } else {
            Log.d("Erreur Permission","Permission de localisation");
            float zoomLevel = 12.25f;
            LatLng centerOfBourg = new LatLng(46.202181, 5.237056);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerOfBourg,zoomLevel));
        }
        mMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
        mMap.setOnInfoWindowClickListener(this);
    }
    private void addMarker(GoogleMap map, double lat, double lon,
                           int title, int snippet) {
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                .title(getString(title))
                .snippet(getString(snippet)));
    }

    private void moveButtonLocation(){

        View mapView = mapFragment.getView();
        if (mapView != null && mapView.findViewById(1) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
    }



    @Override
    public void onInfoWindowClick(Marker marker) {
        int u =1;
        u++;
    }

    @Override
    public void displayLine(int number) {
        Line line = linesAndStops.getLines().get(number);
        LineString aLine=null;
        for (int i=0; i < line.getLines().getNumGeometries();i++ ) {
            aLine = (LineString) line.getLines().getGeometryN(i);
            final PolylineOptions polyOptions = new PolylineOptions().color(Color.parseColor(line.getColor()))
                    ;
            for (int coordinatesIndex = 0; coordinatesIndex < aLine.getCoordinates().length; coordinatesIndex++) {
                polyOptions.add(new LatLng(aLine.getCoordinates()[coordinatesIndex].y,aLine.getCoordinates()[coordinatesIndex].x));
            }
                    mMap.addPolyline(polyOptions);
        }
        listLineDisplay.add(number);
    }

    @Override
    public void removeLine(int number) {
        mMap.clear();
        for (int i = 0; i < listLineDisplay.size() ; i++) {
            int n = listLineDisplay.get(i);
            if(n == number){
                listLineDisplay.remove(i);
                continue;
            }
            displayLine(n);
        }
    }
}
