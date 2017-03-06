package com.example.iem.mapapp.model;



import com.vividsolutions.jts.geom.MultiLineString;

import java.io.Serializable;

/**
 * Created by iem on 28/02/2017.
 */

public class Line implements Serializable{

    Long id;
    String label;
    String color;

    MultiLineString lines=null;
    String kml_path;

    public Line(){

        lines=null;
    }
    public Line(long id)
    {
        this.id=id;
    }
    public Line(Long id, String label, String color, String kml_path) {
        super();
        this.id = id;
        this.label = label;
        this.color = color;
        this.kml_path = kml_path;
        lines=null;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public MultiLineString getLines() {
        return lines;
    }
    public void setLines(MultiLineString lines) {
        this.lines = lines;
    }

}
