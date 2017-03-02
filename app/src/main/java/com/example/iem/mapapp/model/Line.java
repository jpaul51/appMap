package com.example.iem.mapapp.model;

import org.geojson.MultiLineString;

import java.io.Serializable;

/**
 * Created by iem on 28/02/2017.
 */

public class Line implements Serializable{

    Long id;
    String name;
    String color;

    MultiLineString lines=null;
    String pathFile;

    public Line(){

        lines=null;
    }
    public Line(long id)
    {
        this.id=id;
    }
    public Line(Long id, String name, String color, String pathFile) {
        super();
        this.id = id;
        this.name = name;
        this.color = color;
        this.pathFile = pathFile;
        lines=null;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
    public String getPathFile() {
        return pathFile;
    }
    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }
}
