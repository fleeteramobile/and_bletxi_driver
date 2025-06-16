package com.onewaytripcalltaxi.driver;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.data.WayPointsData;
import com.onewaytripcalltaxi.driver.data.apiData.ApiRequestData;
import com.onewaytripcalltaxi.driver.data.apiData.EndStreetPickupResponse;
import com.onewaytripcalltaxi.driver.data.apiData.GetTripDetailResponse;
import com.onewaytripcalltaxi.driver.data.apiData.StreetPickUpResponse;
import com.onewaytripcalltaxi.driver.earningchart.EarningsAct;
import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.interfaces.ClickInterface;
import com.onewaytripcalltaxi.driver.interfaces.GetAddress;
import com.onewaytripcalltaxi.driver.interfaces.LocalDistanceInterface;
import com.onewaytripcalltaxi.driver.interfaces.Pickupupdate;
import com.onewaytripcalltaxi.driver.interfaces.StreetPickupInterface;
import com.onewaytripcalltaxi.driver.locationSearch.LocationSearchActivity;
import com.onewaytripcalltaxi.driver.route.Route;
import com.onewaytripcalltaxi.driver.route.StopData;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.service.CoreClient;
import com.onewaytripcalltaxi.driver.service.LocationUpdate;
import com.onewaytripcalltaxi.driver.service.NonActivity;
import com.onewaytripcalltaxi.driver.service.RetrofitCallbackClass;
import com.onewaytripcalltaxi.driver.service.ServiceGenerator;
import com.onewaytripcalltaxi.driver.utils.CToast;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.FontHelper;
import com.onewaytripcalltaxi.driver.utils.GetAddressFromLatLng;
import com.onewaytripcalltaxi.driver.utils.GpsStatus;
import com.onewaytripcalltaxi.driver.utils.LatLngInterpolator;
import com.onewaytripcalltaxi.driver.utils.LocationUtils;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.NetworkStatus;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.StreetMapWrapperLayout;
import com.onewaytripcalltaxi.driver.utils.Systems;
import com.onewaytripcalltaxi.driver.utils.Utils;
import com.onewaytripcalltaxi.driver.utils.drawable_program.Drawables_program;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.onewaytripcalltaxi.driver.data.CommonData.sContext;
import static com.onewaytripcalltaxi.driver.service.LocationUpdate.runningFor;
import static com.onewaytripcalltaxi.driver.service.LocationUpdate.sTimer;
import static com.onewaytripcalltaxi.driver.service.LocationUpdate.slabAccuracy;

/**
 * Created by developer on 10/11/16.
 */

/**
 * This class is used to show StreetPickUp Page from menu and allow to start trip by driver itself
 */
public class StreetPickUpAct extends BaseActivity implements ClickInterface, OnMapReadyCallback, StreetPickupInterface, LocalDistanceInterface, GoogleMap.OnCameraMoveStartedListener, GetAddress {

    public static final String WAITING_TIME_RUN = "waiting_time_run";
    private static final int MY_PERMISSIONS_REQUEST_GPS = 222;
    public static int z = 1;
    public static int retrycount = 1;
    private static final int UPDATE_INTERVAL = 5000; // 10 sec
    private static final int FATEST_INTERVAL = 1000; // 5 sec
    private static final int DISPLACEMENT = 1; // 10 meters
    private static String LocationRequestedBy = "";
    private static Pickupupdate sendPickupPoints;
    private final int LOCATION_REQUEST_TYPE_RETRY = 1;
    private final int LOCATION_REQUEST_TYPE_INITIAL = 2;
    private final int LOCATION_REQUEST_TYPE_COMPLETE_TRIP = 3;
    private final Handler myHandler = new Handler();
    private final int REQUEST_READ_PHONE_STATE = 292;
    public Dialog alertDialog;
    LatLngInterpolator _latLngInterpolator = new LatLngInterpolator.Spherical();
    ArrayList<LatLng> listPoint = new ArrayList<>();
    ArrayList<LatLng> savedpoint = new ArrayList<>();
    ArrayList<LatLng> _trips = new ArrayList<>();
    String checked = "OUT";
    NonActivity nonactiityobj = new NonActivity();
    TextView slideImg;
    LinearLayout tripDetails_lay;
    RelativeLayout slide_lay;
    FrameLayout shadow;
    CardView drop_lay;
    //    FloatingActionButton mov_cur_loc;
    TextView back_txt, no_taxi_assign;
    TextView drop_txt;
    Route route = new Route();
    ArrayList<StopData> stopList = new ArrayList<>();
    LocalBroadcastManager localBroadcastManager;
    ArrayList<LatLng> listStops = new ArrayList<>();

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest mLocationRequest;

    private AppCompatButton btn_emergency_contact;
    private GoogleMap map;
    private float zoom = 16f;
    private Location mLastLocation;
    private double latitude1;
    private double longitude1;
    private StreetMapWrapperLayout mapWrapperLayout;
    private double P_latitude;
    private double P_longitude;
    private Handler handlerServercall1;
    private Runnable callAddress;
    private GetAddressFromLatLng address;
    private ViewGroup searchlay;
    private FrameLayout
            pickup_pinlay;
    private ImageView drop_pin, pickup_pin, pick_fav, drop_close;
    private LinearLayout pickupp, dropppp, drop_fav;
    private TextView currentlocTxt, droplocEdt;
    private Double pickuplat = 0.0;
    private Double pickuplng = 0.0;
    private Double droplat = 0.0;
    private String fav_place_type;
    private String P_Address;
    private Dialog alertmDialog;
    private Double D_longitude;
    private String droplocTxt = "";
    private String pickuplocTxt = "";
    private ViewGroup lay_model_home, lay_pick_fav;
    private LinearLayout home_lay;
    private ImageView home_iv;
    private TextView home_txt;
    private LinearLayout earnings_lay;
    private ImageView earnings_iv;
    private TextView earnings_txt;
    private LinearLayout profile_lay;
    private ImageView profile_iv;
    private TextView profile_txt;
    private LinearLayout streetpick_lay;
    private ImageView streetpick_iv;
    private TextView streetPick_txt;
    //Trip started bottom Lay
    private RelativeLayout carlayout;
    private CoordinatorLayout trip_started_lay;
    private ImageView botton_layout;
    private View botton_navi;
    private LinearLayout design_bottom_sheet;
    private TextView fare_estimate;
    private TextView min_fare;
    private TextView below_miles_val, distance_fare;
    private TextView below_miles_txt;
    private TextView above_miles_val;
    private TextView above_miles_txt;
    private LinearLayout book_now_r;
    private Button butt_start_end;
    private Dialog mDialog;
    private ImageView map_icon;
    private boolean cancelClicked = false;
    private LinearLayout progresss;
    private float bearing;
    private boolean animLocation = false;
    private Marker a_marker;
    private boolean animStarted = false;
    private LatLng savedLatLng;
    private double speed = 0.0;
    private Marker c_marker;
    private ObjectAnimator animator;
    private String Address = "Driver location";
    private Marker _marker;
    private float animteBearing;
    private String METRIC;
    private TextView btn_shift;
    private boolean NOT_FIRST_TIME;
    private CountDownTimer cTimer;
    private Dialog errorDialog;
    private Dialog cDialog;
    private String waitingTime = "";
    private TextView waiting_time;
    BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sTimer = intent.getStringExtra(CommonData.FINAL_WAITING_TIME);
            waiting_time.setText(CommonData.getDateForWaitingTime(SessionSave.getWaitingTime(StreetPickUpAct.this)));
        }
    };
    private TextView tvTaxiSpeed;
    private Dialog dialog1;
    private AppCompatImageView ssWaitingTime_img;
    private AlertDialog alert;
    private FloatingActionButton fab_initial_currentloc;
    private LinearLayout no_taxi_view;
    private boolean isTripStarted = false;
    private Marker curMarker;
    private float bearings;
    private Location latlongfromService;
    private Double droplng = 0.0;
    private Dialog r_mDialog;
    private NetworkStatus networkStatus;
    private final float tilt_value = 70f;
    private View pickup_drop_Sep, view_line_trip;
    private boolean startClicked;
    private ImageView initial_drop_pin;
    private AppCompatButton btn_emergency;
    private String route_path = "";

    private LinearLayout tapCurrentLocation;
    private LatLng mLastLocationTemp;


    public static void registerDistanceInterface(Pickupupdate distanceInterface) {
        sendPickupPoints = distanceInterface;
    }

    /**
     * Getting pixels from dp
     */
    public static int getPixelsFromDp(final Context context, final float dp) {

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    // Initialize the views on layout and variable declarations
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkStatus.appContext = this;
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        setContentView(R.layout.street_pickup_lay);
        networkStatus = new NetworkStatus();
        registerReceiver(networkStatus, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        btn_shift = findViewById(R.id.btn_shift);
        createLocationRequest();
        init();
        sContext = StreetPickUpAct.this;

        MapsInitializer.initialize(StreetPickUpAct.this);


    }

    public void init() {
        btn_emergency_contact = findViewById(R.id.btn_emergency_contact);
        btn_emergency = findViewById(R.id.btn_emergency);


        btn_emergency_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View view1 = View.inflate(StreetPickUpAct.this, R.layout.emergency_alert, null);
                Dialog emergency_dialog = new Dialog(StreetPickUpAct.this, R.style.dialogwinddow);
                emergency_dialog.setContentView(view1);
                emergency_dialog.setCancelable(true);
                emergency_dialog.show();
                final Button button_success = emergency_dialog.findViewById(R.id.button_success);
                final Button button_failure = emergency_dialog.findViewById(R.id.button_failure);
                button_success.setOnClickListener(view22 -> {
                    emergency_dialog.dismiss();
                    startSOSService();
                });
                button_failure.setOnClickListener(view2 -> emergency_dialog.dismiss());
            }
        });
        btn_emergency.setOnClickListener(view -> btn_emergency_contact.performClick());

       /* if (SessionSave.getSession(CommonData.SOS_ENABLED, this, false)) {
            btn_emergency.setVisibility(View.VISIBLE);
            btn_emergency_contact.setVisibility(View.VISIBLE);
        }*/

        FontHelper.applyFont(StreetPickUpAct.this, findViewById(R.id.ongoing_lay));
        Picasso.get().load(SessionSave.getSession("image_path", this) + "headerLogo_driver.png").into((ImageView) findViewById(R.id.headicon));

        if (SessionSave.getSession("Metric", StreetPickUpAct.this).trim().equalsIgnoreCase("miles"))
            METRIC = "miles";
        else
            METRIC = "km";

        Colorchange.ChangeColor((ViewGroup) (((ViewGroup) StreetPickUpAct.this
                .findViewById(android.R.id.content)).getChildAt(0)), StreetPickUpAct.this);


        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        pickup_drop_Sep = findViewById(R.id.pickup_drop_Sep);
        view_line_trip = findViewById(R.id.view_line_trip);
        searchlay = findViewById(R.id.searchlay);
        pickup_pinlay = findViewById(R.id.pickup_pinlay);
        drop_pin = findViewById(R.id.drop_pin);
        initial_drop_pin = findViewById(R.id.initial_drop_pin);
        pickupp = findViewById(R.id.pickupp);
        pickup_pin = findViewById(R.id.pickup_pin);
        currentlocTxt = findViewById(R.id.currentlocTxt);
        lay_pick_fav = findViewById(R.id.lay_pick_fav);
        pick_fav = findViewById(R.id.pick_fav);
        pick_fav.setVisibility(View.VISIBLE);
        fab_initial_currentloc = findViewById(R.id.fab_currentloc_initial);
        botton_navi = findViewById(R.id.botton_navi);
        dropppp = findViewById(R.id.dropppp);
        droplocEdt = findViewById(R.id.droplocTxt);
        drop_fav = findViewById(R.id.drop_fav);
        drop_close = findViewById(R.id.drop_close);
        currentlocTxt.setSelected(true);
        droplocEdt.setSelected(true);
        map_icon = findViewById(R.id.map_icon);
        slideImg = findViewById(R.id.slideImg);
        slideImg.setVisibility(View.GONE);
        no_taxi_view = findViewById(R.id.no_taxi_view);
        no_taxi_assign = findViewById(R.id.no_taxi_assigned);

        lay_pick_fav.setVisibility(View.VISIBLE);
        carlayout = findViewById(R.id.carlayout);
        botton_layout = findViewById(R.id.botton_layout);
        tripDetails_lay = findViewById(R.id.tripDetails_lay);

        drop_lay = findViewById(R.id.drop_lay);
        slide_lay = findViewById(R.id.slide_lay);
        shadow = findViewById(R.id.shadow);
        back_txt = findViewById(R.id.back_txt);
        drop_txt = findViewById(R.id.drop_txt);

        Log.e("_imageurl_", SessionSave.getSession("image_path", this) + "mapmove.png");

        Glide.with(this).load(SessionSave.getSession("image_path", this) + "mapDirection.png").apply(RequestOptions.placeholderOf(R.drawable.mapmove).error(R.drawable.mapmove)).into((ImageView) findViewById(R.id.botton_layout));


        design_bottom_sheet = findViewById(R.id.design_bottom_sheet);
        fare_estimate = findViewById(R.id.fare_estimate);
        waiting_time = findViewById(R.id.waiting_time);
        tvTaxiSpeed = findViewById(R.id.tvTaxiSpeed);
        min_fare = findViewById(R.id.min_fare);
        below_miles_val = findViewById(R.id.below_miles_val);
        below_miles_txt = findViewById(R.id.below_miles_txt);
        above_miles_val = findViewById(R.id.above_miles_val);
        above_miles_txt = findViewById(R.id.above_miles_txt);
        book_now_r = findViewById(R.id.book_now_r);
        butt_start_end = findViewById(R.id.butt_start_end);
        distance_fare = findViewById(R.id.distance_fare);

        lay_model_home = findViewById(R.id.lay_model_home);
        home_lay = findViewById(R.id.home_lay);
        home_iv = findViewById(R.id.home_iv);
        home_txt = findViewById(R.id.home_txt);
        earnings_lay = findViewById(R.id.earnings_lay);
        earnings_iv = findViewById(R.id.earnings_iv);
        earnings_txt = findViewById(R.id.earnings_txt);
        profile_lay = findViewById(R.id.profile_lay);
        profile_iv = findViewById(R.id.profile_iv);
        profile_txt = findViewById(R.id.profile_txt);
        streetpick_lay = findViewById(R.id.streetpick_lay);
        progresss = findViewById(R.id.progresss);
        ssWaitingTime_img = findViewById(R.id.img_start);

        ImageView iv = findViewById(R.id.nodataTxt);
        DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
        Glide.with(StreetPickUpAct.this)
                .load(R.raw.loading_anim)
                .into(imageViewTarget);

        Glide.with(this).load(SessionSave.getSession("image_path", this) + "streetPickupFocus.png").apply(RequestOptions.placeholderOf(R.drawable.st_pickup_focus).error(R.drawable.st_pickup_focus)).into((ImageView) findViewById(R.id.streetpick_iv));

        streetpick_iv = findViewById(R.id.streetpick_iv);
        streetPick_txt = findViewById(R.id.streetPick_txt);
        mapWrapperLayout = findViewById(R.id.map_relative_layout);
//        currentlocTxt.setText(NC.getString(R.string.fetching_address));
//        setPickuplocTxt(NC.getResources().getString(R.string.fetching_address));
        currentlocTxt.setText(NC.getString(R.string.tap_loc));
        setPickuplocTxt(NC.getResources().getString(R.string.tap_loc));
        tapCurrentLocation = findViewById(R.id.tapCurrentLocation);


        btn_shift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_shift.setClickable(false);
                new RequestingCheckBox();
            }
        });
        botton_navi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (map != null && mLastLocation != null) {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), zoom));
                    if (isTripStarted)
                        botton_layout.setVisibility(View.VISIBLE);
                    botton_navi.setVisibility(View.GONE);
                    StreetMapWrapperLayout.setmMapIsTouched(true);
                }

            }
        });
        fab_initial_currentloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (map != null && mLastLocation != null) {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), zoom));
                    fab_initial_currentloc.setVisibility(View.GONE);
                    StreetMapWrapperLayout.setmMapIsTouched(true);
                }
            }
        });

        LocalBroadcastManager.getInstance(StreetPickUpAct.this).registerReceiver(listener,
                new IntentFilter(LocationUpdate.WAITING_TIME));
        if (SessionSave.getSession(CommonData.WAITING_TIME_MANUAL, StreetPickUpAct.this, false)) {
            ssWaitingTime_img.setVisibility(View.VISIBLE);
        } else {
            ssWaitingTime_img.setVisibility(View.GONE);
        }
        if (!SessionSave.getSession(CommonData.ST_WAITING_TIME, StreetPickUpAct.this, false)) {
            ssWaitingTime_img.setImageResource(R.drawable.ic_play_circle);
            CommonData.km_calc = 1;
            if (!SessionSave.getSession("trip_id", StreetPickUpAct.this).equals("")) {
                if (localBroadcastManager != null) {
                    Intent localIntent = new Intent(WAITING_TIME_RUN);
                    localIntent.putExtra(CommonData.WAITING_TIME_START_STOP, CommonData.WAITING_TIME_STOP);
                    localBroadcastManager.sendBroadcast(localIntent);
                }
            }
        } else {
            ssWaitingTime_img.setImageResource(R.drawable.ic_pause_circle);
            CommonData.km_calc = 0;
            if (!SessionSave.getSession("trip_id", StreetPickUpAct.this).equals("")) {
                if (localBroadcastManager != null) {
                    Intent localIntent = new Intent(WAITING_TIME_RUN);
                    localIntent.putExtra(CommonData.WAITING_TIME_START_STOP, CommonData.WAITING_TIME_START);
                    localBroadcastManager.sendBroadcast(localIntent);
                }
            }
        }

        Systems.out.println("sTimer StreetPickup" + sTimer + "____" + SessionSave.getWaitingTime(StreetPickUpAct.this));
        ssWaitingTime_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SessionSave.getSession(CommonData.ST_WAITING_TIME, StreetPickUpAct.this, false)) {
                    CommonData.km_calc = 0;
                    if (!SessionSave.getSession("trip_id", StreetPickUpAct.this).equals("")) {
                        if (localBroadcastManager != null) {
                            Intent localIntent = new Intent(WAITING_TIME_RUN);
                            localIntent.putExtra(CommonData.WAITING_TIME_START_STOP, CommonData.WAITING_TIME_START);
                            localBroadcastManager.sendBroadcast(localIntent);
                        }
                        ssWaitingTime_img.setImageResource(R.drawable.ic_pause_circle);
                        SessionSave.saveSession(CommonData.ST_WAITING_TIME, true, StreetPickUpAct.this);
                    }
                } else {

                    Systems.out.println("timer started ongoing" + SessionSave.getWaitingTime(StreetPickUpAct.this));
//                    stopService(new Intent(StreetPickUpAct.this, WaitingTimerRun.class));
                    SessionSave.saveSession(CommonData.ST_WAITING_TIME, false, StreetPickUpAct.this);
                    CommonData.km_calc = 1;
                    ssWaitingTime_img.setImageResource(R.drawable.ic_play_circle);
                    if (localBroadcastManager != null) {
                        Intent localIntent = new Intent(WAITING_TIME_RUN);
                        localIntent.putExtra(CommonData.WAITING_TIME_START_STOP, CommonData.WAITING_TIME_STOP);
                        localBroadcastManager.sendBroadcast(localIntent);
                    }
                    //    myHandler.removeCallbacks(r);
                }
            }
        });
        botton_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locationurl;
                if (latitude1 != 0.0 && longitude1 != 0.0 && getDroplat() != 0.0 && getDroplat() != 0.0) {
                    locationurl = "http://maps.google.com/maps?saddr=" + latitude1 + "," + longitude1 + "&daddr=" + getDroplat() + "," + getDroplng();
                    // final String locationurl = "http://maps.google.com/maps?saddr=" + SessionSave.getSession("Driver_locations", OngoingAct.this) + "&daddr=" + mMyStatus.getPassengerOndropLocation().replace("|", "") + "";
                    Log.e("URL_Test", locationurl);
                    final Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(locationurl));
                    startActivity(intent);
                } else {
                    locationurl = "http://maps.google.com/maps?saddr=" + latitude1 + "," + longitude1;
                    // final String locationurl = "http://maps.google.com/maps?saddr=" + SessionSave.getSession("Driver_locations", OngoingAct.this) + "&daddr=" + mMyStatus.getPassengerOndropLocation().replace("|", "") + "";
                    Log.e("URL_Test", locationurl);

                }
                final Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(locationurl));
                startActivity(intent);
            }
        });

        pickupp.setOnClickListener(v -> {
        });
        drop_close.setOnClickListener(v -> {
            if (SessionSave.getSession(CommonData.isNeedtofetchAddress, StreetPickUpAct.this, true)) {
                drop_fav.setVisibility(View.VISIBLE);
                pickup_drop_Sep.setVisibility(View.GONE);
                lay_pick_fav.setVisibility(View.VISIBLE);
                pick_fav.setImageResource(R.drawable.plus1);
                dropppp.setVisibility(View.GONE);
                pickup_pin.setVisibility(View.VISIBLE);
                pickup_pinlay.setVisibility(View.GONE);
                droplocEdt.setText("");
            } else {
                droplocEdt.setText("");
            }
        });
        dropppp.setOnClickListener(v -> {
            LocationRequestedBy = "D";
            Bundle b = new Bundle();
            b.putString("type", "D");
            Intent i = new Intent(StreetPickUpAct.this, LocationSearchActivity.class);
            i.putExtras(b);
            startActivityForResult(i, CommonData.LocationResult);
        });

        earnings_lay.setOnClickListener(view -> {

            Intent intent = new Intent(StreetPickUpAct.this, EarningsAct.class);
            startActivity(intent);
            finish();

        });

        back_txt.setOnClickListener(v -> {
            Intent intent = new Intent(StreetPickUpAct.this, MyStatus.class);
            startActivity(intent);
        });
        home_lay.setOnClickListener(view -> {
            Intent intent = new Intent(StreetPickUpAct.this, MyStatus.class);
            startActivity(intent);
        });

        profile_lay.setOnClickListener(view -> {
            Intent intent = new Intent(StreetPickUpAct.this, MeAct.class);
            startActivity(intent);
            finish();
        });

        streetpick_lay.setOnClickListener(view -> {
        });

        if (SessionSave.getSession("shift_status", StreetPickUpAct.this).equals("IN")) {

            btn_shift.setText(NC.getString(R.string.online));
            Drawables_program.shift_on(btn_shift);
            nonactiityobj.startServicefromNonActivity(StreetPickUpAct.this);
            SessionSave.saveSession(CommonData.SHIFT_OUT, false, StreetPickUpAct.this);
        } else {

            btn_shift.setText(NC.getString(R.string.offline));
            Drawables_program.shift_bg_grey(btn_shift);
            SessionSave.saveSession(CommonData.SHIFT_OUT, false, StreetPickUpAct.this);
        }

        if (SessionSave.getSession(CommonData.isNeedtofetchAddress, StreetPickUpAct.this, true)) {
            pickupDropVisible();
        } else {
            onlyDropVisible();
        }
        handlerServercall1 = new Handler(Looper.getMainLooper());


        callAddress = () -> {
            Systems.out.println("__________" + z + "____" + StreetPickUpAct.this);
            if (z == 1 && StreetPickUpAct.this != null) {
                try {
                    if (address != null)
                        address.cancel(true);
                    address = new GetAddressFromLatLng(StreetPickUpAct.this, new LatLng(P_latitude, P_longitude), StreetPickUpAct.this, "");
                    address.execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        movetoCurrentloc();
        butt_start_end.setOnClickListener(view -> {
            if (NetworkStatus.isOnline(StreetPickUpAct.this)) {
                if (!currentlocTxt.getText().toString().trim().equals(NC.getString(R.string.fetching_address)))
                    if (SessionSave.getSession("trip_id", StreetPickUpAct.this).trim().equals("")) {
                        LocationUpdate.ClearSession(StreetPickUpAct.this);
                        if (mLastLocation != null)
                            if (!NeedToGetAddress(mLastLocation)) {
                                if (!currentlocTxt.getText().toString().trim().equals(NC.getString(R.string.tap_loc))) {
                                    if (mLastLocation.hasAccuracy() && mLastLocation.getAccuracy() <= slabAccuracy)
                                        callStartTrip();
                                    else
                                        showLowAccuracyAlert();
                                } else {
                                    if (mLastLocation.hasAccuracy() && mLastLocation.getAccuracy() <= slabAccuracy) {
                                        P_latitude = mLastLocation.getLatitude();
                                        P_longitude = mLastLocation.getLongitude();
                                        handlerServercall1.postDelayed(callAddress, 0);
                                        startClicked = true;
                                        butt_start_end.setEnabled(false);
                                    } else
                                        showLowAccuracyAlert();
                                }
                            } else {
                                P_latitude = mLastLocation.getLatitude();
                                P_longitude = mLastLocation.getLongitude();
                                handlerServercall1.postDelayed(callAddress, 0);
                                startClicked = true;
                                butt_start_end.setEnabled(false);
                            }
                        else
                            Toast.makeText(StreetPickUpAct.this, NC.getString(R.string.c_tryagain), Toast.LENGTH_SHORT).show();

                    } else {
                        cancelClicked = true;
                        butt_start_end.setEnabled(false);
                        CompleteTrip(StreetPickUpAct.this);

                    }
            } else
                CToast.ShowToast(StreetPickUpAct.this, NC.getString(R.string.check_net_connection));
        });


        cTimer = new CountDownTimer(6000000, 1000) {

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

            }
        };

        if (SessionSave.getSession("driver_type", StreetPickUpAct.this).equalsIgnoreCase("D")) {
            butt_start_end.setClickable(false);
            butt_start_end.setFocusable(false);
        } else {
            butt_start_end.setClickable(true);
            butt_start_end.setFocusable(true);
        }

        if (!SessionSave.getSession("trip_id", StreetPickUpAct.this).trim().equals("")) {
            tripDetails_lay.setVisibility(View.VISIBLE);
        /*    if (SessionSave.getSession(CommonData.SOS_ENABLED, StreetPickUpAct.this, false)) {
                btn_emergency_contact.setVisibility(View.VISIBLE);
            }*/
            slide_lay.setVisibility(View.GONE);
            shadow.setVisibility(View.GONE);

            updateStartTripUI();
            if (!SessionSave.getSession("street_completed", StreetPickUpAct.this).trim().equals("")) {
                final Intent farecal = new Intent(StreetPickUpAct.this, FarecalcAct.class);

                String message = SessionSave.getSession("street_completed", StreetPickUpAct.this);

                farecal.putExtra("from", "direct");
                farecal.putExtra("from_split", true);
                farecal.putExtra("message", message);

                startActivity(farecal);
                finish();
            } else
                callGetTripDetail();
        } else {

            tripDetails_lay.setVisibility(View.GONE);
            btn_emergency_contact.setVisibility(View.GONE);
            slide_lay.setVisibility(View.VISIBLE);
            shadow.setVisibility(View.VISIBLE);
            progresss.setVisibility(View.GONE);

        }


        tapCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLastLocation.getLatitude() != 0.0 && mLastLocation.getLongitude() != 0.0) {
                    if (mLastLocationTemp != null) {
                        if (NeedToGetAddresses(mLastLocation, new LatLng(mLastLocationTemp.latitude, mLastLocationTemp.longitude))) {
                            mLastLocationTemp = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            P_latitude = mLastLocationTemp.latitude;
                            P_longitude = mLastLocationTemp.longitude;
                            handlerServercall1.postDelayed(callAddress, 0);
                        }
                    } else {
                        mLastLocationTemp = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        P_latitude = mLastLocationTemp.latitude;
                        P_longitude = mLastLocationTemp.longitude;
                        handlerServercall1.postDelayed(callAddress, 0);
                    }
                } else
                    CToast.ShowToast(StreetPickUpAct.this, NC.getString(R.string.low_gps_alert_message));
            }
        });
    }

    /**
     * Showing progress dialog
     */
    public void showDialog() {
        try {
            if (NetworkStatus.isOnline(StreetPickUpAct.this)) {
                View view = View.inflate(StreetPickUpAct.this, R.layout.progress_bar, null);
                mDialog = new Dialog(StreetPickUpAct.this, R.style.dialogwinddow);
                mDialog.setContentView(view);
                mDialog.setCancelable(false);
                mDialog.show();

                ImageView iv = mDialog.findViewById(R.id.giff);
                DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
                Glide.with(StreetPickUpAct.this)
                        .load(R.raw.loading_anim)
                        .into(imageViewTarget);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Close dialog
     */
    public void closeDialog() {
        try {
            if (mDialog != null)
                if (mDialog.isShowing())
                    mDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This is method for confirmation for complete the trip
     */
    public void CompleteTrip(final AppCompatActivity context) {
        dialog1 = Utils.alert_view_dialog(context, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.confirm_complete), NC.getResources().getString(R.string.yes), NC.getResources().getString(R.string.cancel), false, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dialog.dismiss();
                    if (runningFor() > 10 && !LocationUpdate.DISTANCE_CALCULATION_INPROGRESS) {
                        getCurrentLocation(LOCATION_REQUEST_TYPE_COMPLETE_TRIP);
                    } else {
                        butt_start_end.setEnabled(true);
                        CToast.ShowToast(StreetPickUpAct.this, NC.getString(R.string.distance_calcuation_inprogress));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    butt_start_end.setEnabled(true);
                }
            }
        }, (dialog, which) -> {
            dialog.dismiss();
            butt_start_end.setEnabled(true);
        }, "");

    }

    private void RetryLocationPopUp() {
        closeDialog();
        if (retrycount > 2) {
            dialog1 = Utils.alert_view_dialog(StreetPickUpAct.this, NC.getResources().getString(R.string.message), NC.getString(R.string.address_cant_fetch), NC.getString(R.string.retry), NC.getString(R.string.use_map), false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    retrycount++;
                    showDialog();
                    getCurrentLocation(LOCATION_REQUEST_TYPE_RETRY);
                }
            }, (dialog, which) -> {
                Intent intent = new Intent(StreetPickUpAct.this, SelectDropLocationAct.class);
                intent.putExtra("dropLocation", new LatLng(getDroplat(), getDroplng()));
                startActivityForResult(intent, 300);
            }, "");
        } else {
            dialog1 = Utils.alert_view_dialog(StreetPickUpAct.this, NC.getResources().getString(R.string.message), NC.getString(R.string.address_cant_fetch), NC.getString(R.string.retry), null, false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    retrycount++;
                    showDialog();
                    getCurrentLocation(LOCATION_REQUEST_TYPE_RETRY);

                }
            }, null, "");
        }
    }

    public void getCurrentLocation(int locationRequestType) {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        handleLastLocation(location, locationRequestType);
                    }
                });
        startLocationUpdates();
    }

    private void handleLastLocation(Location location, int locationRequestType) {
        switch (locationRequestType) {
            case LOCATION_REQUEST_TYPE_RETRY:
                new Handler().postDelayed(() -> {
                    if (location.getAccuracy() < 500) {
                        latitude1 = location.getLatitude();
                        longitude1 = location.getLongitude();
                        new GetAddressFromLatLng(StreetPickUpAct.this, new LatLng((latitude1), (longitude1)), StreetPickUpAct.this, "").execute();
                    } else {
                        RetryLocationPopUp();
                    }
                }, 2000);
                break;
            case LOCATION_REQUEST_TYPE_INITIAL:
                mLastLocation = location;
                new Handler().postDelayed(() -> {
                    if (mLastLocation == null) {
                        try {
                            errorInSplash(NC.getString(R.string.error_in_getting_gps));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 30000);
                if (mLastLocation != null) {
                    latitude1 = mLastLocation.getLatitude();
                    longitude1 = mLastLocation.getLongitude();
                    P_latitude = mLastLocation.getLatitude();
                    P_longitude = mLastLocation.getLongitude();
//                    handlerServercall1.postDelayed(callAddress, 0);
                    if (map != null) {
                        movetoCurrentloc();
                    }
                }
                break;
            case LOCATION_REQUEST_TYPE_COMPLETE_TRIP:
                mLastLocation = location;
                if (mLastLocation != null && mLastLocation.getAccuracy() < 200) {
                    showDialog();
                    latitude1 = mLastLocation.getLatitude();
                    longitude1 = mLastLocation.getLongitude();
                    new GetAddressFromLatLng(StreetPickUpAct.this, new LatLng(latitude1, longitude1), StreetPickUpAct.this, "COMPLETE_TRIP").execute();
                } else {
                    RetryLocationPopUp();
                }
                break;
        }
    }

    /**
     * TripDetail API response parsing.
     */
    private void callGetTripDetail() {
        if (map != null) {
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
        }
//        CoreClient client = new ServiceGenerator(StreetPickUpAct.this).createService(CoreClient.class);
        CoreClient client = MyApplication.getInstance().getApiManagerWithEncryptBaseUrl();

        ApiRequestData.TripDetailRequest request = new ApiRequestData.TripDetailRequest();
        request.trip_id = SessionSave.getSession("trip_id", this);
        Call<GetTripDetailResponse> responsed = client.getTripDetail(ServiceGenerator.COMPANY_KEY, request, "en");
        responsed.enqueue(new RetrofitCallbackClass<>(StreetPickUpAct.this, new Callback<GetTripDetailResponse>() {
            @Override
            public void onResponse(Call<GetTripDetailResponse> call, Response<GetTripDetailResponse> response) {

                if (StreetPickUpAct.this != null && response != null && response.body() != null) {
                    GetTripDetailResponse data = response.body();
                    if (data != null) {
                        progresss.setVisibility(View.GONE);
                        String p_taxi_speed = data.detail.taxi_min_speed;
                        if (p_taxi_speed != null && p_taxi_speed.length() > 0) {
                            SessionSave.saveSession("taxi_speed", p_taxi_speed, StreetPickUpAct.this);
                        }
                        if (data.detail.street_pickup_trip.trim().equals("1")) {
//                            SessionSave.saveSession("travel_status", "2", StreetPickUpAct.this);
                            SessionSave.saveSession("travel_status", data.detail.travel_status, StreetPickUpAct.this);
                            SessionSave.saveSession("status", data.detail.driver_status, StreetPickUpAct.this);

//                            if (SessionSave.getSession("travel_status", StreetPickUpAct.this).equals("5")) {
//
////                                Gson gson = new Gson();
////                                String message = gson.toJson(data);
////                                SessionSave.saveSession("street_fare", "0.00", StreetPickUpAct.this);
////                                SessionSave.saveSession("street_completed", message, StreetPickUpAct.this);
////                                farecal.putExtra("from", "");
////                                farecal.putExtra("from_split", true);
////                                farecal.putExtra("message", message);
////                                startActivity(farecal);
////                                finish();
//                                if(SessionSave.getSession("street_completed",StreetPickUpAct.this).equals("")){
//                                    final Intent farecal = new Intent(StreetPickUpAct.this, FarecalcAct.class);
//
//                                }else{
//
//                                }
//
//                            } else {
                            if (!data.detail.manual_waiting_time.equals("")) {
                                SessionSave.saveSession(CommonData.WAITING_TIME_MANUAL, data.detail.manual_waiting_time.equals("1"), StreetPickUpAct.this);
                            }
                            if (SessionSave.getSession(CommonData.WAITING_TIME_MANUAL, StreetPickUpAct.this, false)) {
                                ssWaitingTime_img.setVisibility(View.VISIBLE);
                            } else {
                                ssWaitingTime_img.setVisibility(View.GONE);
                            }

                            tvTaxiSpeed.setText(String.format(Locale.UK, "%.2f", LocationUpdate.speed) + "(" + METRIC.toLowerCase() + "/hr)");
                            waiting_time.setText(CommonData.getDateForWaitingTime(SessionSave.getWaitingTime(StreetPickUpAct.this)));

                            if (!data.detail.drop_location.trim().equals("") && !data.detail.drop_location.trim().equals(NC.getResources().getString(R.string.droploc).trim())) {
                                map.clear();
                                ArrayList<LatLng> pp = new ArrayList<>();
                                pp.add(new LatLng(Double.parseDouble(data.detail.pickup_latitude), Double.parseDouble(data.detail.pickup_longitude)));
                                pp.add(new LatLng(Double.parseDouble(data.detail.drop_latitude), Double.parseDouble(data.detail.drop_longitude)));
                                setDroplocTxt(data.detail.drop_location);
                                dropppp.setVisibility(View.GONE);
                                pickup_drop_Sep.setVisibility(View.VISIBLE);
                                drop_lay.setVisibility(View.VISIBLE);
                                drop_txt.setText(droplocTxt);
                                drop_txt.setSelected(true);
                                route_path = data.detail.route_path;
                                listStops.add(pp.get(0));
                                listStops.add(pp.get(1));
                                if (!route_path.equals("0")) {
                                    route.drawRouteFromPolyline(map, route_path, listStops);
                                } else {
                                    route.setUpPolyLine(map, StreetPickUpAct.this, pp.get(0), pp.get(1), listStops);
                                }


                                map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(data.detail.pickup_latitude), Double.parseDouble(data.detail.pickup_longitude))).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_green)).draggable(false));
                                map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(data.detail.drop_latitude), Double.parseDouble(data.detail.drop_longitude))).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_red)).draggable(true));
                            } else {
                                lay_pick_fav.setVisibility(View.GONE);
                                dropppp.setVisibility(View.GONE);
                            }
                            isTripStarted = true;
                            botton_layout.setVisibility(View.VISIBLE);
                            fab_initial_currentloc.setVisibility(View.GONE);
                            setPickuplocTxt(data.detail.current_location);
                            setPickuplat(Double.parseDouble(data.detail.pickup_latitude));
                            setPickuplng(Double.parseDouble(data.detail.pickup_longitude));
                            updateStartTripUI();
                            drop_fav.setVisibility(View.GONE);
                            currentlocTxt.setOnClickListener(null);
                            dropppp.setOnClickListener(null);
                            movetoCurrentloc();
                            SessionSave.saveSession("Metric", data.detail.metric, StreetPickUpAct.this);
                            if (SessionSave.getSession("Metric", StreetPickUpAct.this).trim().equalsIgnoreCase("miles"))
                                METRIC = "miles";
                            else
                                METRIC = "km";
//                            }

                        } else {
                            startActivity(new Intent(StreetPickUpAct.this, OngoingAct.class));
                            CToast.ShowToast(StreetPickUpAct.this, NC.getString(R.string.you_are_in_trip));
                            finish();
                        }
                    } else {
                        if (StreetPickUpAct.this != null) {
                           // CToast.ShowToast(StreetPickUpAct.this, NC.getString(R.string.server_error));
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<GetTripDetailResponse> call, Throwable t) {
                t.printStackTrace();
                if (StreetPickUpAct.this != null) {
                    //CToast.ShowToast(StreetPickUpAct.this, NC.getString(R.string.server_error));
                }
            }
        }));
    }

    @Override
    protected void onDestroy() {
//        myHandler.removeCallbacks(r);
        unregisterReceiver(networkStatus);
        if (dialog1 != null)
            Utils.closeDialog(dialog1);
        closeDialog();
        // unregister local broadcast
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);

        if (alert != null) {
            if (alert.isShowing()) {
                alert.dismiss();
            }
        }
        super.onDestroy();
    }

    /**
     * Start Trip API response parsing.
     */
    private void callStartTrip() {
        butt_start_end.setEnabled(false);
        LocationUpdate.ClearSession(StreetPickUpAct.this);
        if (map != null) {
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
        }
        retrycount = 1;
//        CoreClient client = new ServiceGenerator(StreetPickUpAct.this).createService(CoreClient.class);
        CoreClient client = MyApplication.getInstance().getApiManagerWithEncryptBaseUrl();
        ApiRequestData.StreetPickRequest request = new ApiRequestData.StreetPickRequest();
        request.driver_id = SessionSave.getSession("Id", this);
        request.taxi_id = SessionSave.getSession("taxi_id", this);
        request.driver_app_version = BuildConfig.VERSION_NAME;
        request.pickup_latitude = String.valueOf(pickuplat);
        request.pickup_longitude = String.valueOf(pickuplng);
        request.drop_latitude = String.valueOf(droplat);
        request.drop_longitude = String.valueOf(droplng);
        request.pickup_location = getPickuplocTxt();
        request.drop_location = getDroplocTxt();
        request.approx_distance = "";
        request.approx_fare = "";
        request.approx_duration = "";
        request.time_to_reach_passen = "";
        request.cityname = "";
        request.motor_model = "";
        LocationUpdate.sLocation = "";
        LocationUpdate.localDistance = 0.0;
        SessionSave.saveSession(CommonData.DRIVER_LOCATION, "", StreetPickUpAct.this);
        showDialog();
        Call<StreetPickUpResponse> response = client.startStreetTrip(ServiceGenerator.COMPANY_KEY, request, SessionSave.getSession("Lang", StreetPickUpAct.this));
        response.enqueue(new RetrofitCallbackClass<>(StreetPickUpAct.this, new Callback<StreetPickUpResponse>() {
            @Override
            public void onResponse(Call<StreetPickUpResponse> call, Response<StreetPickUpResponse> response) {
                closeDialog();
                butt_start_end.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    listStops = new ArrayList<>();
                    if (curMarker != null) {
                        curMarker.remove();
                    }
                    isTripStarted = true;
                    botton_layout.setVisibility(View.VISIBLE);
                    fab_initial_currentloc.setVisibility(View.GONE);
                    StreetPickUpResponse data = response.body();
                    SessionSave.saveSession("travel_status", "2", StreetPickUpAct.this);
                    if (data != null && StreetPickUpAct.this != null) {
                        if (data.status == 1) {
                            view_line_trip.setVisibility(View.VISIBLE);
                            tvTaxiSpeed.setText(String.format(Locale.UK, "%.2f", LocationUpdate.speed) + "(" + METRIC.toLowerCase() + "/hr)");
                            waiting_time.setText(CommonData.getDateForWaitingTime(SessionSave.getWaitingTime(StreetPickUpAct.this)));

                            CToast.ShowToast(StreetPickUpAct.this, data.message);
                            if (sendPickupPoints != null) {
                                SessionSave.saveLastLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), StreetPickUpAct.this);
                                sendPickupPoints.pickUpdate(mLastLocation);
                            }
                            route_path = data.detail.route_path;
                            SessionSave.setDistance(0, StreetPickUpAct.this);
                            SessionSave.setWaitingTime(0L, StreetPickUpAct.this);

                            CommonData.travel_km = 0.0;
                            lay_model_home.setVisibility(View.GONE);
                            SessionSave.saveSession("status", "A", StreetPickUpAct.this);
                            tripDetails_lay.setVisibility(View.VISIBLE);
                         /*   if (SessionSave.getSession(CommonData.SOS_ENABLED, StreetPickUpAct.this, false)) {
                                btn_emergency_contact.setVisibility(View.VISIBLE);
                            }*/
                            slide_lay.setVisibility(View.GONE);
                            shadow.setVisibility(View.GONE);
                            String p_taxi_speed = data.detail.taxi_min_speed;
                            if (p_taxi_speed != null && p_taxi_speed.length() > 0) {
                                SessionSave.saveSession("taxi_speed", p_taxi_speed, StreetPickUpAct.this);
                            }
                            SessionSave.saveSession(CommonData.IS_STREET_PICKUP, true, StreetPickUpAct.this);
                            SessionSave.saveSession("trip_id", String.valueOf(data.detail.driver_tripid), StreetPickUpAct.this);
                            SessionSave.saveSession("min_fare", data.detail.min_fare, StreetPickUpAct.this);
                            SessionSave.saveSession("km_wise_fare", data.detail.km_wise_fare, StreetPickUpAct.this);
                            SessionSave.saveSession("additional_fare_per_km", data.detail.additional_fare_per_km, StreetPickUpAct.this);
                            SessionSave.saveSession("min_km", data.detail.km_wise_fare, StreetPickUpAct.this);
                            SessionSave.saveSession("above_km", data.detail.above_km, StreetPickUpAct.this);
                            SessionSave.saveSession("below_km", data.detail.below_km, StreetPickUpAct.this);
                            SessionSave.saveSession("below_above_km", data.detail.below_above_km, StreetPickUpAct.this);
                            SessionSave.saveSession("Metric", data.detail.metric, StreetPickUpAct.this);
                            SessionSave.saveSession("street_fare", data.detail.min_fare, StreetPickUpAct.this);
                            SessionSave.saveSession("minute_fare", data.detail.minutes_fare, StreetPickUpAct.this);
                            if (SessionSave.getSession("Metric", StreetPickUpAct.this).trim().equalsIgnoreCase("miles"))
                                METRIC = "miles";
                            else
                                METRIC = "km";
                            if (!getDroplocTxt().trim().equals("")) {
                                map.clear();
                                ArrayList<LatLng> pp = new ArrayList<>();
                                pp.add(new LatLng(getPickuplat(), getPickuplng()));
                                pp.add(new LatLng(getDroplat(), getDroplng()));
                                drop_lay.setVisibility(View.VISIBLE);
                                pickup_drop_Sep.setVisibility(View.VISIBLE);
                                drop_txt.setText(getDroplocTxt());
                                listStops.add(pp.get(0));
                                listStops.add(pp.get(1));
                                if (route_path != null && !route_path.isEmpty() && !route_path.equals("0"))
                                    route.drawRouteFromPolyline(map, route_path, listStops);
                                else
                                    route.setUpPolyLine(map, StreetPickUpAct.this, pp.get(0), pp.get(1), listStops);

                                map.addMarker(new MarkerOptions().position(new LatLng(getPickuplat(), getPickuplng())).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_green)).draggable(false));
                                map.addMarker(new MarkerOptions().position(new LatLng(getDroplat(), getDroplng())).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_red)).draggable(true));
                            } else
                                lay_pick_fav.setVisibility(View.GONE);
                            if (droplat == 0.0)
                                dropGone();
                            updateStartTripUI();
                        } else {
                            CToast.ShowToast(StreetPickUpAct.this, data.message);
                        }
                    }
                } else
                    CToast.ShowToast(StreetPickUpAct.this, NC.getString(R.string.server_error));
            }

            @Override
            public void onFailure(Call<StreetPickUpResponse> call, Throwable t) {
                closeDialog();
                butt_start_end.setEnabled(true);
                CToast.ShowToast(StreetPickUpAct.this, NC.getString(R.string.server_error));
            }
        }));
    }

    /**
     * Updating UI after trip start
     * String.format(Locale.UK, "%.2f", sharedPreferences.getFloat("DISTANCE", 0));
     */
    public void updateStartTripUI() {
        boolean km_wise_fare = false;
        try {
            if (!SessionSave.getSession("km_wise_fare", StreetPickUpAct.this).equals(""))
                km_wise_fare = ((int) Float.parseFloat(SessionSave.getSession("km_wise_fare", StreetPickUpAct.this))) == 1;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (!km_wise_fare) {
            above_miles_val.setText(SessionSave.getSession("site_currency", StreetPickUpAct.this) + " " + SessionSave.getSession("above_km", StreetPickUpAct.this) + "/" + METRIC.toLowerCase());
            below_miles_val.setText(SessionSave.getSession("site_currency", StreetPickUpAct.this) + " " + SessionSave.getSession("below_km", StreetPickUpAct.this) + "/" + METRIC.toLowerCase());
            above_miles_txt.setText(NC.getString(R.string.above) + " " + SessionSave.getSession("below_above_km", StreetPickUpAct.this) + " " + METRIC.toLowerCase());
            below_miles_txt.setText(NC.getString(R.string.below) + " " + SessionSave.getSession("below_above_km", StreetPickUpAct.this) + " " + METRIC.toLowerCase());
        } else {
            above_miles_val.setText(SessionSave.getSession("site_currency", StreetPickUpAct.this) + " " + SessionSave.getSession("additional_fare_per_km", StreetPickUpAct.this) + "/" + METRIC.toLowerCase());
            below_miles_val.setText(SessionSave.getSession("site_currency", StreetPickUpAct.this) + " " + SessionSave.getSession("minute_fare", StreetPickUpAct.this));
            above_miles_txt.setText(NC.getString(R.string.above) + " " + SessionSave.getSession("min_km", StreetPickUpAct.this) + " " + METRIC.toLowerCase());
            below_miles_txt.setText(NC.getString(R.string.min_fare));
        }

        min_fare.setText(SessionSave.getSession("site_currency", StreetPickUpAct.this) + " " + SessionSave.getSession("min_fare", StreetPickUpAct.this));

        butt_start_end.setText(NC.getString(R.string.end_trip));

        try {
            view_line_trip.setVisibility(View.VISIBLE);
            distance_fare.setText(SessionSave.getSession("site_currency", StreetPickUpAct.this) + " " + SessionSave.getSession("street_fare", StreetPickUpAct.this));
            fare_estimate.setText(String.format(Locale.UK, "%.2f", (SessionSave.getDistance(StreetPickUpAct.this) + SessionSave.getGoogleDistance(StreetPickUpAct.this))) + " " + METRIC.toLowerCase());
            waitingTime = CommonData.getDateForWaitingTime(SessionSave.getWaitingTime(StreetPickUpAct.this));
            FontHelper.applyFont(StreetPickUpAct.this, fare_estimate, "digital.ttf");
        } catch (Exception e) {
            e.printStackTrace();
        }
        map_icon.setVisibility(View.GONE);
        botton_navi.setVisibility(View.GONE);
        currentlocTxt.setOnClickListener(null);
        dropppp.setOnClickListener(null);
        pick_fav.setVisibility(View.GONE);
        lay_model_home.setVisibility(View.GONE);
        drop_close.setVisibility(View.GONE);
        btn_shift.setVisibility(View.GONE);
        slideImg.setVisibility(View.VISIBLE);

        slideImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StreetPickUpAct.this, MyStatus.class);
                startActivity(intent);
            }
        });

        if (lay_pick_fav != null)
            lay_pick_fav.setVisibility(View.GONE);

        if (map != null)
            map.addMarker(new MarkerOptions().position(new LatLng(getPickuplat(), getPickuplng())).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_green)).draggable(false));
        if (cTimer != null)
            cTimer.start();

    }

    /**
     * End Trip API response parsing.
     */
    private void callEndTrip() {
        stopList = new ArrayList<>();
//        CoreClient client = new ServiceGenerator(StreetPickUpAct.this).createService(CoreClient.class);
        CoreClient client = MyApplication.getInstance().getApiManagerWithEncryptBaseUrl();
        ApiRequestData.EndStreetPickup request = new ApiRequestData.EndStreetPickup();
        request.trip_id = SessionSave.getSession("trip_id", this);

        request.drop_latitude = String.valueOf(latitude1);
        request.drop_longitude = String.valueOf(longitude1);
        waitingTime = CommonData.getDateForWaitingTime(SessionSave.getWaitingTime(StreetPickUpAct.this));
        if (waitingTime.equals(""))
            waitingTime = "00:00:00";
        String waitNoArabic = FontHelper.convertfromArabic(waitingTime);
        String[] split = waitNoArabic.split(":");
        int hr = Integer.parseInt(split[0]);
        float min = Integer.parseInt(split[1]);
        float sec = Float.parseFloat(split[2]);
        Systems.out.println("Hour:" + hr + "min:" + min + "sec:" + sec);
        min = min / 60;
        sec = sec / 3600;
        float waitingHr = hr + min + sec;
        MainActivity.mMyStatus.setDriverWaitingHr(Float.toString(waitingHr));
        SessionSave.saveSession("waitingHr", Float.toString(waitingHr), StreetPickUpAct.this);
        request.driver_app_version = BuildConfig.VERSION_NAME;
        request.distance = String.valueOf(SessionSave.getDistance(StreetPickUpAct.this) + SessionSave.getGoogleDistance(StreetPickUpAct.this));
        request.waiting_hour = SessionSave.getSession("waitingHr", StreetPickUpAct.this);
        request.drop_location = getDroplocTxt();
        request.actual_distance = String.valueOf(SessionSave.getDistance(StreetPickUpAct.this) + SessionSave.getGoogleDistance(StreetPickUpAct.this));
        request.distance_time = "";
        request.new_distance = LocationUpdate.localDistance;
        stopList.add(new StopData(0, pickuplat, pickuplng, getPickuplocTxt(), "", ""));
        stopList.add(new StopData(0, latitude1, longitude1, getDroplocTxt(), "", ""));
        request.stops = stopList;
        JSONArray jsonArray = SessionSave.ReadGoogleWaypoints(StreetPickUpAct.this);
        WayPointsData data = new WayPointsData();
        ArrayList<WayPointsData> yourClassList = null;
        try {
            Type listType =
                    new TypeToken<ArrayList<WayPointsData>>() {
                    }.getType();
            yourClassList = new Gson().fromJson(jsonArray.toString(), listType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.waypoints = yourClassList;

        Call<EndStreetPickupResponse> response = client.endStreetTrip(ServiceGenerator.COMPANY_KEY, request, "en");

        response.enqueue(new RetrofitCallbackClass<>(StreetPickUpAct.this, new Callback<EndStreetPickupResponse>() {
            @Override
            public void onResponse(Call<EndStreetPickupResponse> call, Response<EndStreetPickupResponse> response) {
                closeDialog();
                butt_start_end.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    EndStreetPickupResponse data = response.body();
                    if (data != null && StreetPickUpAct.this != null) {
                        if (data.status.equals("1") || data.status.equals("4")) {
                            SessionSave.saveSession(CommonData.ST_WAITING_TIME, false, StreetPickUpAct.this);
                            SessionSave.saveSession("travel_status", "", StreetPickUpAct.this);
                            if (cTimer != null)
                                cTimer.cancel();
                            float h = 0.0f;
                            btn_shift.setVisibility(View.VISIBLE);
                            final Intent farecal = new Intent(StreetPickUpAct.this, FarecalcAct.class);

                            Gson gson = new Gson();
                            String message = gson.toJson(data);
                            SessionSave.saveSession("street_fare", "0.00", StreetPickUpAct.this);
                            SessionSave.saveSession("street_completed", message, StreetPickUpAct.this);
                            farecal.putExtra("from", "direct");
                            farecal.putExtra("from_split", true);
                            farecal.putExtra("message", message);
                            startActivity(farecal);
                            finish();
                        } else if (data.status.equals("-1")) {
                            MainActivity.mMyStatus.setOnstatus("");
                            MainActivity.mMyStatus.setOnPassengerImage("");
                            MainActivity.mMyStatus.setOnstatus("Complete");
                            MainActivity.mMyStatus.setOnpassengerName("");
                            MainActivity.mMyStatus.setOndropLocation("");
                            MainActivity.mMyStatus.setOnpickupLatitude("");
                            MainActivity.mMyStatus.setOnpickupLongitude("");
                            MainActivity.mMyStatus.setOndropLatitude("");
                            MainActivity.mMyStatus.setOndropLongitude("");
                            MainActivity.mMyStatus.setOndriverLatitude("");
                            MainActivity.mMyStatus.setOndriverLongitude("");
                            SessionSave.saveSession("status", "F", StreetPickUpAct.this);
                            SessionSave.saveSession("trip_id", "", StreetPickUpAct.this);
                            final Intent jobintent = new Intent(StreetPickUpAct.this, MyStatus.class);
                            startActivity(jobintent);
                            finish();
                        } else
                            CToast.ShowToast(StreetPickUpAct.this, data.message);
                    } else
                        CToast.ShowToast(StreetPickUpAct.this, NC.getString(R.string.server_error));
                } else
                    CToast.ShowToast(StreetPickUpAct.this, NC.getString(R.string.server_error));
            }

            @Override
            public void onFailure(Call<EndStreetPickupResponse> call, Throwable t) {
                butt_start_end.setEnabled(true);
                t.printStackTrace();
                closeDialog();
                CToast.ShowToast(StreetPickUpAct.this, NC.getString(R.string.server_error));
            }
        }));
    }

    /**
     * Getting Drop location longitude
     */
    public Double getDroplng() {
        return droplng;
    }

    /**
     * Getting Drop location latitude
     */
    public Double getDroplat() {
        return droplat;
    }

    /**
     * get pickup longitude
     */
    public Double getPickuplng() {
        return pickuplng;
    }

    /**
     * set pickup longitude
     */
    public void setPickuplng(Double pickuplng) {
        this.pickuplng = pickuplng;
        P_longitude = pickuplng;
    }

    /**
     * get pickup latitude
     */
    public Double getPickuplat() {
        return pickuplat;

    }

    /**
     * set pickup latitude
     */
    public void setPickuplat(Double pickuplat) {
        this.pickuplat = pickuplat;
        P_latitude = pickuplat;
    }

    /**
     * Getting pickup location txt
     */
    public String getPickuplocTxt() {
        return currentlocTxt.getText().toString();
    }

    /**
     * Setting pickup location
     */
    public void setPickuplocTxt(String pickuplocTxt) {
        if (pickuplocTxt != null) {
            this.pickuplocTxt = pickuplocTxt;
            currentlocTxt.setText(pickuplocTxt.replace(", null", ""));
        }
    }

    /**
     * Getting Drop location txt
     */
    public String getDroplocTxt() {
        String dropLoc = "";
        if (droplocEdt.getText() != null)
            if (droplocEdt.getText().toString().trim().equals(NC.getResources().getString(R.string.droploc).trim()))
                dropLoc = "";

            else
                dropLoc = droplocEdt.getText().toString();
        return dropLoc;
    }

    /**
     * Setting drop location text
     */
    public void setDroplocTxt(String droplocTxt) {
        this.droplocTxt = droplocTxt;
        droplocEdt.setText(droplocTxt);

        dropVisible();
    }

    protected void createLocationRequest() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        initializeLocationCallback();
        getCurrentLocation(LOCATION_REQUEST_TYPE_INITIAL);
    }

    private void initializeLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    speed = LocationUpdate.speed;
                    if (location != null && map != null) {
                        if (SessionSave.getSession(CommonData.isNeedtofetchAddress, StreetPickUpAct.this, false)) {
                            if (NeedToGetAddress(location)) {
                                P_latitude = location.getLatitude();
                                P_longitude = location.getLongitude();
//                                handlerServercall1.postDelayed(callAddress, 0);
                            }
                        }
                        mLastLocation = location;
                        if (mapWrapperLayout != null && !mapWrapperLayout.isShown())
                            mapWrapperLayout.setVisibility(View.VISIBLE);
                        latitude1 = location.getLatitude();
                        longitude1 = location.getLongitude();

                        tvTaxiSpeed.setText(String.format(Locale.UK, "%.2f", speed) + "(" + METRIC.toLowerCase() + "/hr)");

                        if (SessionSave.getSession("trip_id", StreetPickUpAct.this).equals("")) {

                            if (curMarker != null) {
                                curMarker.remove();
                            }
                            curMarker = map.addMarker(new MarkerOptions().position(new LatLng(latitude1, longitude1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_green)).draggable(false));
                        }
                        mLastLocation = location;
                        bearing = location.getBearing();
                        bearings = location.getBearing();
                        zoom = map.getCameraPosition().zoom;

                        if (bearing >= 0)
                            bearing = bearing + 90;
                        else
                            bearing = bearing - 90;


                        animate(location);
                    }
                }
            }
        };
    }

    /**
     * visibling and invisibling pickup and dropup address
     */
    public void dropVisible() {

        dropppp.setVisibility(View.VISIBLE);
        pickup_pinlay.setVisibility(View.VISIBLE);
        pickup_drop_Sep.setVisibility(View.VISIBLE);
        pickup_pin.setVisibility(View.GONE);
        if (lay_pick_fav != null)
            lay_pick_fav.setVisibility(View.GONE);
    }

    public void dropGone() {

        dropppp.setVisibility(View.GONE);
        pick_fav.setVisibility(View.VISIBLE);
        pickup_pinlay.setVisibility(View.GONE);
        pickup_pin.setVisibility(View.VISIBLE);
        pickup_drop_Sep.setVisibility(View.GONE);
        if (lay_pick_fav != null)
            lay_pick_fav.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    protected void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onStop() {
        stopLocationUpdates();
        if (handlerServercall1 != null)
            handlerServercall1.removeCallbacks(callAddress);
        if (cTimer != null)
            cTimer.cancel();
        super.onStop();
    }

    /**
     * Starting location updates
     */
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(StreetPickUpAct.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(StreetPickUpAct.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            dialog1 = Utils.alert_view_dialog(StreetPickUpAct.this, "", NC.getResources().getString(R.string.str_loc), NC.getResources().getString(R.string.yes), NC.getResources().getString(R.string.no), true, (dialog, i) -> {
                ActivityCompat.requestPermissions(StreetPickUpAct.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_GPS);
                dialog.dismiss();
            }, (dialog, i) -> dialog.dismiss(), "");
        } else {
            fusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper());
        }

    }

    public void errorInSplash(String message) {

        try {
            if (StreetPickUpAct.this != null) {

                if ((ActivityCompat.checkSelfPermission(StreetPickUpAct.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(StreetPickUpAct.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

                    dialog1 = Utils.alert_view_dialog(StreetPickUpAct.this, "", NC.getResources().getString(R.string.str_loc), NC.getResources().getString(R.string.yes), NC.getResources().getString(R.string.no), false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            ActivityCompat.requestPermissions(StreetPickUpAct.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_GPS);
                            dialog.dismiss();
                        }
                    }, (dialog, i) -> {
                        dialog.dismiss();
                        finish();
                    }, "");
                } else {
                    message = NC.getString(R.string.change_network);
                    Utils.alert_view_dialog_GPS(StreetPickUpAct.this, NC.getResources().getString(R.string.location_disable),
                            message,
                            NC.getResources().getString(R.string.enable),
                            "", false, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent mIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(mIntent);
                                }
                            }, (dialog, which) -> dialog.dismiss(), "");
                }
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean NeedToGetAddresses(Location currentLocation, LatLng previousLocation) {
        float[] dis = new float[1];
        Location.distanceBetween(previousLocation.latitude, previousLocation.longitude, currentLocation.getLatitude(), currentLocation.getLongitude(), dis);
        return dis[0] > 200;
    }


    private boolean NeedToGetAddress(Location location) {
        if (location != null && SessionSave.getSession("trip_id", StreetPickUpAct.this).equals("")) {
            float[] dis = new float[1];
            Location.distanceBetween(P_latitude, P_longitude, location.getLatitude(), location.getLongitude(), dis);
            return dis[0] > 400;
        }
        return false;
    }

    private void animate(Location location) {
        try {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            // marker Animation Function
            if (!animLocation) {
                listPoint.add(latLng);
            } else {
                savedpoint.add(latLng);
            }
            if (listPoint.size() > 1) {

                if (a_marker != null) {
                    a_marker.setVisible(false);
                    a_marker.remove();
                }

                if (!animStarted) {
                    if (savedLatLng != null) {
                        listPoint.set(0, savedLatLng);
                        Systems.out.println("savedLatLng animation fst" + listPoint.get(0));
                    }
                    if (speed > 2) {
                        animStarted = true;
                        animLocation = true;
                        c_marker = map.addMarker(new MarkerOptions().position(listPoint.get(0)).rotation(0).anchor(0.5f, 0.5f).title(Address).icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_img)));
                        c_marker.setVisible(true);
                        Systems.out.println("bearing" + bearing);

                        CameraPosition camPos = CameraPosition
                                .builder(
                                        map.getCameraPosition() // current Camera
                                )
                                .bearing(bearings)
                                .build();

                        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(camPos, zoom));
                        if (StreetMapWrapperLayout.ismMapIsTouched()) {
                            map.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
                        }
                        savedLatLng = listPoint.get(listPoint.size() - 1);
                        animateLine(listPoint, c_marker, bearings);
                    } else {
                        if (c_marker != null) {
                            c_marker.setVisible(false);
                            c_marker.remove();
                        }
                        Systems.out.println("GpsStatus.ischecked  out" + GpsStatus.ischecked);

                        if (GpsStatus.ischecked == 0) {
                            Systems.out.println("GpsStatus.ischecked " + GpsStatus.ischecked);
                            GpsStatus.ischecked = 1;
                            a_marker = map.addMarker(new MarkerOptions().position(latLng).rotation(0).anchor(0.5f, 0.5f).title(Address).icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_img)));
                            a_marker.setVisible(true);

                            CameraPosition camPos = CameraPosition
                                    .builder(
                                            map.getCameraPosition() // current Camera
                                    )
                                    .bearing(bearings)
                                    .build();
                            map.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));


                        } else {

                            a_marker = map.addMarker(new MarkerOptions().position(listPoint.get(0)).rotation(0).anchor(0.5f, 0.5f).title(Address).icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_img)));
                            a_marker.setVisible(true);

                            CameraPosition camPos = CameraPosition
                                    .builder(
                                            map.getCameraPosition() // current Camera
                                    )
                                    .bearing(bearings)
                                    .build();
                            map.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));


                        }
                    }
                }

            }

            bearing = 0;
            bearings = 0;
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    /**
     * Animate the map marker
     */
    public void animateLine(ArrayList<LatLng> Trips, Marker marker, float bearings) {
        _trips.clear();
        _trips.addAll(Trips);
        _marker = marker;
        animteBearing = bearings;
        animateMarker();
    }

    /**
     * Animate the map marker
     */
    public void animateMarker() {
        TypeEvaluator<LatLng> typeEvaluator = (fraction, startValue, endValue) -> _latLngInterpolator.interpolate(fraction, startValue, endValue);
        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");


        for (int i = 0; i < _trips.size(); i++) {
            animator = ObjectAnimator.ofObject(_marker, property, typeEvaluator, _trips.get(i));
        }

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                listPoint.clear();
                if (c_marker != null) {
                    c_marker.setVisible(false);
                    c_marker.remove();
                    a_marker = map.addMarker(new MarkerOptions().position(savedLatLng).rotation(0).anchor(0.5f, 0.5f).title(NC.getResources().getString(R.string.you_are_here)).icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_img)));
                    a_marker.setVisible(true);
                    if (StreetMapWrapperLayout.ismMapIsTouched()) {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(savedLatLng, zoom));
                    }
                    if (savedpoint.size() > 1) {
//                        for (int i = 0; i < savedpoint.size(); i++) {
//                            listPoint.add(savedpoint.get(i));
//                        }
                        listPoint.addAll(savedpoint);
                        savedpoint.clear();
                        animStarted = false;
                        animLocation = true;
                    } else {
                        animStarted = false;
                        animLocation = false;
                    }
                }

            }
        });
        animator.setDuration(5000);
        animator.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_GPS:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper());
                }
                break;
            case REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startSOSService();
                    }
                }
                break;
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result = "";
        Double obtainedlatitude, obtainedlongitude;
        try {
            double lat = 0.0, lng = 0.0;
            if (requestCode == 300) {
                if (data != null) {
                    Bundle res = data.getExtras();
                    Address = res.getString("param_result");
                    SessionSave.saveSession("drop_location", Address, StreetPickUpAct.this);
                    obtainedlatitude = res.getDouble("lat");
                    obtainedlongitude = res.getDouble("lng");
                    latitude1 = obtainedlatitude;
                    longitude1 = obtainedlongitude;
                    if (!SessionSave.getSession(CommonData.LAST_KNOWN_LAT, StreetPickUpAct.this).equals("")) {
                        Double lastknownlatitude = Double.parseDouble(SessionSave.getSession(CommonData.LAST_KNOWN_LAT, StreetPickUpAct.this));
                        Double lastknowlongitude = Double.parseDouble(SessionSave.getSession(CommonData.LAST_KNOWN_LONG, StreetPickUpAct.this));
                        LocalDistanceCalculation.newInstance(StreetPickUpAct.this).haversine(lastknownlatitude, lastknowlongitude, latitude1, longitude1);
                    } else {
                        LocalDistanceCalculation.newInstance(StreetPickUpAct.this).haversine(latitude1, longitude1, latitude1, longitude1);
                    }
                }
            } else {
                try {
                    if (StreetPickUpAct.this != null)
                        startLocationUpdates();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (data != null) {
                    Bundle res = data.getExtras();
                    result = res.getString("param_result");
                    lat = res.getDouble("lat");
                    lng = res.getDouble("lng");
                }
                if (LocationRequestedBy.equals("P") && result != null && !result.trim().equals("")) {
                    currentlocTxt.setText(result);
                    pickuplat = lat;
                    pickuplng = lng;
                    P_latitude = lat;
                    P_longitude = lng;
                    movetoCurrentloc();
                } else if (LocationRequestedBy.equals("D") && result != null && !result.trim().equals("")) {
                    droplocEdt.setText(result);
                    pickup_drop_Sep.setVisibility(View.VISIBLE);
                    droplat = lat;
                    droplng = lng;
                    D_longitude = lng;
                    if (!SessionSave.getSession(CommonData.isNeedtofetchAddress, StreetPickUpAct.this, true)) {
                        drop_fav.setVisibility(View.VISIBLE);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * move the map camera view to current location of the user
     */
    public void movetoCurrentloc() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        if (map != null) {
                            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                            mapWrapperLayout.init(map, getPixelsFromDp(StreetPickUpAct.this, 39 + 20), true);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 16f));
                        }
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnCameraMoveStartedListener(this);
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
        map.setMyLocationEnabled(true);
        new Handler().postDelayed(() -> {
            if (!SessionSave.getSession("trip_id", StreetPickUpAct.this).trim().equals("")) {
                if (ActivityCompat.checkSelfPermission(StreetPickUpAct.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(StreetPickUpAct.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                updateStartTripUI();
            } else {
                movetoCurrentloc();
            }
        }, 500);

    }

    public void CompletetripSuccess(Double latitude, Double longitude) {


        latitude1 = latitude;
        longitude1 = longitude;
        Address = SessionSave.getSession("drop_location", StreetPickUpAct.this);
        if (cancelClicked) {

            long handlerTime = 1000;
            StreetPickUpAct.this.setDroplocTxt(Address);
            boolean distanceCalcInprogress = false;

            JSONArray wayData = SessionSave.ReadGoogleWaypoints(StreetPickUpAct.this);
            try {
                for (int i = 0; i < wayData.length(); i++) {
                    WayPointsData wayPointsData = new Gson().fromJson(wayData.get(i).toString(), WayPointsData.class);
                    if (wayPointsData.getDist() == 0.0)
                        distanceCalcInprogress = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (distanceCalcInprogress) {
                CToast.ShowToast(StreetPickUpAct.this, NC.getString(R.string.calculation_progress));
            } else {
                new Handler().postDelayed(() -> callEndTrip(), handlerTime);
            }
            cancelClicked = false;
        } else
            setDroplocTxt(Address);
    }

    @Override
    public void updateFare(String distanceFare, Location ll) {
        if (ll != null)
            latlongfromService = ll;
        if (distanceFare != null)
            SessionSave.saveSession("street_fare", distanceFare, StreetPickUpAct.this);
        if (!SessionSave.getSession("trip_id", StreetPickUpAct.this).trim().equals("")) {
            if (distanceFare != null && !distanceFare.trim().equals(""))
                distance_fare.setText(SessionSave.getSession("site_currency", StreetPickUpAct.this) + " " + distanceFare);

            waitingTime = CommonData.getDateForWaitingTime(SessionSave.getWaitingTime(StreetPickUpAct.this));
            if (waitingTime.equals(""))
                waitingTime = "00:00:00";
            fare_estimate.setText(String.format(Locale.UK, "%.2f", (SessionSave.getDistance(StreetPickUpAct.this) + SessionSave.getGoogleDistance(StreetPickUpAct.this))) + " " + METRIC.toLowerCase());
            FontHelper.applyFont(StreetPickUpAct.this, fare_estimate, "digital.ttf");
        }
    }

    public void alert_view(Context mContext, String title, String message, String success_txt, String failure_txt) {
        if (alertDialog != null)
            if (alertDialog.isShowing())
                alertDialog.dismiss();
        final View view = View.inflate(mContext, R.layout.alert_view, null);
        alertDialog = new Dialog(mContext, R.style.NewDialog);
        alertDialog.setContentView(view);
        alertDialog.setCancelable(true);
        FontHelper.applyFont(mContext, alertDialog.findViewById(R.id.alert_id));
        alertDialog.show();
        final TextView title_text = alertDialog.findViewById(R.id.title_text);
        final TextView message_text = alertDialog.findViewById(R.id.message_text);
        final Button button_success = alertDialog.findViewById(R.id.button_success);
        final Button button_failure = alertDialog.findViewById(R.id.button_failure);
        button_failure.setVisibility(View.GONE);
        title_text.setText(title);
        message_text.setText(message);
        button_success.setText(success_txt);
        button_success.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    /**
     * Checking network connectivity
     */
    public boolean isOnline() {

        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo networkInfo : info)
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        try {
            if (latlongfromService != null && !SessionSave.getSession("trip_id", StreetPickUpAct.this).equals("")) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latlongfromService.getLatitude(), latlongfromService.getLongitude()), 16f));
                animate(latlongfromService);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        LocationUpdate.registerInterface(StreetPickUpAct.this);
        startLocationUpdates();
        LocalDistanceCalculation.registerDistanceInterface(StreetPickUpAct.this);
    }

    @Override
    public void onBackPressed() {


        Intent intent = new Intent(StreetPickUpAct.this, MyStatus.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void haversineResult(Boolean success) {
        if (success) {
            CompletetripSuccess(latitude1, longitude1);
        }
    }

    @Override
    public void onCameraMoveStarted(int i) {
        if (!StreetMapWrapperLayout.ismMapIsTouched()) {
            botton_layout.setVisibility(View.GONE);
            if (isTripStarted)
                botton_navi.setVisibility(View.VISIBLE);
            else
                fab_initial_currentloc.setVisibility(View.VISIBLE);
        } else {
            if (isTripStarted) {
                botton_layout.setVisibility(View.VISIBLE);
                botton_navi.setVisibility(View.GONE);
            } else
                fab_initial_currentloc.setVisibility(View.GONE);

        }
    }

    @Override
    public void positiveButtonClick(DialogInterface dialog, int id, String s) {
        dialog.dismiss();
    }

    @Override
    public void negativeButtonClick(DialogInterface dialog, int id, String s) {

    }

    @Override
    public void setaddress(double latitude, double longitude, String Address, String type) {
        butt_start_end.setEnabled(true);
        if (Address.length() != 0 && StreetPickUpAct.this != null) {
            try {
                Address = Address.replaceAll("null", "").replaceAll(", ,", "").replaceAll(", ,", "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (SessionSave.getSession("trip_id", StreetPickUpAct.this).trim().equals("")) {

                StreetPickUpAct.this.setPickuplocTxt(Address);
                StreetPickUpAct.this.setPickuplat(latitude);
                StreetPickUpAct.this.setPickuplng(longitude);
                if (startClicked)
                    callStartTrip();
                startClicked = false;
            } else {
                SessionSave.saveSession("drop_location", Address, StreetPickUpAct.this);
                setDroplocTxt(Address);
                haversineResult(true);
             /*   if (!SessionSave.getSession(CommonData.LAST_KNOWN_LAT, StreetPickUpAct.this).equals("")) {
                    Double lastknownlatitude = Double.parseDouble(SessionSave.getSession(CommonData.LAST_KNOWN_LAT, StreetPickUpAct.this));
                    Double lastknowlongitude = Double.parseDouble(SessionSave.getSession(CommonData.LAST_KNOWN_LONG, StreetPickUpAct.this));
                    LocalDistanceCalculation.newInstance(StreetPickUpAct.this).haversine(lastknownlatitude, lastknowlongitude, latitude, longitude);
                } else
                    LocalDistanceCalculation.newInstance(StreetPickUpAct.this).haversine(latitude1, longitude1, latitude1, longitude1);
         */
            }
        } else {

        }


    }


    public void pickupDropVisible() {
        pickupp.setVisibility(View.VISIBLE);
        dropppp.setVisibility(View.GONE);
        initial_drop_pin.setVisibility(View.GONE);
        drop_fav.setVisibility(View.VISIBLE);
        lay_pick_fav.setVisibility(View.VISIBLE);
        pickup_pin.setVisibility(View.VISIBLE);
        pickup_pinlay.setVisibility(View.GONE);
        lay_pick_fav.setOnClickListener(arg0 -> dropVisible());
    }

    public void onlyDropVisible() {
        dropppp.setVisibility(View.VISIBLE);
        pickupp.setVisibility(View.GONE);
        drop_fav.setVisibility(View.VISIBLE);
        initial_drop_pin.setVisibility(View.VISIBLE);
    }

    private void startSOSService() {
        SessionSave.saveSession("sos_id", SessionSave.getSession("Id", StreetPickUpAct.this), StreetPickUpAct.this);
        SessionSave.saveSession("user_type", "d", StreetPickUpAct.this);


      /*  startService(new Intent(StreetPickUpAct.this, SOSService.class));*/
    }

    private void showLowAccuracyAlert() {
        dialog1 = Utils.alert_view_dialog(this, null, NC.getString(R.string.low_gps_alert_message), NC.getString(R.string.ok), NC.getString(R.string.cancel), true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, (dialog, which) -> dialog.dismiss(), "");
    }

    /**
     * Driver Shift API response parsing.
     */

    private class RequestingCheckBox implements APIResult {
        public RequestingCheckBox() {

            try {
                if (btn_shift.getText().toString().equals(NC.getString(R.string.online)))
                    checked = "OUT";
                else
                    checked = "IN";

                JSONObject j = new JSONObject();
                j.put("driver_id", SessionSave.getSession("Id", StreetPickUpAct.this));
                j.put("shiftstatus", checked);
                j.put("update_id", SessionSave.getSession("Shiftupdate_Id", StreetPickUpAct.this));
                j.put("reason", "");
                Log.e("shiftbefore ", j.toString());


                String requestingCheckBox = "type=driver_shift_status";
                if (isOnline())
                    new APIService_Retrofit_JSON(StreetPickUpAct.this, this, j, false).execute(requestingCheckBox);
                else {
                    btn_shift.setClickable(true);
                    dialog1 = Utils.alert_view(StreetPickUpAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, StreetPickUpAct.this, "");
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, final String result) {

            try {

                Log.e("driverstatus", result);

                if (isSuccess && StreetPickUpAct.this != null) {
                    btn_shift.setClickable(true);

                    JSONObject object = new JSONObject(result);
                    if (object.getInt("status") == 1) {
                        if (checked.equals("IN")) {
                            dialog1 = Utils.alert_view(StreetPickUpAct.this, NC.getResources().getString(R.string.message), object.getString("message"), NC.getResources().getString(R.string.ok), "", true, StreetPickUpAct.this, "");
                            btn_shift.setText(NC.getString(R.string.online));
                            Drawables_program.shift_on(btn_shift);
                            SessionSave.saveSession("shift_status", "IN", StreetPickUpAct.this);
                            SessionSave.saveSession("Shiftupdate_Id", object.getJSONObject("detail").getString("update_id"), StreetPickUpAct.this);
                            Log.e("sess", SessionSave.getSession("shift_status", StreetPickUpAct.this));
                            SessionSave.saveSession(CommonData.SHIFT_OUT, false, StreetPickUpAct.this);
                            nonactiityobj.startServicefromNonActivity(StreetPickUpAct.this);
                        } else {
                            dialog1 = Utils.alert_view(StreetPickUpAct.this, NC.getResources().getString(R.string.message), object.getString("message"), NC.getResources().getString(R.string.ok), "", true, StreetPickUpAct.this, "");
                            btn_shift.setText(NC.getString(R.string.offline));
                            Drawables_program.shift_bg_grey(btn_shift);
                            SessionSave.saveSession("shift_status", "OUT", StreetPickUpAct.this);
                            SessionSave.saveSession("trip_id", "", StreetPickUpAct.this);
                            SessionSave.setWaitingTime(0L, StreetPickUpAct.this);
                            Log.e("sess", SessionSave.getSession("shift_status", StreetPickUpAct.this));
                            nonactiityobj.stopServicefromNonActivity(StreetPickUpAct.this);
                        }
                    } else if (object.getInt("status") == -4) {
                        dialog1 = Utils.alert_view(StreetPickUpAct.this, NC.getResources().getString(R.string.message), object.getString("message"), NC.getResources().getString(R.string.ok), "", true, StreetPickUpAct.this, "");
                    } else {
                        dialog1 = Utils.alert_view(StreetPickUpAct.this, NC.getResources().getString(R.string.message), object.getString("message"), NC.getResources().getString(R.string.ok), "", true, StreetPickUpAct.this, "");
                        if (checked.equals("IN")) {
                            btn_shift.setText(NC.getString(R.string.online));
                            Drawables_program.shift_on(btn_shift);
                        } else {
                            btn_shift.setText(NC.getString(R.string.offline));
                            Drawables_program.shift_bg_grey(btn_shift);
                        }
                    }
                } else {

                    runOnUiThread(() -> CToast.ShowToast(StreetPickUpAct.this, NC.getString(R.string.please_check_internet)));
                    btn_shift.setClickable(true);
                    if (checked.equals("IN")) {
                        btn_shift.setText(NC.getString(R.string.online));
                        Drawables_program.shift_on(btn_shift);
                    } else {
                        btn_shift.setText(NC.getString(R.string.offline));
                        Drawables_program.shift_bg_grey(btn_shift);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                btn_shift.setClickable(true);
                dialog1 = Utils.alert_view(StreetPickUpAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.server_error), NC.getResources().getString(R.string.ok), "", true, StreetPickUpAct.this, "");
            }
        }


    }
}