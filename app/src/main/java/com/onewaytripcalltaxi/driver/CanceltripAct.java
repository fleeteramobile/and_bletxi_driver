package com.onewaytripcalltaxi.driver;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.service.NonActivity;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.FontHelper;
import com.onewaytripcalltaxi.driver.utils.SessionSave;

/**
 * This is cancel the trip
 */
public class CanceltripAct extends MainActivity implements OnClickListener {
    private TextView m_message;
    private String cancel_msg;
    private TextView m_cancel;

    /**
     * setting the layout
     */
    @Override
    public int setLayout() {
        // TODO Auto-generated method stub
        return R.layout.canceltrip_lay;
    }

    /**
     * Initializing the component variables
     */
    @Override
    public void Initialize() {
        // TODO Auto-generated method stub

        try{
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); }


        FontHelper.applyFont(CanceltripAct.this, findViewById(R.id.canceltrip));

        Colorchange.ChangeColor((ViewGroup) (((ViewGroup) CanceltripAct.this
                .findViewById(android.R.id.content)).getChildAt(0)), CanceltripAct.this);


        unlockScreen();
        CommonData.mActivitylist.add(this);
        Bundle bun = getIntent().getExtras();
        if (bun != null) {
            m_message = findViewById(R.id.message);
            m_cancel = findViewById(R.id.button1);
            cancel_msg = bun.getString("message");
            m_message.setText(cancel_msg);
            SessionSave.saveSession("trip_id", "", CanceltripAct.this);
            setonclickListener();
        }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * on click listener
     */
    private void setonclickListener() {
        m_cancel.setOnClickListener(this);
    }

    /**
     * on click listener
     */
    @Override
    public void onClick(View v) {
        if (v == m_cancel) {
            MainActivity.mMyStatus.setStatus("F");
            SessionSave.saveSession("status", "F", getApplicationContext());
            MainActivity.mMyStatus.settripId("");
            SessionSave.saveSession("trip_id", "", getApplicationContext());
            MainActivity.mMyStatus.setOnstatus("On");
            MainActivity.mMyStatus.setOnPassengerImage("");
            MainActivity.mMyStatus.setOnpassengerName("");
            MainActivity.mMyStatus.setOndropLocation("");
            MainActivity.mMyStatus.setPassengerOndropLocation("");
            MainActivity.mMyStatus.setOnpickupLatitude("");
            MainActivity.mMyStatus.setOnpickupLongitude("");
            MainActivity.mMyStatus.setOndropLatitude("");
            MainActivity.mMyStatus.setOndropLongitude("");
            MainActivity.mMyStatus.setOndriverLatitude("");
            MainActivity.mMyStatus.setOndriverLongitude("");
            new NonActivity().stopServicefromNonActivity(CanceltripAct.this);
            new NonActivity().startServicefromNonActivity(CanceltripAct.this);
            Intent in = new Intent(CanceltripAct.this, MyStatus.class);
            in.setAction(Intent.ACTION_MAIN);
            in.addCategory(Intent.CATEGORY_LAUNCHER);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            ComponentName cn = new ComponentName(getApplicationContext(), MyStatus.class);
            in.setComponent(cn);
            startActivity(in);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SessionSave.saveSession("trip_id", "", CanceltripAct.this);
        Intent in = new Intent(CanceltripAct.this, MyStatus.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in);
        finish();
    }

    /**
     * This method is to check and open the notification view in front even the mobile screen off.
     */
    private void unlockScreen() {
        Window window = this.getWindow();
        window.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
    }

}