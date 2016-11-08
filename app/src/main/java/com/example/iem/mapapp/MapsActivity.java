package com.example.iem.mapapp;

import android.content.pm.PackageManager;
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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.example.iem.mapapp.Model.BusLign;
import com.example.iem.mapapp.Model.BusStop;
import com.example.iem.mapapp.Model.Schedule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlContainer;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlLineString;
import com.google.maps.android.kml.KmlPlacemark;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MapsActivity extends AbstractMapActivity
        implements NavigationView.OnNavigationItemSelectedListener , OnMapReadyCallback,OnInfoWindowClickListener {


    private GoogleMap mMap;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private Boolean needsInit=false;
    private ArrayList<BusLign> lines = new ArrayList<>();

    private Toolbar toolbar;

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


                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

                if (savedInstanceState == null) {
                    needsInit=true;
                }
                mapFragment.getMapAsync(this);
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

        listDataHeader.add("Ligne");
        List<String> listLine = new ArrayList<String>();
        listLine.add("Ligne 1");
        listLine.add("Ligne 2");
        listLine.add("Ligne 3");
        listLine.add("Ligne 4");
        listLine.add("Ligne 5");
        listLine.add("Ligne 6");
        listLine.add("Ligne 7");
        listLine.add("Ligne 8");

        listDataChild.put(listDataHeader.get(0),listLine);

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



    private KmlLineString getMarkers(KmlContainer layer){
        KmlLineString retour=null;
        if(layer.getPlacemarks().iterator().hasNext()) {
            retour = (KmlLineString) layer.getPlacemarks().iterator().next().getGeometry();
            // System.out.println("INFO:"+layer.getPlacemarks().iterator().next().getGeometry().toString());
        }
        else {
            if (layer.getContainers().iterator().hasNext()) {
                getMarkers(layer.getContainers().iterator().next());
            }
            else{
                System.out.println("Il n'y a pas de markers !!");
            }
        }

        return retour;
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

        LatLng bourgEnBresse = new LatLng(46.2052, 5.2255);
        mMap.addMarker(new MarkerOptions().position(bourgEnBresse).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bourgEnBresse));
        checkPermission();

        InputStream ins = openRawFileByName("data");

        StringBuilder builder =new StringBuilder();
        BufferedReader bReader = new BufferedReader(new InputStreamReader(ins));
        String line="";
        try {
            while ((line = bReader.readLine()) != null) {
                builder.append(line);
            }
            System.out.println("----------------------");

            //System.out.println(builder.toString());
            //create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            //convert json string to object

            List<BusLign> emp = Arrays.asList(objectMapper.readValue(builder.toString(), BusLign[].class));


            //System.out.println("LABEL: " + emp.get(0).getLabel() + " :::::: " + emp.get(0).getstops().get(0).getName());


            InputStream horaire = null;
            //System.out.println("FILENAME: " + emp.get(0).getstops().get(0).getScheduleFile());

            horaire = openRawFileByName(getFileName(emp.get(0).getstops().get(0).getScheduleFile()));


            builder = new StringBuilder();
            bReader = new BufferedReader(new InputStreamReader(horaire));
            line = "";
            try {
                while ((line = bReader.readLine()) != null) {
                    builder.append(line);
                }
                String csv = builder.toString();
                System.out.println(csv);


            } catch (IOException e) {
                e.printStackTrace();
            }


            ArrayList<String> linesName = new ArrayList<>();
            //dev
            linesName.add("ligne1");
            //  linesName.add("ligne21");
            linesName.add("ligne2ainterexpo");
            linesName.add("ligne2norelan");
            linesName.add("ligne3");
            linesName.add("ligne4");

            //endDev

            ArrayList<ArrayList<LatLng>> markersList = new ArrayList<>();

            displayMarkers(linesName);

            //Adding markers to eachline

            for (int i = 0; i < lines.size(); i++) {

                BusLign.putStopsOnMap(lines.get(i).getstops(), mMap);


            }
        }catch(Exception e){}


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

    private void displayMarkers(ArrayList<String> linesToDisplay)
    {
        for(int i=0;i<linesToDisplay.size();i++)
        {
            try {

                InputStream ins = getResources().openRawResource(
                        getResources().getIdentifier(linesToDisplay.get(i),
                                "raw", getPackageName()));

                KmlLayer layer = new KmlLayer(mMap, ins, getApplicationContext());
                System.out.println("ITEMS:"+ layer.getContainers().iterator().next().getProperties().iterator().next());

                layer.addLayerToMap();
                KmlPlacemark k;
                Iterable<KmlPlacemark> markers;
                KmlLineString geo=getMarkers(layer.getContainers().iterator().next());
                // System.out.println("INFO"+geo.getGeometryObject().size());

                //  KmlLineString geo2 = (KmlLineString) layer.getContainers().iterator().next().getPlacemarks().iterator().next().getGeometry();


                ArrayList<LatLng> pointsList = geo.getGeometryObject();





                float zoomLevel = 12.25f;
                LatLng centerOfBourg = new LatLng(46.202181, 5.237056);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerOfBourg,zoomLevel));



                ArrayList<BusStop> firstDirectionStops =  new ArrayList<>();
                ArrayList<BusStop> secondDirectionStops =  new ArrayList<>();


                //Construction données test


                String dateExemple ="09:15";
                String dateExemple2 ="17:45";

                DateTimeFormatter format = DateTimeFormat.forPattern("HH:mm");
                DateTime extractedTime = format.parseDateTime(dateExemple);
                DateTime extractedTime2 = format.parseDateTime(dateExemple2);
                //System.out.println(extractedTime.getHourOfDay()+" : "+ extractedTime.getMinuteOfHour());

                ArrayList<Schedule> scheduleList = new ArrayList<>();
                scheduleList.add(new Schedule(extractedTime,false));
                scheduleList.add(new Schedule(extractedTime2,false));

                //firstDirectionStops.add("Ligne1",1,"red",1,"ligne1.kml",new BusStop(new LatLng(46.2073652781729,5.227577090263367),"gare",scheduleList));
               // secondDirectionStops.add(new BusStop(new LatLng(46.20518602822019,5.227196216583252),"gare2",scheduleList));

                //Fin construction






                //BusLign line = new BusLign(linesToDisplay.get(i),firstDirectionStops);

               // lines.add(line);




            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
