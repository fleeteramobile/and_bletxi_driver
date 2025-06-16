package com.onewaytripcalltaxi.driver.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.onewaytripcalltaxi.driver.IncentiveDetailActivity;
import com.onewaytripcalltaxi.driver.R;
import com.onewaytripcalltaxi.driver.data.IncentiveData;
import com.onewaytripcalltaxi.driver.utils.CL;
import com.onewaytripcalltaxi.driver.utils.FontHelper;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.SessionSave;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IncentiveAdapter_new  extends RecyclerView.Adapter<IncentiveAdapter_new.CustomViewHolder>{

    private final Context mContext;
    private final List<IncentiveData> data;
    private static Typeface ContenttypeFace;



    public IncentiveAdapter_new(Context mContext, List<IncentiveData> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public IncentiveAdapter_new.CustomViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(R.layout.incentive_list_item_new, parent, false);
        FontHelper.applyFont(mContext, view.findViewById(R.id.full_lay));

        return new IncentiveAdapter_new.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull IncentiveAdapter_new.CustomViewHolder holder, int position) {
        holder.title_Txt.setText(data.get(position).getIncentive_name());
        holder.title_Txt.setTypeface(setcontentTypeface(), Typeface.BOLD);
        holder.inc_amtTxt.setText(SessionSave.getSession("site_currency", mContext) + " " +data.get(position).getIncentive_amount());
        holder.finish_dateTxt.setText(data.get(position).getTime_to());
        if (data.get(position).getIs_feature_incentive().equalsIgnoreCase("1")) {
            holder.activeTxt.setText(NC.getResources().getString(R.string.notactive));
            holder.activeTxt.setTextColor(CL.getColor(R.color.grey_color_new));
        }else if (data.get(position).getIs_feature_incentive().equalsIgnoreCase("2")) {
            holder.activeTxt.setText(NC.getResources().getString(R.string.past));
            holder.activeTxt.setTextColor(CL.getColor(R.color.pickup_red));
        }else{
            holder.activeTxt.setText(NC.getResources().getString(R.string.active));
            holder.activeTxt.setTextColor(CL.getColor(R.color.marker_green));

        }
        holder.showall_Txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i = new Intent(mContext, IncentiveDetailActivity.class);
                Bundle b = new Bundle();
                b.putString("incentive_id",data.get(position).get_id());
                b.putString("incentive_name",data.get(position).getIncentive_name());
                b.putString("incentive_amount",data.get(position).getIncentive_amount());
                b.putString("incentive_enddate",data.get(position).getTime_to());
                b.putString("driver_availability",data.get(position).getDriver_availability_range());
                b.putString("driver_rating",data.get(position).getDriver_rating_range());
                b.putString("driver_accept",data.get(position).getDriver_accept_range());
                b.putString("trips_range",data.get(position).getTrips_range());
                b.putString("future_trip",data.get(position).getIs_feature_incentive());
                i.putExtras(b);
                mContext.startActivity(i);
            }
        });

    }

    public Typeface setcontentTypeface() {

        if (ContenttypeFace == null)
            ContenttypeFace = Typeface.createFromAsset(mContext.getAssets(), FontHelper.FONT_TYPEFACE);
        return ContenttypeFace;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView title_Txt, inc_amtTxt,finish_dateTxt,showall_Txt,activeTxt;
        LinearLayout full_lay;

        public CustomViewHolder(View v) {
            super(v);
            title_Txt = v.findViewById(R.id.title_Txt);
            inc_amtTxt = v.findViewById(R.id.inc_amtTxt);
            finish_dateTxt = v.findViewById(R.id.finish_dateTxt);
            full_lay= v.findViewById(R.id.full_lay);
            showall_Txt=v.findViewById(R.id.showall_Txt);
            activeTxt=v.findViewById(R.id.activeTxt);

        }
    }
}
