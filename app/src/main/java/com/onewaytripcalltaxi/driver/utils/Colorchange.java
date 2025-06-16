package com.onewaytripcalltaxi.driver.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.onewaytripcalltaxi.driver.R;


/**
 * Created by developer on 29/11/16.
 */

/**
 * Used to change color and string file for all ui components
 */
public class Colorchange {

    public static void ChangeColor(ViewGroup parentLayout, Context cc) {
        for (int count = 0; count < parentLayout.getChildCount(); count++) {
            View view = parentLayout.getChildAt(count);
            int color = 0;
            Drawable background = view.getBackground();


            if (background instanceof ShapeDrawable) {

            } else if (background instanceof GradientDrawable) {

            } else if (background instanceof LayerDrawable) {

            } else if (background instanceof ColorDrawable) {
                color = ((ColorDrawable) background).getColor();
                String hexColor = String.format("#%06X", (0xFFFFFF & color));

                switch (hexColor.toUpperCase()) {
                    case "#F6F6F6":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.appbg));
                        break;
                    case "#F5F5F5":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.header_bgcolor));
                        break;
                    case "#6F6F6F":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.appbgdark));
                        break;
                    case "#404041":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.header_text));
                        break;
                    case "#47BF28":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.shiftoncolor));
                        break;
                    case "#9C9C9C":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.shiftoffcolor));
                        break;
                    case "#FF0000":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.highlightcolor));
                        break;
                    case "#666666":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.textviewcolor_dark));
                        break;
                    case "#A2A2A2":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.textviewcolor_light));
                        break;
                    case "#646464":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.linebottom_dark));
                        break;
                    case "#979797":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.bordercolor));
                        break;
                    case "#A6000000":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.notificationbg));
                        break;
                    case "#EE3324":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.button_accept));
                        break;
                    case "#8E1F16":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.button_reject));
                        break;
                    case "#00000000":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.transparent));
                        break;
                    case "#88676767":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.semitransparent));
                        break;
                    case "#87DC1F":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.btnaccept));
                        break;
                    case "#5F5F5F":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.trip_history_text_grey));
                        break;
                    case "#BDBDBD":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.light_white));
                        break;
                    case "#3F51B5":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.colorPrimary));
                        break;
                    case "#303F9F":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.colorPrimaryDark));
                        break;
                    case "#FF4081":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.colorAccent));
                        break;
                    case "#87CEFA":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.pastbookingcard));
                        break;
                    case "#A3A3A3":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.invite_gray));
                        break;
                    case "#000000":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.transparent));
                        break;
                    case "#FFFFFF":
                        view.setBackgroundColor(CL.getResources().getColor(R.color.white));
                        break;

                }

            }


            if (view instanceof TextView || view instanceof Button) {
                try {
                    String tt = "";
                    String text = "";
                    if (view instanceof EditText) {
                        EditText tv = (EditText) (view);

                        if (tv.getText().toString().trim().equals("")) {
                            text = tv.getHint().toString();
                            if (NC.fields_value.contains(text)) {
                                String keyValue = NC.fields.get(NC.fields_value.indexOf(text));
                                ((EditText) view).setHint(NC.nfields_byName.get(keyValue));


                            } else {
                                Systems.out.println("null");
                            }
                        }
                        String hexColor = String.format("#%06X", (0xFFFFFF & tv.getCurrentHintTextColor()));


                        switch (hexColor.toUpperCase()) {
                            case "#F6F6F6":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.appbg));
                                break;
                            case "#F5F5F5":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.header_bgcolor));
                                break;
                            case "#6F6F6F":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.appbgdark));
                                break;
                            case "#404041":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.header_text));
                                break;
                            case "#47BF28":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.shiftoncolor));
                                break;
                            case "#9C9C9C":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.shiftoffcolor));
                                break;
                            case "#FF0000":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.highlightcolor));
                                break;
                            case "#666666":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.textviewcolor_dark));
                                break;
                            case "#A2A2A2":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.textviewcolor_light));
                                break;
                            case "#646464":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.linebottom_dark));
                                break;
                            case "#979797":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.bordercolor));
                                break;
                            case "#A6000000":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.notificationbg));
                                break;
                            case "#EE3324":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.button_accept));
                                break;
                            case "#8E1F16":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.button_reject));
                                break;
                            case "#00000000":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.transparent));
                                break;
                            case "#88676767":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.semitransparent));
                                break;
                            case "#87DC1F":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.btnaccept));
                                break;
                            case "#5F5F5F":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.trip_history_text_grey));
                                break;
                            case "#BDBDBD":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.light_white));
                                break;
                            case "#3F51B5":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.colorPrimary));
                                break;
                            case "#303F9F":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.colorPrimaryDark));
                                break;
                            case "#FF4081":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.colorAccent));
                                break;
                            case "#87CEFA":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.pastbookingcard));
                                break;
                            case "#A3A3A3":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.invite_gray));
                                break;
                            case "#000000":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.black));
                                break;
                            case "#FFFFFF":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.white));
                                break;

                            // You can have any number of case statements.

                        }
                        text = tv.getText().toString();
                        if (NC.fields_value.contains(text)) {
                            String keyValue = NC.fields.get(NC.fields_value.indexOf(text));
                            ((EditText) view).setText(NC.nfields_byName.get(keyValue));
                        }

                        String hexColort = String.format("#%06X", (0xFFFFFF & tv.getCurrentTextColor()));


                        switch (hexColort.toUpperCase()) {
                            case "#F6F6F6":
                                tv.setTextColor(CL.getResources().getColor(R.color.appbg));
                                break;
                            case "#F5F5F5":
                                tv.setTextColor(CL.getResources().getColor(R.color.header_bgcolor));
                                break;
                            case "#6F6F6F":
                                tv.setTextColor(CL.getResources().getColor(R.color.appbgdark));
                                break;
                            case "#404041":
                                tv.setTextColor(CL.getResources().getColor(R.color.header_text));
                                break;
                            case "#47BF28":
                                tv.setTextColor(CL.getResources().getColor(R.color.shiftoncolor));
                                break;
                            case "#9C9C9C":
                                tv.setTextColor(CL.getResources().getColor(R.color.shiftoffcolor));
                                break;
                            case "#FF0000":
                                tv.setTextColor(CL.getResources().getColor(R.color.highlightcolor));
                                break;
                            case "#666666":
                                tv.setTextColor(CL.getResources().getColor(R.color.textviewcolor_dark));
                                break;
                            case "#A2A2A2":
                                tv.setTextColor(CL.getResources().getColor(R.color.textviewcolor_light));
                                break;
                            case "#646464":
                                tv.setTextColor(CL.getResources().getColor(R.color.linebottom_dark));
                                break;
                            case "#979797":
                                tv.setTextColor(CL.getResources().getColor(R.color.bordercolor));
                                break;
                            case "#A6000000":
                                tv.setTextColor(CL.getResources().getColor(R.color.notificationbg));
                                break;
                            case "#EE3324":
                                tv.setTextColor(CL.getResources().getColor(R.color.button_accept));
                                break;
                            case "#8E1F16":
                                tv.setTextColor(CL.getResources().getColor(R.color.button_reject));
                                break;
                            case "#00000000":
                                tv.setTextColor(CL.getResources().getColor(R.color.transparent));
                                break;
                            case "#88676767":
                                tv.setTextColor(CL.getResources().getColor(R.color.semitransparent));
                                break;
                            case "#87DC1F":
                                tv.setTextColor(CL.getResources().getColor(R.color.btnaccept));
                                break;
                            case "#5F5F5F":
                                tv.setTextColor(CL.getResources().getColor(R.color.trip_history_text_grey));
                                break;
                            case "#BDBDBD":
                                tv.setTextColor(CL.getResources().getColor(R.color.light_white));
                                break;
                            case "#3F51B5":
                                tv.setTextColor(CL.getResources().getColor(R.color.colorPrimary));
                                break;
                            case "#303F9F":
                                tv.setTextColor(CL.getResources().getColor(R.color.colorPrimaryDark));
                                break;
                            case "#FF4081":
                                tv.setTextColor(CL.getResources().getColor(R.color.colorAccent));
                                break;
                            case "#87CEFA":
                                tv.setTextColor(CL.getResources().getColor(R.color.pastbookingcard));
                                break;
                            case "#A3A3A3":
                                tv.setTextColor(CL.getResources().getColor(R.color.invite_gray));
                                break;
                            case "#000000":
                                tv.setTextColor(CL.getResources().getColor(R.color.black));
                                break;
                            case "#FFFFFF":
                                tv.setTextColor(CL.getResources().getColor(R.color.white));
                                break;
                        }
                    } else if (view instanceof TextView) {

                        TextView tv = (TextView) (view);
                        text = (tv).getText().toString();
                        if (NC.fields_value.contains(text)) {
                            String keyValue = NC.fields.get(NC.fields_value.indexOf(text));
                            tv.setText(NC.nfields_byName.get(keyValue));
                        } else {

                        }
                        String hexColor = String.format("#%06X", (0xFFFFFF & tv.getCurrentTextColor()));

                        switch (hexColor.toUpperCase()) {
                            case "#F6F6F6":
                                tv.setTextColor(CL.getResources().getColor(R.color.appbg));
                                break;
                            case "#F5F5F5":
                                tv.setTextColor(CL.getResources().getColor(R.color.header_bgcolor));
                                break;
                            case "#6F6F6F":
                                tv.setTextColor(CL.getResources().getColor(R.color.appbgdark));
                                break;
                            case "#404041":
                                tv.setTextColor(CL.getResources().getColor(R.color.header_text));
                                break;
                            case "#47BF28":
                                tv.setTextColor(CL.getResources().getColor(R.color.shiftoncolor));
                                break;
                            case "#9C9C9C":
                                tv.setTextColor(CL.getResources().getColor(R.color.shiftoffcolor));
                                break;
                            case "#FF0000":
                                tv.setTextColor(CL.getResources().getColor(R.color.highlightcolor));
                                break;
                            case "#666666":
                                tv.setTextColor(CL.getResources().getColor(R.color.textviewcolor_dark));
                                break;
                            case "#A2A2A2":
                                tv.setTextColor(CL.getResources().getColor(R.color.textviewcolor_light));
                                break;
                            case "#646464":
                                tv.setTextColor(CL.getResources().getColor(R.color.linebottom_dark));
                                break;
                            case "#979797":
                                tv.setTextColor(CL.getResources().getColor(R.color.bordercolor));
                                break;
                            case "#A6000000":
                                tv.setTextColor(CL.getResources().getColor(R.color.notificationbg));
                                break;
                            case "#EE3324":
                                tv.setTextColor(CL.getResources().getColor(R.color.button_accept));

                                break;
                            case "#8E1F16":
                                tv.setTextColor(CL.getResources().getColor(R.color.button_reject));
                                break;
                            case "#00000000":
                                tv.setTextColor(CL.getResources().getColor(R.color.transparent));
                                break;
                            case "#88676767":
                                tv.setTextColor(CL.getResources().getColor(R.color.semitransparent));
                                break;
                            case "#87DC1F":
                                tv.setTextColor(CL.getResources().getColor(R.color.btnaccept));
                                break;
                            case "#5F5F5F":
                                tv.setTextColor(CL.getResources().getColor(R.color.trip_history_text_grey));
                                break;
                            case "#BDBDBD":
                                tv.setTextColor(CL.getResources().getColor(R.color.light_white));
                                break;
                            case "#3F51B5":
                                tv.setTextColor(CL.getResources().getColor(R.color.colorPrimary));
                                break;
                            case "#303F9F":
                                tv.setTextColor(CL.getResources().getColor(R.color.colorPrimaryDark));
                                break;
                            case "#FF4081":
                                tv.setTextColor(CL.getResources().getColor(R.color.colorAccent));
                                break;
                            case "#87CEFA":
                                tv.setTextColor(CL.getResources().getColor(R.color.pastbookingcard));
                                break;
                            case "#A3A3A3":
                                tv.setTextColor(CL.getResources().getColor(R.color.invite_gray));
                                break;
                            case "#000000":
                                tv.setTextColor(CL.getResources().getColor(R.color.black));
                                break;
                            case "#FFFFFF":
                                tv.setTextColor(CL.getResources().getColor(R.color.white));
                                break;
                        }

                        String hexColort = String.format("#%06X", (0xFFFFFF & tv.getCurrentHintTextColor()));

                        switch (hexColort.toUpperCase()) {
                            case "#F6F6F6":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.appbg));
                                break;
                            case "#F5F5F5":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.header_bgcolor));
                                break;
                            case "#6F6F6F":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.appbgdark));
                                break;
                            case "#404041":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.header_text));
                                break;
                            case "#47BF28":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.shiftoncolor));
                                break;
                            case "#9C9C9C":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.shiftoffcolor));
                                break;
                            case "#FF0000":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.highlightcolor));
                                break;
                            case "#666666":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.textviewcolor_dark));
                                break;
                            case "#A2A2A2":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.textviewcolor_light));
                                break;
                            case "#646464":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.linebottom_dark));
                                break;
                            case "#979797":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.bordercolor));
                                break;
                            case "#A6000000":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.notificationbg));
                                break;
                            case "#EE3324":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.button_accept));
                                break;
                            case "#8E1F16":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.button_reject));
                                break;
                            case "#00000000":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.transparent));
                                break;
                            case "#88676767":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.semitransparent));
                                break;
                            case "#87DC1F":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.btnaccept));
                                break;
                            case "#5F5F5F":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.trip_history_text_grey));
                                break;
                            case "#BDBDBD":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.light_white));
                                break;
                            case "#3F51B5":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.colorPrimary));
                                break;
                            case "#303F9F":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.colorPrimaryDark));
                                break;
                            case "#FF4081":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.colorAccent));
                                break;
                            case "#87CEFA":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.pastbookingcard));
                                break;
                            case "#A3A3A3":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.invite_gray));
                                break;
                            case "#000000":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.black));
                                break;
                            case "#FFFFFF":
                                tv.setHintTextColor(CL.getResources().getColor(R.color.white));
                                break;
                        }

                    } else {
                        Button tv = (Button) (view);
                        text = (tv).getText().toString();
                        if (NC.fields_value.contains(text)) {
                            String keyValue = NC.fields.get(NC.fields_value.indexOf(text));
                            tv.setText(NC.nfields_byName.get(keyValue));
                        }

                        String hexColor = String.format("#%06X", (0xFFFFFF & tv.getCurrentTextColor()));


                        switch (hexColor.toUpperCase()) {
                            case "#F6F6F6":
                                tv.setTextColor(CL.getResources().getColor(R.color.appbg));
                                break;
                            case "#F5F5F5":
                                tv.setTextColor(CL.getResources().getColor(R.color.header_bgcolor));
                                break;
                            case "#6F6F6F":
                                tv.setTextColor(CL.getResources().getColor(R.color.appbgdark));
                                break;
                            case "#404041":
                                tv.setTextColor(CL.getResources().getColor(R.color.header_text));
                                break;
                            case "#47BF28":
                                tv.setTextColor(CL.getResources().getColor(R.color.shiftoncolor));
                                break;
                            case "#9C9C9C":
                                tv.setTextColor(CL.getResources().getColor(R.color.shiftoffcolor));
                                break;
                            case "#FF0000":
                                tv.setTextColor(CL.getResources().getColor(R.color.highlightcolor));
                                break;
                            case "#666666":
                                tv.setTextColor(CL.getResources().getColor(R.color.textviewcolor_dark));
                                break;
                            case "#A2A2A2":
                                tv.setTextColor(CL.getResources().getColor(R.color.textviewcolor_light));
                                break;
                            case "#646464":
                                tv.setTextColor(CL.getResources().getColor(R.color.linebottom_dark));
                                break;
                            case "#979797":
                                tv.setTextColor(CL.getResources().getColor(R.color.bordercolor));
                                break;
                            case "#A6000000":
                                tv.setTextColor(CL.getResources().getColor(R.color.notificationbg));
                                break;
                            case "#EE3324":
                                tv.setTextColor(CL.getResources().getColor(R.color.button_accept));
                                break;
                            case "#8E1F16":
                                tv.setTextColor(CL.getResources().getColor(R.color.button_reject));
                                break;
                            case "#00000000":
                                tv.setTextColor(CL.getResources().getColor(R.color.transparent));
                                break;
                            case "#88676767":
                                tv.setTextColor(CL.getResources().getColor(R.color.semitransparent));
                                break;
                            case "#87DC1F":
                                tv.setTextColor(CL.getResources().getColor(R.color.btnaccept));
                                break;
                            case "#5F5F5F":
                                tv.setTextColor(CL.getResources().getColor(R.color.trip_history_text_grey));
                                break;
                            case "#BDBDBD":
                                tv.setTextColor(CL.getResources().getColor(R.color.light_white));
                                break;
                            case "#3F51B5":
                                tv.setTextColor(CL.getResources().getColor(R.color.colorPrimary));
                                break;
                            case "#303F9F":
                                tv.setTextColor(CL.getResources().getColor(R.color.colorPrimaryDark));
                                break;
                            case "#FF4081":
                                tv.setTextColor(CL.getResources().getColor(R.color.colorAccent));
                                break;
                            case "#87CEFA":
                                tv.setTextColor(CL.getResources().getColor(R.color.pastbookingcard));
                                break;
                            case "#A3A3A3":
                                tv.setTextColor(CL.getResources().getColor(R.color.invite_gray));
                                break;
                            case "#000000":
                                tv.setTextColor(CL.getResources().getColor(R.color.black));
                                break;
                            case "#FFFFFF":
                                tv.setTextColor(CL.getResources().getColor(R.color.white));
                                break;
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (view instanceof ViewGroup) {

                ChangeColor((ViewGroup) view, cc);
            }

        }
    }


    private static String getStringResourceByName(String aString, Context cc) {
        String resId = cc.getResources().getString(cc.getResources().getIdentifier(aString, "string", cc.getPackageName()));
        return resId;
    }

}