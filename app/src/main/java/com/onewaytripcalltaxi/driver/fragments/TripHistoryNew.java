package com.onewaytripcalltaxi.driver.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.onewaytripcalltaxi.driver.MyApplication;
import com.onewaytripcalltaxi.driver.R;
import com.onewaytripcalltaxi.driver.adapter.PastBookingAdapter;
import com.onewaytripcalltaxi.driver.data.apiData.ApiRequestData;
import com.onewaytripcalltaxi.driver.data.apiData.UpcomingResponse;
import com.onewaytripcalltaxi.driver.interfaces.UpcomingAdapterInterface;
import com.onewaytripcalltaxi.driver.service.CoreClient;
import com.onewaytripcalltaxi.driver.service.RetrofitCallbackClass;
import com.onewaytripcalltaxi.driver.service.ServiceGenerator;
import com.onewaytripcalltaxi.driver.utils.CToast;
import com.onewaytripcalltaxi.driver.utils.NetworkStatus;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Systems;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by developer on 1/11/16.
 */

/**
 * This class is used to show trip history,Here we can see both upcoming and past booking history
 */
public class TripHistoryNew extends Activity implements UpcomingAdapterInterface {

    private static boolean UP_COMING = true;
    TextView txt_up_coming, txt_past_booking;
    RecyclerView history_recyclerView;
    private TextView no_data;
    int start = 0;
    private int limit = 10;
    private LinearLayoutManager mLayoutManager;
    private final List<UpcomingResponse.PastBooking> pastData = new ArrayList<>();
    private List<UpcomingResponse.PastBooking> upComingData = new ArrayList<>();
    private int prevLimt;
    private PastBookingAdapter past_booking_adapter;
    private Dialog mDialog;

    private boolean isFirst = true;
    private boolean isFirstUpcoming = true;
LinearLayout trip_buttons;
    CardView txt_up_coming_r, txt_past_booking_r,back_trip;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_history_lay_new);
        Initialize();
    }

    /**
     * Initializing UI Components
     */
    public void Initialize() {
        txt_past_booking =findViewById(R.id.txt_past_booking);
        txt_up_coming =findViewById(R.id.txt_up_coming);
        history_recyclerView =findViewById(R.id.history_recyclerView);
        trip_buttons =findViewById(R.id.trip_buttons);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        history_recyclerView.setLayoutManager(mLayoutManager);
        no_data =findViewById(R.id.nodataTxt);
        txt_up_coming_r =findViewById(R.id.txt_up_coming_r);
        txt_past_booking_r =findViewById(R.id.txt_past_booking_r);
        back_trip =findViewById(R.id.back_trip);
        trip_buttons.setVisibility(View.GONE);




        back_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onBackPressed();
            }
        });



                if (isFirst) {
                    isFirst = false;
                    pastData.clear();
                    history_recyclerView.setAdapter(null);
                    past_booking_adapter = null;
                    start = 0;
                    limit = 10;
                    UP_COMING = false;
                    callPastBookingData();
                } else {
                    UP_COMING = false;
                    if (pastData.size() == 0)
                        no_data.setVisibility(View.VISIBLE);
                    else
                        no_data.setVisibility(View.GONE);
                    past_booking_adapter = new PastBookingAdapter(getApplicationContext(), pastData);
                    history_recyclerView.setAdapter(past_booking_adapter);
                }





        history_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!UP_COMING)
                    if (dy > 0) //check for scroll down
                    {
                        int visibleItemCount = mLayoutManager.getChildCount();
                        int totalItemCount = mLayoutManager.getItemCount();
                        int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {

                            if (totalItemCount >= 10 && limit >= prevLimt && totalItemCount == limit) {
                                Systems.out.println("_*____*****_" + limit + "***" + start + "***" + totalItemCount);
                                if (start == 0)
                                    start = 11;
                                else
                                    start += 10;
                                prevLimt = limit;
                                limit += 10;
                                callPastBookingData();
                            }
                        }
                    }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        closeDialog();
    }



    public void showDialog() {
        System.out.println("loading_test"+ " " + "ksaoasfashkfasnnafjsajk");
        try {
            if (NetworkStatus.isOnline(TripHistoryNew.this)) {
                System.out.println("loading_test"+ " " + "1ksaoasfashkfasnnafjsajk");

                View view = View.inflate(TripHistoryNew.this, R.layout.progress_bar, null);
                mDialog = new Dialog(TripHistoryNew.this, R.style.dialogwinddow);
                mDialog.setContentView(view);
                mDialog.setCancelable(false);
                mDialog.show();

                ImageView iv = mDialog.findViewById(R.id.giff);
                DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
                Glide.with(TripHistoryNew.this)
                        .load(R.raw.loading_anim)
                        .into(imageViewTarget);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Close alert dialog
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



    private void callPastBookingData() {
        CoreClient client = MyApplication.getInstance().getApiManagerWithEncryptBaseUrl();
        ApiRequestData.UpcomingRequest request = new ApiRequestData.UpcomingRequest();
        request.setId(SessionSave.getSession("Id", getApplicationContext()));
        request.setDeviceType("2");
        request.setLimit("10");
        request.setStart(String.valueOf(start));
        request.setRequestType("2");

        Call<UpcomingResponse> LoginResponse = client.callData_(ServiceGenerator.COMPANY_KEY, request, SessionSave.getSession("Lang", getApplicationContext()));
        showDialog();
        LoginResponse.enqueue(new RetrofitCallbackClass<>(getApplicationContext(), new Callback<UpcomingResponse>() {
            @Override
            public void onResponse(Call<UpcomingResponse> call, Response<UpcomingResponse> response) {
                closeDialog();
                if ( response.isSuccessful()) {
                    UpcomingResponse data = response.body();

                    if (data != null) {
                        if (data.status == 1) {

                            if (data != null) {
                                if (data.status == 1) {

                                    pastData.addAll(data.detail.past_booking);
                                    if (past_booking_adapter == null) {

                                        past_booking_adapter = new PastBookingAdapter(getApplicationContext(), pastData);
                                        history_recyclerView.setAdapter(past_booking_adapter);
                                        if (data.detail.past_booking.size() == 0)
                                            no_data.setVisibility(View.VISIBLE);
                                        else
                                            no_data.setVisibility(View.GONE);
                                    } else {
                                        past_booking_adapter.notifyDataSetChanged();
                                    }
                                } else {
                                    CToast.ShowToast(getApplicationContext(), data.message);
                                }
                            } else {

                                isFirst = true;
                            }
                        } else {
                            isFirst = true;
                        }
                    } else {

                    }
                } else {
                    isFirst = true;

                }
            }

            @Override
            public void onFailure(Call<UpcomingResponse> call, Throwable t) {
                t.printStackTrace();
                closeDialog();
                isFirst = true;

            }
        }));
    }


    @Override
    public void updateUpcomingAdapter(List<? extends UpcomingResponse.PastBooking> data, int clickedPosition) {
        upComingData = new ArrayList<>();
        upComingData.addAll(data);
        if (upComingData.size() == 0)
            no_data.setVisibility(View.VISIBLE);
        else
            no_data.setVisibility(View.GONE);
     //   upcomingAdapter.notifyDataSetChanged();
    }
}
