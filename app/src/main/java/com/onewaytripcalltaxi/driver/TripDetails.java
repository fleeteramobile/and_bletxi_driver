package com.onewaytripcalltaxi.driver;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.FontHelper;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.squareup.picasso.Picasso;

import java.util.Locale;


/**
 * This class is used to Show trip details for driver
 */
public class TripDetails extends MainActivity {
    // Class members declarations
    private ImageView imgView;
    private TextView txtPassenger, txtTripTime, txtTripPick, txtTripAmount, txtTripPayment, txtTripId, back;
    private String passenger_name;
    private TextView passnameTxt;
    private TextView droptimeTxt;
    private TextView dropplaceTxt;
    private TextView distanceTxt;
    private TextView duartionTxt;
    private double mdistance;

    // Set the layout to activity.
    @Override
    public int setLayout() {

        setLocale();
        return R.layout.trip_detailslayout;
    }


    // Initialize the views on layout
    @Override
    public void Initialize() {

        CommonData.mActivitylist.add(this);
        CommonData.sContext = this;
        CommonData.current_act = "TripDetails";
        imgView = findViewById(R.id.driverImg);

        Colorchange.ChangeColor((ViewGroup) (((ViewGroup) TripDetails.this
                .findViewById(android.R.id.content)).getChildAt(0)), TripDetails.this);

        FontHelper.applyFont(this, findViewById(R.id.id_tripdetails));
        back = findViewById(R.id.slideImg);
        txtPassenger = findViewById(R.id.drivernameTxt);
        txtTripTime = findViewById(R.id.time);
        txtTripPick = findViewById(R.id.place);
        txtTripAmount = findViewById(R.id.amount);
        txtTripPayment = findViewById(R.id.payment);
        txtTripId = findViewById(R.id.jobref);
        passnameTxt = findViewById(R.id.passnameTxt);
        droptimeTxt = findViewById(R.id.droptimeTxt);
        dropplaceTxt = findViewById(R.id.droplocTxt);
        distanceTxt = findViewById(R.id.distanceTxt);
        duartionTxt = findViewById(R.id.duartionTxt);
        Bundle bundle = getIntent().getExtras();
        passenger_name = Character.toUpperCase(bundle.getString("passenger_name").charAt(0)) + bundle.getString("passenger_name").substring(1);
        txtPassenger.setText(passenger_name);
        passnameTxt.setText(passenger_name);
        txtTripTime.setText((bundle.getString("pickup_time")));
        txtTripPick.setText(bundle.getString("pickup_location"));
        txtTripAmount.setText(SessionSave.getSession("site_currency", getApplicationContext()) + " " + bundle.getString("trip_amt"));
        txtTripPayment.setText(bundle.getString("paymenttype"));
        txtTripId.setText(bundle.getString("passengers_log_id"));
        droptimeTxt.setText(bundle.getString("drop_time"));
        dropplaceTxt.setText(bundle.getString("drop_location"));
        if (bundle.getString("distance").length() != 0)
            mdistance = Double.parseDouble(bundle.getString("distance"));
        distanceTxt.setText(String.format(Locale.UK, "%.2f", mdistance) + " " + SessionSave.getSession("Metric", TripDetails.this).toLowerCase());
        duartionTxt.setText(bundle.getString("trip_duration"));
        String imagePath = bundle.getString("pimage");
        // This for load the passenger image in details page image view this current activity.
        try {
            if (imagePath.length() != 0) {
                Picasso.get().load(imagePath).placeholder(getResources().getDrawable(R.drawable.loadingimage)).error(getResources().getDrawable(R.drawable.noimage)).into(imgView);

            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        // This for close this current activity.
        back.setOnClickListener(v -> finish());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
