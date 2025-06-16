package com.onewaytripcalltaxi.driver;

import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.onewaytripcalltaxi.driver.data.WayPointsData;
import com.onewaytripcalltaxi.driver.interfaces.DistanceUpdate;
import com.onewaytripcalltaxi.driver.utils.CToast;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Systems;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by developer on 8/2/18.
 */

public class DummyActivity extends BaseActivity implements DistanceUpdate {

    private String trip_id = "";



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dummytest);

        if (getIntent() != null)
            trip_id = getIntent().getStringExtra("trip_id");
        if (trip_id != null && trip_id.equals(""))
            trip_id = SessionSave.getSession("trip_id", DummyActivity.this);
        Systems.out.println("Trip_idddd" + trip_id);
        CToast.ShowToast(this, trip_id);
//        UpdateLocation.distanceUpdate(DummyActivity.this);
        ((TextView) findViewById(R.id.text)).setText(Html.fromHtml(SessionSave.getSession(trip_id + "data", DummyActivity.this)));

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                UpdateLocation.startLocationService(DummyActivity.this);
//                UpdateLocation.distanceUpdate(DummyActivity.this);
            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                stopService(new Intent(DummyActivity.this, UpdateLocation.class));
            }
        });

    }

    @Override
    protected void onDestroy() {
//        Utils.closeDialog();
        super.onDestroy();
    }

    @Override
    public void onDistanceUpdate(Double distance, String s) {
        Systems.out.println("onDistanceUpdate");
        if (s.equals("1")) {
            ((TextView) findViewById(R.id.txt_haver)).setText(String.valueOf(distance));
        } else {
            JSONArray wayData = SessionSave.ReadGoogleWaypointsWithId(DummyActivity.this, "1");
            Systems.out.println("WayDistance**" + wayData);
            ((TextView) findViewById(R.id.txt_google)).setText(String.valueOf(wayData));
            try {
                for (int i = 0; i < wayData.length(); i++) {
                    WayPointsData wayPointsData = new Gson().fromJson(wayData.get(i).toString(), WayPointsData.class);
                    if (wayPointsData.getDist() == 0.0) {
                        Systems.out.println("WayDistance" + wayPointsData.getDist());
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}