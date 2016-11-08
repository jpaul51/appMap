package com.example.iem.mapapp.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;

/**
 * Created by iem on 21/10/2016.
 */

public class BusLign   {

    @JsonProperty("id")
    int id;
    @JsonProperty("label")
    String label;
    @JsonProperty("number")
    int number;
    @JsonProperty("color")
    String color;
    @JsonProperty("order")
    int order;
    @JsonProperty("traceFile")
    String traceFile;
    @JsonProperty("stops")
    ArrayList<BusStop> stops;



    public BusLign(){

    }

    public BusLign(int id, String label, int number, String color, int order, String traceFile) {
        this.id = id;
        this.label = label;
        this.number = number;
        this.color = color;
        this.order = order;
        this.traceFile = traceFile;
    }


    public BusLign(String label, int number, String color, int order, String traceFile, ArrayList<BusStop> stops) {
        this.label = label;
        this.number = number;
        this.color = color;
        this.order = order;
        this.traceFile = traceFile;
        this.stops = stops;
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

                if(stops.get(j).getScheduleList().get(w).getSchedule().getHourOfDay() == localDateTime.getHourOfDay()  ) {
                    if (stops.get(j).getScheduleList().get(w).getSchedule().getMinuteOfHour() > localDateTime.getMinuteOfHour()) {
                        opt.snippet(oldSnippet + String.valueOf(stops.get(j).getScheduleList().get(w).getSchedule().getHourOfDay()) + ":" + String.valueOf(stops.get(j).getScheduleList().get(w).getSchedule().getMinuteOfHour()) + "\n");
                    }

                }
                else if(stops.get(j).getScheduleList().get(w).getSchedule().getHourOfDay() > localDateTime.getHourOfDay()  ) {
                    opt.snippet(oldSnippet + String.valueOf(stops.get(j).getScheduleList().get(w).getSchedule().getHourOfDay()) + ":" + String.valueOf(stops.get(j).getScheduleList().get(w).getSchedule().getMinuteOfHour()) + "\n");
                }
            }

            mMap.addMarker(opt);
        }


    }




    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    

    public ArrayList<BusStop> getstops() {
        return stops;
    }

    public void setstops(ArrayList<BusStop> stops) {
        this.stops = stops;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTraceFile() {
        return traceFile;
    }

    public void setTraceFile(String traceFile) {
        this.traceFile = traceFile;
    }
}
