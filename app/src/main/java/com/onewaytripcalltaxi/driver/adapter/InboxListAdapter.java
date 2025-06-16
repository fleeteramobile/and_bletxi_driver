package com.onewaytripcalltaxi.driver.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.onewaytripcalltaxi.driver.MessageDetailActivity;
import com.onewaytripcalltaxi.driver.R;
import com.onewaytripcalltaxi.driver.data.InboxData;
import com.onewaytripcalltaxi.driver.utils.FontHelper;

import java.util.List;


public class InboxListAdapter extends RecyclerView.Adapter<InboxListAdapter.CustomViewHolder> {

    private final List<InboxData> data;
    private final Context mContext;

    public InboxListAdapter(Context c, List<InboxData> data) {
        this.mContext = c;
        this.data = data;
    }
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.inbox_list_item, parent, false);
        FontHelper.applyFont(mContext, view.findViewById(R.id.full_lay));

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxListAdapter.CustomViewHolder holder, int position) {
        holder.message_Txt.setText(data.get(position).getMessage());
        holder.time_Txt.setText(data.get(position).getTime());
        holder.full_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i = new Intent(mContext, MessageDetailActivity.class);
                Bundle b = new Bundle();
                b.putString("message",data.get(position).getMessage());
                b.putString("time",data.get(position).getTime());

                i.putExtras(b);
                mContext.startActivity(i);
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView message_Txt, time_Txt;
        LinearLayout full_lay;

        public CustomViewHolder(View v) {
            super(v);
            message_Txt = v.findViewById(R.id.message_Txt);
            time_Txt = v.findViewById(R.id.time_Txt);
            full_lay= v.findViewById(R.id.full_lay);

        }
    }
}
