package com.onewaytripcalltaxi.driver.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onewaytripcalltaxi.driver.R;
import com.onewaytripcalltaxi.driver.data.apiData.ListClass;

import java.util.List;

public class PendingHistoryAdapter extends RecyclerView.Adapter<PendingHistoryAdapter.CustomViewHolder> {

    private final Context mContext;

    private final List<ListClass> data;

    public PendingHistoryAdapter(Context context, List<ListClass> pastData) {

        this.mContext = context;

        this.data = pastData;

    }

    LayoutInflater layoutInflater;

    @Override

    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = null;
        view = inflater.inflate(R.layout.settlement_history_list, parent, false);
        return new CustomViewHolder(view);

    }

    @Override

    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

       // holder.binding.setList(data.get(position));

        holder.txt_trip_id.setVisibility(View.VISIBLE);
        holder.txt_trip_id.setText(String.valueOf(data.get(position)._id));
        holder.txt_date.setText(String.valueOf(data.get(position).settlement_request_date));
        holder.txt_amount.setText(String.valueOf(data.get(position).settlement_process_amount));
        holder.txt_paymentBy.setVisibility(View.GONE);

        if (data.get(position).settlement_status.equals("1")) {

            holder.txt_status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_approved));

        } else {

            holder.txt_status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pending));

        }

    }

    @Override

    public int getItemCount() {

        return data.size();

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView txt_trip_id, txt_date, txt_paymentBy, txt_amount;

        ImageView txt_status;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_trip_id = itemView.findViewById(R.id.txt_trip_id);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_paymentBy = itemView.findViewById(R.id.txt_paymentBy);
            txt_amount = itemView.findViewById(R.id.txt_amount);
            txt_status = itemView.findViewById(R.id.txt_status);
        }



    }

}