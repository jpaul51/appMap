package com.example.iem.mapapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.kml.KmlContainer;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlLineString;
import com.google.maps.android.kml.KmlPlacemark;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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


                ArrayList<LatLng> stopList = geo.getGeometryObject();

            mMap.addMarker(new MarkerOptions().position(stopList.get(0)).title("test").snippet("coucou"));
            float zoomLevel = 12.0f;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stopList.get(0),zoomLevel));


                mMap.addMarker(new MarkerOptions().position(stopList.get(0)).title("test").snippet("coucou"));





            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    }


}
