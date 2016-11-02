package com.example.iem.mapapp.Model;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;

/**
 * Created by iem on 21/10/2016.
 */

public class BusLign   {

    String name;
    ArrayList<BusStop> firstDirectionStops;
    ArrayList<BusStop> secondDirectionStops;
    boolean firstDirection;


    public BusLign(String name,ArrayList<BusStop> firstDirectionStops, ArrayList<BusStop> secondDirectionStops) {
        this.name=name;
        this.firstDirectionStops = firstDirectionStops;
        this.secondDirectionStops = secondDirectionStops;
    }




    static public void putStopsOnMap(ArrayList<BusStop> stops, GoogleMap mMap){
        for (int j=0;j<stops.size();j++) {
            LatLng position = stops.get(j).getPosition();
            String title = stops.get(j).getName();

           // String firstSchedule = String.valueOf(stops.get(j).getScheduleList().get(0).getSchedule().getHourOfDay())+":"+String.valueOf(stops.get(j).getScheduleList().get(0).getSchedule().getMinuteOfHour());

            MarkerOptions opt = new MarkerOptions();
            opt.position(position);
            opt.title(title);
            String oldSnippet="";

            LocalDateTime localDateTime = new LocalDateTime();


            for(int w=0;w<stops.get(j).getScheduleList().size();w++){
                if (opt.getSnippet()!= null)
                 oldSnippet= opt.getSnippet();
                
                if(stops.get(j).getScheduleList().get(w).getSchedule().getHourOfDay() >= localDateTime.getHourOfDay()  )
                    if(stops.get(j).getScheduleList().get(w).getSchedule().getMinuteOfHour() >= localDateTime.getMinuteOfHour())
                        opt.snippet(oldSnippet+String.valueOf(stops.get(j).getScheduleList().get(w).getSchedule().getHourOfDay())+":"+String.valueOf(stops.get(j).getScheduleList().get(w).getSchedule().getMinuteOfHour())+"\n");
            }

            mMap.addMarker(opt);
        }


    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFirstDirection() {
        return firstDirection;
    }

    public void setFirstDirection(boolean firstDirection) {
        this.firstDirection = firstDirection;
    }

    public ArrayList<BusStop> getSecondDirectionStops() {
        return secondDirectionStops;
    }

    public void setSecondDirectionStops(ArrayList<BusStop> secondDirectionStops) {
        this.secondDirectionStops = secondDirectionStops;
    }

    public ArrayList<BusStop> getFirstDirectionStops() {
        return firstDirectionStops;
    }

    public void setFirstDirectionStops(ArrayList<BusStop> firstDirectionStops) {
        this.firstDirectionStops = firstDirectionStops;
    }


}
