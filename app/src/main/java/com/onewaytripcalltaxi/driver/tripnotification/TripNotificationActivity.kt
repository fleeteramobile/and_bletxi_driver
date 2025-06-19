package com.onewaytripcalltaxi.driver.tripnotification // Make sure this matches your actual package name

// import com.google.android.gms.maps.MapView // This import is not needed, as you're using SupportMapFragment
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.onewaytripcalltaxi.driver.MainActivity
import com.onewaytripcalltaxi.driver.OngoingAct
import com.onewaytripcalltaxi.driver.R
import com.onewaytripcalltaxi.driver.data.CommonData
import com.onewaytripcalltaxi.driver.errorLog.ApiErrorModel
import com.onewaytripcalltaxi.driver.errorLog.ErrorLogRepository.Companion.getRepository
import com.onewaytripcalltaxi.driver.homescreen.HomeScreenActivity
import com.onewaytripcalltaxi.driver.interfaces.APIResult
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON
import com.onewaytripcalltaxi.driver.utils.CToast
import com.onewaytripcalltaxi.driver.utils.DriverUtils.driverInfo
import com.onewaytripcalltaxi.driver.utils.ExceptionConverter.buildStackTraceString
import com.onewaytripcalltaxi.driver.utils.NC
import com.onewaytripcalltaxi.driver.utils.NetworkStatus
import com.onewaytripcalltaxi.driver.utils.SessionSave
import com.onewaytripcalltaxi.driver.utils.Systems
import kotlinx.android.synthetic.main.notification_lay.estimate_distanceTxt
import org.json.JSONException
import org.json.JSONObject

class TripNotificationActivity : AppCompatActivity(), OnMapReadyCallback {
    // UI elements
    private lateinit var acceptButton: Button
    private lateinit var pickupCityTxt: TextView
    private lateinit var dropCityTxt: TextView
    private lateinit var pickupLocationTXt: TextView
    private lateinit var dropLocationTxt: TextView
    private lateinit var priceTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var progressBar: ProgressBar // This is the spinner for the accept button
    private lateinit var closeButton: ImageButton
    private lateinit var requestTimerProgressBar: ProgressBar // The line progress bar
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    // Google Map instance
    private var googleMap: GoogleMap? = null

    // Handler and Runnable for the progress animation
    private var progressHandler: Handler? = null
    private var progressRunnable: Runnable? = null

    // Configuration for the dynamic timer
    private val DYNAMIC_TOTAL_TIME_MILLIS = 15000L // Example: 15 seconds for total progress
    private val PROGRESS_INTERVAL_MILLIS = 100L // Update every 100 milliseconds
    var nActivity: AppCompatActivity? = null
    var bun: Bundle? = null
    var pickup_time: String? = null
    var profile_image: String? = null

    var message: String? = null
    var distance: String? = null
    var passenger_id: String? = null
    var time_out = 0

    private var passenger_phone: String? = null
    private var cityname: String? = null
    private var passenger_name: String? = null
    private var notes: String? = null
    private var estimate_amount: String? = null
    private var est_distance: String? = null
    private var eta_time: String? = null
    private var stops_count: String? = null
    private var trip_id = ""
    private var pickup: String? = null
    private var drop: String? = null
    private var bookedby: String? = null
    private var nowAfter = -1
    private var model_name: String? = null
    private var trip_type: String? = null
    private var pickup_lat = 0.0
    private var pickup_lng: Double = 0.0
    private var drop_lattitude = 0.0
    private var drop_longitude = 0.0
    private var pickupCity: String? = null
    private var dropCity: String? = null

    // Member variables that LatlongValue will populate
    private var pick_lat: Double? = null
    private var pick_long: Double? = null
    private var current_lattitude: Double? = null
    private var current_longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_notification)



        bun = intent.extras
        nActivity = this
        CommonData.current_act = "NotificationAct"
        CommonData.current_trip_accept = 1
        message = bun!!.getString("message")


        // 1. Initialize the Map Fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragmentContainer) as? SupportMapFragment

        // 2. Request the map asynchronously
        if (mapFragment != null) {
            mapFragment.getMapAsync(this)
        } else {
            Toast.makeText(this, "Map fragment not found!", Toast.LENGTH_LONG).show()
        }

        // Initialize other UI elements
        acceptButton = findViewById(R.id.acceptButton)
        pickupCityTxt = findViewById(R.id.pickup_city)
        dropCityTxt = findViewById(R.id.drop_city)
        pickupLocationTXt = findViewById(R.id.pickup_location)
        dropLocationTxt = findViewById(R.id.drop_location)
        priceTextView = findViewById(R.id.priceTextView)
        distanceTextView = findViewById(R.id.distanceTextView)
        progressBar = findViewById(R.id.progressBar) // For the accept button's loading state
        closeButton = findViewById(R.id.closeButton)
        requestTimerProgressBar =
            findViewById(R.id.requestTimerProgressBar) // Initialize the line progress bar

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        loadUI()

        startProgressAnimation()
        // Set OnClickListener for the Accept button
        acceptButton.setOnClickListener {
            // Stop the progress animation when the user interacts
            stopProgressAnimation()

            // Show spinner progress bar and disable button
            progressBar.visibility = View.VISIBLE
            acceptButton.text = "" // Optionally clear text
            acceptButton.isEnabled = false
            acceptTripFun()

        }

        // Set OnClickListener for the Close button
        closeButton.setOnClickListener {
            callDeclineAPi()
            stopProgressAnimation()

        }


    }


    private fun callDeclineAPi() {
        try {
            if (NetworkStatus.isOnline(this@TripNotificationActivity)) {

                val j = JSONObject()
                j.put("trip_id", trip_id)
                j.put("driver_id", SessionSave.getSession("Id", this@TripNotificationActivity))
                j.put("taxi_id", SessionSave.getSession("taxi_id", this@TripNotificationActivity))
                j.put(
                    "company_id",
                    SessionSave.getSession("company_id", this@TripNotificationActivity)
                )
                j.put("reason", "")
                j.put("reject_type", "1")
                val Url = "type=reject_trip"
                TripReject(Url, j, 2)
            } else {

                CToast.ShowToast(
                    this@TripNotificationActivity,
                    NC.getString(R.string.check_net_connection)
                )
                finish()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun acceptTripFun() {
        try {
            var latitude = 0.0
            var longitude = 0.0



            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        // Handle the new location here
                        latitude = location.latitude
                        latitude = location.longitude
                        Log.d(
                            "LocationTracker",
                            "Current Location: Lat = $latitude, Lng = $longitude"
                        )
                        Toast.makeText(
                            this@TripNotificationActivity,
                            "Lat: $latitude, Lng: $latitude", Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                    // Handle changes in location availability (e.g., GPS turned off)
                    Log.d(
                        "LocationTracker",
                        "Location available: ${locationAvailability.isLocationAvailable}"
                    )
                    if (!locationAvailability.isLocationAvailable) {
                        Toast.makeText(
                            this@TripNotificationActivity,
                            "Location not available. Please check settings.",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            MainActivity.mMyStatus.settripId(trip_id)
            SessionSave.saveSession("trip_id", trip_id, this)
            MainActivity.mMyStatus.setpassengerId(trip_id)
            val j = JSONObject()
            j.put("pass_logid", trip_id)
            j.put("driver_id", SessionSave.getSession("Id", this))
            j.put("taxi_id", SessionSave.getSession("taxi_id", this))
            j.put("company_id", SessionSave.getSession("company_id", this))
            j.put("driver_reply", "A")
            j.put("field", "rejection")
            j.put("flag", "0")
            j.put("latitude", latitude)
            j.put("longitude", longitude)
            val Url = "type=driver_reply"
            Systems.out.println("result" + "Sucess")
            TripAccept(Url, j)


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun loadUI() {
        Log.d("Driver_location_request", "test")
        try {
            message?.let { jsonString ->
                val rootJson = JSONObject(jsonString) // Renamed to rootJson for clarity
                Log.d("Driver_location_request", rootJson.toString())

                // Now, "trip_details" is a JSONObject at the root level
                val tripdetails = rootJson.getJSONObject("trip_details")
                //   Log.d("Driver_location_request_tripdetails", tripdetails.toString())

                // All these fields are now under "trip_details"
                trip_id = tripdetails.optString("passengers_log_id", "")
                //  Log.d("Driver_location_request_trip_id", trip_id)

                time_out = tripdetails.optInt("notification_time", 0) // Default to 0 if not found
                notes = tripdetails.optString("notes", "")
                estimate_amount = tripdetails.optString("approx_fare", "")

                val pickup_notes = tripdetails.optString("pickup_notes", "")
                val drop_notes = tripdetails.optString("dropoff_notes", "")
                est_distance = tripdetails.optString("approx_distance", "")
                eta_time = tripdetails.optString("approx_distance", "")



                stops_count = tripdetails.optString("stops", "0")

                if (tripdetails.has(CommonData.SHOW_CANCEL_BUTTON)) {
                    SessionSave.saveSession(
                        CommonData.SHOW_CANCEL_BUTTON,
                        tripdetails.getString(CommonData.SHOW_CANCEL_BUTTON),
                        this
                    )
                } else {
                    SessionSave.saveSession(CommonData.SHOW_CANCEL_BUTTON, "0", this)
                }

                // booking_details is a JSONObject inside "trip_details"
                val bookingDetails = tripdetails.getJSONObject("booking_details")

                if (bookingDetails.has("now_after")) {
                    nowAfter = bookingDetails.optInt("now_after", -1)
                    // Your original logic here
                }
                model_name = bookingDetails.optString("model_name", "")

                // Your original logic for pickup_notes and drop_notes if needed

                pickup = bookingDetails.optString("pickupplace", "")
//                pickup_lat_json = bookingDetails.optDouble("pickup_latitude", 0.0)
//                pickup_lng_json = bookingDetails.optDouble("pickup_longitude", 0.0)

                val dropLocation = bookingDetails.optString("dropplace", "")
//                drop_lattitude_json = bookingDetails.optDouble("drop_latitude", 0.0)
//                drop_longitude_json = bookingDetails.optDouble("drop_longitude", 0.0)


                val pickupplace = bookingDetails.optString("pickupplace", "")
                val dropplace = bookingDetails.optString("dropplace", "")


                pickupCity = AddressParser.getCityFromAddress(this, pickupplace)
                dropCity = AddressParser.getCityFromAddress(this, dropplace)
                pickupCityTxt.setText(pickupCity)
                dropCityTxt.setText(pickupCity)

                pickupLocationTXt.setText(pickupplace)
                dropLocationTxt.setText(dropplace)
                SessionSave.getSession("site_currency", this@TripNotificationActivity)
                val currencySymbol = SessionSave.getSession("site_currency", this@TripNotificationActivity) ?: "$" // Provide a default if null
                priceTextView.text = "$currencySymbol $estimate_amount"
                distanceTextView.text = "$est_distance KM"



                val result = bookingDetails.optString("pickup_time", "").split(" ".toRegex())
                    .dropLastWhile { it.isEmpty() }.toTypedArray()
                if (result.size > 1) {
                    pickup_time = result[1]
                } else {
                    pickup_time = ""
                }

                passenger_phone = bookingDetails.optString("passenger_phone", "")
                passenger_id = bookingDetails.optString("passenger_id", "")
                distance = bookingDetails.optString("distance_away", "")
                passenger_name = bookingDetails.optString("passenger_name", "")
                bookedby = bookingDetails.optString("bookedby", "")

                // Log the parsed values


            } ?: run {
                Log.e("TripNotification", "Message is null, cannot parse trip details.")
                Toast.makeText(this, "No trip details received.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Log.e("TripNotification", "Error parsing JSON: ${e.message}", e)
            Toast.makeText(this, "Error processing trip details.", Toast.LENGTH_LONG).show()
        }
    }


    fun LatlongValue(
        pickup_lat: Double?,    // Renamed parameters to avoid confusion with member variables
        pickup_long: Double?,
        current_lat: Double?,
        current_long: Double?
    ) {
        // Assigning parameter values to the member variables of the class instance
        this.pick_lat = pickup_lat
        this.pick_long = pickup_long
        this.current_lattitude = current_lat
        this.current_longitude = current_long
    }

    /**
     * Starts the progress bar animation from 0 to 100 over a dynamic time.
     */
    private fun startProgressAnimation() {
        if (time_out <= 0) {
            Log.w(
                "TripNotification",
                "notification_time is 0 or less, progress animation will not run."
            )
            requestTimerProgressBar.visibility = View.GONE
            Toast.makeText(this, "Invalid notification time received.", Toast.LENGTH_LONG).show()
            return
        }

        requestTimerProgressBar.max = 100
        requestTimerProgressBar.progress = 0
        requestTimerProgressBar.visibility = View.VISIBLE

        progressHandler = Handler(Looper.getMainLooper())
        val startTime = System.currentTimeMillis()

        progressRunnable = object : Runnable {
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - startTime
                // time_out is in seconds, convert to milliseconds for calculation
                val calculatedProgress =
                    ((elapsedTime.toFloat() / (time_out * 1000L)) * 100).toInt()

                if (calculatedProgress <= requestTimerProgressBar.max) {
                    requestTimerProgressBar.progress = calculatedProgress
                    progressHandler?.postDelayed(this, PROGRESS_INTERVAL_MILLIS)
                } else {
                    requestTimerProgressBar.progress = requestTimerProgressBar.max
                    requestTimerProgressBar.visibility = View.GONE
                    try {


                        val j = JSONObject()
                        j.put("trip_id", trip_id)
                        j.put(
                            "driver_id",
                            SessionSave.getSession("Id", this@TripNotificationActivity)
                        )
                        j.put(
                            "taxi_id",
                            SessionSave.getSession("taxi_id", this@TripNotificationActivity)
                        )
                        j.put(
                            "company_id",
                            SessionSave.getSession("company_id", this@TripNotificationActivity)
                        )
                        j.put("reason", "")
                        j.put("reject_type", "0")
                        val Url = "type=reject_trip"
                        Handler().postDelayed({

                            TripReject(Url, j, 1)

                        }, 500)
                    } catch (e: java.lang.Exception) {

                        val intent =
                            Intent(this@TripNotificationActivity, HomeScreenActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(intent)
                        finish()
                        CToast.ShowToast(
                            this@TripNotificationActivity,
                            NC.getString(R.string.server_error)
                        )

                        e.printStackTrace()
                    }
                }
            }
        }
        progressHandler?.post(progressRunnable!!)
    }

    private fun stopProgressAnimation() {
        progressHandler?.removeCallbacks(progressRunnable!!)
        requestTimerProgressBar.visibility = View.GONE
    }


    /**
     * This callback is triggered when the map is ready to be used.
     * @param map The GoogleMap object
     */
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Example: Add a marker in Coimbatore and move the camera
        val coimbatore = LatLng(11.0045, 76.9616) // Approximate coordinates for Coimbatore
        googleMap?.apply { // Use apply scope function for null-safety
            addMarker(MarkerOptions().position(coimbatore).title("Coimbatore"))
            moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    coimbatore,
                    12f
                )
            ) // Zoom level 12 is a good default
        }
    }

    // --- Activity Lifecycle Overrides ---

    // Important: It's good practice to stop Handlers/Runnables in onStop or onDestroy
    // to prevent memory leaks and unnecessary processing when the activity is not active.
    override fun onStop() {
        super.onStop()
        stopProgressAnimation() // Stop the animation if the activity goes into the background
    }

    override fun onDestroy() {
        super.onDestroy()
        // Ensure all callbacks are removed and resources are cleaned up
        progressHandler?.removeCallbacks(progressRunnable!!)
        progressHandler = null
        progressRunnable = null
    }

    // Other lifecycle methods are fine as empty overrides or can be removed if not used.
    // The SupportMapFragment handles its own lifecycle within FragmentContainerView.
    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    inner class TripAccept(url: String, var jsonObject: JSONObject) : APIResult {
        var msg: String? = null

        init {
            Systems.out.println("result$url")

            APIService_Retrofit_JSON(
                this@TripNotificationActivity,
                this,
                jsonObject,
                false
            ).execute(url)
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            try {
                if (isSuccess) {
                    val json = JSONObject(result)
                    msg = json.getString("message")
                    CommonData.current_trip_accept = 1
                    if (json.getInt("status") == 7) {
                        bookedby = ""
                        SessionSave.saveSession("trip_id", "", this@TripNotificationActivity)
                        msg = json.getString("message")
                        val i = Intent(
                            getBaseContext(),
                            HomeScreenActivity::class.java
                        )
                        // showLoading(NotificationAct.this);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        val extras = Bundle()
                        extras.putString("alert_message", msg)
                        CToast.ShowToast(this@TripNotificationActivity, msg)
                        getApplication().startActivity(i)
                        nActivity!!.finish()
                    } else if (json.getInt("status") == 1 || bookedby == "2") {
                        SessionSave.saveSession("speedwaiting", "", this@TripNotificationActivity)
                        MainActivity.mMyStatus.settripId(trip_id)
                        SessionSave.saveSession("trip_id", trip_id, this@TripNotificationActivity)
                        SessionSave.saveSession(
                            "status", "B",
                            this@TripNotificationActivity
                        )
                        SessionSave.saveSession(
                            CommonData.IS_STREET_PICKUP,
                            false,
                            this@TripNotificationActivity
                        )
                        SessionSave.saveSession("bookedby", bookedby, this@TripNotificationActivity)
                        //  showLoading(NotificationAct.this);
                        val intent = Intent(
                            this@TripNotificationActivity,
                            OngoingAct::class.java
                        )
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        val extras = Bundle()
                        extras.putString("alert_message", msg)
                        intent.putExtras(extras)
                        startActivity(intent)
                        finish()
                    } else if (json.getInt("status") == 5) {
                        SessionSave.saveSession("trip_id", "", this@TripNotificationActivity)
                        msg = json.getString("message")
                        val i = Intent(
                            getBaseContext(),
                            HomeScreenActivity::class.java
                        )
                        // showLoading(NotificationAct.this);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        val extras = Bundle()
                        extras.putString("alert_message", msg)
                        //i.putExtras(extras);
                        getApplication().startActivity(i)
                        CToast.ShowToast(this@TripNotificationActivity, msg)
                        nActivity!!.finish()
                    } else if (json.getInt("status") == 25) {
                        runOnUiThread(Runnable {
                            CToast.ShowToast(
                                this@TripNotificationActivity,
                                NC.getString(R.string.server_error)
                            )
                        })
                    } else {
                        runOnUiThread(Runnable {
                            CToast.ShowToast(
                                this@TripNotificationActivity,
                                msg
                            )
                        })
                        finish()
                    }
                } else {
                    runOnUiThread(Runnable {
                        CToast.ShowToast(
                            this@TripNotificationActivity,
                            NC.getString(R.string.server_error)
                        )
                    })
                    finish()
                }
            } catch (e: JSONException) {
                getRepository(this@TripNotificationActivity)!!.insertAllApiErrorLogs(
                    ApiErrorModel(
                        0,
                        CommonData.getCurrentTimeForLogger(),
                        "type=driver_reply",
                        buildStackTraceString(e.stackTrace),
                        driverInfo(this@TripNotificationActivity),
                        jsonObject,
                        this@TripNotificationActivity.javaClass.getSimpleName(),
                        0
                    )
                )

                e.printStackTrace()
            }
        }
    }


    inner class TripReject(url: String?, var jsonObject: JSONObject, type: Int) : APIResult {
        var msg: String? = null
        var cancel_type = 0

        init {
            APIService_Retrofit_JSON(
                this@TripNotificationActivity,
                this,
                jsonObject,
                false
            ).execute(url)
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            Log.d("result", "result$result")
            try {
                if (isSuccess) {
                    CommonData.current_trip_accept = 0
                    //  nonactiityobj.startServicefromNonActivity(this@TripNotificationActivity)
                    CommonData.current_trip_accept = 0
                    val json = JSONObject(result)
                    if (json.getInt("status") == 6) {
                        msg = json.getString("message")
                    } else if (json.getInt("status") == 7) {
                        msg = json.getString("message")
                        cancel_type = if (json.getString("allow_offline_api")
                                .equals("1", ignoreCase = true)
                        ) {
                            1
                        } else {
                            0
                        }
                    } else if (json.getInt("status") == 8) {
                        msg = json.getString("message")
                    } else if (json.getInt("status") != 6 || json.getInt("status") != 8 || json.getInt(
                            "status"
                        ) != 3 || json.getInt("status") != 2 || json.getInt("status") != -1
                    ) {
                        msg = "Trip has been rejected"
                    } else {
                        msg = "Trip has been already cancelled"
                    }
                    SessionSave.saveSession("trip_id", "", this@TripNotificationActivity)
                    // showLoading(NotificationAct.this);

//                    if (cancel_type == 1) {
//                        shiftFunction();
//                    } else {
                    val intent = Intent(
                        this@TripNotificationActivity,
                        HomeScreenActivity::class.java
                    )
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    finish()
                    //}
                    CToast.ShowToast(this@TripNotificationActivity, msg)
                } else {
                    runOnUiThread(Runnable {
                        CToast.ShowToast(
                            this@TripNotificationActivity,
                            NC.getString(R.string.server_error)
                        )
                    })
                    finish()
                }
            } catch (e: JSONException) {
                if (this@TripNotificationActivity != null) {
                    //   shiftFunction();
                    val intent = Intent(
                        this@TripNotificationActivity,
                        HomeScreenActivity::class.java
                    )
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    finish()
                    CToast.ShowToast(
                        this@TripNotificationActivity,
                        NC.getString(R.string.server_error)
                    )
                }
                getRepository(this@TripNotificationActivity)!!.insertAllApiErrorLogs(
                    ApiErrorModel(
                        0,
                        CommonData.getCurrentTimeForLogger(),
                        "type=reject_trip",
                        buildStackTraceString(e.stackTrace),
                        driverInfo(this@TripNotificationActivity),
                        jsonObject,
                        this@TripNotificationActivity.javaClass.getSimpleName(),
                        0
                    )
                )
                e.printStackTrace()
            }
        }
    }


}



