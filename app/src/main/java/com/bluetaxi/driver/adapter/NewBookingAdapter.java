package com.bluetaxi.driver.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bluetaxi.driver.R;
import com.bluetaxi.driver.data.apiData.UpcomingResponse;
import com.bluetaxi.driver.interfaces.NewBookingAdapterInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developer on 1/11/16.
 */

/**
 * This adapter class is used to show upcoming trip and pending trip
 */
public class NewBookingAdapter extends RecyclerView.Adapter<NewBookingAdapter.CustomViewHolder>  {

    private List<UpcomingResponse.ShowBooking> data = new ArrayList<>();
    private final Context mContext;

    NewBookingAdapterInterface mInterface;

    public NewBookingAdapter(Context c, List<UpcomingResponse.ShowBooking> data,
                             NewBookingAdapterInterface upcomingAdapterInterface) {
        this.mContext = c;
        this.data = data;
        this.mInterface = upcomingAdapterInterface;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = null;
        view = inflater.inflate(R.layout.new_booking_list_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        System.out.println("pickup_location_newbooking" + " " + data.get(position).pickup_location);
        System.out.println("pickup_location_newbooking" + " " + data.get(position).drop_location);
        System.out.println("pickup_location_newbooking" + " " + data.get(position).pickup_time);
        System.out.println("pickup_location_newbooking" + " " + data.get(position).time);
        System.out.println("pickup_location_newbooking" + " " + data.get(position).away);
        holder.pickup_location.setText(data.get(position).pickup_location);
        holder.drop_location.setText(data.get(position).drop_location);

        holder.pickupTimeTxt.setText(data.get(position).pickup_time);
        holder.updateTimeTxt.setText(data.get(position).time);
        holder.updateDistanceTxt.setText(data.get(position).away);

        holder.startTripTxt.setOnClickListener(view -> {

        });


    }






    @Override
    public int getItemCount() {
        return data.size();
    }



    public class CustomViewHolder extends RecyclerView.ViewHolder {


        TextView updateTimeTxt, updateDistanceTxt, passengerName, pickupTimeTxt,
                pickup_location, drop_location;
        LinearLayout startTripTxt;

        // private final PickupDropView pickUpDropLayout;

        public CustomViewHolder(View v) {
            super(v);

            updateTimeTxt = v.findViewById(R.id.updateTimeTxt_new);
            updateDistanceTxt = v.findViewById(R.id.updateDisTxt);
            startTripTxt = v.findViewById(R.id.startTripTxt);
            passengerName = v.findViewById(R.id.passengerName);
            pickupTimeTxt = v.findViewById(R.id.pickupTimeTxt);
            pickup_location = v.findViewById(R.id.pickup_location);
            drop_location = v.findViewById(R.id.drop_location);



        }
    }








}
