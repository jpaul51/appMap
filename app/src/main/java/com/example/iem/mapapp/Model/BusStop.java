package com.example.iem.mapapp.Model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by iem on 21/10/2016.
 */

public class BusStop {

    LatLng position;
    String name;
    String scheduleFile;
    ArrayList<Schedule> scheduleList;

    public BusStop(LatLng position, String name, String scheduleFile) {
        this.position = position;
        this.name = name;
        this.scheduleFile = scheduleFile;
    }

    public BusStop(LatLng position, String name, ArrayList<Schedule> schedule) {
        this.position = position;
        this.name = name;
        this.scheduleList = schedule;
    }

    public String getScheduleFile() {
        return scheduleFile;
    }

    public void setScheduleFile(String scheduleFile) {
        this.scheduleFile = scheduleFile;
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

    public ArrayList<Schedule> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(ArrayList<Schedule> schedule) {
        this.scheduleList = scheduleList;
    }
}
