package com.onewaytripcalltaxi.driver.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.onewaytripcalltaxi.driver.MyApplication;
import com.onewaytripcalltaxi.driver.R;
import com.onewaytripcalltaxi.driver.adapter.NewBookingAdapter;
import com.onewaytripcalltaxi.driver.adapter.PastBookingAdapter;
import com.onewaytripcalltaxi.driver.adapter.UpcomingAdapter;
import com.onewaytripcalltaxi.driver.data.apiData.ApiRequestData;
import com.onewaytripcalltaxi.driver.data.apiData.UpcomingResponse;
import com.onewaytripcalltaxi.driver.interfaces.NewBookingAdapterInterface;
import com.onewaytripcalltaxi.driver.interfaces.UpcomingAdapterInterface;
import com.onewaytripcalltaxi.driver.service.CoreClient;
import com.onewaytripcalltaxi.driver.service.RetrofitCallbackClass;
import com.onewaytripcalltaxi.driver.service.ServiceGenerator;
import com.onewaytripcalltaxi.driver.utils.CL;
import com.onewaytripcalltaxi.driver.utils.CToast;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.NetworkStatus;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Systems;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by developer on 1/11/16.
 */

/**
 * This class is used to show trip history,Here we can see both upcoming and past booking history
 */
public class TripHistory extends Fragment implements UpcomingAdapterInterface, NewBookingAdapterInterface {

    private static boolean UP_COMING = true;
    TextView txt_up_coming, txt_past_booking,txt_schdule_booking;
    RecyclerView history_recyclerView;
    private TextView no_data;
    int start = 0;
    private int limit = 10;
    private LinearLayoutManager mLayoutManager;
    private final List<UpcomingResponse.PastBooking> pastData = new ArrayList<>();
    private List<UpcomingResponse.PastBooking> upComingData = new ArrayList<>();
    private List<UpcomingResponse.ShowBooking> newBookings = new ArrayList<>();
    private int prevLimt;
    private PastBookingAdapter past_booking_adapter;
    private Dialog mDialog;

    private View upcoming_underline, past_underline;
    private boolean isFirst = true;
    private boolean isFirstUpcoming = true;

    CardView txt_up_coming_r, txt_past_booking_r,txt_schdule_booking_r;
    private UpcomingAdapter upcomingAdapter;
    private NewBookingAdapter newBookingAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.trip_history_lay, container, false);
        Initialize(v);
        return v;
    }


    /**
     * Initializing UI Components
     */
    public void Initialize(View v) {
        txt_past_booking = v.findViewById(R.id.txt_past_booking);
        txt_schdule_booking = v.findViewById(R.id.txt_schdule_booking);
        txt_up_coming = v.findViewById(R.id.txt_up_coming);
        history_recyclerView = v.findViewById(R.id.history_recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        history_recyclerView.setLayoutManager(mLayoutManager);
        no_data = v.findViewById(R.id.nodataTxt);
        txt_up_coming_r = v.findViewById(R.id.txt_up_coming_r);
        txt_past_booking_r = v.findViewById(R.id.txt_past_booking_r);
        txt_schdule_booking_r = v.findViewById(R.id.txt_schdule_booking_r);

       // Colorchange.ChangeColor((ViewGroup) v, getActivity());

        upcoming_underline = v.findViewById(R.id.upcoming_underline);
        past_underline = v.findViewById(R.id.past_underline);
        txt_up_coming.setTextColor(CL.getResources().getColor(R.color.white));
        txt_past_booking.setTextColor(CL.getResources().getColor(R.color.black));
        txt_up_coming_r.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.app_theme_color));
        txt_past_booking_r.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.pastbooking));
        //upcoming trip history onclick
        txt_up_coming.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                upcoming_underline.setBackgroundColor(CL.getResources().getColor(R.color.black));
                past_underline.setBackgroundColor(CL.getResources().getColor(R.color.white));

                txt_up_coming.setTextColor(CL.getResources().getColor(R.color.white));
                txt_past_booking.setTextColor(CL.getResources().getColor(R.color.black));
                txt_schdule_booking.setTextColor(CL.getResources().getColor(R.color.black));
                txt_up_coming_r.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.app_theme_color));
                txt_past_booking_r.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.pastbooking));
                txt_schdule_booking_r.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.pastbooking));
                if (isFirstUpcoming) {
                    Systems.out.println("innnnnn " + "1st n upComing " + upComingData.size());
                    isFirstUpcoming = false;
                    history_recyclerView.setAdapter(null);
                    UP_COMING = true;
                    callUpComingData();
                } else {
                    Systems.out.println("innnnnn " + "2nd n upComing " + upComingData.size());
                    if (upComingData.size() == 0)
                        no_data.setVisibility(View.VISIBLE);
                    else
                        no_data.setVisibility(View.GONE);

                    UP_COMING = true;
                    upcomingAdapter = new UpcomingAdapter(getContext(), upComingData, TripHistory.this);
                    history_recyclerView.setAdapter(upcomingAdapter);
                }
            }
        });

        //past booking trip history onclick
        txt_past_booking.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                past_underline.setBackgroundColor(CL.getResources().getColor(R.color.white));
                upcoming_underline.setBackgroundColor(CL.getResources().getColor(R.color.black));

                txt_past_booking.setTextColor(CL.getResources().getColor(R.color.white));
                txt_up_coming.setTextColor(CL.getResources().getColor(R.color.black));
                txt_past_booking_r.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.app_theme_color));
                txt_up_coming_r.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.pastbooking));
                txt_schdule_booking_r.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.pastbooking));
                txt_schdule_booking.setTextColor(CL.getResources().getColor(R.color.black));

                if (isFirst) {
                    Systems.out.println("innnnnn " + "1st");
                    isFirst = false;
                    pastData.clear();
                    history_recyclerView.setAdapter(null);
                    past_booking_adapter = null;
                    start = 0;
                    limit = 10;
                    UP_COMING = false;
                    callPastBookingData();
                } else {
                    Systems.out.println("innnnnn " + "2nd " + pastData.size());
                    UP_COMING = false;
                    if (pastData.size() == 0)
                        no_data.setVisibility(View.VISIBLE);
                    else
                        no_data.setVisibility(View.GONE);
                    past_booking_adapter = new PastBookingAdapter(getContext(), pastData);
                    history_recyclerView.setAdapter(past_booking_adapter);
                }
            }
        });
        txt_schdule_booking.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                past_underline.setBackgroundColor(CL.getResources().getColor(R.color.white));
                upcoming_underline.setBackgroundColor(CL.getResources().getColor(R.color.black));

                txt_past_booking.setTextColor(CL.getResources().getColor(R.color.black));
                txt_up_coming.setTextColor(CL.getResources().getColor(R.color.black));
                txt_past_booking_r.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.pastbooking));
                txt_up_coming_r.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.pastbooking));
                txt_schdule_booking_r.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.app_theme_color));
                txt_schdule_booking.setTextColor(CL.getResources().getColor(R.color.white));



                if (isFirst) {
                    Systems.out.println("innnnnn " + "1st");
                    isFirst = false;
                    newBookings.clear();
                    history_recyclerView.setAdapter(null);
                    newBookingAdapter = null;
                    start = 0;
                    limit = 10;
                    UP_COMING = false;
                    callSchduleData();
                } else {
                    Systems.out.println("innnnnn " + "2nd " + newBookings.size());
                    UP_COMING = false;
                    if (newBookings.size() == 0)
                        no_data.setVisibility(View.VISIBLE);
                    else
                        no_data.setVisibility(View.GONE);
                    newBookingAdapter = new NewBookingAdapter(getContext(), newBookings,TripHistory.this);
                    history_recyclerView.setAdapter(newBookingAdapter);
                }

            }
        });

        txt_up_coming.callOnClick();

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
                            // loading = false;
                            //Do pagination.. i.e. fetch new data
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


    /**
     * Upcomingtrip API response parsing.
     */
    private void callUpComingData() {
        isFirstUpcoming = false;
        CoreClient client = MyApplication.getInstance().getApiManagerWithEncryptBaseUrl();
        ApiRequestData.UpcomingRequest request = new ApiRequestData.UpcomingRequest();
        request.setId(SessionSave.getSession("Id", getActivity()));
        request.setDeviceType("2");
        request.setLimit("10");
        request.setStart("0");
        request.setRequestType("1");

        Call<UpcomingResponse> LoginResponse = client.callData(ServiceGenerator.COMPANY_KEY, request, SessionSave.getSession("Lang", getActivity()));

        showDialog();

        LoginResponse.enqueue(new RetrofitCallbackClass<>(getActivity(), new Callback<UpcomingResponse>() {

            @Override
            public void onResponse(Call<UpcomingResponse> call, Response<UpcomingResponse> response) {
                closeDialog();
                if (getView() != null && response.isSuccessful()) {
                    UpcomingResponse data = response.body();
                    if (data != null) {
                        if (data.status == 1) {
                            upComingData.addAll(data.detail.pending_booking);
                            if (data.detail.pending_booking == null)
                                no_data.setVisibility(View.VISIBLE);
                            else {
                                if (data.detail.pending_booking.size() == 0)
                                    no_data.setVisibility(View.VISIBLE);
                                else {
                                    if (upcomingAdapter == null) {
                                        upcomingAdapter = new UpcomingAdapter(getContext(), upComingData, TripHistory.this);
                                        history_recyclerView.setAdapter(upcomingAdapter);

                                        no_data.setVisibility(View.GONE);
                                    } else {
                                        upcomingAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        } else {
                            CToast.ShowToast(getActivity(), data.message);
                            isFirstUpcoming = true;
                        }
                    } else {
                    //    CToast.ShowToast(getActivity(), NC.getString(R.string.server_error));
                        isFirstUpcoming = true;
                    }
                } else {
                   // CToast.ShowToast(getActivity(), NC.getString(R.string.server_error));
                    isFirstUpcoming = true;
                }


            }

            @Override
            public void onFailure(Call<UpcomingResponse> call, Throwable t) {
                t.printStackTrace();
                CToast.ShowToast(getActivity(), NC.getString(R.string.please_check_internet));
                closeDialog();
                isFirstUpcoming = true;
            }
        }));
    }

    private void callSchduleData() {
        isFirstUpcoming = false;
        CoreClient client = MyApplication.getInstance().getApiManagerWithEncryptBaseUrl();
        ApiRequestData.UpcomingRequest request = new ApiRequestData.UpcomingRequest();
        request.setId(SessionSave.getSession("Id", getActivity()));
        request.setDeviceType("2");
        request.setLimit("10");
        request.setStart("0");
        request.setRequestType("3");

        Call<UpcomingResponse> LoginResponse = client.callData(ServiceGenerator.COMPANY_KEY, request, SessionSave.getSession("Lang", getActivity()));

        showDialog();

        LoginResponse.enqueue(new RetrofitCallbackClass<>(getActivity(), new Callback<UpcomingResponse>() {

            @Override
            public void onResponse(Call<UpcomingResponse> call, Response<UpcomingResponse> response) {
                closeDialog();
                if (getView() != null && response.isSuccessful()) {
                    UpcomingResponse data = response.body();
                    if (data != null) {
                        if (data.status == 1) {
                            newBookings.addAll(data.detail.show_booking);
                            if (data.detail.show_booking == null)
                                no_data.setVisibility(View.VISIBLE);
                            else {
                                if (data.detail.show_booking.size() == 0)
                                    no_data.setVisibility(View.VISIBLE);
                                else {
                                    if (newBookingAdapter == null) {
                                        System.out.println("pickup_location_newbooking" + " " + "issettttttttttttttttttt");

                                        newBookingAdapter = new NewBookingAdapter(getContext(), newBookings, TripHistory.this);
                                        history_recyclerView.setAdapter(newBookingAdapter);

                                        no_data.setVisibility(View.GONE);
                                    } else {
                                        newBookingAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        } else {
                            CToast.ShowToast(getActivity(), data.message);
                            isFirstUpcoming = true;
                        }
                    } else {
                        //    CToast.ShowToast(getActivity(), NC.getString(R.string.server_error));
                        isFirstUpcoming = true;
                    }
                } else {
                    // CToast.ShowToast(getActivity(), NC.getString(R.string.server_error));
                    isFirstUpcoming = true;
                }


            }

            @Override
            public void onFailure(Call<UpcomingResponse> call, Throwable t) {
                t.printStackTrace();
                CToast.ShowToast(getActivity(), NC.getString(R.string.please_check_internet));
                closeDialog();
                isFirstUpcoming = true;
            }
        }));
    }
    /**
     * Show alert dialog
     */

    public void showDialog() {
        try {
            if (NetworkStatus.isOnline(getActivity())) {
                View view = View.inflate(getActivity(), R.layout.progress_bar, null);
                mDialog = new Dialog(getActivity(), R.style.dialogwinddow);
                mDialog.setContentView(view);
                mDialog.setCancelable(false);
                mDialog.show();

                ImageView iv = mDialog.findViewById(R.id.giff);
                DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
                Glide.with(TripHistory.this)
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


    /**
     * Pastbooking API response parsing.
     */
    private void callPastBookingData() {
        CoreClient client = MyApplication.getInstance().getApiManagerWithEncryptBaseUrl();
        ApiRequestData.UpcomingRequest request = new ApiRequestData.UpcomingRequest();
        request.setId(SessionSave.getSession("Id", getActivity()));
        request.setDeviceType("2");
        request.setLimit("10");
        request.setStart(String.valueOf(start));
        request.setRequestType("2");

        Call<UpcomingResponse> LoginResponse = client.callData_(ServiceGenerator.COMPANY_KEY, request, SessionSave.getSession("Lang", getActivity()));
        showDialog();
        LoginResponse.enqueue(new RetrofitCallbackClass<>(getActivity(), new Callback<UpcomingResponse>() {
            @Override
            public void onResponse(Call<UpcomingResponse> call, Response<UpcomingResponse> response) {
                closeDialog();
                if (getView() != null && response.isSuccessful()) {
                    UpcomingResponse data = response.body();

                    if (data != null) {
                        if (data.status == 1) {

                            if (data != null) {
                                if (data.status == 1) {

                                    Systems.out.println("_)_____" + data.detail.past_booking.size());
                                    pastData.addAll(data.detail.past_booking);
                                    if (past_booking_adapter == null) {

                                        past_booking_adapter = new PastBookingAdapter(getContext(), pastData);
                                        history_recyclerView.setAdapter(past_booking_adapter);
                                        if (data.detail.past_booking.size() == 0)
                                            no_data.setVisibility(View.VISIBLE);
                                        else
                                            no_data.setVisibility(View.GONE);
                                    } else {
                                        past_booking_adapter.notifyDataSetChanged();
                                    }
                                } else {
                                    CToast.ShowToast(getActivity(), data.message);
                                }
                            } else {
                                //CToast.ShowToast(getActivity(), NC.getString(R.string.server_error));
                                isFirst = true;
                            }
                        } else {
                            isFirst = true;
                        }
                    } else {
                       // CToast.ShowToast(getActivity(), NC.getString(R.string.server_error));
                        isFirst = true;
                    }
                } else {
                    isFirst = true;
                   // CToast.ShowToast(getActivity(), NC.getString(R.string.server_error));
                }
            }

            @Override
            public void onFailure(Call<UpcomingResponse> call, Throwable t) {
                t.printStackTrace();
                closeDialog();
                isFirst = true;
//                if (getActivity() != null)
//                    CToast.ShowToast(getActivity(), NC.getString(R.string.server_error));
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
        upcomingAdapter.notifyDataSetChanged();
    }

    @Override
    public void newbookingUpcomingAdapter(@NonNull List<? extends UpcomingResponse.ShowBooking> data, int clickedPosition) {

    }
}
