package com.example.iem.mapapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iem.mapapp.activity.DetailStop;
import com.example.iem.mapapp.activity.MapWrapperLayout;
import com.example.iem.mapapp.callApi.ApiRequest;
import com.example.iem.mapapp.interfaces.CallbackButtonClick;
import com.example.iem.mapapp.listener.ButtonCliqueListener;
import com.example.iem.mapapp.model.Line;
import com.example.iem.mapapp.model.LinesAndStops;
import com.example.iem.mapapp.model.Schedule;
import com.example.iem.mapapp.model.Stop;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.vividsolutions.jts.geom.LineString;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.iem.mapapp.utils.Constante.RC_SIGN_IN;


public class MapsActivity extends AbstractMapActivity
        implements OnMapReadyCallback,OnInfoWindowClickListener,
        CallbackButtonClick, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

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

    private LinesAndStops linesAndStops;
    private ApiRequest apiRequest;
    private Boolean needsInit = false;
    private List<ButtonCliqueListener> listeners;
    private HashMap<Integer,List<Polyline>> displayedLines;
    private HashMap<Integer,List<Polyline>> displayedLinesOnRoad;
    private HashMap<Stop,Marker> displayedMarkers;
    private SignInButton signInButtonGoogle;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInAccount account;
    private TextView textViewInformation;
    private boolean isSignIn = false;
    private boolean selectLine = true;
    private String messageIndicatif;
    private Button signOut;

    private boolean networkFinished=false;


    private HashMap<Marker,HashMap<Long,HashMap<String,List<DateTime>>>> infowindowContentByMarker;
    private HashMap<Marker,Stop> stopsByMarker;
    private AutoCompleteTextView startStopView;
    private AutoCompleteTextView endStopView;
    private ArrayAdapter<String> adapterAutoComplete;
    private ArrayList<String> listNameStop;
    private boolean autoCompleteLoad = false;

    private HashMap<Marker,Stop> stopsByMarkerRoad;

    private  MapWrapperLayout wrapper;
    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (readyToGo()) {
            setContentView(R.layout.activity_main_v2);

            initUIComponent();
            updateUI(false);




            setSupportActionBar(toolbarMain);

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this , this )
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            signInButtonGoogle.setSize(SignInButton.SIZE_ICON_ONLY);
            signInButtonGoogle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isSignIn){
                        signInWithGoogle();


                    }
                }
            });

            signOut.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isSignIn){
                        signOut();
                    }
                }
            });




            /*exemple d'implementation
            String colorDefault = "#5500FF";
            ((GradientDrawable)btLigne1.getBackground()).setColor(Color.parseColor(colorDefault));*/
            listeners = new ArrayList<>();

            displayedLines = new HashMap<>();
            displayedMarkers = new HashMap<>();
            infowindowContentByMarker = new HashMap<>();
            stopsByMarkerRoad = new HashMap<>();
            stopsByMarkerRoad = new HashMap<>();
            stopsByMarker = new HashMap<>();
            displayedLinesOnRoad = new HashMap<>();

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
                            selectLine = true;
                            updateUI(isSignIn);
                            break;
                        case R.id.tab_direction:
                            toolbarChoixLigne.setVisibility(View.GONE);
                            toolbarRechercheTrajet.setVisibility(View.VISIBLE);
                            selectLine = false;
                            updateUI(isSignIn);
                            loadAutoCompletion();
                            break;
                    }

                }
            });


            apiRequest = ApiRequest.getInstance();

            if (savedInstanceState == null) {
                needsInit = true;
            }


            ImageButton btnSearch = (ImageButton) findViewById(R.id.btnSearch);
            btnSearch.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                    if(connectivityManager.getActiveNetworkInfo().isConnected())
                        findShortestWay();
                    else
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.connectionError),Toast.LENGTH_LONG);
                }
            });

            mapFragment.getMapAsync(this);
        }
    }

    private void  loadAutoCompletion(){
        if(!autoCompleteLoad && linesAndStops != null){
            startStopView = (AutoCompleteTextView) findViewById(R.id.tv_entrerNomLigneDepart);
            endStopView = (AutoCompleteTextView) findViewById(R.id.tv_entrerNomLigneArriver);
            startStopView.setThreshold(1);
            endStopView.setThreshold(1);
            autoCompleteLoad = true;
            listNameStop = new ArrayList<>();
            adapterAutoComplete = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,listNameStop);
            List<Stop> stopList = linesAndStops.getStops();
            for (int i = 0; i < stopList.size(); i++){
                listNameStop.add(stopList.get(i).getLabel());
            }
            startStopView.setAdapter(adapterAutoComplete);
            endStopView.setAdapter(adapterAutoComplete);
            ((BaseAdapter) startStopView.getAdapter()).notifyDataSetChanged();
            ((BaseAdapter) endStopView.getAdapter()).notifyDataSetChanged();
        }
    }



    private void findShortestWay()
    {

        final TextView firstStop = (TextView) findViewById(R.id.tv_entrerNomLigneDepart);
        final TextView endStop = (TextView) findViewById(R.id.tv_entrerNomLigneArriver);



        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String start = firstStop.getText().toString().trim().replace(" ","+");
                    String end = endStop.getText().toString().trim().replace(" ","+");
                   final  LinesAndStops linesAndStops = (LinesAndStops) apiRequest.getShortestWaybetween(start,end);

                    if(linesAndStops == null &&(linesAndStops.getLines().isEmpty() || linesAndStops.getStops().isEmpty()))
                    {
                        Toast.makeText(MapsActivity.this,"These stops couldn't be found. Sorry",Toast.LENGTH_LONG);
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadRoad(linesAndStops);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loadRoad(LinesAndStops linesAndStops)
    {

        for(ButtonCliqueListener listener : listeners)
        {
            if(listener.isChecked())
               listener.toogleUICheck();
        }

        mMap.clear();
        for(Stop s : linesAndStops.getStops())
       {
            MarkerOptions opt = new MarkerOptions()
                    .position(new LatLng(s.getPoint().getCoordinates().getLongitude(), s.getPoint().getCoordinates().getLatitude()))
                    .title(s.getLabel());

           System.out.println(mMap);
           System.out.println(opt.getTitle());
           System.out.println(s.getLabel());
           stopsByMarkerRoad.put(mMap.addMarker(opt),s);

           LineString aLine=null;
           ArrayList<Polyline> polyLines = new ArrayList<>();
           for(Line line : linesAndStops.getLines()) {
               for (int i = 0; i < line.getLines().getNumGeometries(); i++) {
                   aLine = (LineString) line.getLines().getGeometryN(i);
                   final PolylineOptions polyOptions = new PolylineOptions().color(Color.parseColor(line.getColor()));
                   for (int coordinatesIndex = 0; coordinatesIndex < aLine.getCoordinates().length; coordinatesIndex++) {
                       polyOptions.add(new LatLng(aLine.getCoordinates()[coordinatesIndex].y, aLine.getCoordinates()[coordinatesIndex].x));
                   }
                  polyLines.add(mMap.addPolyline(polyOptions));
                   displayedLinesOnRoad.put(i+1,polyLines);
               }

           }

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

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //Button sign in google

        signInButtonGoogle = (SignInButton) findViewById(R.id.google_sign_in_button);

        textViewInformation = (TextView) findViewById(R.id.textInformation);

        signOut = (Button) findViewById(R.id.bt_disconnect);

    }

//region Google authentification
    private void signInWithGoogle() {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        System.out.println(mGoogleApiClient.isConnected());
        if(mGoogleApiClient.isConnected()){
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }
        updateUI(false);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            mGoogleApiClient.connect();
            account = result.getSignInAccount();
            updateUI(true);
        } else {
            updateUI(false);
        }
    }

    private void updateUI(boolean signedIn) {
        if(selectLine){
            messageIndicatif = getResources().getString(R.string.textSelectionLigne);
        }else{
            messageIndicatif = getResources().getString(R.string.textSelectItineraire);
        }
        if (signedIn) {
            signInButtonGoogle.setVisibility(View.GONE);
            isSignIn = true;
            messageIndicatif = account.getGivenName() + " " + messageIndicatif.toLowerCase();
            signOut.setVisibility(View.VISIBLE);
        } else {
            signOut.setVisibility(View.GONE);
            isSignIn = false;
            signInButtonGoogle.setVisibility(View.VISIBLE);

        }
        textViewInformation.setText(messageIndicatif);
    }

//endregion


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    Thread loadNetwork = new Thread(new Runnable() {
        @Override
        public void run() {
            String lines=null;

            linesAndStops=null;

            lines = apiRequest.getlinesAndStops();

            ObjectMapper mapper = CustomObjectMapper.JtsObjectMapper();
            if(mapper != null && lines != null) {
                try {
                    linesAndStops  = mapper.readValue(lines,LinesAndStops.class);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            networkFinished=true;


        }
    });





    @Override
    public void onMapReady(GoogleMap googleMap) {
        loadNetwork.start();
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        LatLng bourgEnBresse = new LatLng(46.2052, 5.2255);
         wrapper = (MapWrapperLayout) findViewById(R.id.wrapper);
        wrapper.init(mMap,getPixelsFromDp(this, 39 + 20));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(bourgEnBresse.latitude, bourgEnBresse.longitude), 12.0f));

        checkPermission();
    }



    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
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

        mMap.setOnInfoWindowClickListener(this);
    }

    private void moveButtonLocation(){

        View view = mapFragment.getView();
        if (mapFragment != null && view.findViewById(1) != null) {
            // Get the button view
            View locationButton = ((View) view.findViewById(1).getParent()).findViewById(2);
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
    }


private void removePolylynes(HashMap<Integer,List<Polyline>> line)
{
    if(line != null) {
        for (Map.Entry<Integer, List<Polyline>> lines : line.entrySet()) {
            for (Polyline aLine : lines.getValue()) {
                aLine.remove();
            }
        }
    }
}

    @Override
    public void onInfoWindowClick(Marker marker) {
        System.out.println("CLICK");
        int u =1;
        u++;
    }

    @Override
    public void displayLine(int number) {

        for(Map.Entry<Marker,Stop> markersToRemove : stopsByMarkerRoad.entrySet())
        {
            markersToRemove.getKey().remove();

        }
      removePolylynes(displayedLinesOnRoad);
    stopsByMarker = new HashMap<>();


        if(linesAndStops != null){


           // Line line = linesAndStops.getLines().get(number);
            Line line = new Line();
            for(Line aLine : linesAndStops.getLines())
            {
                if(aLine.getId()==number+1)
                    line=aLine;
            }

            System.out.println(line.getId());
            LineString aLine=null;
            ArrayList<Polyline> polyLines = new ArrayList<>();
            for (int i=0; i < line.getLines().getNumGeometries();i++ ) {
                aLine = (LineString) line.getLines().getGeometryN(i);
                final PolylineOptions polyOptions = new PolylineOptions().color(Color.parseColor(line.getColor()))
                        ;
                for (int coordinatesIndex = 0; coordinatesIndex < aLine.getCoordinates().length; coordinatesIndex++) {
                    polyOptions.add(new LatLng(aLine.getCoordinates()[coordinatesIndex].y,aLine.getCoordinates()[coordinatesIndex].x));
                }
                polyLines.add(mMap.addPolyline(polyOptions));

            }
            displayedLines.put(number+1,polyLines);


            ArrayList<Stop> stops = (ArrayList<Stop>) linesAndStops.getStops();

            for(Stop s : stops)
            {

              if(s.getLines().contains((long)number+1)) {
                  MarkerOptions opt = new MarkerOptions()
                          .position(new LatLng(s.getPoint().getCoordinates().getLongitude(), s.getPoint().getCoordinates().getLatitude()))
                          .title(s.getLabel());

                  String oldSnippet = "";

                  HashMap<Long,HashMap<String,List<DateTime>>> scheduleByWayByLine = new HashMap<>();
                for(Schedule schedule : s.getSchedules())
                {
                    HashMap<String,List<DateTime>> scheduleByWay = scheduleByWayByLine.get(schedule.getLine());
                    if(scheduleByWay==null) {
                        scheduleByWay = new HashMap<>();
                        scheduleByWayByLine.put(schedule.getLine(),scheduleByWay);
                    }
                    scheduleByWay.put(schedule.getway(),schedule.getSchedules());

                }


                if(!displayedMarkers.containsKey(s)) {
                    Marker marker = mMap.addMarker(opt);
                    stopsByMarker.put(marker,s);
                    infowindowContentByMarker.put(marker,scheduleByWayByLine);
                    displayedMarkers.put(s, marker);
                }

              }
            }

        }
    }

    @Override
    public void removeLine(int number) {
    number+=1;


        Iterator<Polyline> polylineIterator = displayedLines.get(number).iterator();

        while(polylineIterator.hasNext())
        {
            Polyline aLine = polylineIterator.next();
            aLine.remove();
            polylineIterator.remove();

        }
        System.out.println("REMOVE: "+number);
        displayedLines.remove(number);

        Iterator<Map.Entry<Stop,Marker>> markerIterator = displayedMarkers.entrySet().iterator();

        while(markerIterator.hasNext())
        {
            Map.Entry<Stop,Marker> markerEntry = markerIterator.next();
            Stop s = markerEntry.getKey();
            if(s.getLines().contains((long)number))
            {
                System.out.println("Stop belongs to "+s.getLines()+", deleting line "+(number));
                System.out.println("DisplayedLines: "+displayedLines.keySet());
                boolean markerShouldBeRemoved=true;
                for(int i=0;i<displayedLines.size();i++)
                {
                  //  System.out.print(s.getLines().contains((long)i+1) +" ");
                        markerShouldBeRemoved &= !s.getLines().contains((long)i);
                }
                System.out.println("Should be removed: "+markerShouldBeRemoved);
                if(markerShouldBeRemoved) {
                    Marker markerWeWantToRemove = markerEntry.getValue();
                    markerWeWantToRemove.remove();
                    markerIterator.remove();
                }

            }
        }

    }


    @Override
    public boolean onMarkerClick(Marker marker) {

    try {
        Intent intent = new Intent(MapsActivity.this, DetailStop.class);

        intent.putExtra("stopData", infowindowContentByMarker.get(marker));
        if (stopsByMarker.get(marker) != null)
            intent.putExtra("stopObject", stopsByMarker.get(marker));
        else
            intent.putExtra("stopObject", stopsByMarkerRoad.get(marker));
        startActivity(intent);
    }
    catch(Exception e ){
        Toast.makeText(MapsActivity.this,getResources().getString(R.string.error),Toast.LENGTH_SHORT);

    }
        return true;
    }
}
