package com.onewaytripcalltaxi.driver.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.onewaytripcalltaxi.driver.data.CommonData;
import com.squareup.picasso.Picasso;
import com.onewaytripcalltaxi.driver.MainActivity;
import com.onewaytripcalltaxi.driver.OngoingAct;
import com.onewaytripcalltaxi.driver.R;
import com.onewaytripcalltaxi.driver.data.apiData.UpcomingResponse;
import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.interfaces.ClickInterface;
import com.onewaytripcalltaxi.driver.interfaces.UpcomingAdapterInterface;
import com.onewaytripcalltaxi.driver.route.StopData;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.service.NonActivity;
import com.onewaytripcalltaxi.driver.utils.CToast;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import static com.onewaytripcalltaxi.driver.OngoingAct.MY_PERMISSIONS_REQUEST_CALL;

/**
 * Created by developer on 1/11/16.
 */

/**
 * This adapter class is used to show upcoming trip and pending trip
 */
public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.CustomViewHolder> implements
        ClickInterface, ActivityCompat.OnRequestPermissionsResultCallback {

    private List<UpcomingResponse.PastBooking> data = new ArrayList<>();
    private final Context mContext;
    private String passPhoneNo;
    private String booking_Type;
    private String upcomingTripId;
    private String trip_id;
    private int positions;
    private int cancelTripPosition;
    UpcomingAdapterInterface mInterface;
    private Dialog myOTPDialog;
    private Dialog myDialog;
    public UpcomingAdapter(Context c, List<UpcomingResponse.PastBooking> data, UpcomingAdapterInterface upcomingAdapterInterface) {
        this.mContext = c;
        this.data = data;
        this.mInterface = upcomingAdapterInterface;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = null;
        view = inflater.inflate(R.layout.upcoming_list_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (!data.get(position).profile_image.trim().equals("")) {
            Picasso.get().load(data.get(position).map_image).into(holder.map_image);
            Picasso.get().load(data.get(position).profile_image).into(holder.driver_image);
            Picasso.get().load(data.get(position).profile_image).into(holder.passengerImg);
        }
        Colorchange.ChangeColor(holder.book_lay, mContext);
        holder.trip_time.setText(data.get(position).pickup_time);
        holder.trip_driver_name.setText(data.get(position).model_name);
        holder.pickup_location.setText(data.get(position).pickup_location);
        holder.drop_location.setText(data.get(position).drop_location);

        positions = position;
        if (data.get(position).travel_status != null)
           /* if (!data.get(position).travel_status.trim().equals("0")) {
                holder.normalLay.setVisibility(View.VISIBLE);
                holder.trip_track.setVisibility(View.VISIBLE);
                holder.bookLaterLayout.setVisibility(View.GONE);
                holder.trip_cancel.setVisibility(View.GONE);
                holder.trip_track.setTag(position);
                holder.trip_track.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (SessionSave.getSession("shift_status", mContext).equalsIgnoreCase("IN")) {
                            SessionSave.saveSession("trip_id", data.get((Integer) view.getTag()).passengers_log_id.trim(), mContext);
                            Intent in = new Intent(mContext, OngoingAct.class);
                            mContext.startActivity(in);
                        } else {
                            CToast.ShowToast(mContext, NC.getResources().getString(R.string.track_shift_status));
                        }
                    }
                });
//            } else {*/
            if (data.get(position).schedule.trim().equals("1")) {
                holder.trip_track.setVisibility(View.GONE);
                holder.trip_cancel.setVisibility(View.GONE);
                holder.trip_cancel.setTag(position);
                holder.normalLay.setVisibility(View.GONE);
                holder.bookLaterLayout.setVisibility(View.VISIBLE);
                holder.passengerPhoneTxt.setVisibility(View.GONE);
                holder.passengerPhoneTxt.setText(data.get(position).passenger_phone);
                holder.passengerName.setText(data.get(position).passenger_name);
                holder.pickupTimeTxt.setText(data.get(position).pickup_time);
                holder.updateTimeTxt.setText(data.get(position).time);
                holder.updateDistanceTxt.setText(data.get(position).away);

                //  holder.pickUpDropLayout.setData(getStopArray(position, data), "SCHEDULE", SessionSave.getSession("Lang", mContext));

//                    holder.testLay.setData(getStopArray(position, data), "SCHEDULE", SessionSave.getSession("Lang", mContext));

                holder.passengerCallTxt.setOnClickListener(view -> {
                    try {
                        passPhoneNo = data.get(position).passenger_country_code + data.get(position).passenger_phone;
                        if (passPhoneNo.equals("0"))
                            Utils.alert_view(mContext, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.invalid_mobile_number), NC.getResources().getString(R.string.ok), "", true, this, "4");
                        else {
                       /*     if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                Utils.alert_view_dialog(mContext, "", NC.getResources().getString(R.string.str_phone), NC.getResources().getString(R.string.yes), NC.getResources().getString(R.string.no), true, (dialog, i) -> {
                                    ActivityCompat.requestPermissions((Activity) mContext,
                                            new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE},
                                            MY_PERMISSIONS_REQUEST_CALL);
                                    dialog.dismiss();
                                }, (dialog, i) -> dialog.dismiss(), "");
                            } else {*/
                            ensureCall();
//                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                holder.startTripTxt.setOnClickListener(view -> {

                    System.out.println("passengers_log_id"+ " "+ data.get(position).passengers_log_id);
                    System.out.println("passengers_log_id"+ " "+  data.get(position).trip_type);
                    booking_Type = data.get(position).trip_type;
                    trip_id = data.get(position).passengers_log_id;
                    showOtp(mContext);

                });

                holder.cancelTxt.setOnClickListener(view ->
                {

                    upcomingTripId = data.get(position).passengers_log_id.trim();
                    cancelTripPosition = position;
                    Utils.alert_view(mContext, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.cancel_in_going_trip), NC.getResources().getString(R.string.yes), NC.getResources().getString(R.string.no), true, this, "3");
                });

            } else {
                if (!data.get(position).travel_status.trim().equals("0")) {
                    holder.normalLay.setVisibility(View.VISIBLE);
                    holder.trip_track.setVisibility(View.VISIBLE);
                    holder.bookLaterLayout.setVisibility(View.GONE);
                    holder.trip_cancel.setVisibility(View.GONE);
                    holder.trip_track.setTag(position);
                    holder.trip_track.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (SessionSave.getSession("shift_status", mContext).equalsIgnoreCase("IN")) {
                                SessionSave.saveSession("trip_id", data.get((Integer) view.getTag()).passengers_log_id.trim(), mContext);
                                Intent in = new Intent(mContext, OngoingAct.class);
                                mContext.startActivity(in);
                            } else {
                                CToast.ShowToast(mContext, NC.getResources().getString(R.string.track_shift_status));
                            }
                        }
                    });
                } else {
                    holder.normalLay.setVisibility(View.VISIBLE);
                    holder.trip_track.setVisibility(View.GONE);
                    holder.trip_cancel.setVisibility(View.VISIBLE);
                    holder.bookLaterLayout.setVisibility(View.GONE);
                    holder.trip_cancel.setTag(position);
                }
            }
//            }
    }

    public void showOtp(Context mContext) {
        final View view1 = View.inflate(mContext, R.layout.odometer_otp_input, null);
        if (myOTPDialog != null && myOTPDialog.isShowing())
            myOTPDialog.cancel();
        myOTPDialog = new Dialog(mContext, R.style.NewDialog);
        myOTPDialog.setContentView(view1);
        myOTPDialog.setCancelable(false);
        myOTPDialog.setCanceledOnTouchOutside(false);
        myOTPDialog.setCancelable(true);
        myOTPDialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(myOTPDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        myOTPDialog.getWindow().setAttributes(layoutParams);


//
        LinearLayout btn_confirm = myOTPDialog.findViewById(R.id.btn_confirm);

        TextView odameter_heading = myOTPDialog.findViewById(R.id.odameter_heading);
        EditText verifyno1Txt = myOTPDialog.findViewById(R.id.verifyno1Txt);
        EditText verifyno2Txt = myOTPDialog.findViewById(R.id.verifyno2Txt);
        EditText verifyno3Txt = myOTPDialog.findViewById(R.id.verifyno3Txt);
        EditText verifyno4Txt = myOTPDialog.findViewById(R.id.verifyno4Txt);


        verifyno1Txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (s.toString().trim().length() == 1) {
                    verifyno2Txt.requestFocus();
                    verifyno2Txt.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });


        verifyno2Txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (s.toString().trim().length() == 1) {
                    verifyno3Txt.requestFocus();
                    verifyno3Txt.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        verifyno3Txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (s.toString().trim().length() == 1) {
                    verifyno4Txt.requestFocus();
                    verifyno4Txt.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });


        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (verifyno1Txt.getText().toString().equals("")) {
                    Toast.makeText(mContext, "Enter first number", Toast.LENGTH_LONG).show();
                } else if (verifyno2Txt.getText().toString().equals("")) {
                    Toast.makeText(mContext, "Enter second number", Toast.LENGTH_LONG).show();
                } else if (verifyno3Txt.getText().toString().equals("")) {
                    Toast.makeText(mContext, "Enter third number", Toast.LENGTH_LONG).show();

                } else if (verifyno4Txt.getText().toString().equals("")) {
                    Toast.makeText(mContext, "Enter fourth number", Toast.LENGTH_LONG).show();

                } else {
                    myOTPDialog.dismiss();
                    String otpnumber = verifyno1Txt.getText().toString() + verifyno2Txt.getText().toString() + verifyno3Txt.getText().toString() + verifyno4Txt.getText().toString();
                    final String url = "type=booking_otp_verify";


                    new updateOTP(url, "3", otpnumber);
                }


            }
        });

    }

    private ArrayList<StopData> getStopArray(int position, List<UpcomingResponse.PastBooking> data) {
        String pickupLat = data.get(position).pickup_latitude;
        String pickupLng = data.get(position).pickup_longitude;
        String dropLat = data.get(position).drop_latitude;
        String dropLng = data.get(position).drop_longitude;
        String pickLoc = data.get(position).pickup_location;
        String dropLoc = data.get(position).drop_location;
        String tripId = data.get(position).passengers_log_id;
        ArrayList<StopData> stopData = new ArrayList<>();
        if (!pickupLat.equals("") && !pickupLng.equals(""))
            stopData.add(new StopData(new Random().nextInt(), Double.parseDouble(pickupLat), Double.parseDouble(pickupLng), pickLoc, tripId, ""));
        if (!dropLat.equals("") && !dropLng.equals(""))
            stopData.add(new StopData(new Random().nextInt(), Double.parseDouble(dropLat), Double.parseDouble(dropLng), dropLoc, tripId, ""));
        return stopData;
    }

    /**
     * Call passenger
     */
    private void ensureCall() {
        Utils.alert_view(mContext, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.confirm_call), NC.getResources().getString(R.string.call), NC.getResources().getString(R.string.cancel), false, this, "1");
    }


    /**
     * Handling functionality after permission granted
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ensureCall();
            }
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void positiveButtonClick(DialogInterface dialog, int id, String s) {
        switch (s) {
            case "1":
                try {
                    dialog.dismiss();
                    final Intent callIntent = new Intent(Intent.ACTION_VIEW);
                    callIntent.setData(Uri.parse("tel:" + passPhoneNo));
                   /* if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }*/
                    mContext.startActivity(callIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "3":
                try {
                    JSONObject j = new JSONObject();
                    j.put("pass_logid", upcomingTripId);
                    j.put("driver_id", SessionSave.getSession("Id", mContext));
                    j.put("taxi_id", SessionSave.getSession("taxi_id", mContext));
                    j.put("company_id", SessionSave.getSession("company_id", mContext));
                    j.put("driver_reply", "C");
                    j.put("field", "");
                    j.put("flag", "1");
                    if (MainActivity.mMyStatus.getOnstatus().equalsIgnoreCase("Arrivd"))
                        j.put("driver_arrived", 1);
                    else
                        j.put("driver_arrived", 0);
                    j.put("schedule", "1");
                    final String canceltrip_url = "type=driver_reply";
                    new CancelTrip(canceltrip_url, j, mInterface, cancelTripPosition);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void negativeButtonClick(DialogInterface dialog, int id, String s) {
        dialog.dismiss();
    }


    /**
     * View holder class member this contains in every row in list.
     */
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView map_image, driver_image;
        TextView trip_time, trip_driver_name, trip_track,
                trip_cancel;
        LinearLayout book_lay;

        CardView bookLaterLayout;
        TextView updateTimeTxt, updateDistanceTxt, passengerPhoneTxt, passengerName, pickupTimeTxt, pickup_location, drop_location;
        LinearLayout passengerCallTxt, cancelTxt, startTripTxt;
        ImageView passengerImg;
        // private final PickupDropView pickUpDropLayout;
        private final CardView normalLay;

        public CustomViewHolder(View v) {
            super(v);
            map_image = v.findViewById(R.id.map_image);
            driver_image = v.findViewById(R.id.driver_image);
            trip_time = v.findViewById(R.id.trip_time);
            trip_driver_name = v.findViewById(R.id.trip_driver_name);
            trip_track = v.findViewById(R.id.trip_track);
            trip_cancel = v.findViewById(R.id.trip_cancel);
            book_lay = v.findViewById(R.id.book_lay);

            bookLaterLayout = v.findViewById(R.id.bookLaterLay);
            updateTimeTxt = v.findViewById(R.id.updateTimeTxt);
            updateDistanceTxt = v.findViewById(R.id.updateDisTxt);
            passengerPhoneTxt = v.findViewById(R.id.passengerPhoneTxt);
            passengerCallTxt = v.findViewById(R.id.passengerCallTxt);
            cancelTxt = v.findViewById(R.id.cancelTxt);
            startTripTxt = v.findViewById(R.id.startTripTxt);
            passengerName = v.findViewById(R.id.passengerName);
            pickupTimeTxt = v.findViewById(R.id.pickupTimeTxt);
            pickup_location = v.findViewById(R.id.pickup_location);
            drop_location = v.findViewById(R.id.drop_location);
            passengerImg = v.findViewById(R.id.passengerImg);
            //    pickUpDropLayout = v.findViewById(R.id.pd_view);
            normalLay = v.findViewById(R.id.normalLay);

        }
    }


    private class CancelTrip implements APIResult {
        UpcomingAdapterInterface upcomingAdapterInterface;
        int clickedPosition;

        CancelTrip(final String url, JSONObject data, UpcomingAdapterInterface mInterface, int position) {
            this.upcomingAdapterInterface = mInterface;
            this.clickedPosition = position;
            try {
                if (isOnline()) {
                    new APIService_Retrofit_JSON(mContext, this, data, false).execute(url);
                } else {
                    Utils.alert_view(mContext, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, UpcomingAdapter.this, "4");
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
                        CToast.ShowToast(mContext, json.getString("message"));
                    } else {
                        CToast.ShowToast(mContext, json.getString("message"));
                    }
                    data.remove(clickedPosition);
                    upcomingAdapterInterface.updateUpcomingAdapter(data, clickedPosition);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                //  CToast.ShowToast(mContext, NC.getString(R.string.server_error));
            }
        }

    }

    private class updateOTP implements APIResult {
        updateOTP(final String url, String type, String odometer_number) {
            try {

                JSONObject j = new JSONObject();

                j.put("trip_id", trip_id);
                j.put("otp", odometer_number);


                new APIService_Retrofit_JSON(mContext, this, j, false).execute(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            try {
                if (isSuccess) {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        {
                            SessionSave.saveSession("otp_enter", "no", mContext);
                            SessionSave.saveSession("odameter_status", "2", mContext);
//showodometer();
                            if (booking_Type.equals("2") || booking_Type.equals("3")) {
                                showodometer();
                            } else {


                                try {
                                    JSONObject j = new JSONObject();
                                    j.put("trip_id",trip_id);
                                    j.put("driver_id", SessionSave.getSession("Id", mContext));
                                    j.put("pickup_latitude", SessionSave.getSession(CommonData.CURRENT_LAT, mContext));
                                    j.put("pickup_longitude", SessionSave.getSession(CommonData.CURRENT_LNG, mContext));
                                    String scheduleTripUrl = "type=schedule_start_trip";
                                    new NonActivity().stopServicefromNonActivity(mContext);
                                    new ScheduleStartTrip(scheduleTripUrl, j, mInterface, positions);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }

                            //  startTrip();
                        }
                    } else {
                        // dialog1 = Utils.alert_view(mContext, NC.getResources().getString(R.string.message),json.getString("message") , NC.getResources().getString(R.string.ok), "", true, mContext, "4");
                        Utils.alert_view(mContext, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, UpcomingAdapter.this, "4");

                    }

                }
            } catch (final Exception e) {
                e.printStackTrace();

            }
        }
    }

    public void showodometer() {
        final View view1 = View.inflate(mContext, R.layout.odometer_input, null);
        if (myDialog != null && myDialog.isShowing())
            myDialog.cancel();
        myDialog = new Dialog(mContext, R.style.NewDialog);
        myDialog.setContentView(view1);
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.setCancelable(true);
        myDialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(myDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        myDialog.getWindow().setAttributes(layoutParams);


//
        LinearLayout btn_confirm = myDialog.findViewById(R.id.btn_confirm);

        TextView odameter_heading = myDialog.findViewById(R.id.odameter_heading);
        EditText verifyno1Txt = myDialog.findViewById(R.id.verifyno1Txt);
        EditText verifyno2Txt = myDialog.findViewById(R.id.verifyno2Txt);
        EditText verifyno3Txt = myDialog.findViewById(R.id.verifyno3Txt);
        EditText verifyno4Txt = myDialog.findViewById(R.id.verifyno4Txt);
        EditText verifyno5Txt = myDialog.findViewById(R.id.verifyno5Txt);
        EditText verifyno6Txt = myDialog.findViewById(R.id.verifyno6Txt);
        //   EditText verifyno7Txt = myDialog.findViewById(R.id.verifyno7Txt);


        if (SessionSave.getSession("odameter_status", mContext).equals("2")) {

            odameter_heading.setText("Start  Reading");
        } else if (SessionSave.getSession("odameter_status", mContext).equals("3")) {

            odameter_heading.setText("End Reading");
        } else if (SessionSave.getSession("odameter_status", mContext).equals("3")) {
            odameter_heading.setText("Accept Reading");
        }
        verifyno1Txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (s.toString().trim().length() == 1) {
                    verifyno2Txt.requestFocus();
                    verifyno2Txt.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });


        verifyno2Txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (s.toString().trim().length() == 1) {
                    verifyno3Txt.requestFocus();
                    verifyno3Txt.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        verifyno3Txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (s.toString().trim().length() == 1) {
                    verifyno4Txt.requestFocus();
                    verifyno4Txt.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        verifyno4Txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (s.toString().trim().length() == 1) {
                    verifyno5Txt.requestFocus();
                    verifyno5Txt.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        verifyno5Txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (s.toString().trim().length() == 1) {
                    verifyno6Txt.requestFocus();
                    verifyno6Txt.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

//        verifyno6Txt.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // TODO Auto-generated method stub
//                if (s.toString().trim().length() == 1) {
//                    verifyno7Txt.requestFocus();
//                    verifyno7Txt.setText("");
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // TODO Auto-generated method stub
//            }
//        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (verifyno1Txt.getText().toString().equals("")) {
                    Toast.makeText(mContext, "Enter first number", Toast.LENGTH_LONG).show();
                } else if (verifyno2Txt.getText().toString().equals("")) {
                    Toast.makeText(mContext, "Enter second number", Toast.LENGTH_LONG).show();
                } else if (verifyno3Txt.getText().toString().equals("")) {
                    Toast.makeText(mContext, "Enter third number", Toast.LENGTH_LONG).show();

                } else if (verifyno4Txt.getText().toString().equals("")) {
                    Toast.makeText(mContext, "Enter fourth number", Toast.LENGTH_LONG).show();

                } else if (verifyno5Txt.getText().toString().equals("")) {
                    Toast.makeText(mContext, "Enter fifth number", Toast.LENGTH_LONG).show();

                } else if (verifyno6Txt.getText().toString().equals("")) {
                    Toast.makeText(mContext, "Enter sixth number", Toast.LENGTH_LONG).show();

                } else {
                    myDialog.dismiss();
                    String otpnumber = verifyno1Txt.getText().toString() + verifyno2Txt.getText().toString() + verifyno3Txt.getText().toString() + verifyno4Txt.getText().toString() + verifyno5Txt.getText().toString() + verifyno6Txt.getText().toString() + "0";
                    final String url = "type=update_odometer";


                    new updateOdaMeter(url, "2", otpnumber);



                }


            }
        });

    }

    private class updateOdaMeter implements APIResult {
        updateOdaMeter(final String url, String type, String odometer_number) {
            try {

                JSONObject j = new JSONObject();
                j.put("driver_id", SessionSave.getSession("Id", mContext));
                j.put("trip_id",trip_id);
                j.put("odometer_number", odometer_number);
                j.put("level", type);

                new APIService_Retrofit_JSON(mContext, this, j, false).execute(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            try {
                if (isSuccess) {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        {
                            try {
                                JSONObject j = new JSONObject();
                                j.put("trip_id",trip_id);
                                j.put("driver_id", SessionSave.getSession("Id", mContext));
                                j.put("pickup_latitude", SessionSave.getSession(CommonData.CURRENT_LAT, mContext));
                                j.put("pickup_longitude", SessionSave.getSession(CommonData.CURRENT_LNG, mContext));
                                String scheduleTripUrl = "type=schedule_start_trip";
                                new NonActivity().stopServicefromNonActivity(mContext);
                                new ScheduleStartTrip(scheduleTripUrl, j, mInterface, positions);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    else {
                        Utils.alert_view(mContext, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, UpcomingAdapter.this, "4");

                    }

                }
            } catch (final Exception e) {
                e.printStackTrace();

            }
        }
    }

    private class ScheduleStartTrip implements APIResult {
        int clickedPosition;
        UpcomingAdapterInterface upcomingAdapterInterface;

        ScheduleStartTrip(final String url, JSONObject data, UpcomingAdapterInterface mInterface, int position) {
            this.clickedPosition = position;
            this.upcomingAdapterInterface = mInterface;
            try {
                if (isOnline()) {
                    new APIService_Retrofit_JSON(mContext, this, data, false).execute(url);
                } else {
                    Utils.alert_view(mContext, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, UpcomingAdapter.this, "4");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, String result) {
            if (isSuccess) {
                new NonActivity().startServicefromNonActivity(mContext);
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        SessionSave.saveSession("trip_id", json.getString("trip_id"), mContext);
                        SessionSave.saveSession("status", json.getString("driver_status"), mContext);
                        SessionSave.saveSession("travel_status", json.getString("travel_status"), mContext);
                        if (SessionSave.getSession("shift_status", mContext).equalsIgnoreCase("IN")) {
                            SessionSave.saveSession("trip_id", data.get(clickedPosition).passengers_log_id.trim(), mContext);
                            Intent in = new Intent(mContext, OngoingAct.class);
                            mContext.startActivity(in);
                        } else {
                            CToast.ShowToast(mContext, NC.getString(R.string.track_shift_status));
                        }
                    } else if (json.getInt("status") == -2) {
                        data.remove(clickedPosition);
                        upcomingAdapterInterface.updateUpcomingAdapter(data, clickedPosition);
                    } else {
                        CToast.ShowToast(mContext, json.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                // CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                new NonActivity().startServicefromNonActivity(mContext);
            }
        }
    }

    /**
     * This is method for check the Internet connection
     */
    public boolean isOnline() {

        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
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


}
