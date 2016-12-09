package com.example.iem.mapapp;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.Toast;

import com.example.iem.mapapp.callApi.ApiRequest;
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


public class MapsActivity extends AbstractMapActivity
        implements NavigationView.OnNavigationItemSelectedListener , OnMapReadyCallback,OnInfoWindowClickListener {


    private GoogleMap mMap;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ApiRequest apiRequest;
    private Boolean needsInit = false;

    private Toolbar toolbar;
    private LinesAndStops linesAndStops;


    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (readyToGo()) {
            setContentView(R.layout.activity_main);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("All");
            setSupportActionBar(toolbar);

            apiRequest = ApiRequest.getInstance();


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            menuListInit();
            expListView = (ExpandableListView) findViewById(R.id.ExpandableList);

            listAdapter = new ExpandableListAdapterPerso(this, listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);

            Button displayNetworkButton = (Button)findViewById(R.id.btnDisplayNetwork);
            displayNetworkButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getBaseContext(), "LOAD NETWORK"
                            , Toast.LENGTH_LONG).show();
                    loadNetwork();
                }
            });


            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(map);

            if (savedInstanceState == null) {
                needsInit = true;
            }
            mapFragment.getMapAsync(this);
        }
    }



    private void loadNetwork(){
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
            /* building test data to remove */
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

            /* End building */
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




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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








    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;


        // Add a marker in Sydney and move the camera

        checkPermission();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);



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



    @Override
    public void onInfoWindowClick(Marker marker) {
        int u =1;
        u++;
    }

}
