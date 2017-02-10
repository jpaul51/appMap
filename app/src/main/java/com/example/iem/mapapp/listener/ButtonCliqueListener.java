package com.example.iem.mapapp.listener;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.Button;

import com.example.iem.mapapp.R;
import com.example.iem.mapapp.interfaces.CallbackButtonClick;

/**
 * Created by iem on 03/02/2017.
 */

public class ButtonCliqueListener implements View.OnClickListener {

    private boolean isChecked = false;
    private int colorDefault = Color.WHITE;
    private int colorActivated = Color.GREEN;
    private CallbackButtonClick callback;
    private int myNumber = -1;



    public ButtonCliqueListener(String colorDefault, String colorActivated , CallbackButtonClick callback, int number) {
        this.colorDefault = Color.parseColor(colorDefault);
        this.colorActivated = Color.parseColor(colorActivated);
        this.callback = callback;
        this.myNumber = number;
    }

    public ButtonCliqueListener(CallbackButtonClick callback,int number) {
        this.callback = callback;
        this.myNumber = number;
    }

    @Override
    public void onClick(View view) {
        GradientDrawable bgShape = (GradientDrawable)view.getBackground();
        toggleCheck();
        if(isChecked) {
            bgShape.setColor(colorActivated);
            callback.displayLine(myNumber);
        }
        else{
            bgShape.setColor(colorDefault);
            callback.removeLine(myNumber);
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public int getColorDefault() {
        return colorDefault;
    }

    public void setColorDefault(int colorDefault) {
        this.colorDefault = colorDefault;
    }

    public int getColorActivated() {
        return colorActivated;
    }

    public void setColorActivated(int colorActivated) {
        this.colorActivated = colorActivated;
    }

    private void toggleCheck(){
        isChecked = !isChecked;
    }
}
