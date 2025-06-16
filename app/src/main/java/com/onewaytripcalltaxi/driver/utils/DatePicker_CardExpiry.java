package com.onewaytripcalltaxi.driver.utils;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;

import com.onewaytripcalltaxi.driver.R;
import com.onewaytripcalltaxi.driver.data.CommonData;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by developer on 3/25/16.
 */
public class DatePicker_CardExpiry extends DialogFragment {
    Calendar cal;
    int curMonth;
    int curYear;
    int curday;
    DatePicker datePicker;
    TextView ok, cancel;
    TextView dialogtitle;
    String key;
    private String selectedDate;
    public interface DialogInterface {
        void onSuccess(int month, int year, int day);
        void failure(String inputText);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.date_picker_dialog, container, false);
        Colorchange.ChangeColor((ViewGroup)v, requireActivity());
        FontHelper.applyFont(getActivity(), v);
        dialogtitle = v.findViewById(R.id.dialogtitle);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        cal = Calendar.getInstance(Locale.UK);
        curMonth = cal.get(Calendar.MONTH) + 1;
        curYear = cal.get(Calendar.YEAR);
        key = getArguments().getString("KEY");
        if (getArguments().getString("selected_date") != null)
            selectedDate = getArguments().getString("selected_date");
        curday = cal.get(Calendar.DAY_OF_MONTH);
        datePicker = v.findViewById(R.id.datePicker1);
        if (key.equalsIgnoreCase("1")) {
            datePicker.setMinDate(cal.getTimeInMillis() - 1000);
        } else {
            datePicker.setMaxDate(cal.getTimeInMillis());
            if (selectedDate != null && selectedDate.contains("-")) {
                String[] dateArray = selectedDate.replaceAll("\\s", "").split("-");
                if (dateArray.length > 1)
                    cal.set(Integer.parseInt(dateArray[0]), Integer.parseInt(dateArray[1]) - 1, Integer.parseInt(dateArray[2]));
            }
        }
        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), (view, year, monthOfYear, dayOfMonth) -> {
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            FontHelper.overrideFonts(getActivity(), view);
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            String formatedDate = CommonData.getCurrentTimeForDatePickerInMillis();
            dialogtitle.setText(formatedDate + " " + view.getYear());
        });
        ok = v.findViewById(R.id.ok);
        cancel = v.findViewById(R.id.cancel);
        ok.setOnClickListener(v1 -> ((DialogInterface) getActivity()).onSuccess(datePicker.getMonth(), datePicker.getYear(), datePicker.getDayOfMonth()));
        cancel.setOnClickListener(v12 -> ((DialogInterface) getActivity()).failure("cancel"));
        return v;
    }
}