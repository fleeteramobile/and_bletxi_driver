package com.onewaytripcalltaxi.driver.earningchart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;


public class Weekaxisformatter extends ValueFormatter {
    private final String[] mValues = new String[] {};
    private final int mValueCount = 0;
    public static List<String> mMonths;

    public Weekaxisformatter() {

    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
     if (value >= 0) {
         if (value <= mMonths.size() - 1) {
             System.out.println("mMonths_return"+ " "+ mMonths.get(0).toString());
             return "salkcjas"+mMonths.get((int) value);

         }
         return "bcbdfbf";
     }
        return "sdvdvxcx";
    }

    //
//    @Override
//    public String getFormattedValue(float value, AxisBase axis) {
//
//        if (mMonths.size() == ((int) value)) {
//            return "11";
//        } else {
//            if (mMonths.size() > ((int) value))
//                return mMonths.get(((int) value));
//            else
//                return "0";
//        }
//
//    }


}

