package com.example.iem.mapapp.Model;

import java.util.Date;

/**
 * Created by iem on 02/11/2016.
 */

public class Schedule {

    Date schedule;
    boolean isVacation;


    public Schedule(){}

    public Schedule(Date schedule, boolean isVacation) {
        this.schedule = schedule;
        this.isVacation = isVacation;
    }


    public Date getSchedule() {
        return schedule;
    }

    public void setSchedule(Date schedule) {
        this.schedule = schedule;
    }

    public boolean isVacation() {
        return isVacation;
    }

    public void setVacation(boolean vacation) {
        isVacation = vacation;
    }
}
