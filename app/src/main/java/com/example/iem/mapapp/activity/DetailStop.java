package com.example.iem.mapapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iem.mapapp.R;
import com.example.iem.mapapp.model.Stop;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailStop extends AppCompatActivity {

    private HashMap<Long,HashMap<String,List<DateTime>>> scheduleByWayByLine = new HashMap<>();
    private Stop stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_stop);



        Intent intent = getIntent();
        scheduleByWayByLine = (HashMap<Long, HashMap<String, List<DateTime>>>) intent.getSerializableExtra("stopData");
        stop = (Stop) intent.getSerializableExtra("stopObject");
        LinearLayout linesContainer = (LinearLayout) findViewById(R.id.lines_container);
        final LinearLayout waysContainer = (LinearLayout) findViewById(R.id.way_container);
        final LinearLayout  scheduleContainer = (LinearLayout) findViewById(R.id.schedules_container);
        try {
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(stop.getLabel());
       final LocalDateTime localDateTime = new LocalDateTime();


            for (final Map.Entry<Long, HashMap<String, List<DateTime>>> lineEntry : scheduleByWayByLine.entrySet()) {
                Button lineButton = new Button(this.getApplicationContext());
                lineButton.setText(lineEntry.getKey().toString());
                lineButton.setBackgroundResource(R.drawable.roundedbutton);
                lineButton.setTextColor(Color.parseColor("#000000"));

                lineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        waysContainer.removeAllViews();
                        scheduleContainer.removeAllViews();
                        for (final Map.Entry<String, List<DateTime>> wayEntry : lineEntry.getValue().entrySet()) {
                            Button wayButton = new Button(DetailStop.this.getApplicationContext());
                            wayButton.setText(wayEntry.getKey());
                            waysContainer.addView(wayButton);

                            wayButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    scheduleContainer.removeAllViews();
                                    for (DateTime upcomingDates : wayEntry.getValue()) {
                                        boolean display = false;
                                        if (upcomingDates.getHourOfDay() == localDateTime.getHourOfDay()) {
                                            if (upcomingDates.getMinuteOfHour() > localDateTime.getMinuteOfHour()) {
                                                display = true;
                                            }
                                        } else if (upcomingDates.getHourOfDay() > localDateTime.getHourOfDay()) {
                                            display = true;
                                        }
                                        if (upcomingDates.getHourOfDay() > localDateTime.getHourOfDay() + 3)
                                            display = false;

                                        if (display) {
                                            TextView oneSchedule = new TextView(DetailStop.this.getApplicationContext());
                                            String minute = "";
                                            if( upcomingDates.getMinuteOfHour() <10)
                                                minute = "0";
                                            minute += upcomingDates.getMinuteOfHour();
                                            oneSchedule.setText(upcomingDates.getHourOfDay() + ":" + minute);
                                            oneSchedule.setTextColor(Color.BLACK);
                                            scheduleContainer.addView(oneSchedule);
                                        }
                                    }
                                }
                            });
                        }


                    }
                });
                //lineButton.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
                linesContainer.addView(lineButton);
            }

        }catch (Exception e )
        {
            Toast.makeText(DetailStop.this,"An error has occured", Toast.LENGTH_SHORT).show();
        }

    }


    public HashMap<Long, HashMap<String, List<DateTime>>> getScheduleByWayByLine() {
        return scheduleByWayByLine;
    }

    public void setScheduleByWayByLine(HashMap<Long, HashMap<String, List<DateTime>>> scheduleByWayByLine) {
        this.scheduleByWayByLine = scheduleByWayByLine;
    }
}
