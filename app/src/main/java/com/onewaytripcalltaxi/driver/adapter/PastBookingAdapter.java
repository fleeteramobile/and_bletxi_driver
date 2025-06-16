package com.onewaytripcalltaxi.driver.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onewaytripcalltaxi.driver.R;
import com.onewaytripcalltaxi.driver.data.apiData.UpcomingResponse;
import com.onewaytripcalltaxi.driver.triphistory.TripDetailsActivity;
import com.onewaytripcalltaxi.driver.utils.CL;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Systems;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by developer on 1/11/16.
 */

/**
 * This adapter class is used to show driver past booking history
 */
public class PastBookingAdapter extends RecyclerView.Adapter<PastBookingAdapter.CustomViewHolder> {

    private final List<UpcomingResponse.PastBooking> data;
    private final Context mContext;


    public PastBookingAdapter(Context c, List<UpcomingResponse.PastBooking> data) {
        this.mContext = c;
        this.data = data;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = null;
        view = inflater.inflate(R.layout.past_booking_item, parent, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        if (!data.get(position).profile_image.trim().equals("")) {
            Glide.with(mContext).load(data.get(position).map_image).into(holder.map_image);
            Systems.out.println("imageLink_" + data.get(position).profile_image);
            Picasso.get().load(data.get(position).profile_image).into(holder.driver_image);
        }
        Colorchange.ChangeColor(holder.book_lay, mContext);
        holder.trip_time.setText(data.get(position).pickup_time_text);
        holder.trip_driver_name.setText(data.get(position).model_name);
        holder.trip_time.setText(data.get(position).pickup_time_text);




        holder.pickup_time.setText(data.get(position).started_time);
        holder.drop_time.setText(data.get(position).drop_time);
        holder.pickup_location.setText(data.get(position).pickup_location);
        holder.drop_location.setText(data.get(position).drop_location);


        if (data.get(position).travel_status.trim().equals("1")) {

            holder.trip_fare.setText(SessionSave.getSession("site_currency", mContext) + data.get(position).amt);

            holder.trip_fare.setTextColor(mContext.getResources().getColor(R.color.sucess_txt));


        } else {
            holder.trip_fare.setText(NC.getString(R.string.cancelled));
            holder.trip_fare.setTextColor(mContext.getResources().getColor(R.color.sos_color));

        }




        if (data.get(position).payment_type.trim().equalsIgnoreCase("Cash")) {
            holder.trip_payment_type.setText(NC.getString(R.string.cash));
            holder.trip_payment_type.setTextColor(CL.getResources().getColor(R.color.pastbookingcashtext));

        } else if (data.get(position).payment_type.trim().equalsIgnoreCase("Wallet")) {
            holder.trip_payment_type.setText(NC.getString(R.string.wallet));
            holder.trip_payment_type.setTextColor(CL.getResources().getColor(R.color.pastbookingcashtext));
        } else {
            holder.trip_payment_type.setText(NC.getString(R.string.card));
            holder.trip_payment_type.setTextColor(CL.getResources().getColor(R.color.pastbookingcard));
        }
        if (data.get(position).corporate_booking != null) {
            if (data.get(position).corporate_booking.equalsIgnoreCase("1")) {
                holder.trip_payment_type.setText(NC.getString(R.string.corporate));
                holder.trip_payment_type.setTextColor(CL.getResources().getColor(R.color.pastbookingcashtext));
            }
        }
        holder.trip_payment_amount.setText(SessionSave.getSession("site_currency", mContext) + " " + data.get(position).amt);


        if (data.get(position).travel_status.trim().equals("1")) {
            holder.trip_status.setText(NC.getString(R.string.trip_completed));
            holder.pasbooking_mainlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SessionSave.saveSession("bookingid",data.get(position).passengers_log_id,mContext);
                    Intent intent = new Intent(mContext, TripDetailsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    mContext.startActivity(intent);
//
//                    Fragment ff = new TripDetailNewFrag();
//                    Bundle b = new Bundle();
//                    b.putString("trip_id", data.get(position).passengers_log_id);
//                    b.putString("title", data.get(position).pickup_time);
//                    ff.setArguments(b);
//                    ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction().addToBackStack(null).add(R.id.mainFrag, ff).commit();
                }
            });
        } else {
            holder.trip_status.setText(NC.getString(R.string.cancelled));
            holder.map_image.setOnClickListener(null);
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    /**
     * View holder class member this contains in every row in list.
     */
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView map_image, driver_image;
        TextView trip_time, trip_driver_name;
        TextView trip_payment_type, trip_payment_amount, trip_status,pickup_time,drop_time,pickup_location,drop_location,trip_fare;
        LinearLayout book_lay;
FrameLayout pasbooking_mainlay;
        public CustomViewHolder(View v) {
            super(v);
            map_image = v.findViewById(R.id.map_image);
            driver_image = v.findViewById(R.id.driver_image);
            trip_time = v.findViewById(R.id.trip_time);
            trip_driver_name = v.findViewById(R.id.trip_driver_name);
            book_lay = v.findViewById(R.id.book_lay);
            trip_payment_type = v.findViewById(R.id.trip_payment_type);
            trip_payment_amount = v.findViewById(R.id.trip_payment_amount);
            trip_status = v.findViewById(R.id.trip_status);
            trip_fare = v.findViewById(R.id.trip_fare);

            pickup_time = v.findViewById(R.id.pickup_time);
            drop_time = v.findViewById(R.id.drop_time);
            pickup_location = v.findViewById(R.id.pickup_location);
            drop_location = v.findViewById(R.id.drop_location);
            pasbooking_mainlay = v.findViewById(R.id.pasbooking_mainlay);


            //super(view);
        }
    }
}
