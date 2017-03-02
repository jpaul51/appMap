package com.example.iem.mapapp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by iem on 28/02/2017.
 */

public class LinesAndStops implements Serializable {

        private List<Line> lines;
        private List<Stop> stops;

        public LinesAndStops(){

        }
    public List<Line> getLines() {
            return lines;
        }
    public void setLines(List<Line> lines) {
            this.lines = lines;
        }
    public List<Stop> getStops() {
            return stops;
        }
    public void setStops(List<Stop> stops) {
            this.stops = stops;
        }
}
