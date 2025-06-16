package com.onewaytripcalltaxi.driver.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onewaytripcalltaxi.driver.R;
import com.onewaytripcalltaxi.driver.data.WalletHistoryData;
import com.onewaytripcalltaxi.driver.utils.SessionSave;

import java.util.List;

/**
 * Created by developer on 1/11/16.
 * use to populate the past booking recyclerview
 */
public class WalletHistoryListAdapter extends RecyclerView.Adapter<WalletHistoryListAdapter.CustomViewHolder> {

    private final List<WalletHistoryData> data;
    private final Context mContext;

    public WalletHistoryListAdapter(Context c, List<WalletHistoryData> data) {
        this.mContext = c;
        this.data = data;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.wallet_history_item, parent, false);
        return new CustomViewHolder(view);
    }

    /**
     * binds view to recyclerview
     *
     * @param holder
     * @param position
     */

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {
        holder.name_txt.setText(data.get(position).getComments() + " " + ". Total available balace is" + " " + SessionSave.getSession("site_currency", mContext) + " " + data.get(position).getUpdated_balance());
        holder.pdate.setText(data.get(position).getCreated_date());
        holder.mobile_number.setText(data.get(position).getSign() + " " + SessionSave.getSession("site_currency", mContext) + data.get(position).getAmt());
        textColorChange(holder.mobile_number, data.get(position).getSign());
    }

    public void textColorChange(TextView mTextView, String sign) {

        if (sign.equals("-")) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                mTextView.setTextColor(ContextCompat.getColor(mContext, R.color.pickup_red));

            } else {
                mTextView.setTextColor(mContext.getResources().getColor(R.color.pickup_red));

            }
        } else {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                mTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorFontGreen));

            } else {
                mTextView.setTextColor(mContext.getResources().getColor(R.color.colorFontGreen));

            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView name_txt, mobile_number;
        TextView pdate;

        public CustomViewHolder(View v) {
            super(v);
            name_txt = v.findViewById(R.id.name_txt);
            mobile_number = v.findViewById(R.id.mobile_number);
            pdate = v.findViewById(R.id.pdate);

        }
    }
}
