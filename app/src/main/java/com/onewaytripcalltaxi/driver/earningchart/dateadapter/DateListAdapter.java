package com.onewaytripcalltaxi.driver.earningchart.dateadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.onewaytripcalltaxi.driver.R;

import java.util.List;

public class DateListAdapter extends RecyclerView.Adapter<DateListAdapter.MyView> {
    private List<String> list;

    public DateListAdapter(List<String> horizontalList)
    {
        this.list = horizontalList;
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.dateitems,
                        parent,
                        false);

        // return itemView
        return new MyView(itemView);     }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        holder.textView.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyView extends RecyclerView.ViewHolder {


        TextView textView;


        public MyView(View view)
        {
            super(view);

            // initialise TextView with id
            textView = (TextView)view
                    .findViewById(R.id.date_item_txt);
        }
    }




}
