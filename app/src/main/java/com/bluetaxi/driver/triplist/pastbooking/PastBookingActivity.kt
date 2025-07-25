package com.bluetaxi.driver.triplist.pastbooking

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bluetaxi.driver.MyApplication
import com.bluetaxi.driver.R
import com.bluetaxi.driver.data.apiData.ApiRequestData
import com.bluetaxi.driver.interfaces.ClickInterface
import com.bluetaxi.driver.service.RetrofitCallbackClass
import com.bluetaxi.driver.service.ServiceGenerator
import com.bluetaxi.driver.triplist.adapter.CompletedTripListAdapter
import com.bluetaxi.driver.triplist.adapter.OngoingTripListAdapter
import com.bluetaxi.driver.triplist.adapter.interfaces.CompletedTrip
import com.bluetaxi.driver.triplist.model.ResponseOngoingBooking
import com.bluetaxi.driver.triplist.model.ResponsePastBooking
import com.bluetaxi.driver.utils.SessionSave
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PastBookingActivity : AppCompatActivity(), CompletedTrip, ClickInterface {
    lateinit var upcoming_trip_list: RecyclerView
    lateinit var no_data_image: ImageView
    private var upComingData: ArrayList<ResponsePastBooking.Detail.PastBooking> = ArrayList()
    private lateinit var completedTripListAdapter: CompletedTripListAdapter
    var mshowDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_booking)
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
        upcoming_trip_list = findViewById(R.id.completed_trip_list_new)
        no_data_image = findViewById(R.id.no_data_image)
        upcoming_trip_list.layoutManager = LinearLayoutManager(this@PastBookingActivity)


        completedTripListAdapter = CompletedTripListAdapter(this@PastBookingActivity, upComingData,
            this@PastBookingActivity)

        upcoming_trip_list.adapter = completedTripListAdapter

        loadCancelledListApi()

    }

    private fun loadCancelledListApi() {



        showLoadings(this@PastBookingActivity)
        val client = MyApplication.getInstance().apiManagerWithEncryptBaseUrl

        val request = ApiRequestData.UpcomingRequest()
        request.setId(SessionSave.getSession("Id", this@PastBookingActivity))
        request.setDeviceType("2")
        request.setLimit("10")
        request.setStart("0")
        request.setRequestType("2")
        val LoginResponse = client.completedTrips(
            ServiceGenerator.COMPANY_KEY,
            request,
            SessionSave.getSession("Lang",this@PastBookingActivity)
        )
        LoginResponse.enqueue(
            RetrofitCallbackClass<ResponsePastBooking>(
                this@PastBookingActivity,
                object : Callback<ResponsePastBooking?> {
                    override fun onResponse(
                        call: Call<ResponsePastBooking?>,
                        response: Response<ResponsePastBooking?>
                    ) {
                        if (response.isSuccessful) {
                            cancelLoadings()




                            if (response.isSuccessful) {
                                val data = response.body()

                                if (data != null && data.status == 1) {
                                    upComingData.clear() // Clear the old data
                                    // Add all new bookings to the mutable list


                                    if (data.detail.past_booking?.size  !=0 )
                                    {
                                        data.detail.past_booking?.let {
                                            upComingData.addAll(it)
                                        }
                                        println("pickup_location_newbooking_size" + " " + upComingData.size)

                                        // Notify the adapter that the data set has changed
                                        completedTripListAdapter.notifyDataSetChanged()

                                        println("pickup_location_newbooking" + " " + "issettttttttttttttttttt")
                                        upcoming_trip_list.visibility = View.VISIBLE
                                        no_data_image .visibility = View.GONE
                                    }
                                    else{
                                        upcoming_trip_list.visibility = View.GONE
                                        no_data_image .visibility = View.VISIBLE
                                    }

                                } else {
                                    upComingData.clear() // Clear data if status is not 1 or data is null
                                    completedTripListAdapter.notifyDataSetChanged() // Update UI to show empty list
                                    upcoming_trip_list.visibility = View.GONE
                                    no_data_image .visibility = View.VISIBLE
                                    Toast.makeText(
                                        this@PastBookingActivity,
                                        data?.message ?: "No bookings found", // Use data.message if available
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                // Handle HTTP errors (e.g., 404, 500)
                                // cancelLoadings() // Uncomment if you have this function
                                Toast.makeText(
                                    this@PastBookingActivity,
                                    "API Error: ${response.code()}",
                                    Toast.LENGTH_SHORT
                                ).show()

                                upcoming_trip_list.visibility = View.GONE
                                no_data_image .visibility = View.VISIBLE
                            }



                        } else {
                            cancelLoadings()
                        }
                    }

                    override fun onFailure(call: Call<ResponsePastBooking?>, t: Throwable) {
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
            if (mshowDialog != null) if (mshowDialog!!.isShowing && this@PastBookingActivity != null) mshowDialog!!.dismiss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }
    override fun showTripDetails(_category: ResponsePastBooking.Detail.PastBooking) {
       
    }

    override fun positiveButtonClick(dialog: DialogInterface?, id: Int, s: String?) {
        dialog!!.dismiss()
    }

    override fun negativeButtonClick(dialog: DialogInterface?, id: Int, s: String?) {
        dialog!!.dismiss()
    }
}