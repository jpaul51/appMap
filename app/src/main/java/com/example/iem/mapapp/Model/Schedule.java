package com.example.iem.mapapp.Model;

import org.joda.time.DateTime;

/**
 * Created by iem on 02/11/2016.
 */

public class Schedule {

    DateTime schedule;
    boolean isVacation;


    public Schedule(){}

    public Schedule(DateTime schedule, boolean isVacation) {
        this.schedule = schedule;
        this.isVacation = isVacation;

    }


    public DateTime getSchedule() {
        return schedule;
    }

    public void setSchedule(DateTime schedule) {
        this.schedule = schedule;
    }

    public boolean isVacation() {
        return isVacation;
    }

    public void setVacation(boolean vacation) {
        isVacation = vacation;
    }
}
