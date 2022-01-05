package com.glass.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by txiaozhe on 09/02/2017.
 */


public class ThresholdHelp {
    private Context context;
    private EditText picker1, picker2;
    private SharedPreferences sp;
    private Button showDialog;

    private String controltype;//控制柜温度， 车辆轴温

    //控制柜温度
    private int threshold_low;
    private int threshold_high;

    // 车辆轴温
    private int AxleTemp_low;
    private int AxleTemp_high;

    public int getThreshold_low() {
        if (controltype.contains("控制柜"))
            return threshold_low;
        else
            return AxleTemp_low;
    }
    public int getThreshold_high() {

        if (controltype.contains("控制柜"))
            return threshold_high;
        else
            return AxleTemp_high;
    }

    public SharedPreferences getSp() {
        return sp;
    }

    public EditText getPicker1() {
        return picker1;
    }

    public EditText getPicker2() {
        return picker2;
    }

    public void setThreshold_low(int temp_low) {
        if (controltype.contains("控制柜"))
            this.threshold_low = temp_low;
        else
            this.AxleTemp_low = temp_low;
    }

    public void setThreshold_high(int temp_high) {
        if (controltype.contains("控制柜"))
            this.threshold_high = temp_high;
        else
            this.AxleTemp_high = temp_high;
    }

    public void setSp(SharedPreferences sp) {
        this.sp = sp;
    }

    public ThresholdHelp(Context context, Button showDialog, String type) {
        this.context = context;
        this.showDialog = showDialog;
        this.controltype = type;
    }

}