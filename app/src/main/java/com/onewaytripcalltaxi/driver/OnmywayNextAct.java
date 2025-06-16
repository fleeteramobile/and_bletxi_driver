package com.onewaytripcalltaxi.driver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class OnmywayNextAct extends MainActivity {

    private TextView HeadTitle, btn_back, btn_submit, route_Btn;
    private EditText edt_start_loc, edt_start_time, edt_end_loc, edt_end_time, title1;
    private static String LocationRequestedBy = "";
    private Double pickuplat = 0.0;
    private Double pickuplng = 0.0;
    private Double droplat = 0.0;
    private Double droplng = 0.0;
    private double P_latitude;
    private double P_longitude;
    private AlertDialog alertDialog;
    private String selectedTime = "", selected_weekdays = "";

    private RecyclerView id_weekdays;
    private ArrayList<String> listdata;
    private DaysAdapter daysadapter;
    String values = "";
    ArrayList<String> selectedStrings = new ArrayList<String>();
    private final int is_route = 1;

    LinearLayout route1_lay;

    private String route_is = "1";
    LocalTime time1, time2;

    @Override
    public int setLayout() {
        setLocale();
        return R.layout.onmyway_next_lay;
    }

    @Override
    public void Initialize() {

        CommonData.mActivitylist.add(this);
        CommonData.sContext = this;
        CommonData.current_act = "Onmyway";

        Colorchange.ChangeColor((ViewGroup) (((ViewGroup) OnmywayNextAct.this
                .findViewById(android.R.id.content)).getChildAt(0)), OnmywayNextAct.this);

        FontHelper.applyFont(this, findViewById(R.id.slide_lay));


        edt_start_loc = findViewById(R.id.edt_start_loc);
        edt_end_loc = findViewById(R.id.edt_end_loc);
        edt_start_time = findViewById(R.id.edt_start_time);
        edt_end_time = findViewById(R.id.edt_end_time);
        btn_submit = findViewById(R.id.btn_submit);
        btn_back = findViewById(R.id.back_btn);
        route1_lay = findViewById(R.id.route1_lay);
        route_Btn = findViewById(R.id.route_Btn);


        title1 = findViewById(R.id.title1);
        id_weekdays = findViewById(R.id.id_weekdays);

        id_weekdays.setLayoutManager(new GridLayoutManager(this, 3));

        Bundle bun = getIntent().getExtras();
        if (bun != null) {
            route_is = bun.getString("route");
            title1.setText(bun.getString("title"));
            edt_start_loc.setText(bun.getString("start_location"));
            edt_end_loc.setText(bun.getString("end_location"));
            edt_start_time.setText(bun.getString("start_time"));
            edt_end_time.setText(bun.getString("end_time"));
            selected_weekdays = bun.getString("week_days");

            try {
                pickuplat = Double.parseDouble(bun.getString("start_latitude"));
                pickuplng = Double.parseDouble(bun.getString("start_longitude"));
                droplat = Double.parseDouble(bun.getString("end_latitude"));
                droplng = Double.parseDouble(bun.getString("end_longitude"));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        if (route_is.equalsIgnoreCase("1")) {

        } else if (route_is.equalsIgnoreCase("2")) {

        } else if (route_is.equalsIgnoreCase("3")) {

        }

        try {
            listdata = new ArrayList<>();
            listdata.add(NC.getResources().getString(R.string.Sunday));
            listdata.add(NC.getResources().getString(R.string.Monday));
            listdata.add(NC.getResources().getString(R.string.Tuesday));
            listdata.add(NC.getResources().getString(R.string.Wednesday));
            listdata.add(NC.getResources().getString(R.string.Thursday));
            listdata.add(NC.getResources().getString(R.string.Friday));
            listdata.add(NC.getResources().getString(R.string.Saturday));

            daysadapter = new DaysAdapter(OnmywayNextAct.this, listdata);
            id_weekdays.setAdapter(daysadapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
        edt_start_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationRequestedBy = "P";
                Intent intent = new Intent(OnmywayNextAct.this, SearchMapLocation.class);
                if (pickuplat != null && pickuplat != 0.0) {
                    intent.putExtra("dropLocation", new LatLng(pickuplat, pickuplng));
                }
                //intent.putExtra("type", "P");
                startActivityForResult(intent, 300);
            }
        });
        edt_end_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationRequestedBy = "D";
                Intent intent = new Intent(OnmywayNextAct.this, SearchMapLocation.class);
                if (droplat != null && droplat != 0.0) {
                    intent.putExtra("dropLocation", new LatLng(droplat, droplng));
                }
                //intent.putExtra("type", "D");
                startActivityForResult(intent, 300);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        edt_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(1);
            }
        });

        edt_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(2);
            }
        });

        route_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edt_start_loc.getText().toString().trim())) {
                    CToast.ShowToast(OnmywayNextAct.this, NC.getResources().getString(R.string.select_start_location));
                } else if (TextUtils.isEmpty(edt_end_loc.getText().toString().trim())) {
                    CToast.ShowToast(OnmywayNextAct.this, NC.getResources().getString(R.string.select_end_location));
                } else {
                    Intent intent = new Intent(OnmywayNextAct.this, MapActivity.class);
                    Bundle b = new Bundle();
                    b.putString("start_latitude", String.valueOf(pickuplat));
                    b.putString("start_longitude", String.valueOf(pickuplng));
                    b.putString("end_latitude", String.valueOf(droplat));
                    b.putString("end_longitude", String.valueOf(droplng));
                    b.putString("route_is", route_is);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            }
        });


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                String week_days = selectedStrings.toString().replace("[", "").replace("]", "")
                        .replace(", ", ",");



                int returnVal = 0;

                try {

                    String[] replace = edt_start_time.getText().toString().trim().split(":");
                    String start_hr = replace[0];
                    String start_min = replace[1];

                    time1 = LocalTime.of(Integer.parseInt(start_hr), Integer.parseInt(start_min));


                    String[] replace1 = edt_end_time.getText().toString().trim().split(":");
                    String end_hr = replace1[0];
                    String end_min = replace1[1];

                    time2 = LocalTime.of(Integer.parseInt(end_hr), Integer.parseInt(end_min));

                    returnVal = time1.compareTo(time2);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (is_route == 1) {
                    if (TextUtils.isEmpty(edt_start_loc.getText().toString().trim())) {
                        CToast.ShowToast(OnmywayNextAct.this, NC.getResources().getString(R.string.select_start_location));
                    } else if (TextUtils.isEmpty(edt_start_time.getText().toString().trim())) {
                        CToast.ShowToast(OnmywayNextAct.this, NC.getResources().getString(R.string.select_start_time));
                    } else if (TextUtils.isEmpty(edt_end_loc.getText().toString().trim())) {
                        CToast.ShowToast(OnmywayNextAct.this, NC.getResources().getString(R.string.select_end_location));
                    } else if (TextUtils.isEmpty(edt_end_time.getText().toString().trim())) {
                        CToast.ShowToast(OnmywayNextAct.this, NC.getResources().getString(R.string.select_end_time));
                    } else if (TextUtils.isEmpty(title1.getText().toString().trim())) {
                        CToast.ShowToast(OnmywayNextAct.this, NC.getResources().getString(R.string.select_title));
                    } else if (edt_start_loc.getText().toString().trim().equalsIgnoreCase(edt_end_loc.getText().toString().trim())) {
                        CToast.ShowToast(OnmywayNextAct.this, NC.getResources().getString(R.string.location_same));
                    } else if (TextUtils.isEmpty(week_days)) {
                        CToast.ShowToast(OnmywayNextAct.this, NC.getResources().getString(R.string.select_weekdays));
                    } else if (returnVal >= 0) {
                        CToast.ShowToast(OnmywayNextAct.this, NC.getResources().getString(R.string.time_wrong));
                    } else {
                        String url = "type=update_job_data";
                        new UpdateJob(url);
                    }
                }
            }
        });

       /* try {
            JSONObject j = new JSONObject();
            j.put("userid", SessionSave.getSession("Id", OnmywayNextAct.this));
            final String url = "type=driver_profile";
            new Profile(url, j);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

    }

    public void showTimePicker(int type) {
        selectedTime = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(OnmywayNextAct.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.time_picker_dialog, viewGroup, false);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        TextView tv_dismss = dialogView.findViewById(R.id.tv_dismss);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String hour = String.valueOf(hourOfDay);
                String min = String.valueOf(minute);
                if (hourOfDay < 10) {
                    hour = "0" + hourOfDay;
                }
                if (minute < 10) {
                    min = "0" + minute;
                }
                selectedTime = hour + ":" + min + ":00";
                Log.e("time", selectedTime);

                /*if (type == 1) {
                    time1 = LocalTime.of(Integer.parseInt(hour), Integer.parseInt(min));
                } else {
                    time2 = LocalTime.of(Integer.parseInt(hour), Integer.parseInt(min));
                }*/


            }
        });

        tv_dismss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(selectedTime.trim())) {
                    // edt_dbs_cert_expiry_date_time.setText(dbs_expiry_date + " " + selectedTime);

                    if (type == 1) {
                        edt_start_time.setText(selectedTime);
                    } else {
                        edt_end_time.setText(selectedTime);
                    }
                    alertDialog.dismiss();
                    //hideKeyboard(OnmywayNextAct.this);
                    // edt_start_time.setImeOptions(EditorInfo.IME_ACTION_DONE);
                } else {
                    CToast.ShowToast(OnmywayNextAct.this, NC.getResources().getString(R.string.select_time));
                }

            }
        });

        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // hideKeyboard(OnmywayNextAct.this);
    }

    private class UpdateJob implements APIResult {
        String msg = "";

        public UpdateJob(String url) {
            try {
                JSONObject j = new JSONObject();
                j.put("driver_id", SessionSave.getSession("Id", OnmywayNextAct.this));
                j.put("title1", "");
                j.put("start_latitude1", "");
                j.put("start_longitude1", "");
                j.put("start_location1", "");
                j.put("end_latitude1", "");
                j.put("end_longitude1", "");
                j.put("end_location1", "");
                j.put("start_time1", "");
                j.put("end_time1", "");

                j.put("title2", "");
                j.put("start_latitude2", "");
                j.put("start_longitude2", "");
                j.put("start_location2", "");
                j.put("end_latitude2", "");
                j.put("end_longitude2", "");
                j.put("end_location2", "");
                j.put("start_time2", "");
                j.put("end_time2", "");

                j.put("title3", "");
                j.put("start_latitude3", "");
                j.put("start_longitude3", "");
                j.put("start_location3", "");
                j.put("end_latitude3", "");
                j.put("end_longitude3", "");
                j.put("end_location3", "");
                j.put("start_time3", "");
                j.put("end_time3", "");

                j.put("week_days1", "");
                j.put("week_days2", "");
                j.put("week_days3", "");

                j.put("overview_polyline1", "");
                j.put("overview_polyline2", "");
                j.put("overview_polyline3", "");


                if (route_is.equalsIgnoreCase("1")) {
                    j.put("is_updated1", "1");
                    j.put("is_updated2", "0");
                    j.put("is_updated3", "0");
                    j.put("title1", title1.getText().toString());
                    j.put("start_latitude1", pickuplat);
                    j.put("start_longitude1", pickuplng);
                    j.put("start_location1", edt_start_loc.getText().toString().trim());
                    j.put("end_latitude1", droplat);
                    j.put("end_longitude1", droplng);
                    j.put("end_location1", edt_end_loc.getText().toString().trim());
                    j.put("start_time1", edt_start_time.getText().toString().trim());
                    j.put("end_time1", edt_end_time.getText().toString().trim());

                    j.put("week_days1", selectedStrings.toString().replace("[", "").replace("]", "").replace(", ", ","));
                    j.put("overview_polyline1", SessionSave.getSession("polyline1", OnmywayNextAct.this));
                } else if (route_is.equalsIgnoreCase("2")) {
                    j.put("is_updated1", "0");
                    j.put("is_updated2", "1");
                    j.put("is_updated3", "0");

                    j.put("title2", title1.getText().toString());
                    j.put("start_latitude2", pickuplat);
                    j.put("start_longitude2", pickuplng);
                    j.put("start_location2", edt_start_loc.getText().toString().trim());
                    j.put("end_latitude2", droplat);
                    j.put("end_longitude2", droplng);
                    j.put("end_location2", edt_end_loc.getText().toString().trim());
                    j.put("start_time2", edt_start_time.getText().toString().trim());
                    j.put("end_time2", edt_end_time.getText().toString().trim());

                    j.put("week_days2", selectedStrings.toString().replace("[", "").replace("]", "").replace(", ", ","));
                    j.put("overview_polyline2", SessionSave.getSession("polyline2", OnmywayNextAct.this));

                } else if (route_is.equalsIgnoreCase("3")) {
                    j.put("is_updated1", "0");
                    j.put("is_updated2", "0");
                    j.put("is_updated3", "1");

                    j.put("title3", title1.getText().toString());
                    j.put("start_latitude3", pickuplat);
                    j.put("start_longitude3", pickuplng);
                    j.put("start_location3", edt_start_loc.getText().toString().trim());
                    j.put("end_latitude3", droplat);
                    j.put("end_longitude3", droplng);
                    j.put("end_location3", edt_end_loc.getText().toString().trim());
                    j.put("start_time3", edt_start_time.getText().toString().trim());
                    j.put("end_time3", edt_end_time.getText().toString().trim());

                    j.put("week_days3", selectedStrings.toString().replace("[", "").replace("]", "").replace(", ", ","));
                    j.put("overview_polyline3", SessionSave.getSession("polyline3", OnmywayNextAct.this));

                }

                j.put("route_path", "");

                if (isOnline()) {
                    new APIService_Retrofit_JSON(OnmywayNextAct.this, this, j, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(OnmywayNextAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, OnmywayNextAct.this, "");
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
                        msg = json.getString("message");


                        Toast.makeText(OnmywayNextAct.this, msg, Toast.LENGTH_LONG).show();
                        onBackPressed();
                       /* startActivity(new Intent(OnmywayNextAct.this, OnmywayActivity.class));
                        finish();
*/

                        //  dialog1 = Utils.alert_view(OnmywayNextAct.this, "" + NC.getResources().getString(R.string.message), "" + msg, "" + NC.getResources().getString(R.string.ok), "", true, OnmywayNextAct.this, "");


                    } else {
                        msg = json.getString("message");
                        dialog1 = Utils.alert_view(OnmywayNextAct.this, NC.getResources().getString(R.string.message), msg, NC.getResources().getString(R.string.ok), "", true, OnmywayNextAct.this, "");
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // CToast.ShowToast(OnmywayNextAct.this, NC.getString(R.string.server_error));
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result = "", Address;
        Double obtainedlatitude, obtainedlongitude;
        try {
            double lat = 0.0, lng = 0.0;


            if (requestCode == 300) {
                if (data != null) {
                    Bundle res = data.getExtras();
                    Address = res.getString("param_result");
                    obtainedlatitude = res.getDouble("lat");
                    obtainedlongitude = res.getDouble("lng");
                    lat = obtainedlatitude;
                    lng = obtainedlongitude;

                    SessionSave.saveSession("polyline1", "", OnmywayNextAct.this);
                    SessionSave.saveSession("polyline2", "", OnmywayNextAct.this);
                    SessionSave.saveSession("polyline3", "", OnmywayNextAct.this);

                    if (LocationRequestedBy.equals("P")) {
                        edt_start_loc.setText(Address);
                        pickuplat = lat;
                        pickuplng = lng;
                        P_latitude = lat;
                        P_longitude = lng;
                        // movetoCurrentloc();
                    } else if (LocationRequestedBy.equals("D")) {
                        edt_end_loc.setText(Address);
                        droplat = lat;
                        droplng = lng;

                    }

                }
            } else {


                try {
                    // if (OnmywayNextAct.this != null)
                    //  startLocationUpdates();
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
                    edt_start_loc.setText(result);
                    pickuplat = lat;
                    pickuplng = lng;
                    P_latitude = lat;
                    P_longitude = lng;
                    // movetoCurrentloc();
                } else if (LocationRequestedBy.equals("D") && result != null && !result.trim().equals("")) {
                    edt_end_loc.setText(result);
                    // pickup_drop_Sep.setVisibility(View.VISIBLE);
                    droplat = lat;
                    droplng = lng;
                    //D_longitude = lng;
//                if (!SessionSave.getSession(CommonData.isNeedtofetchAddress, OnmywayNextAct.this, true)) {
//                    drop_fav.setVisibility(View.VISIBLE);
//                }


                }
            }
        } catch (Exception e) {
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
                    new APIService_Retrofit_JSON(OnmywayNextAct.this, this, data, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(OnmywayNextAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, OnmywayNextAct.this, "");
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


                        edt_start_loc.setText(Html.fromHtml(details.getString("start_location")));
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
                        droplng = Double.parseDouble(details.getString("end_loc_longitude"));

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.ViewHolder> {
        Context context;
        private final List<String> colorData;
        int selectpos = -1;

        public DaysAdapter(Context context, List<String> data) {
            this.context = context;
            this.colorData = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(OnmywayNextAct.this);
            View view = null;
            view = inflater.inflate(R.layout.days_list, parent, false);
            FontHelper.applyFont(context, view.findViewById(R.id.rlMain));
            return new DaysAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.Text.setText(colorData.get(position));
           /* holder.check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.check.isChecked()) {
                        values += colorData.get(position) + ",";
                    }
                }
            });*/

            try {
                if (!selected_weekdays.equalsIgnoreCase("")) {
                    String[] replace = selected_weekdays.split(",");


                    for (int i = 0; i < replace.length; i++) {
                        if (colorData.get(position).equalsIgnoreCase(shortformday_reverse(replace[i]))) {
                            holder.check.setChecked(true);
                            selectedStrings.add(shortformday(colorData.get(position)));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            holder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedStrings.add(shortformday(holder.Text.getText().toString()));

                    } else {
                        selectedStrings.remove(shortformday(holder.Text.getText().toString()));
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return colorData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox check;
            TextView Text;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                check = itemView.findViewById(R.id.check);
                Text = itemView.findViewById(R.id.Text);
            }

        }
    }

    private String shortformday(String day) {
        String shortday = null;
        if (day.equalsIgnoreCase(NC.getResources().getString(R.string.Monday))) {
            shortday = "Mon";
        } else if (day.equalsIgnoreCase(NC.getResources().getString(R.string.Tuesday))) {
            shortday = "Tue";
        } else if (day.equalsIgnoreCase(NC.getResources().getString(R.string.Wednesday))) {
            shortday = "Wed";
        } else if (day.equalsIgnoreCase(NC.getResources().getString(R.string.Thursday))) {
            shortday = "Thu";
        } else if (day.equalsIgnoreCase(NC.getResources().getString(R.string.Friday))) {
            shortday = "Fri";
        } else if (day.equalsIgnoreCase(NC.getResources().getString(R.string.Saturday))) {
            shortday = "Sat";
        } else if (day.equalsIgnoreCase(NC.getResources().getString(R.string.Sunday))) {
            shortday = "Sun";
        }
        return shortday;

    }

    private String shortformday_reverse(String day) {
        String shortday = null;
        if (day.equalsIgnoreCase("Mon")) {
            shortday = NC.getResources().getString(R.string.Monday);
        } else if (day.equalsIgnoreCase("Tue")) {
            shortday = NC.getResources().getString(R.string.Tuesday);
        } else if (day.equalsIgnoreCase("Wed")) {
            shortday = NC.getResources().getString(R.string.Wednesday);
        } else if (day.equalsIgnoreCase("Thu")) {
            shortday = NC.getResources().getString(R.string.Thursday);
        } else if (day.equalsIgnoreCase("Fri")) {
            shortday = NC.getResources().getString(R.string.Friday);
        } else if (day.equalsIgnoreCase("Sat")) {
            shortday = NC.getResources().getString(R.string.Saturday);
        } else if (day.equalsIgnoreCase("Sun")) {
            shortday = NC.getResources().getString(R.string.Sunday);
        }
        return shortday;

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
