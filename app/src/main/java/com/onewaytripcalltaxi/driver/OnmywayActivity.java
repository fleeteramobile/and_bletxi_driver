package com.onewaytripcalltaxi.driver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.utils.CToast;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.FontHelper;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class OnmywayActivity extends MainActivity {

    private TextView HeadTitle, btn_back;
    private AlertDialog alertDialog;
    private SwitchCompat status_switch, stops_switch;
    private int status_checked = 0;
    private int stops_checked = 0;
    private int is_def_cus = 0;

    private EditText pickup_range, stops_range, drop_range;
    private TextView route1_Txt, route2_Txt, route3_Txt;
    private RadioGroup rb_default;

    String values = "";
    String pickup_range_def, stop_range_def, dropoff_range_def, pickup_range_cus, stop_range_cus, dropoff_range_cus;
    private TextView btn_setting, addmyway_Btn;
    private RadioButton rb_def, rb_cus;
    private String route1_loc, route2_loc, route3_loc;

    private String title1, start_loc1, end_loc1, start_time1, end_time1, week_days1, title2, start_loc2, end_loc2, start_time2, end_time2, week_days2, title3, start_loc3, end_loc3, start_time3, end_time3, week_days3;
    private String start_latitude1, start_longitude1, end_latitude1, end_longitude1, start_latitude2, start_longitude2, end_latitude2, end_longitude2, start_latitude3, start_longitude3, end_latitude3, end_longitude3;

    private String max_pickrange, max_stoprange, max_droprange;

    @Override
    public int setLayout() {
        setLocale();
        return R.layout.onmyway_lay;
    }

    @Override
    public void Initialize() {
        CommonData.mActivitylist.add(this);
        CommonData.sContext = this;
        CommonData.current_act = "Onmyway";

        Colorchange.ChangeColor((ViewGroup) (((ViewGroup) OnmywayActivity.this
                .findViewById(android.R.id.content)).getChildAt(0)), OnmywayActivity.this);

        FontHelper.applyFont(this, findViewById(R.id.slide_lay));


        btn_back = findViewById(R.id.back_btn);
        status_switch = findViewById(R.id.status_switch);
        stops_switch = findViewById(R.id.stops_switch);

        pickup_range = findViewById(R.id.pickup_range);
        stops_range = findViewById(R.id.stops_range);
        drop_range = findViewById(R.id.drop_range);
        rb_default = findViewById(R.id.rb_default);

        route1_Txt = findViewById(R.id.route1_Txt);
        route2_Txt = findViewById(R.id.route2_Txt);
        route3_Txt = findViewById(R.id.route3_Txt);

        btn_setting = findViewById(R.id.btn_setting);
        rb_def = findViewById(R.id.rb_def);
        rb_cus = findViewById(R.id.rb_cus);

        addmyway_Btn = findViewById(R.id.addmyway_Btn);


        pickup_range.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                pickup_range.setCursorVisible(true);
                return false;

            }
        });

        stops_range.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                stops_range.setCursorVisible(true);
                return false;

            }
        });

        drop_range.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                drop_range.setCursorVisible(true);
                return false;

            }
        });


        pickup_range.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.length() != 0) {
                        String s = String.valueOf(charSequence.charAt(0));

                        if (s.equalsIgnoreCase("0")||s.equalsIgnoreCase(".")) {
                            pickup_range.setText("");
                        } else {
                            int maxLength = 4;
                            InputFilter[] FilterArray = new InputFilter[1];
                            FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                            pickup_range.setFilters(FilterArray);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        stops_range.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.length() != 0) {
                        String s = String.valueOf(charSequence.charAt(0));

                        if (s.equalsIgnoreCase("0")||s.equalsIgnoreCase(".")) {
                            stops_range.setText("");
                        } else {
                            int maxLength = 4;
                            InputFilter[] FilterArray = new InputFilter[1];
                            FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                            stops_range.setFilters(FilterArray);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        drop_range.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.length() != 0) {
                        String s = String.valueOf(charSequence.charAt(0));

                        if (s.equalsIgnoreCase("0")||s.equalsIgnoreCase(".")) {
                            drop_range.setText("");
                        } else {
                            int maxLength = 4;
                            InputFilter[] FilterArray = new InputFilter[1];
                            FilterArray[0] = new InputFilter.LengthFilter(maxLength);
                            drop_range.setFilters(FilterArray);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        rb_default.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getCheckedRadioButtonId() == R.id.rb_def) {
                    is_def_cus = 0;
                    pickup_range.setText(pickup_range_def);
                    stops_range.setText(stop_range_def);
                    drop_range.setText(dropoff_range_def);

                    pickup_range.setEnabled(false);
                    stops_range.setEnabled(false);
                    drop_range.setEnabled(false);

                } else if (group.getCheckedRadioButtonId() == R.id.rb_cus) {
                    is_def_cus = 1;
                    pickup_range.setText(pickup_range_cus);
                    stops_range.setText(stop_range_cus);
                    drop_range.setText(dropoff_range_cus);

                    pickup_range.setEnabled(true);
                    stops_range.setEnabled(true);
                    drop_range.setEnabled(true);
                }

            }
        });

        addmyway_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (route1_loc.equalsIgnoreCase("")) {
                    Intent intent = new Intent(OnmywayActivity.this, OnmywayNextAct.class);
                    Bundle b = new Bundle();
                    b.putString("route", "1");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtras(b);
                    startActivity(intent);
                   // finish();
                } else if (route2_loc.equalsIgnoreCase("")) {
                    Intent intent = new Intent(OnmywayActivity.this, OnmywayNextAct.class);
                    Bundle b = new Bundle();
                    b.putString("route", "2");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtras(b);
                    startActivity(intent);
                   // finish();

                } else if (route3_loc.equalsIgnoreCase("")) {
                    Intent intent = new Intent(OnmywayActivity.this, OnmywayNextAct.class);
                    Bundle b = new Bundle();
                    b.putString("route", "3");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtras(b);
                    startActivity(intent);
                    //finish();

                } else {
                    CToast.ShowToast(OnmywayActivity.this, NC.getResources().getString(R.string.max_ways));

                }
            }
        });


        route1_Txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OnmywayActivity.this, OnmywayNextAct.class);
                Bundle b = new Bundle();
                b.putString("route", "1");
                b.putString("title", title1);
                b.putString("start_location", start_loc1);
                b.putString("end_location", end_loc1);
                b.putString("start_time", start_time1);
                b.putString("end_time", end_time1);
                b.putString("start_latitude", start_latitude1);
                b.putString("start_longitude", start_longitude1);
                b.putString("end_latitude", end_latitude1);
                b.putString("end_longitude", end_longitude1);
                b.putString("week_days", week_days1);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtras(b);
                startActivity(intent);
                 // finish();

            }
        });
        route2_Txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OnmywayActivity.this, OnmywayNextAct.class);
                Bundle b = new Bundle();
                b.putString("route", "2");
                b.putString("title", title2);
                b.putString("start_location", start_loc2);
                b.putString("end_location", end_loc2);
                b.putString("start_time", start_time2);
                b.putString("end_time", end_time2);
                b.putString("start_latitude", start_latitude2);
                b.putString("start_longitude", start_longitude2);
                b.putString("end_latitude", end_latitude2);
                b.putString("end_longitude", end_longitude2);
                b.putString("week_days", week_days2);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtras(b);
                startActivity(intent);
                 // finish();

            }
        });
        route3_Txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OnmywayActivity.this, OnmywayNextAct.class);
                Bundle b = new Bundle();
                b.putString("route", "3");
                b.putString("title", title3);
                b.putString("start_location", start_loc3);
                b.putString("end_location", end_loc3);
                b.putString("start_time", start_time3);
                b.putString("end_time", end_time3);
                b.putString("start_latitude", start_latitude3);
                b.putString("start_longitude", start_longitude3);
                b.putString("end_latitude", end_latitude3);
                b.putString("end_longitude", end_longitude3);
                b.putString("week_days", week_days3);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtras(b);
                startActivity(intent);
                 // finish();

            }
        });

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (is_def_cus == 0) {

                    try {
                        JSONObject j = new JSONObject();
                        j.put("driver_id", SessionSave.getSession("Id", OnmywayActivity.this));
                        j.put("on_my_way_enabled_status", String.valueOf(status_checked));
                        j.put("on_my_way_deviation_pickup", "");
                        j.put("on_my_way_deviation_stop", "");
                        j.put("on_my_way_deviation_dropoff", "");
                        j.put("on_my_way_allow_stop_point", String.valueOf(stops_checked));
                        j.put("on_my_way_custom_enabled", String.valueOf(is_def_cus));

                        final String url = "type=driver_update_onmy_way_config";
                        new Update_Onmyway(url, j);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    if (TextUtils.isEmpty(pickup_range.getText().toString().trim())) {
                        CToast.ShowToast(OnmywayActivity.this, NC.getResources().getString(R.string.enter_pickup_range));
                    } else if (TextUtils.isEmpty(stops_range.getText().toString().trim())) {
                        CToast.ShowToast(OnmywayActivity.this, NC.getResources().getString(R.string.enter_stop_range));
                    } else if (TextUtils.isEmpty(drop_range.getText().toString().trim())) {
                        CToast.ShowToast(OnmywayActivity.this, NC.getResources().getString(R.string.enter_drop_range));
                    } else {

                        if (Double.parseDouble(pickup_range.getText().toString()) > Double.parseDouble(max_pickrange)) {
                            CToast.ShowToast(OnmywayActivity.this,  NC.getResources().getString(R.string.pickup_high));
                        } else if (Double.parseDouble(stops_range.getText().toString()) > Double.parseDouble(max_stoprange)) {
                            CToast.ShowToast(OnmywayActivity.this,  NC.getResources().getString(R.string.stop_high));
                        } else if (Double.parseDouble(drop_range.getText().toString()) > Double.parseDouble(max_droprange)) {
                            CToast.ShowToast(OnmywayActivity.this,  NC.getResources().getString(R.string.drop_high));
                        } else {
                            try {
                                JSONObject j = new JSONObject();
                                j.put("driver_id", SessionSave.getSession("Id", OnmywayActivity.this));
                                j.put("on_my_way_enabled_status", String.valueOf(status_checked));
                                j.put("on_my_way_deviation_pickup", pickup_range.getText().toString());
                                j.put("on_my_way_deviation_stop", stops_range.getText().toString());
                                j.put("on_my_way_deviation_dropoff", drop_range.getText().toString());
                                j.put("on_my_way_allow_stop_point", String.valueOf(stops_checked));
                                j.put("on_my_way_custom_enabled", String.valueOf(is_def_cus));

                                final String url = "type=driver_update_onmy_way_config";
                                new Update_Onmyway(url, j);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }


                //Setting_Update();
            }
        });


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        status_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    status_checked = 1;
                } else {
                    status_checked = 0;
                }

                if (buttonView.isPressed()) {
                    try {
                        JSONObject j = new JSONObject();
                        j.put("driver_id", SessionSave.getSession("Id", OnmywayActivity.this));
                        j.put("on_my_way_enabled_status", status_checked);
                        final String url = "type=driver_enable_disable_on_my_way";
                        new Active_Onmyway(url, j);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        stops_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    stops_checked = 1;
                } else {
                    stops_checked = 0;
                }
            }
        });



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideKeyboard(OnmywayActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            JSONObject j = new JSONObject();
            j.put("userid", SessionSave.getSession("Id", OnmywayActivity.this));
            final String url = "type=driver_profile";
            new Profile(url, j);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @API call(get method) to get the driver profile data and parsing the response
     */
    private class Profile implements APIResult {
        public Profile(String url, JSONObject data) {

            try {
                if (isOnline()) {
                    new APIService_Retrofit_JSON(OnmywayActivity.this, this, data, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(OnmywayActivity.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, OnmywayActivity.this, "");
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, final String result) {
            cancelLoading();
            try {
                if (isSuccess) {
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        JSONObject details = json.getJSONObject("detail");

                    /*    edt_start_loc.setText(Html.fromHtml(details.getString("start_location")));
                        edt_end_loc.setText(Html.fromHtml(details.getString("end_location")));
                        if (!details.getString("start_time").equalsIgnoreCase("0")) {
                            edt_start_time.setText(details.getString("start_time"));
                        } else {
                            edt_start_time.setText("");
                        }

                        if (!details.getString("end_time").equalsIgnoreCase("0")) {
                            edt_end_time.setText(details.getString("end_time"));
                        } else {
                            edt_end_time.setText("");
                        }
                        pickuplat = Double.parseDouble(details.getString("start_loc_latitude"));
                        pickuplng = Double.parseDouble(details.getString("start_loc_longitude"));

                        droplat = Double.parseDouble(details.getString("end_loc_latitude"));
                        droplng = Double.parseDouble(details.getString("end_loc_longitude"));*/

                        route1_loc = details.getString("start_location1");
                        route2_loc = details.getString("start_location2");
                        route3_loc = details.getString("start_location3");

                        title1 = details.getString("title1");
                        title2 = details.getString("title2");
                        title3 = details.getString("title3");

                        start_loc1 = details.getString("start_location1");
                        end_loc1 = details.getString("end_location1");
                        start_time1 = details.getString("start_time1");
                        end_time1 = details.getString("end_time1");
                        week_days1 = details.getString("week_days1");
                        start_loc2 = details.getString("start_location2");
                        end_loc2 = details.getString("end_location2");
                        start_time2 = details.getString("start_time2");
                        end_time2 = details.getString("end_time2");
                        week_days2 = details.getString("week_days2");
                        start_loc3 = details.getString("start_location3");
                        end_loc3 = details.getString("end_location3");
                        start_time3 = details.getString("start_time3");
                        end_time3 = details.getString("end_time3");
                        week_days3 = details.getString("week_days3");


                        start_latitude1 = details.getString("start_latitude1");
                        start_longitude1 = details.getString("start_longitude1");
                        end_latitude1 = details.getString("end_latitude1");
                        end_longitude1 = details.getString("end_longitude1");
                        start_latitude2 = details.getString("start_latitude2");
                        start_longitude2 = details.getString("start_longitude2");
                        end_latitude2 = details.getString("end_latitude2");
                        end_longitude2 = details.getString("end_longitude2");
                        start_latitude3 = details.getString("start_latitude3");
                        start_longitude3 = details.getString("start_longitude3");
                        end_latitude3 = details.getString("end_latitude3");
                        end_longitude3 = details.getString("end_longitude3");


                        //route1_Txt.setVisibility(View.VISIBLE);
                        //route2_Txt.setVisibility(View.VISIBLE);
                        // route3_Txt.setVisibility(View.VISIBLE);


                        if (route1_loc.equalsIgnoreCase("")) {
                            route1_Txt.setVisibility(View.GONE);
                            route2_Txt.setVisibility(View.GONE);
                            route3_Txt.setVisibility(View.GONE);
                        } else if (route2_loc.equalsIgnoreCase("")) {
                            route1_Txt.setVisibility(View.VISIBLE);
                            route2_Txt.setVisibility(View.GONE);
                            route3_Txt.setVisibility(View.GONE);
                        } else if (route3_loc.equalsIgnoreCase("")) {
                            route1_Txt.setVisibility(View.VISIBLE);
                            route2_Txt.setVisibility(View.VISIBLE);
                            route3_Txt.setVisibility(View.GONE);
                        } else if (!route1_loc.equalsIgnoreCase("") && !route2_loc.equalsIgnoreCase("") && !route3_loc.equalsIgnoreCase("")) {
                            route1_Txt.setVisibility(View.VISIBLE);
                            route2_Txt.setVisibility(View.VISIBLE);
                            route3_Txt.setVisibility(View.VISIBLE);
                        }

                        max_pickrange = details.getString("on_my_way_max_pickup_range");
                        max_stoprange = details.getString("on_my_way_max_stop_range");
                        max_droprange = details.getString("on_my_way_max_dropup_range");


                        route1_Txt.setText(details.getString("title1"));
                        route2_Txt.setText(details.getString("title2"));
                        route3_Txt.setText(details.getString("title3"));

                        pickup_range_def = details.getString("admin_on_my_way_deviation_pickup");
                        stop_range_def = details.getString("admin_on_my_way_deviation_stop");
                        dropoff_range_def = details.getString("admin_on_my_way_deviation_dropoff");

                        pickup_range_cus = details.getString("driver_on_my_way_deviation_pickup");
                        if (pickup_range_cus.equalsIgnoreCase("0")) {
                            pickup_range_cus = "";
                        }
                        stop_range_cus = details.getString("driver_on_my_way_deviation_stop");
                        if (stop_range_cus.equalsIgnoreCase("0")) {
                            stop_range_cus = "";
                        }
                        dropoff_range_cus = details.getString("driver_on_my_way_deviation_dropoff");
                        if (dropoff_range_cus.equalsIgnoreCase("0")) {
                            dropoff_range_cus = "";
                        }

                        if (details.getString("route_activate").equalsIgnoreCase("1")) {
                            status_switch.setChecked(true);
                            status_checked = 1;
                        } else {
                            status_switch.setChecked(false);
                            status_checked = 0;

                        }

                        if (details.getString("on_my_way_custom_enabled").equalsIgnoreCase("1")) {
                            rb_cus.setChecked(true);
                            pickup_range.setText(pickup_range_cus);
                            stops_range.setText(stop_range_cus);
                            drop_range.setText(dropoff_range_cus);
                            pickup_range.setEnabled(true);
                            stops_range.setEnabled(true);
                            drop_range.setEnabled(true);

                            pickup_range.setCursorVisible(false);
                            stops_range.setCursorVisible(false);
                            drop_range.setCursorVisible(false);
                        } else {
                            rb_def.setChecked(true);
                            pickup_range.setText(pickup_range_def);
                            stops_range.setText(stop_range_def);
                            drop_range.setText(dropoff_range_def);
                            pickup_range.setEnabled(false);
                            stops_range.setEnabled(false);
                            drop_range.setEnabled(false);


                        }

                        if (details.getString("on_my_way_allow_stop_point").equalsIgnoreCase("1")) {
                            stops_switch.setChecked(true);
                            stops_checked = 1;
                        } else {
                            stops_switch.setChecked(false);
                            stops_checked = 0;
                        }



                        SessionSave.saveSession("polyline1", details.getString("overview_polyline1"), OnmywayActivity.this);
                        SessionSave.saveSession("polyline2", details.getString("overview_polyline2"), OnmywayActivity.this);
                        SessionSave.saveSession("polyline3", details.getString("overview_polyline3"), OnmywayActivity.this);


                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class Update_Onmyway implements APIResult {
        public Update_Onmyway(String url, JSONObject data) {

            try {
                if (isOnline()) {
                    new APIService_Retrofit_JSON(OnmywayActivity.this, this, data, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(OnmywayActivity.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, OnmywayActivity.this, "");
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, final String result) {
            cancelLoading();
            try {
                if (isSuccess) {
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                           Toast.makeText(OnmywayActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        //dialog1 = Utils.alert_view(OnmywayActivity.this, "" + NC.getResources().getString(R.string.message), "" + json.getString("message"), "" + NC.getResources().getString(R.string.ok), "", true, OnmywayActivity.this, "");
                        //startActivity(new Intent(OnmywayActivity.this, OnmywayActivity.class));
                        //finish();

                        try {
                            JSONObject j = new JSONObject();
                            j.put("userid", SessionSave.getSession("Id", OnmywayActivity.this));
                            final String url = "type=driver_profile";
                            new Profile(url, j);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {
                        dialog1 = Utils.alert_view(OnmywayActivity.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, OnmywayActivity.this, "");

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class Active_Onmyway implements APIResult {
        public Active_Onmyway(String url, JSONObject data) {

            try {
                if (isOnline()) {
                    new APIService_Retrofit_JSON(OnmywayActivity.this, this, data, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(OnmywayActivity.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, OnmywayActivity.this, "");
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, final String result) {
            cancelLoading();
            try {
                if (isSuccess) {
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        //dialog1 = Utils.alert_view(OnmywayActivity.this, "" + NC.getResources().getString(R.string.message), "" + json.getString("message"), "" + NC.getResources().getString(R.string.ok), "", true, OnmywayActivity.this, "");
                        Toast.makeText(OnmywayActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();

                    } else {
                        dialog1 = Utils.alert_view(OnmywayActivity.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, OnmywayActivity.this, "");

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
