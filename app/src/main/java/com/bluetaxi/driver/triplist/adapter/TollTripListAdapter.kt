package com.bluetaxi.driver.triplist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bluetaxi.driver.R
import com.bluetaxi.driver.triplist.adapter.interfaces.TollAmountRequest
import com.bluetaxi.driver.triplist.model.ResponseTollTripList
import com.bluetaxi.driver.utils.SessionSave


class TollTripListAdapter(
    val tollAmountRequest: TollAmountRequest,
    upComingList: List<ResponseTollTripList.Detail.PastBooking>,
    mContext: Context
) :
    RecyclerView.Adapter<TollTripListAdapter.ViewHolder>() {

    val upComingList = upComingList
    var mContext = mContext


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvPickup: TextView
        var tvDrop: TextView
        var tvPrice: TextView
        var tvStatus: TextView
        var lay_toll: CardView
        init {


            tvDrop = itemView.findViewById(R.id.tvDrop) as TextView
            tvPickup = itemView.findViewById(R.id.tvPickup) as TextView
            tvPrice = itemView.findViewById(R.id.tvPrice) as TextView
            tvStatus = itemView.findViewById(R.id.tvStatus) as TextView
            lay_toll = itemView.findViewById(R.id.lay_toll) as CardView

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutInflater.from(parent.context).inflate(R.layout.toll_request_items, parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount() = upComingList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {



        holder.tvPickup.setText("${upComingList[position].pickup_location } ")
        holder.tvDrop.setText("${upComingList[position].drop_location } ")
        val currencySymbol = SessionSave.getSession("site_currency", mContext) ?: "₹" // Provide a default if null
        holder.tvPrice.setText("${currencySymbol} ${upComingList[position].total_toll_fare } ")

        if (upComingList[position].toll_fee_requested == 0)
        {
            holder.tvStatus.setText("Request ")

        }
       else  if (upComingList[position].toll_fee_requested == 1)
        {
            holder.tvStatus.setText("Waiting for approval ")

        }
        else{
            holder.tvStatus.setText("Waiting for approval ")

        }


        holder.lay_toll.setOnClickListener {
            if (upComingList[position].toll_fee_requested == 0) {
                tollAmountRequest.tollAmountRequest(upComingList[position])
            }
            else{
                Toast.makeText(mContext,"Already Requested",Toast.LENGTH_LONG).show()
            }
        }



    }


}