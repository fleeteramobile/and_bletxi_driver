package com.bluetaxi.driver.triplist.upcoming

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bluetaxi.driver.MainActivity
import com.bluetaxi.driver.MyApplication
import com.bluetaxi.driver.OngoingAct
import com.bluetaxi.driver.R
import com.bluetaxi.driver.data.apiData.ApiRequestData
import com.bluetaxi.driver.interfaces.APIResult
import com.bluetaxi.driver.interfaces.ClickInterface
import com.bluetaxi.driver.service.APIService_Retrofit_JSON
import com.bluetaxi.driver.service.NonActivity
import com.bluetaxi.driver.service.RetrofitCallbackClass
import com.bluetaxi.driver.service.ServiceGenerator
import com.bluetaxi.driver.triplist.adapter.interfaces.OngoingTrip
import com.bluetaxi.driver.triplist.adapter.OngoingTripListAdapter
import com.bluetaxi.driver.triplist.model.ResponseOngoingBooking
import com.bluetaxi.driver.utils.CToast
import com.bluetaxi.driver.utils.NC
import com.bluetaxi.driver.utils.NetworkStatus
import com.bluetaxi.driver.utils.SessionSave
import com.bluetaxi.driver.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpcomingTripsActivity : AppCompatActivity(), OngoingTrip, ClickInterface {
    lateinit var upcoming_trip_list: RecyclerView
    lateinit var no_data_image: ImageView
    var mshowDialog: Dialog? = null
    var myOtoMetter: Dialog? = null

    private var upComingData: ArrayList<ResponseOngoingBooking.Detail.PendingBooking> = ArrayList()
    private lateinit var newBookingAdapter: OngoingTripListAdapter
    private var trip_id: String? = null
    private var lat: String? = null
    private var langs: String? = null
    private var myOTPDialog: Dialog? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_upcoming_trips)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }

        val locationResult = fusedLocationProviderClient.lastLocation
        locationResult.addOnCompleteListener(this@UpcomingTripsActivity) { task ->
            if (task.isSuccessful) {
                // Set the map's camera position to the current location of the device.
                lastKnownLocation = task.result
                lat = lastKnownLocation!!.latitude.toString()
                langs = lastKnownLocation!!.longitude.toString()

            }
        }
        upcoming_trip_list = findViewById(R.id.upcoming_trip_list_new)
        no_data_image = findViewById(R.id.no_data_image)
        upcoming_trip_list.layoutManager = LinearLayoutManager(this@UpcomingTripsActivity)


        newBookingAdapter = OngoingTripListAdapter(this@UpcomingTripsActivity, upComingData,
            this@UpcomingTripsActivity)
// Pass 'this' as the listener
        upcoming_trip_list.adapter = newBookingAdapter

        loadCancelledListApi()

    }

    private fun loadCancelledListApi() {



        showLoadings(this@UpcomingTripsActivity)
        val client = MyApplication.getInstance().apiManagerWithEncryptBaseUrl

        val request = ApiRequestData.UpcomingRequest()
        request.setId(SessionSave.getSession("Id", this@UpcomingTripsActivity))
        request.setDeviceType("2")
        request.setLimit("10")
        request.setStart("0")
        request.setRequestType("1")
        val LoginResponse = client.onGoing(
            ServiceGenerator.COMPANY_KEY,
            request,
            SessionSave.getSession("Lang",this@UpcomingTripsActivity)
        )
        LoginResponse.enqueue(
            RetrofitCallbackClass<ResponseOngoingBooking>(
                this@UpcomingTripsActivity,
                object : Callback<ResponseOngoingBooking?> {
                    override fun onResponse(
                        call: Call<ResponseOngoingBooking?>,
                        response: Response<ResponseOngoingBooking?>
                    ) {
                        if (response.isSuccessful) {
                            cancelLoadings()




                            if (response.isSuccessful) {
                                val data = response.body()

                                if (data != null && data.status == 1) {
                                    upComingData.clear() // Clear the old data
                                    // Add all new bookings to the mutable list


                                    if (data.detail.pending_booking?.size  !=0 )
                                    {
                                        data.detail.pending_booking?.let {
                                            upComingData.addAll(it)
                                        }
                                        println("pickup_location_newbooking_size" + " " + upComingData.size)

                                        // Notify the adapter that the data set has changed
                                        newBookingAdapter.notifyDataSetChanged()

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
                                    newBookingAdapter.notifyDataSetChanged() // Update UI to show empty list
                                    upcoming_trip_list.visibility = View.GONE
                                    no_data_image .visibility = View.VISIBLE
                                    Toast.makeText(
                                        this@UpcomingTripsActivity,
                                        data?.message ?: "No bookings found", // Use data.message if available
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                // Handle HTTP errors (e.g., 404, 500)
                                // cancelLoadings() // Uncomment if you have this function
                                Toast.makeText(
                                    this@UpcomingTripsActivity,
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

                    override fun onFailure(call: Call<ResponseOngoingBooking?>, t: Throwable) {
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
            if (mshowDialog != null) if (mshowDialog!!.isShowing && this@UpcomingTripsActivity != null) mshowDialog!!.dismiss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    override fun startTrip(_category: ResponseOngoingBooking.Detail.PendingBooking) {
        trip_id = _category.passengers_log_id
        showOtp(this@UpcomingTripsActivity)
    }

    override fun contactPassenger(_category: ResponseOngoingBooking.Detail.PendingBooking) {
        openDialer(_category.passenger_phone)
    }

    override fun cancelTrip(_category: ResponseOngoingBooking.Detail.PendingBooking) {
        trip_id = _category.passengers_log_id

        try {
            val j = JSONObject()
            j.put("pass_logid", _category.passengers_log_id)
            j.put("driver_id", SessionSave.getSession("Id", this))
            j.put("taxi_id", SessionSave.getSession("taxi_id", this))
            j.put("company_id", SessionSave.getSession("company_id", this))
            j.put("driver_reply", "C")
            j.put("field", "")
            j.put("flag", "1")
            if (MainActivity.mMyStatus.onstatus.equals(
                    "Arrivd",
                    ignoreCase = true
                )
            ) j.put("driver_arrived", 1) else j.put("driver_arrived", 0)
            j.put("schedule", "1")
            val canceltrip_url = "type=driver_reply"
            CancelTrip(canceltrip_url, j)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun trackTrip(_category: ResponseOngoingBooking.Detail.PendingBooking) {

        trip_id = _category.passengers_log_id
        if (SessionSave.getSession("shift_status", this@UpcomingTripsActivity).equals("IN", ignoreCase = true)) {
            SessionSave.saveSession(
                "trip_id",
                trip_id!!.trim(),
                this@UpcomingTripsActivity
            )
            val intent = Intent(this@UpcomingTripsActivity, OngoingAct::class.java)
            startActivity(intent)
        } else {
            CToast.ShowToast(this@UpcomingTripsActivity, NC.getString(R.string.track_shift_status))
        }
    }

    private fun openDialer(phoneNumber: String) {
        // Create a URI with the "tel:" scheme and the phone number
        val dialIntentUri = Uri.parse("tel:$phoneNumber")

        // Create an Intent with ACTION_DIAL to open the dialer app
        val dialIntent = Intent(Intent.ACTION_DIAL, dialIntentUri)

        // Check if there's an app that can handle this Intent
        if (dialIntent.resolveActivity(packageManager) != null) {
            startActivity(dialIntent)
        } else {
            Toast.makeText(this, "No dialer app found to handle this request.", Toast.LENGTH_SHORT).show()
        }
    }

    inner class CancelTrip internal constructor(url: String?, data: JSONObject?) :
        APIResult {
        init {
            try {
                if (NetworkStatus.isOnline(this@UpcomingTripsActivity)) {
                    APIService_Retrofit_JSON(
                        this@UpcomingTripsActivity,
                        this,
                        data,
                        false
                    ).execute(url)
                } else {
                    Utils.alert_view(
                        this@UpcomingTripsActivity,
                        NC.getString(R.string.message),
                        NC.getString(R.string.check_net_connection),
                        NC.getString(R.string.ok),
                        "",
                        true,
                        this@UpcomingTripsActivity,
                        "4"
                    )
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            if (isSuccess) {
                try {
                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {
                        CToast.ShowToast(this@UpcomingTripsActivity, json.getString("message"))
                    } else {
                        CToast.ShowToast(this@UpcomingTripsActivity, json.getString("message"))
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
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

    fun showOtp(mContext: Context?) {
        val view1 = View.inflate(mContext, R.layout.odometer_otp_input, null)
        if (myOTPDialog != null && myOTPDialog!!.isShowing()) myOTPDialog!!.cancel()
        myOTPDialog = Dialog(mContext!!, R.style.NewDialog)
        myOTPDialog!!.setContentView(view1)
        myOTPDialog!!.setCancelable(false)
        myOTPDialog!!.setCanceledOnTouchOutside(false)
        myOTPDialog!!.setCancelable(true)
        myOTPDialog!!.show()
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(myOTPDialog!!.getWindow()!!.getAttributes())
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        myOTPDialog!!.getWindow()!!.setAttributes(layoutParams)


//
        val btn_confirm: TextView = myOTPDialog!!.findViewById<TextView>(R.id.btn_confirm)
        val verifyno1Txt: EditText = myOTPDialog!!.findViewById<EditText>(R.id.verifyno1Txt)
        val verifyno2Txt: EditText = myOTPDialog!!.findViewById<EditText>(R.id.verifyno2Txt)
        val verifyno3Txt: EditText = myOTPDialog!!.findViewById<EditText>(R.id.verifyno3Txt)
        val verifyno4Txt: EditText = myOTPDialog!!.findViewById<EditText>(R.id.verifyno4Txt)
        verifyno1Txt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
                if (s.toString().trim { it <= ' ' }.length == 1) {
                    verifyno2Txt.requestFocus()
                    verifyno2Txt.setText("")
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })
        verifyno2Txt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
                if (s.toString().trim { it <= ' ' }.length == 1) {
                    verifyno3Txt.requestFocus()
                    verifyno3Txt.setText("")
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })
        verifyno3Txt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
                if (s.toString().trim { it <= ' ' }.length == 1) {
                    verifyno4Txt.requestFocus()
                    verifyno4Txt.setText("")
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })
        btn_confirm.setOnClickListener {
            if (verifyno1Txt.getText().toString() == "") {
                Toast.makeText(
                    mContext,
                    "Enter first number",
                    Toast.LENGTH_LONG
                ).show()
            } else if (verifyno2Txt.getText().toString() == "") {
                Toast.makeText(
                    mContext,
                    "Enter second number",
                    Toast.LENGTH_LONG
                ).show()
            } else if (verifyno3Txt.getText().toString() == "") {
                Toast.makeText(
                    mContext,
                    "Enter third number",
                    Toast.LENGTH_LONG
                ).show()
            } else if (verifyno4Txt.getText().toString() == "") {
                Toast.makeText(
                    mContext,
                    "Enter fourth number",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                myOTPDialog!!.dismiss()
                val otpnumber = verifyno1Txt.getText().toString() + verifyno2Txt.getText()
                    .toString() + verifyno3Txt.getText().toString() + verifyno4Txt.getText()
                    .toString()
                val url = "type=booking_otp_verify"
                updateOTP(url, "3", otpnumber)
            }
        }
    }


    inner class updateOTP internal constructor(
        url: String?,
        type: String?,
        odometer_number: String?
    ) :
        APIResult {
        init {
            try {
                val j = JSONObject()
                j.put("trip_id", trip_id)
                j.put("otp", odometer_number)
                APIService_Retrofit_JSON(this@UpcomingTripsActivity, this, j, false).execute(url)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String?) {
            try {
                if (isSuccess) {
                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {
                        run {
                            SessionSave.saveSession(
                                "otp_enter",
                                "no",
                                this@UpcomingTripsActivity
                            )
                            SessionSave.saveSession(
                                "odameter_status",
                                "2",
                                this@UpcomingTripsActivity
                            )
                            //showodometer();

                            showodometer()

                        }
                    } else {
                        // dialog1 = Utils.alert_view(mContext, NC.getResources().getString(R.string.message),json.getString("message") , NC.getResources().getString(R.string.ok), "", true, mContext, "4");
                        Utils.alert_view(
                            this@UpcomingTripsActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),
                            NC.getString(R.string.ok),
                            "",
                            true,
                            this@UpcomingTripsActivity,
                            "4"
                        )
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun showodometer() {
        val view1 = View.inflate(this@UpcomingTripsActivity, R.layout.odometer_input, null)
        if (myOtoMetter != null && myOtoMetter!!.isShowing()) myOtoMetter!!.cancel()
        myOtoMetter = Dialog(this@UpcomingTripsActivity, R.style.NewDialog)
        myOtoMetter!!.setContentView(view1)
        myOtoMetter!!.setCancelable(false)
        myOtoMetter!!.setCanceledOnTouchOutside(false)
        myOtoMetter!!.setCancelable(true)
        myOtoMetter!!.show()
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(myOtoMetter!!.getWindow()!!.getAttributes())
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        myOtoMetter!!.getWindow()!!.setAttributes(layoutParams)


//
        val btn_confirm: LinearLayout = myOtoMetter!!.findViewById<LinearLayout>(R.id.btn_confirm)
        val odameter_heading: TextView = myOtoMetter!!.findViewById<TextView>(R.id.odameter_heading)
        val verifyno1Txt: EditText = myOtoMetter!!.findViewById<EditText>(R.id.verifyno1Txt)
        val verifyno2Txt: EditText = myOtoMetter!!.findViewById<EditText>(R.id.verifyno2Txt)
        val verifyno3Txt: EditText = myOtoMetter!!.findViewById<EditText>(R.id.verifyno3Txt)
        val verifyno4Txt: EditText = myOtoMetter!!.findViewById<EditText>(R.id.verifyno4Txt)
        val verifyno5Txt: EditText = myOtoMetter!!.findViewById<EditText>(R.id.verifyno5Txt)
        val verifyno6Txt: EditText = myOtoMetter!!.findViewById<EditText>(R.id.verifyno6Txt)
        //   EditText verifyno7Txt = myOtoMetter!!.findViewById(R.id.verifyno7Txt);
        if (SessionSave.getSession("odameter_status", this@UpcomingTripsActivity) == "2") {
            odameter_heading.text = "Start  Reading"
        } else if (SessionSave.getSession("odameter_status", this@UpcomingTripsActivity) == "3") {
            odameter_heading.text = "End Reading"
        } else if (SessionSave.getSession("odameter_status", this@UpcomingTripsActivity) == "3") {
            odameter_heading.text = "Accept Reading"
        }
        verifyno1Txt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
                if (s.toString().trim { it <= ' ' }.length == 1) {
                    verifyno2Txt.requestFocus()
                    verifyno2Txt.setText("")
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })
        verifyno2Txt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
                if (s.toString().trim { it <= ' ' }.length == 1) {
                    verifyno3Txt.requestFocus()
                    verifyno3Txt.setText("")
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })
        verifyno3Txt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
                if (s.toString().trim { it <= ' ' }.length == 1) {
                    verifyno4Txt.requestFocus()
                    verifyno4Txt.setText("")
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })
        verifyno4Txt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
                if (s.toString().trim { it <= ' ' }.length == 1) {
                    verifyno5Txt.requestFocus()
                    verifyno5Txt.setText("")
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })
        verifyno5Txt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
                if (s.toString().trim { it <= ' ' }.length == 1) {
                    verifyno6Txt.requestFocus()
                    verifyno6Txt.setText("")
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }
        })


        btn_confirm.setOnClickListener {
            if (verifyno1Txt.getText().toString() == "") {
                Toast.makeText(
                    this@UpcomingTripsActivity,
                    "Enter first number",
                    Toast.LENGTH_LONG
                ).show()
            } else if (verifyno2Txt.getText().toString() == "") {
                Toast.makeText(
                    this@UpcomingTripsActivity,
                    "Enter second number",
                    Toast.LENGTH_LONG
                ).show()
            } else if (verifyno3Txt.getText().toString() == "") {
                Toast.makeText(
                    this@UpcomingTripsActivity,
                    "Enter third number",
                    Toast.LENGTH_LONG
                ).show()
            } else if (verifyno4Txt.getText().toString() == "") {
                Toast.makeText(
                    this@UpcomingTripsActivity,
                    "Enter fourth number",
                    Toast.LENGTH_LONG
                ).show()
            } else if (verifyno5Txt.getText().toString() == "") {
                Toast.makeText(
                    this@UpcomingTripsActivity,
                    "Enter fifth number",
                    Toast.LENGTH_LONG
                ).show()
            } else if (verifyno6Txt.getText().toString() == "") {
                Toast.makeText(
                    this@UpcomingTripsActivity,
                    "Enter sixth number",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                myOtoMetter!!.dismiss()
                val otpnumber = verifyno1Txt.getText().toString() + verifyno2Txt.getText()
                    .toString() + verifyno3Txt.getText().toString() + verifyno4Txt.getText()
                    .toString() + verifyno5Txt.getText().toString() + verifyno6Txt.getText()
                    .toString() + "0"
                val url = "type=update_odometer"
                updateOdaMeter(url, "2", otpnumber)
            }
        }
    }


    inner class updateOdaMeter internal constructor(
        url: String?,
        type: String?,
        odometer_number: String?
    ) :
        APIResult {
        init {
            try {
                val j = JSONObject()
                j.put("driver_id", SessionSave.getSession("Id", this@UpcomingTripsActivity))
                j.put("trip_id", trip_id)
                j.put("odometer_number", odometer_number)
                j.put("level", type)
                APIService_Retrofit_JSON(this@UpcomingTripsActivity, this, j, false).execute(url)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        @SuppressLint("MissingPermission")
        override fun getResult(isSuccess: Boolean, result: String) {
            try {
                if (isSuccess) {



                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {
                        run {
                            try {
                                val j = JSONObject()
                                j.put("trip_id", trip_id)
                                j.put(
                                    "driver_id",
                                    SessionSave.getSession("Id", this@UpcomingTripsActivity)
                                )
                                j.put(
                                    "pickup_latitude",
                                    lat

                                )
                                j.put(
                                    "pickup_longitude",
                                    langs
                                )
                                val scheduleTripUrl = "type=schedule_start_trip"
                                NonActivity().stopServicefromNonActivity(this@UpcomingTripsActivity)
                                ScheduleStartTrip(scheduleTripUrl, j)
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        Utils.alert_view(
                            this@UpcomingTripsActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),
                            NC.getString(R.string.ok),
                            "",
                            true,
                            this@UpcomingTripsActivity,
                            "4"
                        )
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }


    inner class ScheduleStartTrip internal constructor(
        url: String?,
        data: JSONObject?

    ) :
        APIResult {
        init {
            try {
                if (NetworkStatus.isOnline(this@UpcomingTripsActivity)) {
                    APIService_Retrofit_JSON(this@UpcomingTripsActivity, this, data, false).execute(url)
                } else {
                    Utils.alert_view(
                        this@UpcomingTripsActivity,
                        NC.getString(R.string.message),
                        NC.getString(R.string.check_net_connection),
                        NC.getString(R.string.ok),
                        "",
                        true,
                        this@UpcomingTripsActivity,
                        "4"
                    )
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            if (isSuccess) {
                NonActivity().startServicefromNonActivity(this@UpcomingTripsActivity)
                try {
                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {
                        SessionSave.saveSession("trip_id", json.getString("trip_id"), this@UpcomingTripsActivity)
                        SessionSave.saveSession("status", json.getString("driver_status"), this@UpcomingTripsActivity)
                        SessionSave.saveSession(
                            "travel_status",
                            json.getString("travel_status"),
                            this@UpcomingTripsActivity
                        )
                        if (SessionSave.getSession("shift_status", this@UpcomingTripsActivity)
                                .equals("IN", ignoreCase = true)
                        ) {
                            SessionSave.saveSession(
                                "trip_id",
                                trip_id,
                                this@UpcomingTripsActivity
                            )
                            val `in` = Intent(
                                this@UpcomingTripsActivity,
                                OngoingAct::class.java
                            )
                            startActivity(`in`)
                        } else {
                            CToast.ShowToast(this@UpcomingTripsActivity, NC.getString(R.string.track_shift_status))
                        }
                    } else if (json.getInt("status") == -2) {

                        loadCancelledListApi()
                    } else {
                        CToast.ShowToast(this@UpcomingTripsActivity, json.getString("message"))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                // CToast.ShowToast(mContext, NC.getString(R.string.server_error));
                NonActivity().startServicefromNonActivity(this@UpcomingTripsActivity)
            }
        }
    }
}