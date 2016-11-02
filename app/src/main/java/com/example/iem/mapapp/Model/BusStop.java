package com.example.iem.mapapp.Model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by iem on 21/10/2016.
 */

public class BusStop {

    LatLng position;
    String name;
    ArrayList<Schedule> schedule;


    public BusStop(LatLng position, String name, ArrayList<Schedule> schedule) {
        this.position = position;
        this.name = name;
        this.schedule = schedule;
    }


    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Schedule> getSchedule() {
        return schedule;
    }

    public void setSchedule(ArrayList<Schedule> schedule) {
        this.schedule = schedule;
    }
}
