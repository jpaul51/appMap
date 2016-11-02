package com.example.iem.mapapp.Model;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by iem on 21/10/2016.
 */

public class BusLign   {

    String name;
    ArrayList<BusStop> firstDirectionStops;
    ArrayList<BusStop> secondDirectionStops;
    boolean firstDirection;
    Color color;


    public BusLign(ArrayList<BusStop> firstDirectionStops, ArrayList<BusStop> secondDirectionStops,Color color) {
        this.firstDirectionStops = firstDirectionStops;
        this.secondDirectionStops = secondDirectionStops;
        this.color=color;
    }
}
