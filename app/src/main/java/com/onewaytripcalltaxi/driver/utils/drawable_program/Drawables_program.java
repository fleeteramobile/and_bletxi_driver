package com.onewaytripcalltaxi.driver.utils.drawable_program;

import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.onewaytripcalltaxi.driver.R;
import com.onewaytripcalltaxi.driver.utils.CL;

/**
 * Created by developer on 7/3/17.
 */
public class Drawables_program {
    public static View shift_bg_grey(View v) {


        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setColor(CL.getColor(R.color.transparent));
        gdDefault.setCornerRadius(50);
        gdDefault.setStroke(1, CL.getColor(R.color.button_reject));
        v.setBackground(gdDefault);
        return v;
    }

    public static View shift_on(View v) {


        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        GradientDrawable gdDefault = new GradientDrawable();
        gdDefault.setColor(CL.getColor(R.color.blue_color));
        gdDefault.setCornerRadius(50);
        gdDefault.setStroke(1, CL.getColor(R.color.blue_color));
        v.setBackground(gdDefault);
        return v;
    }
}
