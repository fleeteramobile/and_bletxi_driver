package com.onewaytripcalltaxi.driver.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.onewaytripcalltaxi.driver.R;
import com.onewaytripcalltaxi.driver.data.apiData.ListClass;
import com.onewaytripcalltaxi.driver.utils.SessionSave;

import java.util.List;

public class SettlementHistoryAdapter extends RecyclerView.Adapter<SettlementHistoryAdapter.CustomViewHolder> {

    private final Context mContext;

    private final List<ListClass> data;

    public SettlementHistoryAdapter(Context context, List<ListClass> pastData) {

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



holder.txt_trip_id.setText(String.valueOf(data.get(position)._id));
holder.txt_date.setText(String.valueOf(data.get(position).settlement_request_date));
if(data.get(position).settlement_type.equals("A-D"))
{
    holder.txt_paymentBy.setText("Payment By: Admin to Driver");

}
else {
    holder.txt_paymentBy.setText("Payment By: Driver to Admin");

}
holder.txt_amount.setText(SessionSave.getSession("site_currency", mContext) + data.get(position).settlement_process_amount);

        if(data.get(position).settlement_approval.equals("1"))
        {
            holder.txt_status.setText("Approved");
            holder.txt_status.setTextColor(Color.parseColor("#018947"));

        } else  if(data.get(position).settlement_approval.equals("0"))
        {
            holder.txt_status.setText("Rejected");
            holder.txt_status.setTextColor(Color.parseColor("#C30000"));

        } else {
            holder.txt_status.setText("-");
            holder.txt_status.setTextColor(Color.parseColor("#C30000"));
        }
//        holder.binding.setList(data.get(position));
//
     // holder.txt_status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_settled));

    }

    @Override

    public int getItemCount() {

        return data.size();

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView txt_trip_id, txt_date, txt_paymentBy, txt_amount,txt_status;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_trip_id = itemView.findViewById(R.id.txt_trip_id);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_paymentBy = itemView.findViewById(R.id.txt_paymentBy);
            txt_amount = itemView.findViewById(R.id.txt_amount);
            txt_status = itemView.findViewById(R.id.status_txt);
        }



    }

}
