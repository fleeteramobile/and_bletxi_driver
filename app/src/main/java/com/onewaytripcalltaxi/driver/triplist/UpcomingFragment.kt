package com.onewaytripcalltaxi.driver.triplist

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.onewaytripcalltaxi.driver.MyApplication
import com.onewaytripcalltaxi.driver.R
import com.onewaytripcalltaxi.driver.adapter.NewBookingAdapter
import com.onewaytripcalltaxi.driver.adapter.UpcomingAdapter
import com.onewaytripcalltaxi.driver.data.apiData.ApiRequestData
import com.onewaytripcalltaxi.driver.data.apiData.UpcomingResponse
import com.onewaytripcalltaxi.driver.interfaces.UpcomingAdapterInterface
import com.onewaytripcalltaxi.driver.service.RetrofitCallbackClass
import com.onewaytripcalltaxi.driver.service.ServiceGenerator
import com.onewaytripcalltaxi.driver.utils.SessionSave
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpcomingFragment : Fragment(), UpcomingAdapterInterface {

    lateinit var upcoming_trip_list: RecyclerView
    var mshowDialog: Dialog? = null
    private var upComingData: ArrayList<UpcomingResponse.PastBooking> = ArrayList()
    private lateinit var newBookingAdapter: UpcomingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        upcoming_trip_list = view.findViewById(R.id.upcoming_trip_list)
        upcoming_trip_list.layoutManager = LinearLayoutManager(context)


        newBookingAdapter = UpcomingAdapter(context, upComingData,
            this@UpcomingFragment)
// Pass 'this' as the listener
        upcoming_trip_list.adapter = newBookingAdapter

        loadCancelledListApi()
    }

    private fun loadCancelledListApi() {



        showLoadings(requireActivity())
        val client = MyApplication.getInstance().apiManagerWithEncryptBaseUrl

        val request = ApiRequestData.UpcomingRequest()
        request.setId(SessionSave.getSession("Id", activity))
        request.setDeviceType("2")
        request.setLimit("10")
        request.setStart("0")
        request.setRequestType("1")
        val LoginResponse = client.callData(
            ServiceGenerator.COMPANY_KEY,
            request,
            SessionSave.getSession("Lang", requireActivity())
        )
        LoginResponse.enqueue(
            RetrofitCallbackClass<UpcomingResponse>(
                requireActivity(),
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
                                        requireActivity(),
                                        data?.message ?: "No bookings found", // Use data.message if available
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                // Handle HTTP errors (e.g., 404, 500)
                                // cancelLoadings() // Uncomment if you have this function
                                Toast.makeText(
                                    requireActivity(),
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
            if (mshowDialog != null) if (mshowDialog!!.isShowing && requireActivity() != null) mshowDialog!!.dismiss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    override fun updateUpcomingAdapter(
        data: List<UpcomingResponse.PastBooking>,
        clickedPosition: Int
    ) {
        TODO("Not yet implemented")
    }


}