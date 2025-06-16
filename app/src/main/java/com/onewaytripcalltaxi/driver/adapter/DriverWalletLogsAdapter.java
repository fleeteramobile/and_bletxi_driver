package com.onewaytripcalltaxi.driver.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.onewaytripcalltaxi.driver.R;

import java.util.ArrayList;

public class DriverWalletLogsAdapter extends RecyclerView.Adapter<DriverWalletLogsAdapter.ViewHolder> {
    ArrayList<String> genre = new ArrayList<String>();
    ArrayList<String> year = new ArrayList<String>();

    public DriverWalletLogsAdapter(ArrayList<String> genre, ArrayList<String> year) {
        this.genre = genre;
        this.year = year;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.driver_wallet_logs_list_items, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if(year.get(i).isEmpty()){
            viewHolder.year.setVisibility(View.GONE) ;
        }else{
            viewHolder.year.setText(year.get(i));
        }
        viewHolder.title.setText(genre.get(i));
    }

    @Override
    public int getItemCount() {
        return genre.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre,date;

        private ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            genre = (TextView) view.findViewById(R.id.genre);
            year = (TextView) view.findViewById(R.id.year);
            date = (TextView) view.findViewById(R.id.date);
        }
    }
}
