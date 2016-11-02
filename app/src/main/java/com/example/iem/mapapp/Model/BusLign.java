package com.example.iem.mapapp.Model;

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








}
