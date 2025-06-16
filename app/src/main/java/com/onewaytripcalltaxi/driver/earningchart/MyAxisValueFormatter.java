package com.onewaytripcalltaxi.driver.earningchart;


import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by developer on 12/11/16.
 */
public class MyAxisValueFormatter extends ValueFormatter {

    private final DecimalFormat mFormat;
    private final String currency;

    public MyAxisValueFormatter(String currency) {

        mFormat = new DecimalFormat("###,###,###,##0.00");
        this.currency = currency;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        NumberFormat numberFormat = DecimalFormat.getInstance(Locale.ENGLISH);
        DecimalFormat formatter = (DecimalFormat) numberFormat;
        formatter.applyPattern("###,###,###,##0.00");
        String fString = formatter.format(value);
        return " " + fString;
    }


    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

        NumberFormat numberFormat = DecimalFormat.getInstance(Locale.ENGLISH);
        DecimalFormat formatter = (DecimalFormat) numberFormat;
        formatter.applyPattern("###,###,###,##0.00");
        String fString = formatter.format(value);
        return " " + fString;
    }
}
