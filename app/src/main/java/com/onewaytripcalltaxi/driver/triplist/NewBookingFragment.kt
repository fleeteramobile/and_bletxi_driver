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
import com.onewaytripcalltaxi.driver.data.apiData.ApiRequestData
import com.onewaytripcalltaxi.driver.data.apiData.UpcomingResponse
import com.onewaytripcalltaxi.driver.interfaces.NewBookingAdapterInterface
import com.onewaytripcalltaxi.driver.service.RetrofitCallbackClass
import com.onewaytripcalltaxi.driver.service.ServiceGenerator
import com.onewaytripcalltaxi.driver.utils.SessionSave
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewBookingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewBookingFragment : Fragment(),NewBookingAdapterInterface {

    lateinit var new_trip_list: RecyclerView
    var mshowDialog: Dialog? = null
    private var newBookings: ArrayList<UpcomingResponse.ShowBooking> = ArrayList()
    private lateinit var newBookingAdapter: NewBookingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        new_trip_list = view.findViewById(R.id.new_trip_list)
        new_trip_list.layoutManager = LinearLayoutManager(context)


        newBookingAdapter = NewBookingAdapter(context, newBookings,
            this@NewBookingFragment)
// Pass 'this' as the listener
        new_trip_list.adapter = newBookingAdapter

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
        request.setRequestType("3")
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
                                    newBookings.clear() // Clear the old data
                                    // Add all new bookings to the mutable list
                                    data.detail.show_booking?.let {
                                        newBookings.addAll(it)
                                    }
                                    println("pickup_location_newbooking_size" + " " + newBookings.size)

                                    // Notify the adapter that the data set has changed
                                    newBookingAdapter.notifyDataSetChanged()

                                    println("pickup_location_newbooking" + " " + "issettttttttttttttttttt")

                                } else {
                                    newBookings.clear() // Clear data if status is not 1 or data is null
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

    override fun newbookingUpcomingAdapter(
        data: List<UpcomingResponse.ShowBooking>,
        clickedPosition: Int
    ) {

    }


    private fun cancelLoadings() {

        try {
            if (mshowDialog != null) if (mshowDialog!!.isShowing && requireActivity() != null) mshowDialog!!.dismiss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }


}