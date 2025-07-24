package com.bluetaxi.driver.triplist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bluetaxi.driver.R
import com.bluetaxi.driver.triplist.adapter.interfaces.CompletedTrip
import com.bluetaxi.driver.triplist.adapter.interfaces.OngoingTrip
import com.bluetaxi.driver.triplist.model.ResponseOngoingBooking
import com.bluetaxi.driver.triplist.model.ResponsePastBooking
import com.bluetaxi.driver.utils.SessionSave
import com.google.android.material.imageview.ShapeableImageView


class CompletedTripListAdapter(
    val completedTrip: CompletedTrip,
    upComingList: List<ResponsePastBooking.Detail.PastBooking>,
    mContext: Context
) :
    RecyclerView.Adapter<CompletedTripListAdapter.ViewHolder>() {

    val upComingList = upComingList
    var mContext = mContext


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textViewUserName: TextView
        var textViewApplePay: TextView
        var textViewPrice: TextView
        var textViewDistance: TextView
        var textViewPickupAddress: TextView
        var textViewDropOffAddress: TextView
        var completed_trip_lay: CardView


        init {


            textViewUserName = itemView.findViewById(R.id.textViewUserName) as TextView
            textViewApplePay = itemView.findViewById(R.id.textViewApplePay) as TextView
            textViewPrice = itemView.findViewById(R.id.textViewPrice) as TextView
            textViewPickupAddress = itemView.findViewById(R.id.textViewPickupAddress) as TextView
            textViewDistance = itemView.findViewById(R.id.textViewDistance) as TextView
            textViewDropOffAddress = itemView.findViewById(R.id.textViewDropOffAddress) as TextView
            completed_trip_lay = itemView.findViewById(R.id.completed_trip_lay) as CardView


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutInflater.from(parent.context).inflate(R.layout.completed_trips_items, parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount() = upComingList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var textViewUserName: TextView
        var textViewApplePay: TextView
        var textViewPrice: TextView
        var textViewDistance: TextView
        var textViewPickupAddress: TextView
        var textViewDropOffAddress: TextView
        var completed_trip_lay: CardView


        holder.textViewUserName.setText("${upComingList[position].passenger_name } ")
        holder.textViewApplePay.setText("${upComingList[position].payment_type } ")
        val currencySymbol = SessionSave.getSession("site_currency", mContext) ?: "₹" // Provide a default if null

        holder.textViewPrice.setText("${currencySymbol} ${upComingList[position].amt } ")
        holder.textViewDistance.setText("${upComingList[position].travelled_distance }  KM ")
        holder.textViewPickupAddress.setText("${upComingList[position].pickup_location } ")
        holder.textViewDropOffAddress.setText(" ${upComingList[position].drop_location } ")


        holder.completed_trip_lay.setOnClickListener {


                    completedTrip.showTripDetails(upComingList[position])


            }

        }







}