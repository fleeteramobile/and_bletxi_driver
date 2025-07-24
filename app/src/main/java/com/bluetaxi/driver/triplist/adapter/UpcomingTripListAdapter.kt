package com.bluetaxi.driver.triplist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bluetaxi.driver.R
import com.bluetaxi.driver.triplist.adapter.interfaces.OutstationStartTrip
import com.bluetaxi.driver.triplist.model.ResponseOutstationTripList
import com.bluetaxi.driver.utils.SessionSave
import com.google.android.material.imageview.ShapeableImageView


class UpcomingTripListAdapter(
    val outstationStartTrip: OutstationStartTrip,
    upComingList: List<ResponseOutstationTripList.Detail.PendingBooking>,
    mContext: Context
) :
    RecyclerView.Adapter<UpcomingTripListAdapter.ViewHolder>() {

    val upComingList = upComingList
    var mContext = mContext


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvdate: TextView
        var iv_profile_pic: ShapeableImageView
        var tv_name: TextView
        var tv_pick: TextView
        var tv_drop: TextView
        var btn_get_in_contact: TextView
        var btn_start: TextView
        var btn_decline: TextView
        var tv_trip_cost: TextView
        var tv_remaining: TextView
        var tv_mobile: TextView
        var tv_date_value: TextView
        var date_lay: ConstraintLayout
        init {


            tvdate = itemView.findViewById(R.id.tvdate) as TextView
            iv_profile_pic = itemView.findViewById(R.id.iv_profile_pic) as ShapeableImageView
            tv_name = itemView.findViewById(R.id.tv_name) as TextView
            tv_pick = itemView.findViewById(R.id.tv_pick) as TextView
            tv_drop = itemView.findViewById(R.id.tv_drop) as TextView
            tv_remaining = itemView.findViewById(R.id.tv_remaining) as TextView
            tv_trip_cost = itemView.findViewById(R.id.tv_trip_cost) as TextView
            btn_get_in_contact = itemView.findViewById(R.id.btn_get_in_contact) as TextView
            btn_start = itemView.findViewById(R.id.btn_start) as TextView
            btn_decline = itemView.findViewById(R.id.btn_decline) as TextView
            tv_mobile = itemView.findViewById(R.id.tv_mobile) as TextView
            tv_date_value = itemView.findViewById(R.id.tv_date_value) as TextView
            date_lay = itemView.findViewById(R.id.date_lay) as ConstraintLayout

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutInflater.from(parent.context).inflate(R.layout.outsation_trips_items, parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount() = upComingList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {



        holder.tvdate.setText("${upComingList[position].pickup_time } ")
        holder.tv_name.setText("${upComingList[position].passenger_name } ")
        holder.tv_pick.setText("${upComingList[position].pickup_location } ")
        holder.tv_drop.setText("${upComingList[position].drop_location } ")
        holder.tv_remaining.setText("${upComingList[position].time } ")
        holder.tv_date_value.setText("${upComingList[position].pickup_time } ")
        holder.tv_mobile.setText("${upComingList[position].passenger_country_code } ${upComingList[position].passenger_phone } ")

        val currencySymbol = SessionSave.getSession("site_currency", mContext) ?: "₹" // Provide a default if null
        holder.tv_trip_cost.setText("${currencySymbol} ${upComingList[position].approx_fare } ")

        holder.btn_start.visibility = View.VISIBLE
        holder.date_lay.visibility = View.VISIBLE
//        Glide.with(mContext)
//            .load(upComingList[position].map_image) // Your drawable resource
//            .into(holder.iv_profile_pic)


        holder.btn_get_in_contact.setOnClickListener {

            outstationStartTrip.contactPassenger(upComingList[position])

        }
        holder.btn_start.setOnClickListener {

            outstationStartTrip.startTrip(upComingList[position])

        }
        holder.btn_decline.setOnClickListener {

            outstationStartTrip.cancelTrip(upComingList[position])

        }



    }


}