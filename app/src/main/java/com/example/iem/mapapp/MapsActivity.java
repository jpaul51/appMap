package com.example.iem.mapapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.ListView;

import com.example.iem.mapapp.Model.BusLign;
import com.example.iem.mapapp.Model.BusStop;
import com.example.iem.mapapp.Model.Schedule;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MapsActivity extends AbstractMapActivity   implements
        OnMapReadyCallback, OnInfoWindowClickListener {

    private GoogleMap mMap;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Boolean needsInit=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (readyToGo()) {
            setContentView(R.layout.activity_maps);

            MapFragment mapFrag=
                    (MapFragment)getFragmentManager().findFragmentById(R.id.map);

            if (savedInstanceState == null) {
                needsInit=true;
            }

            mapFrag.getMapAsync(this);
        }
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
       ArrayList<BusLign> lines = new ArrayList<>();

        mMap = googleMap;


        // Add a marker in Sydney and move the camera

        LatLng bourgEnBresse = new LatLng(46.2052, 5.2255);
        mMap.addMarker(new MarkerOptions().position(bourgEnBresse).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bourgEnBresse));
        checkPermission();

        ArrayList<String> linesName= new ArrayList<>();
        //dev
        linesName.add("ligne1");
      //  linesName.add("ligne21");
        linesName.add("ligne2ainterexpo");
        linesName.add("ligne2norelan");
        linesName.add("ligne3");
        linesName.add("ligne4");

        //endDev

        ArrayList<ArrayList<LatLng>> markersList = new ArrayList<>();

        for(int i=0;i<linesName.size();i++)
        {
            try {

                InputStream ins = getResources().openRawResource(
                        getResources().getIdentifier(linesName.get(i),
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

           // mMap.addMarker(new MarkerOptions().position(stopList.get(0)).title("test").snippet("coucou"));
            float zoomLevel = 12.0f;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointsList.get(0),zoomLevel));




                ArrayList<BusStop> firstDirectionStops =  new ArrayList<>();
                ArrayList<BusStop> secondDirectionStops =  new ArrayList<>();


                //Construction données test


                String dateExemple ="09:15";
                String dateExemple2 ="17:45";

                DateTimeFormatter format = DateTimeFormat.forPattern("HH:mm");
                DateTime extractedTime = format.parseDateTime(dateExemple);
                DateTime extractedTime2 = format.parseDateTime(dateExemple2);

                ArrayList<Schedule> scheduleList = new ArrayList<>();
                scheduleList.add(new Schedule(extractedTime,false));
                scheduleList.add(new Schedule(extractedTime2,false));

                firstDirectionStops.add(new BusStop(new LatLng(46.2073652781729,5.227577090263367),"gare",scheduleList));
                secondDirectionStops.add(new BusStop(new LatLng(46.20518602822019,5.227196216583252),"gare2",scheduleList));

                //Fin construction






                BusLign line = new BusLign(linesName.get(i),firstDirectionStops,secondDirectionStops);

                lines.add(line);




            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Adding markers to eachline

        for(int i=0; i < lines.size(); i++){

            BusLign.putStopsOnMap(lines.get(i).getFirstDirectionStops(),mMap);
            BusLign.putStopsOnMap(lines.get(i).getSecondDirectionStops(),mMap);



        }



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);



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
            // Show rationale and request permission.
            Log.d("Erreur Permission","Permission de localisation");
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

    }
}
