package com.onewaytripcalltaxi.driver;

import static com.onewaytripcalltaxi.driver.service.FirebaseService.BOOKLATER_NOTIFICATION_ID;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.ViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.data.MapWrapperLayout;
import com.onewaytripcalltaxi.driver.earningchart.EarningsAct;
import com.onewaytripcalltaxi.driver.fragments.TripHistoryNew;
import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.more.MoreActivity;
import com.onewaytripcalltaxi.driver.profile.ProfileActivity;
import com.onewaytripcalltaxi.driver.roomDB.GeocoderModel;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON_NoProgress;
import com.onewaytripcalltaxi.driver.service.LocationUpdate;
import com.onewaytripcalltaxi.driver.service.NonActivity;
import com.onewaytripcalltaxi.driver.support.SupportActivity;
import com.onewaytripcalltaxi.driver.tracklocation.TrackLocationActivity;
import com.onewaytripcalltaxi.driver.triplist.CommonTripHistory;
import com.onewaytripcalltaxi.driver.utils.CToast;
import com.onewaytripcalltaxi.driver.utils.ListViewEX;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Systems;
import com.onewaytripcalltaxi.driver.utils.Utils;
import com.onewaytripcalltaxi.driver.utils.drawable_program.Drawables_program;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mayan.sospluginmodlue.service.SOSService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author developer
 */

/**
 * This class is home page where you can select other menus from here
 */
public class MyStatus extends MainActivity   {
    public static boolean GEOCODE_EXPIRY = false;
    // Class members declarations.
    public static GoogleMap googleMap;
    public static MyStatus mystatus;
    // Location updates intervals in sec
    private static final int UPDATE_INTERVAL = 5000; // 10 sec
    private static final int FATEST_INTERVAL = 5000; // 5 sec
    private static final int DISPLACEMENT = 250; // 10 meters
    private final int REQUEST_READ_PHONE_STATE = 292;
    LatLng coordinate;

    String checked = "OUT";
    NonActivity nonactiityobj = new NonActivity();
    Bitmap theBitmap = null;
    Bitmap bm = null;
    int height = 60;
    int width = 100;
    int templength = 0;
    private Marker c_marker;
    private TextView slider;
    private TextView currentLocation1, headerTxt;
    private TextView curlocation;
    private int mapstatus;
    private MapWrapperLayout mapWrapperLayout;

    private final boolean animStarted = false;
    private final boolean animLocation = false;
    // private double speed = 0.0;
    private final float zoom = 16f;
    private TextView Trip_history, Second_trip_history_header, third_trip_history_header;
    private TextView third_tripamt, name_user, wishes, first_tripdate, today_earning, completed_trips, wallet_amount, first_tripmodel, first_tripamt, second_tripdate, second_tripmodel, second_tripamt, third_tripdate, third_tripmodel;
    private LinearLayout trip_detailslay;
    private LinearLayout first_lay, second_lay, third_lay;
    private LinearLayout home_lay, earnings_lay, profile_lay, streetpick_lay, track_now_lay, inbox_lay, onmyway_lay, booking, earnings, profile,support_lay;
    private ImageView home_iv, track_now_iv, imagefirsttrip, imagesecondtrip, imagethirdtrip;
    private TextView lasttripheader, second_trip_header, third_trip_header;
    private TextView btn_shift, no_taxi_assign;
    private Bundle alert_bundle;
    private String alert_msg;

    private String alert_trip_id;

    private Dialog errorDialog;
    private final int HighAccuracyCount = 0;
    private CountDownTimer countDownTimer;
    private boolean doubleBackToExitPressedOnce;
    private LinearLayout animlayout;
    private Dialog alertDialog;
    private FloatingActionButton mov_cur_loc;
    private ViewGroup coordinatorLayout;
    private LinearLayout no_taxi_view;
    private RelativeLayout slide_lay;
    private AsyncTask<String, String, GeocoderModel> getAddress;
    private AppCompatButton btn_emergency;
    private ImageView pick_pin, profile_img;


    Dialog dialog1;
    private Snackbar redAlertSnackBar;
    private ProgressBar progressBar;

    private static final int MY_PERMISSIONS_REQUEST_GPS = 111;

    String recentListMessage = "";


    private LinearLayout tapCurrentLocation;
    private LatLng mLastLocationTemp;


    private Boolean scheduleAlert = false;
    private String scheduleTripId = "";

    private LinearLayout lay_btn, track_location, more;
    private ImageView onmyway_iv;
    RelativeLayout earnings_lays, see_history;

    /**
     * Get the google map pixels from xml density independent pixel.
     */
    public static int getPixelsFromDp(final Context context, final float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean show_alert = intent.getBooleanExtra("show_alert", false);
            if (show_alert) {

                showRedAlert(NC.getString(R.string.low_gps_alert_message));
            } else {
                hideRedAlert();
            }

        }
    };

    /**
     * Set the layout to activity.
     */
    @Override
    public int setLayout() {
        mapstatus = 0;
        setLocale();
        return R.layout.mystatus_lay;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = this;

        LocalBroadcastManager.getInstance(this).registerReceiver(listener,
                new IntentFilter(LocationUpdate.LOCATION_ACCURACY_LOW));

    }


    /**
     * Initialize the views on layout
     */
    @Override
    public void Initialize() {

        btn_emergency = findViewById(R.id.btn_emergency_new);
        lay_btn = findViewById(R.id.lay_btn);
        lay_btn.setVisibility(View.VISIBLE);
        onmyway_iv = findViewById(R.id.onmyway_iv);


        btn_emergency.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                final View view1 = View.inflate(MyStatus.this, R.layout.emergency_alert, null);
                Dialog emergency_dialog = new Dialog(MyStatus.this, R.style.dialogwinddow);
                emergency_dialog.setContentView(view1);
                emergency_dialog.setCancelable(true);
                emergency_dialog.show();
                final Button button_success = emergency_dialog.findViewById(R.id.button_success);
                final Button button_failure = emergency_dialog.findViewById(R.id.button_failure);
                button_success.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        emergency_dialog.dismiss();
                        startSOSService();

                    }
                });
                button_failure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        emergency_dialog.dismiss();
                    }
                });

            }
        });
        animlayout = findViewById(R.id.lay_home);
        coordinatorLayout = findViewById(R.id.mystatus_layout);
      //  if (SessionSave.getSession("need_animation", MyStatus.this, false)) {
         //   AnimationInScreen();
       // }
        Point pointSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(pointSize);
        height = pointSize.x / 6;
        width = pointSize.x / 9;
        CommonData.mActivitylist.add(this);
        CommonData.sContext = this;
        mapstatus = 0;
        CommonData.current_act = "MyStatus";

        try {
            alert_bundle = getIntent().getExtras();
            if (alert_bundle != null) {
                alert_msg = alert_bundle.getString("alert_message");
                alert_trip_id = alert_bundle.getString("alert_trip_id");
                String alertSchedule = alert_bundle.getString("alert_schedule");
                if (alertSchedule != null && alertSchedule.equals("1")) {
                    scheduleAlert = true;
                }
            }
            if (scheduleAlert) {
                if (alert_msg != null && alert_msg.length() != 0) {
                    bookLaterNotificationAlert(alert_msg);
                }
            } else {
                if (alert_msg != null && alert_msg.length() != 0)
                    dialog1 = Utils.alert_view(MyStatus.this, NC.getResources().getString(R.string.message), alert_msg, NC.getResources().getString(R.string.ok), "", true, MyStatus.this, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }






        currentLocation1 = findViewById(R.id.currentlocation);
        Trip_history = findViewById(R.id.trip_history_header);

        Trip_history.setText(Html.fromHtml("<u>" + NC.getResources().getString(R.string.trip_history) + "</u>"));

        btn_shift = findViewById(R.id.btn_shift_new);
        lasttripheader = findViewById(R.id.lasttrip_header);
        second_trip_header = findViewById(R.id.second_trip_header);
        third_trip_header = findViewById(R.id.third_trip_header);
        Second_trip_history_header = findViewById(R.id.Second_trip_history_header);
        third_trip_history_header = findViewById(R.id.third_trip_history_header);
        Second_trip_history_header.setText(Html.fromHtml("<u>" + NC.getResources().getString(R.string.trip_history) + "</u>"));
        third_trip_history_header.setText(Html.fromHtml("<u>" + NC.getResources().getString(R.string.trip_history) + "</u>"));
        first_tripdate = findViewById(R.id.first_tripdate);
        today_earning = findViewById(R.id.today_earning);
        completed_trips = findViewById(R.id.completed_trips);
        wallet_amount = findViewById(R.id.wallet_amount);
        imagefirsttrip = findViewById(R.id.imagefirsttrip);
        imagesecondtrip = findViewById(R.id.imagesecondtrip);
        imagethirdtrip = findViewById(R.id.imagethirdtrip);
        name_user = findViewById(R.id.name_user);
        wishes = findViewById(R.id.wishes);
        name_user.setText(SessionSave.getSession("Name", MyStatus.this));
        first_tripmodel = findViewById(R.id.first_tripmodel);
        first_tripamt = findViewById(R.id.first_tripamt);
        second_tripdate = findViewById(R.id.second_tripdate);
        second_tripmodel = findViewById(R.id.second_tripmodel);
        second_tripamt = findViewById(R.id.second_tripamt);
        third_tripdate = findViewById(R.id.third_tripdate);
        third_tripmodel = findViewById(R.id.third_tripmodel);
        third_tripamt = findViewById(R.id.third_tripamt);
        trip_detailslay = findViewById(R.id.trip_detailslay);
        second_lay = findViewById(R.id.second_lay);
        first_lay = findViewById(R.id.firstlay);
        third_lay = findViewById(R.id.third_lay);
        ImageView headerlogo = findViewById(R.id.headicon);
        headerlogo.setVisibility(View.VISIBLE);
        tapCurrentLocation = findViewById(R.id.tapCurrentLocation);
        track_location = findViewById(R.id.track_location);
        more = findViewById(R.id.more);
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            wishes.setText("Good Morning!");
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            wishes.setText("Good Afternoon!");
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            wishes.setText("Good Evening!");
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            wishes.setText("Good Night!");


        }






        if (SessionSave.getSession(CommonData.isNeedtofetchAddress, MyStatus.this, false)) {
            currentLocation1.setVisibility(View.VISIBLE);
        } else {
            currentLocation1.setVisibility(View.GONE);
        }
        currentLocation1.setText(NC.getString(R.string.tap_loc));

        Picasso.get().load(SessionSave.getSession("image_path", this) + "headerLogo_driver.png").into((ImageView) findViewById(R.id.headicon));

        Log.e("_imagepath_", SessionSave.getSession("image_path", this));

        Trip_history.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyStatus.this, TripHistoryAct.class);
                startActivity(intent);
            }
        });
        Second_trip_history_header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyStatus.this, TripHistoryAct.class);
                startActivity(intent);
            }
        });
        third_trip_history_header.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyStatus.this, TripHistoryAct.class);
                startActivity(intent);
            }
        });
        track_location.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyStatus.this, TrackLocationActivity.class);
                startActivity(intent);
            }
        });

        more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyStatus.this, MoreActivity.class);
                startActivity(intent);
            }
        });
        home_iv = findViewById(R.id.home_iv);
        track_now_iv = findViewById(R.id.track_now_iv);
        home_iv.setImageResource(R.drawable.ic_home);

        //  Glide.with(this).load(SessionSave.getSession("image_path", this) + "homeFocus.png").apply(new RequestOptions().error(R.drawable.home_focus)).into((ImageView) findViewById(R.id.home_iv));
        home_lay = findViewById(R.id.home_lay);
        earnings_lay = findViewById(R.id.earnings_lay);
        earnings_lays = findViewById(R.id.earnings_lays);
        see_history = findViewById(R.id.see_history);
        profile_lay = findViewById(R.id.profile_lay);
        booking = findViewById(R.id.booking);
        earnings = findViewById(R.id.earnings);
        profile = findViewById(R.id.profile);
        support_lay = findViewById(R.id.support_lay);
        track_now_lay = findViewById(R.id.track_now_lay);
        inbox_lay = findViewById(R.id.inbox_lay);
        onmyway_lay = findViewById(R.id.onmyway_lay);
        streetpick_lay = findViewById(R.id.streetpick_lay);
        no_taxi_view = findViewById(R.id.no_taxi_view);
        no_taxi_assign = findViewById(R.id.no_taxi_assigned);

        pick_pin = findViewById(R.id.pick_pin);
        profile_img = findViewById(R.id.profile_img);
//        Picasso.get().load(SessionSave.getSession("Picture", MyStatus.this)).placeholder(getResources().getDrawable(R.drawable.loadingimage)).error(getResources().getDrawable(R.drawable.noimage)).into(profile_img);
        earnings_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MyStatus.this, EarningsAct.class);
                startActivity(intent);
                finish();

            }
        });
        earnings_lays.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MyStatus.this, EarningsAct.class);
                startActivity(intent);
                finish();

            }
        });


        booking.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //CommonTripHistory
                Intent intent = new Intent(MyStatus.this, CommonTripHistory.class);
                startActivity(intent);
            }
        });

        see_history.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyStatus.this, TripHistoryNew.class);
                startActivity(intent);
            }
        });

        see_history.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyStatus.this, TripHistoryNew.class);
                startActivity(intent);
            }
        });

        earnings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyStatus.this, EarningsAct.class);
                startActivity(intent);

//                btn_shift.setClickable(false);
//                new RequestingCheckBox();

            }
        });
        profile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyStatus.this, ProfileActivity.class);
                startActivity(intent);

//                Intent intent = new Intent(MyStatus.this, ProfileActivity.class);
//                startActivity(intent);

             //  startActivity(new Intent(MyStatus.this, WithdrawHistoryAct.class));


            }
        });

        support_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyStatus.this, SupportActivity.class);
                startActivity(intent);

            }
        });

        profile_img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyStatus.this, ProfileActivity.class);
                startActivity(intent);

            }
        });
        profile_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyStatus.this, ProfileActivity.class);
                startActivity(intent);

            }
        });

        track_now_lay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SessionSave.getSession("trip_id", MyStatus.this).equals("")) {
                    Intent in = new Intent(MyStatus.this, OngoingAct.class);
                    startActivity(in);
                } else {
                    CToast.ShowToast(MyStatus.this, NC.getResources().getString(R.string.no_tracking));
                }

            }
        });





      //  FontHelper.applyFont(this, curlocation);
        slider = findViewById(R.id.backup);
        headerTxt = findViewById(R.id.headerTxt);
        headerTxt.setText(NC.getResources().getString(R.string.my_status));
        headerTxt.setVisibility(View.GONE);


        // Close this activity.
        slider.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // menu.toggle();
                if (c_marker != null) {
                    c_marker = null;
                }

                showLoading(MyStatus.this);
                finish();
            }
        });



        if (SessionSave.getSession("driver_type", MyStatus.this).equalsIgnoreCase("D")) {
            AccountNotActivated(SessionSave.getSession("account_message", MyStatus.this));
        } else {
            SessionSave.saveSession("account_activate", true, MyStatus.this);
            no_taxi_view.setVisibility(View.GONE);
            Window window = getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.header_text));
            }
            Systems.out.println("nan---nOTyET Activated");
        }





        JSONObject j = new JSONObject();
        try {
            j.put("userid", SessionSave.getSession("Id", MyStatus.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String pro_url = "type=driver_profile";
        new GetProfileData(pro_url, j);

    }


    public void AccountNotActivated(String Message) {
        Systems.out.println("nan-----AccountNotActivated");
        SessionSave.saveSession("account_activate", false, MyStatus.this);
        no_taxi_view.setVisibility(View.VISIBLE);
        Window window = getWindow();
        no_taxi_assign.setText(Message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.button_accept));
        }
        Intent i = new Intent(MyStatus.this, LocationUpdate.class);
        stopService(i);
    }

    public void showRedAlert(String alert_msg) {

        if (redAlertSnackBar == null) {

            redAlertSnackBar = Snackbar.make(animlayout, alert_msg, Snackbar.LENGTH_LONG)
                    .setDuration(Snackbar.LENGTH_INDEFINITE);
            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) redAlertSnackBar.getView();
            // Hide the text
            TextView textView = layout.findViewById(R.id.snackbar_text);
            textView.setVisibility(View.INVISIBLE);

            View snackView = LayoutInflater.from(MyStatus.this).inflate(R.layout.layout_red_alert, null);
            TextView textViewTop = snackView.findViewById(R.id.text_redAlertMessage);
            textViewTop.setText(alert_msg);
            textViewTop.setTextColor(Color.WHITE);

            progressBar = snackView.findViewById(R.id.retry_progress);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }, 3000);

            layout.setPadding(0, 0, 0, 0);

            layout.addView(snackView, 0);
            layout.setBackgroundColor(Color.RED);
            redAlertSnackBar.show(); // Don’t forget to show!
        } else {
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }, 3000);
            }
        }
    }

    public void hideRedAlert() {
        if (redAlertSnackBar != null) {
            redAlertSnackBar.dismiss();
            redAlertSnackBar = null;
        }
    }

    /**
     * Creating google api client object
     */

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (MyStatus.this != null /*&& MyStatus.this.getCurrentFocus() != null*/) {
                    try {
                        JSONObject j = new JSONObject();
                        j.put("driver_id", SessionSave.getSession("Id", MyStatus.this));
                        j.put("driver_type", SessionSave.getSession("driver_type", MyStatus.this));
                        j.put("device_token", SessionSave.getSession(CommonData.DEVICE_TOKEN, MyStatus.this));
                        String pro_url = "type=driver_recent_trip_list";
                        if (!SessionSave.getSession("Id", MyStatus.this).equals(""))
                            new GetTripData(pro_url, j);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 500);


    }



    @Override
    public void positiveButtonClick(DialogInterface dialog, int id, String s) {
        switch (s) {
            case "1":
                Intent intent = new Intent(MyStatus.this, WebviewAct.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("fromMyStatus", "YES");
                intent.putExtra("type", "2");
                startActivity(intent);
                finish();
                break;
            case "2":
                Intent intent1 = new Intent(MyStatus.this, WebviewAct.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("fromMyStatus", "YES");
                intent1.putExtra("type", "1");
                startActivity(intent1);
                finish();
                break;
            case "3":
            case "6":
                dialog.dismiss();
                break;
            case "4":
                dialog.dismiss();
                Intent intent2 = getIntent();
                finish();
                startActivity(intent2);
                break;
            case "5":
                dialog.dismiss();

                Intent i = new Intent(MyStatus.this, OngoingAct.class);
                startActivity(i);
                break;

            default:
                break;

        }
    }

    @Override
    public void negativeButtonClick(DialogInterface dialog, int id, String s) {
        switch (s) {
            case "1":
            case "2":
            case "5":
                dialog.dismiss();
                break;
            case "3":
                try {
                    if (MyStatus.this != null && dialog != null)
                        dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "4":
                Activity activity1 = MyStatus.this;
                final Intent intent1 = new Intent(Intent.ACTION_MAIN);
                intent1.addCategory(Intent.CATEGORY_HOME);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity1.startActivity(intent1);
                activity1.finish();
                dialog.dismiss();
                break;
            default:
                break;
        }
    }


    private void bookLaterNotificationAlert(String bookLaterDetails) {
        final View bookLaterView = View.inflate(MyStatus.this, R.layout.booklater_alert, null);
        Dialog bookLaterDialog = new Dialog(MyStatus.this, R.style.dialogwinddow);
        bookLaterDialog.setContentView(bookLaterView);
        bookLaterDialog.setCancelable(false);
        bookLaterDialog.show();
        ListViewEX listViewEX = bookLaterView.findViewById(R.id.testLay);
        listViewEX.setData(getStopArray(bookLaterDetails), "SCHEDULE", SessionSave.getSession("Lang", MyStatus.this));
        bookLaterView.findViewById(R.id.btnAccept).setOnClickListener(view -> {
            bookLaterDialog.dismiss();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(BOOKLATER_NOTIFICATION_ID);
            try {
                JSONObject j = new JSONObject();
                j.put("trip_id", scheduleTripId);
                j.put("driver_id", SessionSave.getSession("Id", MyStatus.this));
                String scheduleTripUrl = "type=schedule_accept_trip";
                new ScheduleTrip(scheduleTripUrl, j);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        bookLaterView.findViewById(R.id.btnDecline).setOnClickListener(view -> {
            bookLaterDialog.dismiss();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(BOOKLATER_NOTIFICATION_ID);
            try {
                JSONObject j = new JSONObject();
                j.put("pass_logid", scheduleTripId);
                j.put("driver_id", SessionSave.getSession("Id", MyStatus.this));
                j.put("taxi_id", SessionSave.getSession("taxi_id", MyStatus.this));
                j.put("company_id", SessionSave.getSession("company_id", MyStatus.this));
                j.put("driver_reply", "C");
                j.put("field", "");
                j.put("flag", "1");
                if (MainActivity.mMyStatus.getOnstatus().equalsIgnoreCase("Arrivd"))
                    j.put("driver_arrived", 1);
                else
                    j.put("driver_arrived", 0);
                j.put("schedule", "1");
                final String canceltrip_url = "type=driver_reply";
                new CancelTrip(canceltrip_url, j);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private ArrayList<HashMap<String, String>> getStopArray(String alertMsg) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(alertMsg);
            if (jsonObject.has("info")) {
                JSONObject infoJsonObject = jsonObject.getJSONObject("info");
                Iterator<String> iter = infoJsonObject.keys();
                while (iter.hasNext()) {
                    HashMap<String, String> h2 = new HashMap<>();
                    String key = iter.next();
                    try {
                        Object value = infoJsonObject.get(key);
                        h2.put("KEY", key);
                        h2.put("VALUE", value.toString());
                        scheduleTripId = infoJsonObject.getString("trip_id");
                        arrayList.add(h2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }


    private class CancelTrip implements APIResult {

        CancelTrip(final String url, JSONObject data) {
            try {
                if (isOnline()) {
                    new APIService_Retrofit_JSON(MyStatus.this, this, data, false).execute(url);
                } else {
                    Utils.alert_view(MyStatus.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, MyStatus.this, "4");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, String result) {
            if (isSuccess) {
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        CToast.ShowToast(MyStatus.this, json.getString("message"));
                    } else {
                        CToast.ShowToast(MyStatus.this, json.getString("message"));
                    }
                    if (!SessionSave.getSession("trip_id", MyStatus.this).equals("")) {
                        startActivity(new Intent(MyStatus.this, OngoingAct.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                //CToast.ShowToast(MyStatus.this, NC.getString(R.string.server_error));
            }
        }

    }


    private class ScheduleTrip implements APIResult {

        ScheduleTrip(final String url, JSONObject data) {
            try {
                if (isOnline()) {
                    new APIService_Retrofit_JSON(MyStatus.this, this, data, false).execute(url);
                } else {
                    Utils.alert_view(MyStatus.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, MyStatus.this, "4");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, String result) {
            if (isSuccess) {
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        CToast.ShowToast(MyStatus.this, json.getString("message"));
                    } else {
                        CToast.ShowToast(MyStatus.this, json.getString("message"));
                    }
                    if (!SessionSave.getSession("trip_id", MyStatus.this).equals("")) {
                        startActivity(new Intent(MyStatus.this, OngoingAct.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                // CToast.ShowToast(MyStatus.this, NC.getString(R.string.server_error));
            }
        }

    }



    public void enableDrivertoActiveState() {
        SessionSave.saveSession("driver_type", "A", MyStatus.this);
        if (SessionSave.getSession("shift_status", MyStatus.this).equals("IN")) {
            nonactiityobj.startServicefromNonActivity(MyStatus.this);
        }
        SessionSave.saveSession("account_activate", true, MyStatus.this);
        no_taxi_view.setVisibility(View.GONE);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.header_text));
        }
    }




    @Override
    protected void onDestroy() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
        super.onDestroy();
    }


    public void showStreetAlert(String message) {
        try {
            if (MyStatus.this != null) {
                dialog1 = Utils.alert_view(MyStatus.this, NC.getString(R.string.message), message, NC.getString(R.string.track_now), NC.getString(R.string.cancel), false, MyStatus.this, "5");

            } else {
                try {
                    if (MyStatus.this != null && errorDialog != null)
                        errorDialog.dismiss();
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//
//            startActivity(intent);
            final Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            this.doubleBackToExitPressedOnce = true;
            CToast.ShowToast(this, NC.getResources().getString(R.string.please_click_back_again_exit));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    private void AnimationInScreen() {
        if (Utils.HigherThanLollipop()) {
            ViewCompat.postOnAnimation(animlayout, new Runnable() {
                @Override
                public void run() {
                    //Cicular animation
                    Animator animator = Utils.animateRevealWithoutColorFromCoordinates(animlayout);
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            SessionSave.saveSession("need_animation", false, MyStatus.this);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }

                    });

                }
            });
        } else {
        }
    }

    private void startSOSService() {
        SessionSave.saveSession("sos_id", SessionSave.getSession("Id", MyStatus.this), MyStatus.this);
        SessionSave.saveSession("user_type", "d", MyStatus.this);


        startService(new Intent(MyStatus.this, SOSService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        startSOSService();
                    }
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_GPS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                       // nonactiityobj.stopServicefromNonActivity(MyStatus.this);
                        final Intent i = new Intent(getApplicationContext(), SplashAct.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        finish();
                    }
                }
            }
        }
    }

    /**
     * @API call(get method) to get the driver trip data and parsing the response
     */
    private class GetTripData implements APIResult {
        public GetTripData(String url, JSONObject data) {


            try {
                if (isOnline()) {
                    new APIService_Retrofit_JSON_NoProgress(MyStatus.this, this, data, false).execute(url);
                } else {
                    Log.d("No Internet Connection", "No Internet");
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, final String result) {

            try {

                if (isSuccess) {
                    JSONObject json = new JSONObject(result);
                    if (json.has("trip_list")) {
                        JSONArray details1 = json.getJSONArray("trip_list");

                    }

                    if (json.getInt("status") == 1 ||
                            json.getInt("status") == -4 ||
                            json.getInt("status") == -2 ||
                            json.getInt("status") == -3) {
                        if (json.has("total_amount")) {
                           today_earning.setText(SessionSave.getSession("site_currency", MyStatus.this) + json.getString("total_amount").trim());
                            completed_trips.setText(json.getString("total_trips").trim() + " "+"Rides");
                            //
                            //

                            wallet_amount.setText(SessionSave.getSession("site_currency", MyStatus.this) + json.getString("driver_wallet").trim());
                            enableDrivertoActiveState();
                        }


                        if (json.getInt("status") == 1) {



                        }

                        if (json.getInt("status") == -4) {
                            dialog1 = Utils.alert_view(MyStatus.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, MyStatus.this, "3");
                        } else if (json.getInt("status") == -2) {
                            Systems.out.println("myCode_______" + "in -2 condition");
                            dialog1 = Utils.alert_view(MyStatus.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), NC.getResources().getString(R.string.cancel), false, MyStatus.this, "1");
                        } else if (json.getInt("status") == -3)
                        {
                            Systems.out.println("myCode_______" + "in -3 condition");
                            dialog1 = Utils.alert_view_dialog(MyStatus.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), NC.getResources().getString(R.string.cancel), false, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    Utils.closeDialog();
                                    dialogInterface.dismiss();
                                    Intent intent1 = new Intent(MyStatus.this, WebviewAct.class);
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent1.putExtra("fromMyStatus", "YES");
                                    intent1.putExtra("type", "1");
                                    startActivity(intent1);
                                    finish();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }, "");

                        }

                        JSONArray details = json.getJSONArray("trip_list");
                        templength = details.length();

                        if (templength > 0) {

                            trip_detailslay.setVisibility(View.VISIBLE);
                            if (templength == 1) {
                                first_lay.setVisibility(View.VISIBLE);
                                second_lay.setVisibility(View.GONE);
                                third_lay.setVisibility(View.GONE);

//                                lasttripheader.setText(NC.getString(R.string.Lasttrip));
//                                second_trip_header.setText(NC.getString(R.string.Lasttrip));
//                                third_trip_header.setText(NC.getString(R.string.Lasttrip));

                            } else if (templength == 2) {
                                first_lay.setVisibility(View.VISIBLE);
                                second_lay.setVisibility(View.VISIBLE);
                                third_lay.setVisibility(View.GONE);

//                                lasttripheader.setText(NC.getString(R.string.Last2trip));
//                                second_trip_header.setText(NC.getString(R.string.Last2trip));
//                                third_trip_header.setText(NC.getString(R.string.Last2trip));
                            } else if (templength == 3) {
                                first_lay.setVisibility(View.VISIBLE);
                                second_lay.setVisibility(View.VISIBLE);
                                third_lay.setVisibility(View.VISIBLE);

//                                lasttripheader.setText(NC.getString(R.string.Last3trip));
//                                second_trip_header.setText(NC.getString(R.string.Last3trip));
//                                third_trip_header.setText(NC.getString(R.string.Last3trip));
                            } else {
                                first_lay.setVisibility(View.GONE);
                                second_lay.setVisibility(View.GONE);
                                third_lay.setVisibility(View.GONE);

//                                lasttripheader.setText(NC.getString(R.string.Last3trip));
//                                second_trip_header.setText(NC.getString(R.string.Last3trip));
//                                third_trip_header.setText(NC.getString(R.string.Last3trip));
                            }

                            for (int i = 0; i < templength; i++) {

                                if (i == 0) {

                                    first_tripdate.setText(details.getJSONObject(i).getString("pickup_location").trim());
                                    first_tripmodel.setText(details.getJSONObject(i).getString("drop_location").trim());
                                    first_tripamt.setText(SessionSave.getSession("site_currency", MyStatus.this) + details.getJSONObject(i).getString("fare").trim());
                                    Picasso.get().load(details.getJSONObject(i).getString("profile_image")).placeholder(getResources().getDrawable(R.drawable.loadingimage)).error(getResources().getDrawable(R.drawable.noimage)).into(imagefirsttrip);


                                }
                                if (i == 1) {
                                    second_tripdate.setText(details.getJSONObject(i).getString("pickup_location").trim());
                                    second_tripmodel.setText(details.getJSONObject(i).getString("drop_location").trim());
                                    second_tripamt.setText(SessionSave.getSession("site_currency", MyStatus.this) + details.getJSONObject(i).getString("fare").trim());
                                    Picasso.get().load(details.getJSONObject(i).getString("profile_image")).placeholder(getResources().getDrawable(R.drawable.loadingimage)).error(getResources().getDrawable(R.drawable.noimage)).into(imagesecondtrip);


                                }
                                if (i == 2) {
                                    third_tripdate.setText(details.getJSONObject(i).getString("pickup_location").trim());
                                    third_tripmodel.setText(details.getJSONObject(i).getString("drop_location").trim());
                                    third_tripamt.setText(SessionSave.getSession("site_currency", MyStatus.this) + " " + details.getJSONObject(i).getString("fare").trim());
                                    //
                                    Picasso.get().load(details.getJSONObject(i).getString("profile_image")).placeholder(getResources().getDrawable(R.drawable.loadingimage)).error(getResources().getDrawable(R.drawable.noimage)).into(imagethirdtrip);

                                }
                            }

                        } else {

                            trip_detailslay.setVisibility(View.VISIBLE);
                        }

                    } else if (json.getInt("status") == 10) {
                        trip_detailslay.setVisibility(View.VISIBLE);
                        SessionSave.saveSession("driver_type", "D", MyStatus.this);
                        SessionSave.saveSession("account_activate", false, MyStatus.this);
                        AccountNotActivated(SessionSave.getSession("account_message", MyStatus.this));
                    } else if (json.getInt("status") == -4) {
                        dialog1 = Utils.alert_view(MyStatus.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, MyStatus.this, "3");
                        trip_detailslay.setVisibility(View.VISIBLE);
                    } else if (json.getInt("status") == 40) {
                        enableDrivertoActiveState();
                        dialog1 = Utils.alert_view(MyStatus.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, MyStatus.this, "3");
                        trip_detailslay.setVisibility(View.VISIBLE);
                    } else if (json.getInt("status") == 41) {
                        dialog1 = Utils.alert_view(MyStatus.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, MyStatus.this, "3");

                        recentListMessage = json.getString("message");
                        no_taxi_view.setVisibility(View.VISIBLE);
                        Window window = getWindow();
                        no_taxi_assign.setText(recentListMessage);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(getResources().getColor(R.color.button_accept));
                        }
                        if (SessionSave.getSession("shift_status", MyStatus.this).equals("IN")) {
                            nonactiityobj.startServicefromNonActivity(MyStatus.this);
                        }
                        trip_detailslay.setVisibility(View.GONE);
//                        SessionSave.saveSession("driver_type", "D", MyStatus.this);
//                        SessionSave.saveSession("account_activate", false, MyStatus.this);
//                        AccountNotActivated(json.getString("message"));
                    } else if (json.getInt("status") == -1) {
//                        if (json.getString("route_activate").equalsIgnoreCase("1")) {
//                            onmyway_iv.setImageResource(R.drawable.ic_onmyway_online);
//                        } else {
//                            onmyway_iv.setImageResource(R.drawable.ic_onmyway_offline);
//
//                        }


//                        if (json.getString("admin_on_my_way_enabled_status").equalsIgnoreCase("1")) {
//                            onmyway_lay.setVisibility(View.VISIBLE);
//                        } else {
//                            onmyway_lay.setVisibility(View.GONE);
//                        }
                        dialog1 = Utils.alert_view(MyStatus.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, MyStatus.this, "3");

                        trip_detailslay.setVisibility(View.GONE);
                        enableDrivertoActiveState();
                    } else {
                        trip_detailslay.setVisibility(View.VISIBLE);
                    }
                } else {
                    trip_detailslay.setVisibility(View.VISIBLE);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //  CToast.ShowToast(MyStatus.this, NC.getString(R.string.server_error));
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
                j.put("driver_id", SessionSave.getSession("Id", MyStatus.this));
                j.put("shiftstatus", checked);
                j.put("reason", "");
                Log.e("shiftbefore ", j.toString());

                j.put("update_id", SessionSave.getSession("Shiftupdate_Id", MyStatus.this));
                String requestingCheckBox = "type=driver_shift_status";
                if (isOnline())
                    new APIService_Retrofit_JSON(MyStatus.this, this, j, false).execute(requestingCheckBox);
                else {
                    btn_shift.setClickable(true);
                    dialog1 = Utils.alert_view(MyStatus.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.server_error), NC.getResources().getString(R.string.ok), "", true, MyStatus.this, "3");
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

                if (isSuccess && MyStatus.this != null) {
                    btn_shift.setClickable(true);

                    JSONObject object = new JSONObject(result);
                    if (object.getInt("status") == 1)
                    {
                        if (checked.equals("IN")) {
                            dialog1 = Utils.alert_view(MyStatus.this, NC.getResources().getString(R.string.message), object.getString("message"), NC.getResources().getString(R.string.ok), "", true, MyStatus.this, "6");
                            btn_shift.setText(NC.getString(R.string.online));
                            Drawables_program.shift_on(btn_shift);
                            btn_shift.setTextColor(Color.WHITE);
                            pick_pin.setImageResource(R.drawable.ic_pick_dot);


                            SessionSave.saveSession("shift_status", "IN", MyStatus.this);
                            SessionSave.saveSession(CommonData.SHIFT_OUT, false, MyStatus.this);
                            SessionSave.saveSession("Shiftupdate_Id", object.getJSONObject("detail").getString("update_id"), MyStatus.this);

                            if (!SessionSave.getSession("driver_type", MyStatus.this).equalsIgnoreCase("D"))
                                nonactiityobj.startServicefromNonActivity(MyStatus.this);
                        } else {
                            dialog1 = Utils.alert_view(MyStatus.this, NC.getResources().getString(R.string.message), object.getString("message"), NC.getResources().getString(R.string.ok), "", true, MyStatus.this, "6");
                            btn_shift.setText(NC.getString(R.string.offline));
                            Drawables_program.shift_bg_grey(btn_shift);
                            btn_shift.setTextColor(getResources().getColor(R.color.button_reject));
                            pick_pin.setImageResource(R.drawable.ic_drop_dot);

                            Systems.out.println("innnnnn " + "shift_chek_4");
                            SessionSave.saveSession("shift_status", "OUT", MyStatus.this);
                            SessionSave.saveSession("trip_id", "", MyStatus.this);
                            SessionSave.setWaitingTime(0L, MyStatus.this);
                         //   nonactiityobj.stopServicefromNonActivity(MyStatus.this);
                        }
                    } else {
                        dialog1 = Utils.alert_view(MyStatus.this, NC.getResources().getString(R.string.message), object.getString("message"), NC.getResources().getString(R.string.ok), "", true, MyStatus.this, "3");

                    }
                } else {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            CToast.ShowToast(MyStatus.this, NC.getString(R.string.please_check_internet));
                        }
                    });
                    btn_shift.setClickable(true);
                    if (checked.equals("IN")) {
                        btn_shift.setText(NC.getString(R.string.online));
                        Systems.out.println("shift_chek_12");
                        Drawables_program.shift_on(btn_shift);
                        btn_shift.setTextColor(Color.WHITE);
                        pick_pin.setImageResource(R.drawable.ic_pick_dot);


                    } else {
                        btn_shift.setText(NC.getString(R.string.offline));
                        Systems.out.println("shift_chek_13");
                        Drawables_program.shift_bg_grey(btn_shift);
                        btn_shift.setTextColor(getResources().getColor(R.color.button_reject));
                        pick_pin.setImageResource(R.drawable.ic_drop_dot);


                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Systems.out.println("thambiError" + ex.getLocalizedMessage());
                btn_shift.setClickable(true);
                dialog1 = Utils.alert_view(MyStatus.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.server_error), NC.getResources().getString(R.string.ok), "", true, MyStatus.this, "3");
            }
        }
    }

    private class GetProfileData implements APIResult {
        public GetProfileData(String url, JSONObject data) {

            try {
                if (isOnline()) {
                    new APIService_Retrofit_JSON(MyStatus.this, this, data, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(MyStatus.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, MyStatus.this, "");
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, final String result) {

            try {
                if (isSuccess) {
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        JSONObject details = json.getJSONObject("detail");

                        String imgPath = details.getString("main_image_path").trim();

                        if (imgPath != null && imgPath.length() > 0) {
                            Picasso.get().load(imgPath).placeholder(getResources().getDrawable(R.drawable.loadingimage)).error(getResources().getDrawable(R.drawable.noimage)).into(profile_img);
                        }
                        SessionSave.saveSession("taxi_model", details.getString("taxi_model"), MyStatus.this);
                        SessionSave.saveSession("taxi_no", details.getString("taxi_no"), MyStatus.this);
                        SessionSave.saveSession("taxi_map_from", details.getString("taxi_map_from"), MyStatus.this);
                        SessionSave.saveSession("taxi_map_to", details.getString("taxi_map_to"), MyStatus.this);

                        SessionSave.saveSession("driver_lic_front", details.getString("driver_licence_path"), MyStatus.this);
                        SessionSave.saveSession("driver_lic_back", details.getString("driver_licence_back_side_path"), MyStatus.this);

                    } else {
                        // CToast.ShowToast(MeAct.this, NC.getString(R.string.server_error));
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }


}