package com.example.iem.mapapp.model;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;
/**
 * Created by iem on 28/02/2017.
 */

public class Schedule implements Serializable{

    long id;

    List<DateTime> schedules;
    List<String> constraints;
    String way;
    Line line;//Contains multiLineString, to remove
    Boolean schoolPeriod=true;

    public Schedule() {

        super();

    }

    public Schedule(List<DateTime> schedules, List<String> constraints, String way, Line line, Boolean schoolPeriod) {

        super();

        this.schedules = schedules;

        this.constraints = constraints;

        this.way = way;

        this.line = line;

        this.schoolPeriod = schoolPeriod;

    }

    public Schedule(List<DateTime> schedules, String way, Line line, Boolean schoolPeriod) {

        super();

        this.schedules = schedules;

        this.way = way;

        this.line = line;

        this.schoolPeriod = schoolPeriod;

    }

    public Schedule(String way, Line line, Boolean schoolPeriod) {

        super();

        this.way = way;

        this.line = line;

        this.schoolPeriod = schoolPeriod;

    }

    public Schedule(long id, List<DateTime> schedules, String way, Line line, Boolean schoolPeriod) {

        super();

        this.id = id;

        this.schedules = schedules;

        this.way = way;

        this.line = line;

        this.schoolPeriod = schoolPeriod;

    }

    public Schedule(long id, List<DateTime> schedules, String way, Line line) {

        super();

        this.id = id;

        this.schedules = schedules;

        this.way = way;

        this.line = line;

    }

    public List<String> getConstraints() {

        return constraints;

    }

    public void setConstraints(List<String> constraints) {

        this.constraints = constraints;

    }



    public Boolean getSchoolPeriod() {

        return schoolPeriod;

    }

    public void setSchoolPeriod(Boolean schoolPeriod) {

        this.schoolPeriod = schoolPeriod;

    }

    public long getId() {

        return id;

    }

    public void setId(long id) {

        this.id = id;

    }

    public List<DateTime> getSchedules() {

        return schedules;

    }

    public void setSchedules(List<DateTime> schedules) {

        this.schedules = schedules;

    }

    public String getway() {

        return way;

    }

    public void setway(String way) {

        this.way = way;

    }



    public Line getLine() {

        return line;

    }

    public void setLine(Line line) {

        this.line = line;

    }
}
