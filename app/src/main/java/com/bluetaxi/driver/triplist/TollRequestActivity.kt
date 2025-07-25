package com.bluetaxi.driver.triplist

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bluetaxi.driver.MyApplication

import com.bluetaxi.driver.R
import com.bluetaxi.driver.data.apiData.ApiRequestData
import com.bluetaxi.driver.interfaces.APIResult
import com.bluetaxi.driver.interfaces.ClickInterface
import com.bluetaxi.driver.service.APIService_Retrofit_JSON
import com.bluetaxi.driver.service.RetrofitCallbackClass
import com.bluetaxi.driver.service.ServiceGenerator
import com.bluetaxi.driver.triplist.adapter.interfaces.TollAmountRequest
import com.bluetaxi.driver.triplist.adapter.TollTripListAdapter
import com.bluetaxi.driver.triplist.model.ResponseTollTripList
import com.bluetaxi.driver.utils.NC
import com.bluetaxi.driver.utils.NetworkStatus
import com.bluetaxi.driver.utils.SessionSave
import com.bluetaxi.driver.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TollRequestActivity : AppCompatActivity(), TollAmountRequest, ClickInterface {
    lateinit var completed_trip_list: RecyclerView
    var mshowDialog: Dialog? = null
    private var pastData: ArrayList<ResponseTollTripList.Detail.PastBooking> = ArrayList()
    private lateinit var past_booking_adapter: TollTripListAdapter
    lateinit var no_data_image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_toll_request)
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
        completed_trip_list = findViewById(R.id.toll_trip_list)
        completed_trip_list.layoutManager = LinearLayoutManager(this@TollRequestActivity)

        past_booking_adapter = TollTripListAdapter(
            this@TollRequestActivity,
            pastData,
            this@TollRequestActivity
        )
        completed_trip_list.adapter = past_booking_adapter

        loadCancelledListApi()

    }

    private fun loadCancelledListApi() {



        showLoadings(this@TollRequestActivity)
        val client = MyApplication.getInstance().apiManagerWithEncryptBaseUrl

        val request = ApiRequestData.UpcomingRequest()
        request.setId(SessionSave.getSession("Id", this@TollRequestActivity))
        request.setDeviceType("2")
        request.setLimit("50")
        request.setStart("0")
        request.setRequestType("2")
        val LoginResponse = client.tollTripList(
            ServiceGenerator.COMPANY_KEY,
            request,
            SessionSave.getSession("Lang", this@TollRequestActivity)
        )
        LoginResponse.enqueue(
            RetrofitCallbackClass<ResponseTollTripList>(
                this@TollRequestActivity,
                object : Callback<ResponseTollTripList?> {
                    override fun onResponse(
                        call: Call<ResponseTollTripList?>,
                        response: Response<ResponseTollTripList?>
                    ) {
                        if (response.isSuccessful) {
                            cancelLoadings()




                            if (response.isSuccessful) {
                                val data = response.body()

                                if (data != null && data.status == 1) {

                                    pastData.clear() // Clear the old data
                                    // Add all new bookings to the mutable list


                                    if (data.detail.past_booking?.size  !=0 )
                                    {
                                        data.detail.past_booking?.let {
                                            pastData.addAll(it)
                                        }
                                        println("pickup_location_newbooking_size" + " " + pastData.size)

                                        // Notify the adapter that the data set has changed
                                        past_booking_adapter.notifyDataSetChanged()
                                        completed_trip_list.visibility = View.VISIBLE
                                        no_data_image .visibility = View.GONE
                                    }
                                    else
                                    {
                                        completed_trip_list.visibility = View.GONE
                                        no_data_image .visibility = View.VISIBLE
                                    }


                                    println("pickup_location_newbooking" + " " + "issettttttttttttttttttt")

                                } else {
                                    pastData.clear() // Clear data if status is not 1 or data is null
                                    past_booking_adapter.notifyDataSetChanged() // Update UI to show empty list
                                    completed_trip_list.visibility = View.GONE
                                    no_data_image .visibility = View.VISIBLE
                                    Toast.makeText(
                                        this@TollRequestActivity,
                                        data?.message ?: "No bookings found", // Use data.message if available
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                // Handle HTTP errors (e.g., 404, 500)
                                // cancelLoadings() // Uncomment if you have this function
                                Toast.makeText(
                                    this@TollRequestActivity,
                                    "API Error: ${response.code()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                completed_trip_list.visibility = View.GONE
                                no_data_image .visibility = View.VISIBLE
                            }



                        } else {
                            completed_trip_list.visibility = View.GONE
                            no_data_image .visibility = View.VISIBLE
                            cancelLoadings()
                        }
                    }

                    override fun onFailure(call: Call<ResponseTollTripList?>, t: Throwable) {
                        completed_trip_list.visibility = View.GONE
                        no_data_image .visibility = View.VISIBLE
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
            if (mshowDialog != null) if (mshowDialog!!.isShowing &&this@TollRequestActivity != null) mshowDialog!!.dismiss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    override fun tollAmountRequest(_category: ResponseTollTripList.Detail.PastBooking) {
        CoroutineScope(Dispatchers.Main).launch {
            // Code before delay
            delay(1000) // Delay for 2 seconds (2000 milliseconds)
            val js = JSONObject()
            try {
                js.put("driver_id", SessionSave.getSession("Id", this@TollRequestActivity))
                js.put("toll_amount", _category.total_toll_fare.toString())
                js.put("trip_id", _category.passengers_log_id)
                val Url = "type=toll_withdraw_request"
                Tripdetails(Url, js)
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            // Code after delay
            Log.d("Delay", "2 seconds later")
        }
    }

    inner class Tripdetails internal constructor(url: String?, data: JSONObject?) :
        APIResult {
        init {
            try {
                println("homeactivity"+" "+"6")

                if (NetworkStatus.isOnline(this@TollRequestActivity)) {
                    APIService_Retrofit_JSON(
                        this@TollRequestActivity,
                        this,
                        data,
                        false
                    ).execute(url)
                } else {
                    Utils.alert_view(
                        this@TollRequestActivity,
                        NC.getString(R.string.message),
                        NC.getString(R.string.check_net_connection),
                        NC.getString(R.string.ok),
                        "",
                        true,
                        this@TollRequestActivity,
                        "4"
                    )
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String?) {
            if (isSuccess && result != null) {
                try {

                    println("homeactivity"+" "+"4")

                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {

                        Utils.alert_view(
                            this@TollRequestActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),                            NC.getString(R.string.ok),
                            "",
                            true,
                            this@TollRequestActivity,
                            "4"
                        )
                    loadCancelledListApi()

                    } else {
                        // CToast.ShowToast(MeAct.this, NC.getString(R.string.server_error));
                    }

                } catch (e: JSONException) {
                    throw java.lang.RuntimeException(e)
                }
            } else {
                //CToast.ShowToast(MyStatus.this, NC.getString(R.string.server_error));
            }
        }
    }

    override fun positiveButtonClick(dialog: DialogInterface?, id: Int, s: String?) {
        dialog!!.dismiss()
    }

    override fun negativeButtonClick(dialog: DialogInterface?, id: Int, s: String?) {
        dialog!!.dismiss()
    }
}