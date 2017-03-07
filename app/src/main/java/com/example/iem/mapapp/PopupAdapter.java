package com.example.iem.mapapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.iem.mapapp.activity.MapWrapperLayout;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iem on 02/11/2016.
 */


class PopupAdapter implements InfoWindowAdapter {
    private View popup=null;
    private LayoutInflater inflater=null;
   private HashMap<Long,HashMap<String,List<DateTime>>> scheduleByWayByLine = new HashMap<>();
    public MapWrapperLayout wrapper;

    PopupAdapter(LayoutInflater inflater) {
        this.inflater=inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(Marker marker) {
        if (popup == null) {
            popup=inflater.inflate(R.layout.custom_popup, null);
        }

        TextView tv=(TextView)popup.findViewById(R.id.title);

        tv.setText(marker.getTitle());

        LinearLayout linesContainer = (LinearLayout) popup.findViewById(R.id.lines_container);

        for(Map.Entry<Long,HashMap<String,List<DateTime>>> lineEntry : scheduleByWayByLine.entrySet()) {
            Button lineButton = new Button(inflater.getContext());
            lineButton.setText(lineEntry.getKey().toString());
           // lineButton.setBackgroundResource(R.drawable.roundedbutton);
            //lineButton.setTextColor(Color.parseColor("#000000"));

            lineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("button click");
                }
            });
            //lineButton.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
            linesContainer.addView(lineButton);
        }
        //linesContainer.addView(lineButton);
        //tv=(TextView)popup.findViewById(R.id.snippet);
//        tv.setText(marker.getSnippet());

        wrapper.setMarkerWithInfoWindow(marker,popup);

        return(popup);
    }

    public HashMap<Long, HashMap<String, List<DateTime>>> getScheduleByWayByLine() {
        return scheduleByWayByLine;
    }

    public void setScheduleByWayByLine(HashMap<Long, HashMap<String, List<DateTime>>> scheduleByWayByLine) {
        this.scheduleByWayByLine = scheduleByWayByLine;
    }
}