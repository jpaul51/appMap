package com.example.iem.mapapp;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

/**
 * Created by Jonas on 28/11/2016.
 */

public class MultiLineString extends com.vividsolutions.jts.geom.MultiLineString {
    public MultiLineString(LineString[] lineStrings, GeometryFactory factory) {
        super(lineStrings, factory);
    }
    public MultiLineString(){
        super(new LineString[] {},new GeometryFactory());

    }
}
