package com.onewaytripcalltaxi.driver;

import static com.google.android.gms.maps.model.JointType.ROUND;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.ui.IconGenerator;
import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.data.MapWrapperLayout;
import com.onewaytripcalltaxi.driver.errorLog.ApiErrorModel;
import com.onewaytripcalltaxi.driver.errorLog.ErrorLogRepository;
import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.pdview.PickupDropView;
import com.onewaytripcalltaxi.driver.roomDB.GoogleMapModel;
import com.onewaytripcalltaxi.driver.roomDB.MapLoggerRepository;
import com.onewaytripcalltaxi.driver.route.Route;
import com.onewaytripcalltaxi.driver.route.StopData;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.service.CoreClient;
import com.onewaytripcalltaxi.driver.service.NonActivity;
import com.onewaytripcalltaxi.driver.service.RetrofitCallbackClass;
import com.onewaytripcalltaxi.driver.utils.CToast;
import com.onewaytripcalltaxi.driver.utils.DriverUtils;
import com.onewaytripcalltaxi.driver.utils.ExceptionConverter;
import com.onewaytripcalltaxi.driver.utils.FontHelper;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.NetworkStatus;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Systems;
import com.onewaytripcalltaxi.driver.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * This class is the base  for showing app notifications
 */
public class NotificationAct extends MainActivity implements OnMapReadyCallback {
    public static NotificationAct notificationObject;
    public String pickup_time, profile_image;
    public String message, distance, passenger_id;
    int time_out;
    AppCompatActivity nActivity;
    CountDownTimer countDownTimer;
    Ringtone r;
    NonActivity nonactiityobj = new NonActivity();
    Bundle bun;
    private boolean ACCEPT_TRIP_IN_PROGRESS = false;
    private TextView remnTimeTxt, secTxt,trip_types;
    private TextView passNameTxt, pickupLocTxt, dropLocTxt, stopsTxt, typelbl, estlbl, stopslbl, etalbl,noteslbl,notesTxt;
    private TextView minTxt, slideImg, text_notes, trip_estimate_amount, trip_payment_type, total_estimate_amount, estimate_distanceTxt, eta_Txt, total_est_label, extra_incentiveAmt;
    private TextView passenger_name_txt,model_name_txt, tv_tripType, Rightlay, backupTxt, HeadTitle, pick_notesTxt, drop_notesTxt, incentive_distance_txt, incentive_amount_txt;
    //layout declaration
    private LinearLayout noteslayout, droplayout, pick_lay, estimatelay, pic_noteslayout, drop_noteslayout, incentive_distance_lay, incentive_amount_lay, paymenttypelay;
    // Class members declarations.
    private String trip_id = "", pickup, drop, bookedby;
    private String passenger_phone, cityname, passenger_name, notes, estimate_amount, est_distance, eta_time, stops_count;
    private String model_name, trip_type;
    private double pickup_lat, pickup_lng;
    //Class declartion
    private GoogleMap map;
    private MapWrapperLayout mapWrapperLayout;
    private DonutProgress donutProgress;
    private AnimatorSet set;
    private PickupDropView pickUpDropLayout;
    private int nowAfter = -1;
    LinearLayout pickupTime_layout;
    TextView pickup_time_txt;
    LocationManager locationManager;
    private static Double drop_lattitude = 0.0;
    private static Double drop_longitude = 0.0;

    private LinearLayout accept_btn;
    RelativeLayout decline_btn;

    MediaPlayer mPlayer;
    private Location mLastLocation;

    private final Route route = null;

    private String audio_url = "";

    private static Double pick_lat;
    private static Double pick_long;
    private static Double current_lattitude;
    private static Double current_longitude;
    private int requestedType = 0;
    protected MapLoggerRepository mRepository;
    private static Typeface ContenttypeFace;
    LinearProgressIndicator progressIndicator,progressIndicator1,progressIndicator2;
    JSONArray stops = null;
    private ArrayList<LatLng> stopListData = new ArrayList<>();


    /**
     * Get the google map pixels from xml density independent pixel.
     */
    public static int getPixelsFromDp(final Context context, final float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    // Set the layout to activity.
    @Override
    public int setLayout() {

        setLocale();
        return R.layout.notification_lay;
    }

    /**
     * This method is used to enable gps
     */
    private boolean GPSEnabled(Context mContext) {
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    public Typeface setcontentTypeface() {

        if (ContenttypeFace == null)
            ContenttypeFace = Typeface.createFromAsset(NotificationAct.this.getAssets(), FontHelper.FONT_TYPEFACE);
        return ContenttypeFace;
    }

    // Initialize the views on layout
    @Override
    public void Initialize() {
     /*   Colorchange.ChangeColor((ViewGroup) (((ViewGroup) NotificationAct.this
                .findViewById(android.R.id.content)).getChildAt(0)), NotificationAct.this);*/

        CommonData.current_trip_accept = 1;

        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            bun = getIntent().getExtras();
            nActivity = this;
            CommonData.current_act = "NotificationAct";
            CommonData.current_trip_accept = 1;


            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);

            if (bun != null) {
                unlockScreen();
             //   FontHelper.applyFont(this, findViewById(R.id.noti_font));
               // nonactiityobj.stopServicefromNonActivity(NotificationAct.this);
                pickUpDropLayout = findViewById(R.id.pd_view);
                HeadTitle = findViewById(R.id.headerTxt);
                HeadTitle.setText(" " + NC.getResources().getString(R.string.Trip_Request));
                slideImg = findViewById(R.id.slideImg);
                slideImg.setVisibility(View.GONE);
                remnTimeTxt = findViewById(R.id.rmnTimeTxt);
                secTxt = findViewById(R.id.secTxt);
                model_name_txt = findViewById(R.id.model_name_txt);
                passenger_name_txt = findViewById(R.id.passenger_name_txt);
                tv_tripType = findViewById(R.id.trip_type);
                passNameTxt = findViewById(R.id.pass_name);
                pickupLocTxt = findViewById(R.id.pickup_loc);
                dropLocTxt = findViewById(R.id.drop_loc);
                minTxt = findViewById(R.id.minTxt);
                text_notes = findViewById(R.id.notes);
                noteslayout = findViewById(R.id.noteslayout);
                droplayout = findViewById(R.id.droplayout);
                backupTxt = findViewById(R.id.backup);
                Rightlay = findViewById(R.id.Rightlay);
                pick_lay = findViewById(R.id.pic);
                pickupTime_layout = findViewById(R.id.pickupTime_layout);
                pickup_time_txt = findViewById(R.id.pickup_time_txt);
                ((TextView) findViewById(R.id.request_for)).setText(NC.getString(R.string.req_model));
                Rightlay.setVisibility(View.VISIBLE);
                backupTxt.setVisibility(View.GONE);
                donutProgress = findViewById(R.id.donut_progress);
                estimatelay = findViewById(R.id.estimatelay);
                trip_estimate_amount = findViewById(R.id.trip_estimate_amount);
                pick_notesTxt = findViewById(R.id.pick_notes);
                drop_notesTxt = findViewById(R.id.drop_notes);
                paymenttypelay = findViewById(R.id.paymenttypelay);
                trip_payment_type = findViewById(R.id.trip_payment_type);
                total_estimate_amount = findViewById(R.id.total_estimate_amount);
                estimate_distanceTxt = findViewById(R.id.estimate_distanceTxt);
                eta_Txt = findViewById(R.id.eta_Txt);
                total_est_label = findViewById(R.id.total_est_label);
              //  total_est_label.setTypeface(setcontentTypeface(), Typeface.BOLD);
               // total_estimate_amount.setTypeface(setcontentTypeface(), Typeface.BOLD);
                trip_types=findViewById(R.id.trip_types);

                typelbl = findViewById(R.id.typelbl);
                estlbl = findViewById(R.id.estlbl);
                stopslbl = findViewById(R.id.stopslbl);
                etalbl = findViewById(R.id.etalbl);
                noteslbl=findViewById(R.id.noteslbl);
                notesTxt=findViewById(R.id.notesTxt);

                //typelbl.setTypeface(setcontentTypeface(), Typeface.BOLD);
                //estlbl.setTypeface(setcontentTypeface(), Typeface.BOLD);
                //stopslbl.setTypeface(setcontentTypeface(), Typeface.BOLD);
               // etalbl.setTypeface(setcontentTypeface(), Typeface.BOLD);
              //  trip_payment_type.setTypeface(setcontentTypeface(), Typeface.BOLD);
               // model_name_txt.setTypeface(setcontentTypeface(), Typeface.BOLD);
                //estimate_distanceTxt.setTypeface(setcontentTypeface(), Typeface.BOLD);

               // noteslbl.setTypeface(setcontentTypeface(), Typeface.BOLD);


                accept_btn = findViewById(R.id.accept_btn);
                decline_btn = findViewById(R.id.decline_btn);
                stopsTxt = findViewById(R.id.stopsTxt);
               // stopsTxt.setTypeface(setcontentTypeface(), Typeface.BOLD);

                incentive_distance_txt = findViewById(R.id.incentive_distance_txt);
                incentive_amount_txt = findViewById(R.id.incentive_amount_txt);
                pic_noteslayout = findViewById(R.id.pic_noteslayout);
                drop_noteslayout = findViewById(R.id.drop_noteslayout);
                incentive_distance_lay = findViewById(R.id.incentive_distance_lay);
                incentive_amount_lay = findViewById(R.id.incentive_amount_lay);
              //  FontHelper.applyFont(this, passNameTxt, "Roboto_Medium.ttf");

             //   FontHelper.applyFont(this, minTxt, "Roboto_Medium.ttf");

                extra_incentiveAmt = findViewById(R.id.extra_incentiveAmt);
                progressIndicator = findViewById(R.id.progress);
                progressIndicator1 = findViewById(R.id.progress1);
                progressIndicator2 = findViewById(R.id.progress2);



                progressIndicator.setProgress(0);
                progressIndicator1.setProgress(0);
                progressIndicator2.setProgress(0);


                message = bun.getString("message");
                final Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                //  r = RingtoneManager.getRingtone(NotificationAct.this, notification);
                try {
                    final JSONObject json = new JSONObject(message);
                    final JSONObject tripdetails = json.getJSONObject("trip_details");
                    time_out = tripdetails.getInt("notification_time");
                    notes = tripdetails.getString("notes");
                    estimate_amount = tripdetails.getString("approx_fare");
                    trip_id = tripdetails.getString("passengers_log_id");
                    String pickup_notes = tripdetails.getString("pickup_notes");
                    String drop_notes = tripdetails.getString("dropoff_notes");
                    est_distance = tripdetails.getString("approx_distance");
                    eta_time =   tripdetails.getString("approx_distance");
                    //eta_time =   new DecimalFormat("##.##").format(String.valueOf(tripdetails.getString("approx_distance")));

                    if (tripdetails.has("stops")) {
                        stops_count = tripdetails.getString("stops");

                    } else {
                        stops_count = "0";
                    }

                    // String pickup_notes ="";
                    //String drop_notes ="";


                    if (tripdetails.has(CommonData.SHOW_CANCEL_BUTTON))
                        SessionSave.saveSession(CommonData.SHOW_CANCEL_BUTTON, tripdetails.getString(CommonData.SHOW_CANCEL_BUTTON), this);
                    else
                        SessionSave.saveSession(CommonData.SHOW_CANCEL_BUTTON, "0", this);

                    final JSONObject details = tripdetails.getJSONObject("booking_details");

                    if (details.has("pay_mod_id")) {

                        if (details.getString("pay_mod_id").equals("1")) {
                            trip_payment_type.setText(NC.getString(R.string.cash));
                            paymenttypelay.setVisibility(View.VISIBLE);
                        } else if (details.getString("pay_mod_id").equals("5")) {
                            trip_payment_type.setText(NC.getString(R.string.wallet));
                            paymenttypelay.setVisibility(View.VISIBLE);
                        } else {
                            paymenttypelay.setVisibility(View.GONE);
                        }

                    } else {
                        paymenttypelay.setVisibility(View.GONE);
                    }

                    if (details.has("incentive_distance")) {
                        incentive_distance_lay.setVisibility(View.VISIBLE);
                        incentive_distance_txt.setText(details.getString("incentive_distance") + " " + "KM");

                    } else {
                        incentive_distance_lay.setVisibility(View.GONE);

                    }

//                    if (details.has("incentive_amount")) {
//                        incentive_amount_lay.setVisibility(View.VISIBLE);
//                        incentive_amount_txt.setText(SessionSave.getSession("site_currency", NotificationAct.this) + " " + details.getString("incentive_amount"));
//
//                    } else {
//                        incentive_amount_lay.setVisibility(View.GONE);
//
//                    }
                    if (details.has("now_after")) {
                        nowAfter = details.getInt("now_after");
                        if (nowAfter == 1) {
                            pickupTime_layout.setVisibility(View.VISIBLE);
                            if (details.has("pickup_time"))
                                pickup_time_txt.setText(details.getString("pickup_time"));
//                            r = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse("android.resource://"
//                                    + context.getPackageName() + "/" + R.raw.trip_later));

                            audio_url = SessionSave.getSession("book_later_tone", NotificationAct.this);
                        } else {
//                            r = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse("android.resource://"
//                                    + context.getPackageName() + "/" + R.raw.taxi_arrived));
                            audio_url = SessionSave.getSession("book_now_tone", NotificationAct.this);

                            pickupTime_layout.setVisibility(View.GONE);
                        }
                    }
                    model_name = details.getString("model_name");
                    model_name_txt.setText(model_name);

                    if (!pickup_notes.equalsIgnoreCase("")) {
                        notesTxt.setVisibility(View.VISIBLE);
                        notesTxt.setText(notes);
                    } else {
                        notesTxt.setVisibility(View.GONE);
                    }


                    if (tripdetails.has("trip_type")) {
                        trip_type = tripdetails.getString("trip_type");

                        if (trip_type.equals("0"))
                            tv_tripType.setText(NC.getString(R.string.trip_type_normal));
                        else if (trip_type.equals("2"))
                            tv_tripType.setText(NC.getString(R.string.trip_type_rental));
                        else if (trip_type.equals("3"))
                            tv_tripType.setText(NC.getString(R.string.trip_type_outstation));
                        else if (trip_type.equals("22"))
                            tv_tripType.setText(NC.getString(R.string.corporate_trip));
                    }

                    if (tripdetails.has("is_on_my_way_trip")) {
                        if (tripdetails.getString("is_on_my_way_trip").equals("0"))
                            trip_types.setText(NC.getString(R.string.triptype)+" : "+NC.getString(R.string.trip_normal));
                        else if (tripdetails.getString("is_on_my_way_trip").equals("1"))
                            trip_types.setText(NC.getString(R.string.triptype)+" : "+NC.getString(R.string.trip_onmyway));
                    }



                    pickup = details.getString("pickupplace");
                    pickup_lat = details.getDouble("pickup_latitude");
                    pickup_lng = details.getDouble("pickup_longitude");
                    String dropLocation = details.getString("dropplace");
                    String dropLattitude = details.getString("drop_latitude");
                    String dropLongitude = details.getString("drop_longitude");

                    drop_lattitude = details.getDouble("drop_latitude");
                    drop_longitude = details.getDouble("drop_longitude");

                    pickup = pickup.trim();
                    if (pickup.length() != 0)
                        pickup = Character.toUpperCase(pickup.charAt(0)) + pickup.substring(1);
                    drop = details.getString("drop");
                    drop = drop.trim();
                    if (drop.length() != 0 || !drop.equals(""))
                        drop = Character.toUpperCase(drop.charAt(0)) + drop.substring(1);
                    else
                        droplayout.setVisibility(View.GONE);


                    if (tripdetails.has("stops_array"))
                        stops = tripdetails.getJSONArray("stops_array");

                    if (stops != null && stops.length() > 0) {
                        parseStop(stops.toString());
                    } else {
                        droplayout.setVisibility(View.GONE);
                        pick_lay.setVisibility(View.GONE);
                        //sabari
                        // createPickAndStopView(pickup, pickup_lat, pickup_lng, dropLocation, dropLattitude, dropLongitude);
                    }

                   // pickup_time = details.getString("pickup_time");

                    String[] result = details.getString("pickup_time").split(" ");
                    pickup_time = result[1];
                            passenger_phone = details.getString("passenger_phone");
                    passenger_id = details.getString("passenger_id");
                    distance = details.getString("distance_away");
                    passenger_name = details.getString("passenger_name");
                    if (details.getString("bookedby").length() != 0) {
                        bookedby = details.getString("bookedby");
                    }
                    MainActivity.mMyStatus.setpassengerphone(passenger_phone);
                    passenger_name = passenger_name.trim();
                    if (passenger_name.length() != 0)
                        passenger_name = Character.toUpperCase(passenger_name.charAt(0)) + passenger_name.substring(1);
                    cityname = details.getString("cityname").trim();
                    if (cityname.length() != 0)
                        cityname = Character.toUpperCase(cityname.charAt(0)) + cityname.substring(1);
                    profile_image = details.getString("profile_image");
//new hide may 05
                    set = (AnimatorSet) AnimatorInflater.loadAnimator(NotificationAct.this, R.animator.progress_anim);
                    set.setInterpolator(new DecelerateInterpolator());
                    set.setTarget(donutProgress);
                    set.setDuration((time_out) * 1000L);
                    set.start();

                    double eightee = 0.8 * (time_out+2);
                    double nitefive = 0.95 * (time_out+2);
                    double hundered = 0.99 *(time_out+2);

                    progressIndicator.setMax((int) eightee);
                    progressIndicator1.setMax((int) nitefive);
                    progressIndicator2.setMax((int) hundered);
                    // setProgressValue(0);

                    setProgressValue2();


                    if (!pickup_notes.equalsIgnoreCase("")) {
                        pic_noteslayout.setVisibility(View.VISIBLE);
                        pick_notesTxt.setText(pickup_notes);
                    } else {
                        pic_noteslayout.setVisibility(View.GONE);
                    }

                    if (!drop_notes.equalsIgnoreCase("")) {
                        drop_noteslayout.setVisibility(View.VISIBLE);
                        drop_notesTxt.setText(drop_notes);
                    } else {
                        drop_noteslayout.setVisibility(View.GONE);
                    }


                    Double total_est_amt = 0.0;
                    total_est_amt = Double.parseDouble(estimate_amount);

                    total_estimate_amount.setText(SessionSave.getSession("site_currency", NotificationAct.this) + " " + String.format(Locale.UK, "%.2f", total_est_amt));
                    extra_incentiveAmt.setText(SessionSave.getSession("site_currency", NotificationAct.this) + " 0");

                } catch (final JSONException e) {
                    e.printStackTrace();
                }
                if (notes.length() != 0 && !notes.contains("null")) {
                    text_notes.setText(notes);
                    noteslayout.setVisibility(View.VISIBLE);
                }

                estimate_distanceTxt.setText(est_distance);
                eta_Txt.setText(eta_time + " " + "KM");


                if (!estimate_amount.equalsIgnoreCase("0") && !estimate_amount.contains("null")) {
                    trip_estimate_amount.setText(SessionSave.getSession("site_currency", NotificationAct.this) + " " + estimate_amount);
                    estimatelay.setVisibility(View.VISIBLE);
                } else {
                    trip_estimate_amount.setText(SessionSave.getSession("site_currency", NotificationAct.this) + " 0");
                    estimatelay.setVisibility(View.GONE);
                }

                passNameTxt.setText(passenger_name);
                passenger_name_txt.setText(passenger_name);
                pickup_time_txt.setText(pickup_time);

                pickupLocTxt.setText(pickup);
                dropLocTxt.setText(drop);
                stopsTxt.setText(stops_count);

//                mPlayer = new MediaPlayer();
//                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                try {
//                    mPlayer.setDataSource(audio_url);
//                    mPlayer.prepare();
////                    mPlayer.start();
//                    //Toast.makeText(NotificationAct.this,"Playing",Toast.LENGTH_SHORT).show();
//                } catch (IOException e) {
//                    // Catch the exception
//                    e.printStackTrace();
//                } catch (IllegalArgumentException e) {
//                    e.printStackTrace();
//                } catch (SecurityException e) {
//                    e.printStackTrace();
//                } catch (IllegalStateException e) {
//                    e.printStackTrace();
//                }

                // Timer function runs based on server response and once it finished, Onfinish() method calls the reject _trip API to update the driver timeout status to server.
                countDownTimer = new CountDownTimer((time_out) * 1000L, 1000) {
                    int time = 1;

                    @Override
                    public void onTick(final long millisUntilFinished_) {
                        Systems.out.println("NOTIFY onTick");
                        // r.play();

                        long sec = millisUntilFinished_ / 1000;
                        long minutes = 0;
                        if (sec >= 60) {
                            minutes = sec / 60;
                            sec = sec - (minutes * 60);
                        }
                        minTxt.setText(String.format(Locale.UK, String.valueOf(minutes)));
                        secTxt.setText(String.format(Locale.UK, "%1$02d", sec));
                        if (minutes > 0)
                            remnTimeTxt.setText(String.format("%1$02d", minutes) + " " + NC.getResources().getString(R.string.minutestxt).toUpperCase() + ":" + String.format("%1$02d", sec) + " " + NC.getResources().getString(R.string.secondstxt).toUpperCase() + " " + NC.getResources().getString(R.string.seconds_to_left));
                        else
                            remnTimeTxt.setText(String.format("%1$02d", sec) + " " + NC.getResources().getString(R.string.secondstxt).toUpperCase() + " " + NC.getResources().getString(R.string.seconds_to_left));
                        time++;

                        tone_play();
                    }

                    @Override
                    public void onFinish() {
                        Systems.out.println("NOTIFY onFinish");
                        try {
                            countDownTimer.cancel();
                            //r.stop();
                            tone_stop();
                            JSONObject j = new JSONObject();
                            j.put("trip_id", trip_id);
                            j.put("driver_id", SessionSave.getSession("Id", NotificationAct.this));
                            j.put("taxi_id", SessionSave.getSession("taxi_id", NotificationAct.this));
                            j.put("company_id", SessionSave.getSession("company_id", NotificationAct.this));
                            j.put("reason", "");
                            j.put("reject_type", "0");
                            final String Url = "type=reject_trip";

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!ACCEPT_TRIP_IN_PROGRESS) {

                                        new TripReject(Url, j, 1);
                                    }
                                }
                            }, 500);

                        } catch (Exception e) {
                            countDownTimer.cancel();
                          //  set.cancel();
                            // r.stop();
                            tone_stop();
                            if (NotificationAct.this != null) {
                                final Intent intent = new Intent(NotificationAct.this, MyStatus.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                                CToast.ShowToast(NotificationAct.this, NC.getString(R.string.server_error));
                            }
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
            // If driver accept the trip,following actions will perform.
            accept_btn.setOnClickListener(v -> {

                try {

                    double latitude = 0.0;
                    double longitude = 0.0;

                    ViewEnabledWithDelay(3000, donutProgress);
                    if (NetworkStatus.isOnline(NotificationAct.this)) {
                        if (GPSEnabled(NotificationAct.this)) {
                            countDownTimer.cancel();
                         //   set.cancel();
                            //r.stop();
                            tone_stop();

                            if (locationManager != null) {
                                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                mLastLocation = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if (mLastLocation != null) {
                                    latitude = mLastLocation.getLatitude();
                                    longitude = mLastLocation.getLongitude();
                                }
                            }

                            MainActivity.mMyStatus.settripId(trip_id);
                            SessionSave.saveSession("trip_id", trip_id, NotificationAct.this);
                            MainActivity.mMyStatus.setpassengerId(trip_id);
                            JSONObject j = new JSONObject();
                            j.put("pass_logid", trip_id);
                            j.put("driver_id", SessionSave.getSession("Id", NotificationAct.this));
                            j.put("taxi_id", SessionSave.getSession("taxi_id", NotificationAct.this));
                            j.put("company_id", SessionSave.getSession("company_id", NotificationAct.this));
                            j.put("driver_reply", "A");
                            j.put("field", "rejection");
                            j.put("flag", "0");
                            j.put("latitude", latitude);
                            j.put("longitude", longitude);
                            final String Url = "type=driver_reply";
                            Systems.out.println("result" + "Sucess");
                            new TripAccept(Url, j);
                        } else {
                            CToast.ShowToast(NotificationAct.this, "GPS Connection Failed");
                            countDownTimer.cancel();
                          //  set.cancel();
                            // r.stop();
                            tone_stop();
                            nonactiityobj.startServicefromNonActivity(NotificationAct.this);
                            finish();
                        }
                    } else {
                        countDownTimer.cancel();
                     //   set.cancel();
                        // r.stop();
                        tone_stop();

                        CToast.ShowToast(NotificationAct.this, NC.getResources().getString(R.string.check_net_connection));
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            // If driver decline the trip,following actions will perform.

            decline_btn.setOnClickListener(v -> {

                try {

                    if (NetworkStatus.isOnline(NotificationAct.this)) {
                        countDownTimer.cancel();
                     //   set.cancel();
                        //  r.stop();
                        tone_stop();

                        JSONObject j = new JSONObject();
                        j.put("trip_id", trip_id);
                        j.put("driver_id", SessionSave.getSession("Id", NotificationAct.this));
                        j.put("taxi_id", SessionSave.getSession("taxi_id", NotificationAct.this));
                        j.put("company_id", SessionSave.getSession("company_id", NotificationAct.this));
                        j.put("reason", "");
                        j.put("reject_type", "1");
                        final String Url = "type=reject_trip";
                        new TripReject(Url, j, 2);
                    } else {
                        countDownTimer.cancel();
                    //    set.cancel();
                        tone_stop();
                        // r.stop();
                        CToast.ShowToast(NotificationAct.this, NC.getResources().getString(R.string.check_net_connection));
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setProgressValue2() {
        try {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                int progress;

                @SuppressLint("WrongConstant")
                @Override
                public void run() {
                    progress = progress + 1;


                    progressIndicator.setTrackColor(getResources().getColor(R.color.textviewcolor_light));
                    progressIndicator1.setTrackColor(getResources().getColor(R.color.textviewcolor_light));
                    progressIndicator2.setTrackColor(getResources().getColor(R.color.textviewcolor_light));

                    double eightee = 0.8 * (time_out+2);
                    double nitefive = 0.95 * (time_out+2);

                    progressIndicator.setIndicatorColor(getResources().getColor(R.color.accept_clr));
                    progressIndicator1.setIndicatorColor(getResources().getColor(R.color.quantum_yellow));
                    progressIndicator2.setIndicatorColor(getResources().getColor(R.color.decline_clr));

                    if(progress<=(int) eightee){

                        progressIndicator.setProgressCompat(progress, true);

                    }else if(progress<=(int) nitefive){

                        progressIndicator1.setProgressCompat(progress, true);

                    }else if(progress > (int) nitefive){
                        progressIndicator2.setProgressCompat(progress, true);
                    }



                   /* if (progressIndicator.getProgress() <= (int) eightee) {
                        // BUT : ANDROID STUDIO DOES NOT RESOLVE indicatorColor
                        progressIndicator.setIndicatorColor(getResources().getColor(R.color.accept_clr));
                        progressIndicator.setIndeterminateAnimationType(1);
                    } else if (progressIndicator.getProgress() <= (int) nitefive) {
                        // BUT : ANDROID STUDIO DOES NOT RESOLVE indicatorColor
                        progressIndicator.setIndicatorColor(getResources().getColor(R.color.quantum_yellow));

                    } else if (progressIndicator.getProgress() > (int) nitefive) {
                        // BUT : ANDROID STUDIO DOES NOT RESOLVE indicatorColor
                        progressIndicator.setIndicatorColor(getResources().getColor(R.color.decline_clr));
                    } else {
                        // BUT : ANDROID STUDIO DOES NOT RESOLVE indicatorColor
                        progressIndicator.setIndicatorColor(getResources().getColor(R.color.accept_clr));
                    }*/

                    if (progress == time_out+2) {
                        timer.cancel();
                    }

                }
            };
            timer.schedule(task, 1000, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void tone_play() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop(); // Stop any currently playing audio
            mPlayer.release(); // Release the MediaPlayer resources
        }

        try {
            // Initialize MediaPlayer
            mPlayer = MediaPlayer.create(this, R.raw.newtone); // Use the resource ID for the raw file
            mPlayer.start(); // Start playback

            // Optional: Add a listener to release MediaPlayer when done
            mPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mPlayer = null; // Prevent further operations on the released MediaPlayer
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tone_stop() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop(); // Stop playback
            mPlayer.release(); // Release resources
            mPlayer = null; // Prevent memory leaks
        }
    }


    /**
     * View enabling in display with delay
     *
     * @param i
     * @param butt_onboard
     */
    public void ViewEnabledWithDelay(int i, View butt_onboard) {
        butt_onboard.setEnabled(false);
        new Handler().postDelayed(() -> {
            if (butt_onboard != null)
                butt_onboard.setEnabled(true);
        }, i);
    }


    /**
     * Parse the stops from path
     *
     * @param path
     * @return
     */
   /* private ArrayList parseStop(String path) {
        ArrayList tempstopLists = new ArrayList<>();
        stopListData = new ArrayList<>();
        Gson gson = new Gson();
        Type type = new TypeToken<List<StopData>>() {
        }.getType();
        ArrayList<StopData> stopList = gson.fromJson(path, type);

        for (int i = 0; i < stopList.size(); i++) {
            stopListData.add(stopList.get(i).getLatLng());

            tempstopLists.add(new StopData(0, stopList.get(i).getLat(), stopList.get(i).getLng(), stopList.get(i).getPlaceName(), "", stopList.get(i).getPlaceId()));
        }
        pickUpDropLayout.setData(stopList, "NOTIFY", SessionSave.getSession("Lang", NotificationAct.this));
        return tempstopLists;

    }*/
    private ArrayList<StopData> parseStop(String path) {
        ArrayList<StopData> stopDataArrayList = new ArrayList<>();
        stopListData = new ArrayList<>();
        Gson gson = new Gson();
        Type type = new TypeToken<List<StopData>>() {
        }.getType();

        ArrayList<StopData> stopList = gson.fromJson(path, type);

        for (int i = 0; i < stopList.size(); i++) {
            stopListData.add(stopList.get(i).getLatLng());
            stopDataArrayList.add(stopList.get(i));
        }
        //  pickUpDropView.setData(stopList, "ONGOING", SessionSave.getSession("Lang", OngoingAct.this));
        return stopDataArrayList;

    }

    /**
     * Navigate the screen to home page
     *
     * @param msg
     */
    public void stopTimerAndNavigateToHome(final String msg) {
        countDownTimer.cancel();
       // runOnUiThread(() -> set.cancel());
        // r.stop();
        tone_stop();

        final Intent intent = new Intent(NotificationAct.this, MyStatus.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle extras = new Bundle();
        extras.putString("alert_message", msg);
        intent.putExtras(extras);
        startActivity(intent);
        finish();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CToast.ShowToast(getBaseContext(), msg);
            }
        });

    }

    @Override
    protected void onResume() {
        notificationObject = this;
        super.onResume();
    }

    public static void LatlongValue(Double pickup_lat, Double pickup_long, Double current_lat, Double current_long) {

        pick_lat = pickup_lat;
        pick_long = pickup_long;
        current_lattitude = current_lat;
        current_longitude = current_long;

    }

    /**
     * This method is initalize map settings
     */
    public void initalizemap() {
        try {
            final int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(NotificationAct.this);
            if (resultCode == ConnectionResult.SUCCESS) {
                MapsInitializer.initialize(NotificationAct.this);
                mapWrapperLayout = findViewById(R.id.map_relative_layout);
                mapWrapperLayout.init(map, getPixelsFromDp(this, 39 + 20));
                mapWrapperLayout.setVisibility(View.VISIBLE);
                map.getUiSettings().setZoomControlsEnabled(false);
                map.getUiSettings().setCompassEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                map.setMyLocationEnabled(false);
                map.setPadding(0, 0, 0, 120);
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                try {
// Customise the styling of the base map using a JSON object defined
// in a raw resource file.
                    boolean success = map.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    NotificationAct.this, R.raw.map_style));

                    if (!success) {
                        Systems.out.println("Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Systems.out.println("Can't find style. Error: ");
                }
//                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pickup_lat - 0.001, pickup_lng + 0.0001), 17f));
                if (pick_lat != null) {

                    List<Marker> markersList = new ArrayList<Marker>();
                    markersList.clear();

                    int height = 100;
                    int width = 100;
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.map_icon_green);
                    BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.map_icon_red);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap b2 = bitmapdraw2.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, width, height, false);
SessionSave.saveSession("model_name",model_name,NotificationAct.this);
                    Marker current;
                    if(model_name.equals("Auto"))
                    {
                         current = map.addMarker(new MarkerOptions().position(new LatLng(current_lattitude, current_longitude)).title(NC.getResources().getString(R.string.currentloc)).icon(BitmapDescriptorFactory.fromResource(R.drawable.auto)).draggable(true));

                    }
                    else if(model_name.equals("Bike"))
                    {
                        current = map.addMarker(new MarkerOptions().position(new LatLng(current_lattitude, current_longitude)).title(NC.getResources().getString(R.string.currentloc)).icon(BitmapDescriptorFactory.fromResource(R.drawable.bike)).draggable(true));

                    }
                    else {
                        current = map.addMarker(new MarkerOptions().position(new LatLng(current_lattitude, current_longitude)).title(NC.getResources().getString(R.string.currentloc)).icon(BitmapDescriptorFactory.fromResource(R.drawable.top)).draggable(true));

                    }

                    Marker pickup = map.addMarker(new MarkerOptions().position(new LatLng(pickup_lat, pickup_lng)).title(NC.getResources().getString(R.string.pickuploc)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).draggable(true));


                    markersList.add(current);
                    markersList.add(pickup);


                    if (drop_lattitude != 0 & drop_lattitude != null) {
                        Marker dropoff = map.addMarker(new MarkerOptions().position(new LatLng(drop_lattitude, drop_longitude)).title(NC.getResources().getString(R.string.droploc)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker2)).draggable(true));
                        markersList.add(dropoff);

                    }

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker m : markersList) {
                        builder.include(m.getPosition());
                    }

                    int padding = 160;

                    LatLngBounds bounds = builder.build();

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                    map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            //animate camera here
                            map.animateCamera(cu);


                        }
                    });
                    ArrayList<LatLng> pp = new ArrayList<>();
                    LatLng pickupLatLng = new LatLng(pickup_lat, pickup_lng);
                    LatLng currentLatLng = new LatLng(current_lattitude, current_longitude);


                    pp.add(currentLatLng);
                    pp.add(pickupLatLng);
                    setUpPickUpDropPolyLine(map, this, currentLatLng, pickupLatLng);


                    if (drop_lattitude != 0 & drop_lattitude != null) {
                        LatLng dropLatLng = new LatLng(drop_lattitude, drop_longitude);
                        pp.add(dropLatLng);

                        if (stops != null && stops.length() > 0) {
                            //  parseStop(stops.toString());

                            if (pp != null & map != null & stopListData != null) {

                                setUpPolyLine(map, this, pickupLatLng, dropLatLng, stopListData);


                            }
                        } else {
                            setUpPickUpDropPolyLine2(map, this, pickupLatLng, dropLatLng);

                        }
                    }


                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * draw route from pickup to drop location
     *
     * @param map
     * @param mcontext
     * @param source
     * @param destination
     */
    public void setUpPickUpDropPolyLine(final GoogleMap map, final MainActivity mcontext, final LatLng source, final LatLng destination) {
        this.map = map;
        context = mcontext;
        requestedType = 1;
        mRepository = new MapLoggerRepository(context);
        if (source != null && destination != null) {
            getMultiRouteFromGoogle(source.latitude, source.longitude, destination.latitude, destination.longitude);
        }

    }

    public void setUpPickUpDropPolyLine2(final GoogleMap map, final MainActivity mcontext, final LatLng source, final LatLng destination) {
        this.map = map;
        context = mcontext;
        requestedType = 1;
        mRepository = new MapLoggerRepository(context);
        if (source != null && destination != null) {
            getMultiRouteFromGoogle2(source.latitude, source.longitude, destination.latitude, destination.longitude);
        }

    }


    public void setUpPolyLine(final GoogleMap map, final MainActivity mcontext, final LatLng source, final LatLng destination, ArrayList<LatLng> points) {
        this.map = map;
        context = mcontext;
        requestedType = 1;
        stopListData = points;
        mRepository = new MapLoggerRepository(context);
        if (source != null && destination != null) {
            new GetGoogleRouteLog(source, destination, points).execute();
        }


    }

    /**
     * get direction for pickup to drop location
     */
    private void getMultiRouteFromGoogle2(double P_latitude, double P_longitude, double D_latitude, double D_longitude) {
        String alternatives = "false";
        CoreClient polyline = MyApplication.getInstance().getApiManagerWithoutEncryptBaseUrl();
        Call<ResponseBody> coreResponse = polyline.getPolylineData("https://maps.googleapis.com/maps/api/directions/json", P_latitude + "," + P_longitude, D_latitude + "," + D_longitude, alternatives, SessionSave.getSession(CommonData.GOOGLE_KEY, context));
        coreResponse.enqueue(new RetrofitCallbackClass<ResponseBody>(context, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                String data = null;
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject gson = new JSONObject(response.body().string());
                        drawPolyline(parsePolyline(gson.toString()), 2);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        }));

    }

    /**
     * get direction for pickup to drop location
     */
    private void getMultiRouteFromGoogle(double P_latitude, double P_longitude, double D_latitude, double D_longitude) {
        String alternatives = "false";
        CoreClient polyline = MyApplication.getInstance().getApiManagerWithoutEncryptBaseUrl();
        Call<ResponseBody> coreResponse = polyline.getPolylineData("https://maps.googleapis.com/maps/api/directions/json", P_latitude + "," + P_longitude, D_latitude + "," + D_longitude, alternatives, SessionSave.getSession(CommonData.GOOGLE_KEY, context));
        coreResponse.enqueue(new RetrofitCallbackClass<ResponseBody>(context, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                String data = null;
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject gson = new JSONObject(response.body().string());
                        drawPolyline(parsePolyline(gson.toString()), 1);

                        double distance = 0.0;
                        JSONArray legsArray = new JSONObject(gson.toString()).getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
                        if (legsArray != null) {
                            for (int i = 0; i < legsArray.length(); i++) {
                                JSONObject distanceObject = legsArray.getJSONObject(i).getJSONObject("distance");
                                String distanceString = distanceObject.getString("value");
                                if (distanceString != null && !distanceString.isEmpty()) {
                                    distance += Double.parseDouble(distanceString);
                                }

                            }
                        }
                        double approx_travel_dist = distance / 1000;


                        TextView text = new TextView(context);
                        text.setText(approx_travel_dist + " KM");
                        text.setTextColor(Color.WHITE);
                        text.setPadding(15, 25, 15, 25);
                        IconGenerator generator = new IconGenerator(context);
                        generator.setStyle(IconGenerator.STYLE_BLUE);
                        //generator.setBackground(getResources().getDrawable(R.drawable.pickup));
                        generator.setContentView(text);
                        Bitmap icon = generator.makeIcon();

                        MarkerOptions tp = new MarkerOptions().position(new LatLng(D_latitude, D_longitude)).icon(BitmapDescriptorFactory.fromBitmap(icon));
                        tp.anchor(0.5f, 1.8f);
                        map.addMarker(tp);

                        //  D_latitude+0.0009

                      //  FontHelper.applyFont(context, text);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        }));

    }

    /**
     * parse data from google api response
     */
    public List<List<List<HashMap<String, String>>>> parsePolyline(String... jsonData) {

        JSONObject jObject;
        List<List<List<HashMap<String, String>>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);


            DirectionsJSONParser parser = new DirectionsJSONParser();
            // Starts parsing data
            routes = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    /**
     * Draw route form parsed data
     */
    public void drawPolyline(List<List<List<HashMap<String, String>>>> result, int type) {

        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = new PolylineOptions();


        PolylineOptions lineOptions1 = null;
        MarkerOptions markerOptions = new MarkerOptions();

        Integer size1 = 0;
        Integer size2 = 0;
        Integer size3 = 0;

        List<LatLng> aline1 = new ArrayList<LatLng>();
        List<LatLng> aline2 = new ArrayList<LatLng>();
        List<LatLng> aline3 = new ArrayList<LatLng>();


        if (result != null) {
            int i = 0;
            Log.d("TAG", "onPostExecute: result size " + result.size());

            while (i < result.size()) {

                points = new ArrayList<LatLng>();

                List<List<HashMap<String, String>>> path1 = result.get(i);

                for (int s = 0; s < path1.size(); s++) {
                    Log.d("pathsize1", String.valueOf(path1.size()));

                    // Fetching i-th route
                    List<HashMap<String, String>> path = path1.get(s);
                    Log.d("pathsize", String.valueOf(path.size()));
                    // Fetching all the points in i-th route

                    for (int j = 0; j < path.size(); j++) {
                        lineOptions1 = new PolylineOptions();
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }
                }
                if (i == 0) {

                    size1 = points.size();
                    aline1.addAll(points);
                } else if (i == 1) {
                    aline2.addAll(points);
                    size2 = points.size();
                } else if (i == 2) {
                    aline3.addAll(points);
                    size3 = points.size();
                }
                i++;
            }
        }

        if (size3 != 0) {

            if ((size1 > size2 && size1 > size3)) {
                if (size2 > size3) {
                    PolylineOptions line1 = new PolylineOptions().width(7).color(R.color.app_theme_color);
                    PolylineOptions line2 = new PolylineOptions().width(7).color(R.color.app_theme_color);
                    PolylineOptions line3 = new PolylineOptions().width(8).color(R.color.app_theme_color);

                    line1.addAll(aline1);
                    line2.addAll(aline2);
                    line3.addAll(aline3);
                    map.addPolyline(line1);
                    map.addPolyline(line2);
                    map.addPolyline(line3);

                } else {

                    PolylineOptions line1 = new PolylineOptions().width(7).color(R.color.app_theme_color);
                    PolylineOptions line2 = new PolylineOptions().width(8).color(R.color.app_theme_color);
                    PolylineOptions line3 = new PolylineOptions().width(7).color(R.color.app_theme_color);

                    line1.addAll(aline1);
                    line2.addAll(aline2);
                    line3.addAll(aline3);
                    map.addPolyline(line1);
                    map.addPolyline(line3);
                    map.addPolyline(line2);
                }
            } else if ((size2 > size1 && size2 > size3)) {
                if (size1 > size3) {
                    PolylineOptions line1 = new PolylineOptions().width(7).color(R.color.app_theme_color);
                    PolylineOptions line2 = new PolylineOptions().width(7).color(R.color.app_theme_color);
                    PolylineOptions line3 = new PolylineOptions().width(8).color(R.color.app_theme_color);

                    line1.addAll(aline1);
                    line2.addAll(aline2);
                    line3.addAll(aline3);
                    map.addPolyline(line1);
                    map.addPolyline(line2);
                    map.addPolyline(line3);
                } else {

                    PolylineOptions line1 = new PolylineOptions().width(8).color(R.color.app_theme_color);
                    PolylineOptions line2 = new PolylineOptions().width(7).color(R.color.app_theme_color);
                    PolylineOptions line3 = new PolylineOptions().width(7).color(R.color.app_theme_color);

                    line1.addAll(aline1);
                    line2.addAll(aline2);
                    line3.addAll(aline3);
                    map.addPolyline(line2);
                    map.addPolyline(line3);
                    map.addPolyline(line1);

                }
            } else if ((size3 > size1 && size3 > size2)) {
                if (size1 > size2) {
                    PolylineOptions line1 = new PolylineOptions().width(7).color(R.color.app_theme_color);
                    PolylineOptions line2 = new PolylineOptions().width(8).color(R.color.app_theme_color);
                    PolylineOptions line3 = new PolylineOptions().width(7).color(R.color.app_theme_color);

                    line1.addAll(aline1);
                    line2.addAll(aline2);
                    line3.addAll(aline3);
                    map.addPolyline(line3);
                    map.addPolyline(line1);
                    map.addPolyline(line2);

                } else {
                    PolylineOptions line1 = new PolylineOptions().width(8).color(R.color.app_theme_color);
                    PolylineOptions line2 = new PolylineOptions().width(7).color(R.color.app_theme_color);
                    PolylineOptions line3 = new PolylineOptions().width(7).color(R.color.app_theme_color);

                    line1.addAll(aline1);
                    line2.addAll(aline2);
                    line3.addAll(aline3);
                    map.addPolyline(line3);
                    map.addPolyline(line2);
                    map.addPolyline(line1);
                }
            } else {

            }

        } else if (size2 != 0) {
            if (size1 > size2) {

                PolylineOptions line1 = new PolylineOptions().width(7).color(R.color.app_theme_color);
                PolylineOptions line2 = new PolylineOptions().width(8).color(R.color.app_theme_color);

                line1.addAll(aline1);
                line2.addAll(aline2);
                map.addPolyline(line1);
                map.addPolyline(line2);

            } else {
                PolylineOptions line1 = new PolylineOptions().width(8).color(R.color.app_theme_color);
                PolylineOptions line2 = new PolylineOptions().width(7).color(R.color.app_theme_color);

                line1.addAll(aline1);
                line2.addAll(aline2);
                map.addPolyline(line2);
                map.addPolyline(line1);
            }

        } else if (size1 != 0) {


            if (type == 1) {
                PolylineOptions polyOptions = new PolylineOptions();
                polyOptions.color(getResources().getColor(R.color.black));
                polyOptions.addAll(aline1);
                List<PatternItem> pattern = Arrays.asList(
                        new Gap(10), new Dash(20));
                polyOptions.pattern(pattern);
                Polyline polyline = map.addPolyline(polyOptions);

            } else {

                PolylineOptions line1 = new PolylineOptions();
                line1.width(7);
                line1.color(getResources().getColor(R.color.app_theme_color));
                line1.startCap(new SquareCap());
                line1.endCap(new SquareCap());
                line1.jointType(ROUND);
                line1.addAll(aline1);
                map.addPolyline(line1);
            }


        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        bun.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bun.clear();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        initalizemap();
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

    @Override
    public void onBackPressed() {
    }

    /**
     * Method to create views dynamically if ArrayList<StopData> value not available (ie., Normal flow)
     * <p>
     * New ArrayList of StopData values created with pickup and drop(if available) and dynamic views created based on that ArrayList
     *
     * @param pickup_location
     * @param pickup_latitude
     * @param pickup_longitude
     * @param drop_location
     * @param drop_latitude
     * @param drop_longitude
     */
    private void createPickAndStopView(String pickup_location, double pickup_latitude, double pickup_longitude, String drop_location, String drop_latitude, String drop_longitude) {
        ArrayList<StopData> pickUpDropList = new ArrayList<>();
        StopData pickUpData = new StopData(0, 0.0, 0.0, pickup_location, "", "");
        pickUpDropList.add(pickUpData);
        if (drop_location != null && !drop_location.isEmpty()) {
            StopData dropData = new StopData((1 + new Random().nextInt()), 0.0, 0.0, drop_location, "", "");
            pickUpDropList.add(dropData);
        }
        pickUpDropLayout.setData(pickUpDropList, "NOTIFY", SessionSave.getSession("Lang", NotificationAct.this));
    }

    /**
     * Used to call the trip accept API and parse the response
     */
    private class TripAccept implements APIResult {
        String msg;
        JSONObject jsonObject;

        public TripAccept(final String url, JSONObject data) {
            jsonObject = data;
            Systems.out.println("result" + url);
            ACCEPT_TRIP_IN_PROGRESS = true;

            new APIService_Retrofit_JSON(NotificationAct.this, this, data, false).execute(url);
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {

            ACCEPT_TRIP_IN_PROGRESS = false;
            try {
                if (isSuccess) {

                    final JSONObject json = new JSONObject(result);
                    msg = json.getString("message");
                    CommonData.current_trip_accept = 1;
                    if (json.getInt("status") == 7) {
                        bookedby = "";
                        SessionSave.saveSession("trip_id", "", NotificationAct.this);
                        msg = json.getString("message");
                        Intent i = new Intent(getBaseContext(), MyStatus.class);
                       // showLoading(NotificationAct.this);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        Bundle extras = new Bundle();
                        extras.putString("alert_message", msg);
                        CToast.ShowToast(NotificationAct.this, msg);
                        getApplication().startActivity(i);
                        nActivity.finish();
                    } else if (json.getInt("status") == 1 || bookedby.equals("2")) {
                        SessionSave.saveSession("speedwaiting", "", NotificationAct.this);
                        MainActivity.mMyStatus.settripId(trip_id);
                        SessionSave.saveSession("trip_id", trip_id, NotificationAct.this);
                        SessionSave.saveSession("status", "B",
                                NotificationAct.this);
                        SessionSave.saveSession(CommonData.IS_STREET_PICKUP, false, NotificationAct.this);
                        SessionSave.saveSession("bookedby", bookedby, NotificationAct.this);
                      //  showLoading(NotificationAct.this);
                        final Intent intent = new Intent(NotificationAct.this, OngoingAct.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        Bundle extras = new Bundle();
                        extras.putString("alert_message", msg);
                        intent.putExtras(extras);
                        startActivity(intent);
                        finish();
                    } else if (json.getInt("status") == 5) {
                        SessionSave.saveSession("trip_id", "", NotificationAct.this);
                        msg = json.getString("message");
                        Intent i = new Intent(getBaseContext(), MyStatus.class);
                       // showLoading(NotificationAct.this);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        Bundle extras = new Bundle();
                        extras.putString("alert_message", msg);
                        //i.putExtras(extras);
                        getApplication().startActivity(i);
                        CToast.ShowToast(NotificationAct.this, msg);
                        nActivity.finish();

                    } else if (json.getInt("status") == 25) {
                        runOnUiThread(() -> CToast.ShowToast(NotificationAct.this, NC.getString(R.string.server_error)));
                    } else {
                        runOnUiThread(() -> CToast.ShowToast(NotificationAct.this, msg));
                        finish();
                    }
                } else {

                    runOnUiThread(() -> CToast.ShowToast(NotificationAct.this, NC.getString(R.string.server_error)));
                    finish();
                }
            } catch (final JSONException e) {
                ErrorLogRepository.getRepository(NotificationAct.this).insertAllApiErrorLogs(new ApiErrorModel(0, CommonData.getCurrentTimeForLogger(), "type=driver_reply", ExceptionConverter.INSTANCE.buildStackTraceString(e.getStackTrace()), DriverUtils.INSTANCE.driverInfo(NotificationAct.this), jsonObject, NotificationAct.this.getClass().getSimpleName(), 0));

                ACCEPT_TRIP_IN_PROGRESS = false;
                e.printStackTrace();
            }
        }
    }

    /**
     * Used to call the trip reject API and parse the response
     */
    public class TripReject implements APIResult {
        String msg;
        JSONObject jsonObject;
        int cancel_type;

        public TripReject(final String url, JSONObject data, int type) {
            jsonObject = data;
            new APIService_Retrofit_JSON(NotificationAct.this, this, data, false).execute(url);
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            Log.d("result", "result" + result);
            try {
                if (isSuccess) {
                    CommonData.current_trip_accept = 0;
                    nonactiityobj.startServicefromNonActivity(NotificationAct.this);
                    CommonData.current_trip_accept = 0;
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 6) {
                        msg = json.getString("message");
                    } else if (json.getInt("status") == 7) {
                        msg = json.getString("message");
                        if (json.getString("allow_offline_api").equalsIgnoreCase("1")) {
                            cancel_type = 1;
                        } else {
                            cancel_type = 0;
                        }

                    } else if (json.getInt("status") == 8) {
                        msg = json.getString("message");
                    } else if (json.getInt("status") != 6 || json.getInt("status") != 8 || json.getInt("status") != 3 || json.getInt("status") != 2 || json.getInt("status") != -1) {
                        msg = "Trip has been rejected";
                    } else {
                        msg = "Trip has been already cancelled";
                    }
                    SessionSave.saveSession("trip_id", "", NotificationAct.this);
                   // showLoading(NotificationAct.this);

//                    if (cancel_type == 1) {
//                        shiftFunction();
//                    } else {
                        final Intent intent = new Intent(NotificationAct.this, MyStatus.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    //}

                    CToast.ShowToast(NotificationAct.this, msg);
                } else {
                    runOnUiThread(() -> CToast.ShowToast(NotificationAct.this, NC.getString(R.string.server_error)));
                    finish();
                }
            } catch (final JSONException e) {
                if (NotificationAct.this != null) {
                 //   shiftFunction();
                    final Intent intent = new Intent(NotificationAct.this, MyStatus.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                    CToast.ShowToast(NotificationAct.this, NC.getString(R.string.server_error));
                }
                ErrorLogRepository.getRepository(NotificationAct.this).insertAllApiErrorLogs(new ApiErrorModel(0, CommonData.getCurrentTimeForLogger(), "type=reject_trip", ExceptionConverter.INSTANCE.buildStackTraceString(e.getStackTrace()), DriverUtils.INSTANCE.driverInfo(NotificationAct.this), jsonObject, NotificationAct.this.getClass().getSimpleName(), 0));

                e.printStackTrace();
            }
        }
    }

    private void shiftFunction() {

        try {

            JSONObject j = new JSONObject();
            j.put("driver_id", SessionSave.getSession("Id", NotificationAct.this));
            j.put("shiftstatus", "OUT");
            j.put("reason", "");
            Log.e("shiftbefore ", j.toString());
            j.put("update_id", SessionSave.getSession("Shiftupdate_Id", NotificationAct.this));
            final String Url = "type=driver_shift_status";
            new ShiftStatus(Url, j);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public class ShiftStatus implements APIResult {
        String msg;
        JSONObject jsonObject;

        public ShiftStatus(final String url, JSONObject data) {
            jsonObject = data;
            new APIService_Retrofit_JSON(NotificationAct.this, this, data, false).execute(url);
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            Log.d("result", "result" + result);
            try {
                if (isSuccess) {
                    final JSONObject object = new JSONObject(result);
                    if (object.getInt("status") == 1) {

                        dialog1 = Utils.alert_view(NotificationAct.this, NC.getResources().getString(R.string.message), object.getString("message"), NC.getResources().getString(R.string.ok), "", true, NotificationAct.this, "6");
                        Systems.out.println("innnnnn " + "shift_chek_4");
                        SessionSave.saveSession("shift_status", "OUT", NotificationAct.this);
                        SessionSave.saveSession("trip_id", "", NotificationAct.this);
                        SessionSave.setWaitingTime(0L, NotificationAct.this);
                        Log.e("sess", SessionSave.getSession("shift_status", NotificationAct.this));
                        nonactiityobj.stopServicefromNonActivity(NotificationAct.this);

                        final Intent intent = new Intent(NotificationAct.this, MyStatus.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();

                    } else {
                        dialog1 = Utils.alert_view(NotificationAct.this, NC.getResources().getString(R.string.message), object.getString("message"), NC.getResources().getString(R.string.ok), "", true, NotificationAct.this, "3");

                    }
                } else {
                    runOnUiThread(() -> CToast.ShowToast(NotificationAct.this, NC.getString(R.string.server_error)));
                    finish();
                }
            } catch (final JSONException e) {

                e.printStackTrace();
            }
        }
    }

    private class DirectionsJSONParser {
        /**
         * Receives a JSONObject and returns a list of lists containing latitude and
         * longitude
         */
        public List<List<List<HashMap<String, String>>>> parse(JSONObject jObject) {

            List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
            List<List<List<HashMap<String, String>>>> routes1 = new ArrayList<List<List<HashMap<String, String>>>>();
            JSONArray jRoutes = null;
            JSONArray jLegs = null;
            JSONArray jSteps = null;

            try {

                jRoutes = jObject.getJSONArray("routes");

                /** Traversing all routes */
                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                    List path = new ArrayList<HashMap<String, String>>();
                    List path1 = new ArrayList<ArrayList<HashMap<String, String>>>();

                    // Log.d("legs",jLegs.toString());
                    /** Traversing all legs */
                    for (int j = 0; j < jLegs.length(); j++) {
                        HashMap<String, String> hm1 = new HashMap<String, String>();
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                        // Log.d("steps",jSteps.toString());
                        /** Traversing all steps */
                        for (int k = 0; k < jSteps.length(); k++) {

                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);
                            //  Log.d("polyline",polyline.toString());
                            /** Traversing all points */

                            for (int l = 0; l < list.size(); l++) {
                                HashMap<String, String> hm = new HashMap<String, String>();
                                hm.put("lat", Double.toString(list.get(l).latitude));
                                hm.put("lng", Double.toString(list.get(l).longitude));

                                path.add(hm);
                                //  Log.d("lat", Double.toString(((LatLng)list.get(l)).latitude));
                                //  Log.d("lng", Double.toString(((LatLng)list.get(l)).longitude));
                            }

                        }

                        path1.add(path);

                    }
                    routes1.add(path1);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }

            return routes1;
        }

        /**
         * Method to decode polyline points
         * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
         */
        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }

    }


    String overViewPolyLine = "";

    private class GetGoogleRouteLog extends AsyncTask<Void, Void, GoogleMapModel> {
        private final double P_latitude;
        private final double P_longitude;
        private final double D_latitude;
        private final double D_longitude;
        private String from = "";
        private String to = "";


        public GetGoogleRouteLog(LatLng source, LatLng destination, ArrayList<LatLng> points) {
            this.P_latitude = source.latitude;
            this.P_longitude = source.longitude;
            this.D_latitude = destination.latitude;
            this.D_longitude = destination.longitude;
            stopListData = points;
            from = P_latitude + "," + P_longitude;
            to = D_latitude + "," + D_longitude;
        }

        @Override
        protected GoogleMapModel doInBackground(Void... voids) {
            GoogleMapModel model = mRepository.getGoogleModel(from.trim() + to.trim());
            return model;
        }

        @Override
        protected void onPostExecute(GoogleMapModel model) {
            super.onPostExecute(model);
            if (model != null && !model.routeResult.equals("")) {

                try {
                    parsePolylineFromPoints(new JSONObject(model.routeResult));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Systems.out.println("routee onPostExecute called ");
                getRouteFromGoogle(P_latitude, P_longitude, D_latitude, D_longitude, stopListData);

            }
        }
    }

    public List<LatLng> parsePolylineFromPoints(JSONObject jObject) {
        JSONArray jRoutes = null;
        JSONObject jOverviewPoly = null;

        List path = new ArrayList<LatLng>();

        try {
            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            jOverviewPoly = ((JSONObject) jRoutes.get(0)).getJSONObject("overview_polyline");
            overViewPolyLine = jOverviewPoly.getString("points");

            path = decodePoly(overViewPolyLine);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return path;
    }


    public void getRouteFromGoogle(double P_latitude, double P_longitude, double D_latitude, double D_longitude, ArrayList<LatLng> wayPoints) {
        String wayPointsUrl = makeDirectionUrl(P_latitude, P_longitude, D_latitude, D_longitude, wayPoints);

        CoreClient client = MyApplication.getInstance().getApiManagerWithoutEncryptBaseUrl();

        //     Call<ResponseBody> coreResponse = client.getPolylineDataWithWayPoint("https://maps.googleapis.com/maps/api/directions/json", p_lat + "," + p_lng, d_lat + "," + d_lng, wayPointsUrl, SessionSave.getSession(CommonData.GOOGLE_KEY, mContext));
        Call<ResponseBody> coreResponse = client.getPolylineDataWithWayPoint("https://maps.googleapis.com/maps/api/directions/json", P_latitude + "," + P_longitude, D_latitude + "," + D_longitude, wayPointsUrl, SessionSave.getSession(CommonData.GOOGLE_KEY, NotificationAct.this));

        coreResponse.enqueue(new RetrofitCallbackClass<>(NotificationAct.this, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                String data = null;
                if (response.isSuccessful()) {
                    try {

                        JSONObject gson = new JSONObject(response.body().string());
                        Systems.out.println("routee onResponse " + gson.getString("status") + requestedType);
                        if (!gson.getString("status").equalsIgnoreCase("OK")) {
                        } else {
                            // saveGoogleLog(P_latitude + "," + P_longitude + D_latitude + "," + D_longitude, gson.toString());
                            drawRoutePolyline(parsePolylineFromPoints(gson));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        }));

    }

    private List<LatLng> decodePoly(String encodedPath) {
        int len = encodedPath.length();
        List<LatLng> path = new ArrayList();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;

            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 31);

            lat += (result & 1) != 0 ? ~(result >> 1) : result >> 1;
            result = 1;
            shift = 0;

            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 31);

            lng += (result & 1) != 0 ? ~(result >> 1) : result >> 1;
            path.add(new LatLng((double) lat * 1.0E-5D, (double) lng * 1.0E-5D));
        }

        return path;
    }


    private List<LatLng> listLatLng;
    private Polyline blackPolyLine, greyPolyLine;


    void drawRoutePolyline(List<LatLng> result) {
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = new PolylineOptions();
        listLatLng = new ArrayList<>();
        this.listLatLng = result;
        if (map != null && lineOptions != null) {
            /*lineOptions.width(5);
            lineOptions.color(Color.GRAY);
            lineOptions.startCap(new SquareCap());
            lineOptions.endCap(new SquareCap());
            lineOptions.jointType(ROUND);
            blackPolyLine = map.addPolyline(lineOptions);

            PolylineOptions greyOptions = new PolylineOptions();
            greyOptions.width(5);
            greyOptions.color(Color.BLACK);
            greyOptions.startCap(new SquareCap());
            greyOptions.endCap(new SquareCap());
            greyOptions.jointType(ROUND);
            greyPolyLine = map.addPolyline(greyOptions);*/
            List<LatLng> aline1 = new ArrayList<LatLng>();


            PolylineOptions line1 = new PolylineOptions();
            line1.width(7);
            line1.color(getResources().getColor(R.color.app_theme_color));
            line1.startCap(new SquareCap());
            line1.endCap(new SquareCap());
            line1.jointType(ROUND);
            line1.addAll(listLatLng);
            map.addPolyline(line1);

            // animatePolyLine(1000);

            drawMarker();
        }
    }


    public void drawMarker() {

        int height = 70;
        int width = 70;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.map_icon_blue);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        if (stopListData != null && stopListData.size() > 2) {
            for (int i = 1; i < stopListData.size() - 1; i++) {
                map.addMarker(new MarkerOptions()
                        .position(stopListData.get(i))
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            }
        }
    }

    StringBuilder way_point = new StringBuilder();


    public String makeDirectionUrl(double p_lat, double p_lng, double d_lat, double d_lng, ArrayList<LatLng> points) {
        way_point = new StringBuilder();
        if (points != null && points.size() > 0) {
            if (points.size() > 2) {
                p_lat = points.get(0).latitude;
                p_lng = points.get(0).longitude;
                d_lat = points.get(points.size() - 1).latitude;
                d_lng = points.get(points.size() - 1).longitude;
                for (int i = 1; i < points.size() - 1; i++) {
                    way_point.append(points.get(i).latitude);
                    way_point.append(',');
                    way_point.append(points.get(i).longitude);
                    if (i != (points.size() - 2)) {
                        way_point.append("|");
                    }
                }
            } else {
                p_lat = points.get(0).latitude;
                p_lng = points.get(0).longitude;
                d_lat = points.get(points.size() - 1).latitude;
                d_lng = points.get(points.size() - 1).longitude;
            }
        } else {
            p_lat = p_lat;
            p_lng = p_lng;
            d_lat = d_lat;
            d_lng = d_lng;
        }
        return way_point.toString();
    }

}


