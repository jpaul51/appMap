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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        return id.equals(line.id);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (lines != null ? lines.hashCode() : 0);
        result = 31 * result + (kml_path != null ? kml_path.hashCode() : 0);
        return result;
    }
}
