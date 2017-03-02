package com.example.iem.mapapp.model;

import org.geojson.Point;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by iem on 28/02/2017.
 */

public class Stop implements Serializable {

    long id;
    String label;
    Point point;
    Boolean firstDirection;
    Boolean secondDirection;

    HashMap<Long,HashMap<String,Integer>> orderInLineByWay;
    List<Line> lines;
    List<Long> neighboursId;
    List<Schedule> schedules;

    public Stop() {}



    public Stop(Stop stop) {
        this.setId(stop.getId());
        this.setLabel(stop.getLabel());
        this.setLines(stop.getLines());
        this.setNeighboursId(stop.getNeighboursId());
        this.setOrderInLineByWay(stop.getOrderInLineByWay());
        this.setPoint(stop.getPoint());
        this.setSchedules(stop.getSchedules());
    }

    public Stop(long id) {
        this.id=id;
    }



    public Stop(long id, String label, Point point, Boolean firstDirection, Boolean secondDirection, List<Line> lines,
                List<Long> neighbours, List<Schedule> schedules) {
        super();
        this.id = id;
        this.label = label;
        this.point = point;
        this.firstDirection = firstDirection;
        this.secondDirection = secondDirection;
        this.lines = lines;
        this.neighboursId = neighbours;
        this.schedules = schedules;
    }

    public Stop(long id, String label, Point point, Boolean firstDirection, Boolean secondDirection,
                HashMap<Long, HashMap<String,Integer>> orderInLineByWay, List<Line> lines, List<Long> neighbours,
                List<Schedule> schedules) {
        super();
        this.id = id;
        this.label = label;
        this.point = point;
        this.firstDirection = firstDirection;
        this.secondDirection = secondDirection;
        this.orderInLineByWay = orderInLineByWay;
        this.lines = lines;
        this.neighboursId = neighbours;
        this.schedules = schedules;
    }

    public Stop(long id, String label, Point point, List<Line> lines, List<Long> neighbours,
                List<Schedule> schedules) {
        super();
        this.id = id;
        this.label = label;
        this.point = point;
        this.lines = lines;
        this.neighboursId = neighbours;
        this.schedules = schedules;
    }

    public Stop(long id, String label, Point point, List<Line> lines, List<Long> neighbours) {
        super();
        this.id = id;
        this.label = label;
        this.point = point;
        this.lines = lines;
        this.neighboursId = neighbours;
    }

    public Stop(long id, String label, Point point, List<Line> lines) {
        super();
        this.id = id;
        this.label = label;
        this.point = point;
        this.lines = lines;
    }

    public Stop(long id, String label, Point point) {
        super();
        this.id = id;
        this.label = label;
        this.point = point;
    }

    public Boolean getFirstDirection() {
        return firstDirection;
    }

    public void setFirstDirection(Boolean firstDirection) {
        this.firstDirection = firstDirection;
    }

    public Boolean getSecondDirection() {
        return secondDirection;
    }

    public void setSecondDirection(Boolean secondDirection) {
        this.secondDirection = secondDirection;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HashMap<Long, HashMap<String, Integer>> getOrderInLineByWay() {
        return orderInLineByWay;
    }

    public void setOrderInLineByWay(HashMap<Long, HashMap<String, Integer>> orderInLineByWay) {
        this.orderInLineByWay = orderInLineByWay;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public List<Long> getNeighboursId() {
        return neighboursId;
    }

    public void setNeighboursId(List<Long> neighboursId) {
        this.neighboursId = neighboursId;
    }

}
