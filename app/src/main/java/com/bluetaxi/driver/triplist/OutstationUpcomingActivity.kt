package com.bluetaxi.driver.triplist

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bluetaxi.driver.MyApplication
import com.bluetaxi.driver.R
import com.bluetaxi.driver.adapter.UpcomingAdapter
import com.bluetaxi.driver.data.apiData.ApiRequestData
import com.bluetaxi.driver.data.apiData.UpcomingResponse
import com.bluetaxi.driver.interfaces.UpcomingAdapterInterface
import com.bluetaxi.driver.service.RetrofitCallbackClass
import com.bluetaxi.driver.service.ServiceGenerator
import com.bluetaxi.driver.utils.SessionSave
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OutstationUpcomingActivity : AppCompatActivity(), UpcomingAdapterInterface {
    lateinit var upcoming_trip_list: RecyclerView
    var mshowDialog: Dialog? = null
    private var upComingData: ArrayList<UpcomingResponse.PastBooking> = ArrayList()
    private lateinit var newBookingAdapter: UpcomingAdapter
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      
        setContentView(R.layout.activity_outstation_upcoming)
        upcoming_trip_list = findViewById(R.id.outupcoming_trip_list)
        upcoming_trip_list.layoutManager = LinearLayoutManager(this@OutstationUpcomingActivity)


        newBookingAdapter = UpcomingAdapter(this@OutstationUpcomingActivity, upComingData,
            this@OutstationUpcomingActivity)
// Pass 'this' as the listener
        upcoming_trip_list.adapter = newBookingAdapter

        loadCancelledListApi()
    }

    override fun updateUpcomingAdapter(
        data: List<UpcomingResponse.PastBooking>,
        clickedPosition: Int
    ) {

    }


    private fun loadCancelledListApi() {



        showLoadings(this@OutstationUpcomingActivity)
        val client = MyApplication.getInstance().apiManagerWithEncryptBaseUrl

        val request = ApiRequestData.UpcomingRequest()
        request.setId(SessionSave.getSession("Id", this@OutstationUpcomingActivity))
        request.setDeviceType("2")
        request.setLimit("10")
        request.setStart("0")
        request.setRequestType("1")
        val LoginResponse = client.callDataOutstationUpcoming(
            ServiceGenerator.COMPANY_KEY,
            request,
            SessionSave.getSession("Lang",this@OutstationUpcomingActivity)
        )
        LoginResponse.enqueue(
            RetrofitCallbackClass<UpcomingResponse>(
               this@OutstationUpcomingActivity,
                object : Callback<UpcomingResponse?> {
                    override fun onResponse(
                        call: Call<UpcomingResponse?>,
                        response: Response<UpcomingResponse?>
                    ) {
                        if (response.isSuccessful) {
                            cancelLoadings()




                            if (response.isSuccessful) {
                                val data = response.body()

                                if (data != null && data.status == 1) {
                                    upComingData.clear() // Clear the old data
                                    // Add all new bookings to the mutable list
                                    data.detail.pending_booking?.let {
                                        upComingData.addAll(it)
                                    }
                                    println("pickup_location_newbooking_size" + " " + upComingData.size)

                                    // Notify the adapter that the data set has changed
                                    newBookingAdapter.notifyDataSetChanged()

                                    println("pickup_location_newbooking" + " " + "issettttttttttttttttttt")

                                } else {
                                    upComingData.clear() // Clear data if status is not 1 or data is null
                                    newBookingAdapter.notifyDataSetChanged() // Update UI to show empty list

                                    Toast.makeText(
                                      this@OutstationUpcomingActivity,
                                        data?.message ?: "No bookings found", // Use data.message if available
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                // Handle HTTP errors (e.g., 404, 500)
                                // cancelLoadings() // Uncomment if you have this function
                                Toast.makeText(
                                   this@OutstationUpcomingActivity,
                                    "API Error: ${response.code()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }



                        } else {
                            cancelLoadings()
                        }
                    }

                    override fun onFailure(call: Call<UpcomingResponse?>, t: Throwable) {
                        cancelLoadings()
                    }
                })
        )
    }


    fun showLoadings(context: Context) {
        try {
            if (mshowDialog != null) if (mshowDialog!!.isShowing) mshowDialog!!.dismiss()
            val view = View.inflate(context, R.layout.progress_bar, null)
            mshowDialog = Dialog(context, R.style.dialogwinddow)
            mshowDialog!!.setContentView(view)
            mshowDialog!!.setCancelable(false)
            mshowDialog!!.show()
            val iv = mshowDialog!!.findViewById<ImageView>(R.id.giff)
            val imageViewTarget = DrawableImageViewTarget(iv)
            Glide.with(context)
                .load(R.raw.loading_anim)
                .into<DrawableImageViewTarget>(imageViewTarget)
        } catch (e: Exception) {
            // TODO: handle exception
        }
    }




    private fun cancelLoadings() {

        try {
            if (mshowDialog != null) if (mshowDialog!!.isShowing && this@OutstationUpcomingActivity != null) mshowDialog!!.dismiss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }
}