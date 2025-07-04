package com.bluetaxi.driver.tripinprogress

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.bluetaxi.driver.MainActivity
import com.bluetaxi.driver.R
import com.bluetaxi.driver.StreetPickUpAct
import com.bluetaxi.driver.data.CommonData
import com.bluetaxi.driver.data.MystatusData
import com.bluetaxi.driver.interfaces.APIResult
import com.bluetaxi.driver.interfaces.ClickInterface
import com.bluetaxi.driver.interfaces.LocalDistanceInterface
import com.bluetaxi.driver.route.Route
import com.bluetaxi.driver.route.StopData
import com.bluetaxi.driver.service.APIService_Retrofit_JSON
import com.bluetaxi.driver.service.LocationUpdate
import com.bluetaxi.driver.service.NonActivity
import com.bluetaxi.driver.utils.LocationUtils
import com.bluetaxi.driver.utils.NC
import com.bluetaxi.driver.utils.SessionSave
import com.bluetaxi.driver.utils.Utils
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.Locale


class TripInprogressActivity : AppCompatActivity(), OnMapReadyCallback, LocalDistanceInterface,
    OnCameraMoveStartedListener, ClickInterface {

    private val nonactiityobj = NonActivity()
    private var googleMap: GoogleMap? = null
    var zoom = 17f
    var bearing:Float = 0f
    var bearings:Float = 0f
    private val c_marker: Marker? = null
    private var p_marker:Marker? = null
    private var d_marker:Marker? = null
    private val a_marker: Marker? = null
    private var dialog1: Dialog? = null

    var p_logid = ""
    var p_name = ""
    var p_pickloc = ""
    var p_droploc = ""
    var p_picklat = ""
    var p_picklng = ""
    var p_droplat = ""
    var p_droplng = ""
    var p_driverlat = ""
    var p_driverlng = ""

    private var p_image = ""
    private var p_phone = ""
    private var p_notes = ""
    private var p_driverstatus = ""
    private var p_taxi_speed:String? = ""
    private var pickup_notes:String? = ""
    private var dropoff_notes:String? = ""
    private var p_travelstatus:String? = ""
    private var booking_Type:String? = ""
    private var model_id:String? = ""
    private var payment_type_label:String? = ""
    private var mroute: String? = null
    var enable_os_waiting_fare = false

    private lateinit var profile_avatar: ShapeableImageView
    private lateinit var origin_name: TextView
    private lateinit var origin_address: TextView
    private lateinit var destination_name: TextView
    private lateinit var destination_address: TextView
    private lateinit var profile_name: TextView
    private lateinit var payment_method_button: Button
    private lateinit var btn_trip_update: Button
    private var route: Route? = null

    var localBroadcastManager: LocalBroadcastManager? = null

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLastLocation: Location? = null
    private var latitude1 = 0.0
    private var longitude1 = 0.0
    private var speed = 0.0
    private var pickupLatLng: LatLng? = null
    private var dropLatLng:LatLng? = null
    private var currentLatLng:LatLng? = null
    private var p_latitude: Double? = null
    private var p_longtitude: Double? = null
    private var d_latitude: Double? = null
    private var d_longtitude:Double? = null
    private var driver_latitude: Double? = null
    private var driver_longtitude:Double? = null
    private var alert_msg:String? = ""
    private var status:String? = null
    private var address:String? = ""
    private val MY_PERMISSIONS_REQUEST_GPS = 113
    private var ROUTE_DRAW_ON_START = false
    private var LOCATION_UPDATE_STOPPED:Boolean = false
    private val viaLatlng: LatLng? = null
    private var stopListData = ArrayList<LatLng>()
    private var stopLists = ArrayList<StopData>()
    private val stops: JSONArray? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_trip_inprogress)
        // FontHelper.applyFont(this, findViewById(R.id.ongoing_lay));
        route = Route()
        createLocationRequest()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as? SupportMapFragment



        profile_avatar = findViewById(R.id.profile_avatar)
        origin_name = findViewById(R.id.origin_name)
        origin_address = findViewById(R.id.origin_address)
        destination_name = findViewById(R.id.destination_name)
        destination_address = findViewById(R.id.destination_address)
        profile_name = findViewById(R.id.profile_name)
        payment_method_button = findViewById(R.id.payment_method_button)
        btn_trip_update = findViewById(R.id.btn_trip_update)


        // 2. Request the map asynchronously
        if (mapFragment != null) {
            mapFragment.getMapAsync(this)
        } else {
            Toast.makeText(this, "Map fragment not found!", Toast.LENGTH_LONG).show()
        }

        loadTripDetails()



    }

    private fun createLocationRequest() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS)
        mLocationRequest!!.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS)
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

//        initializeLocationCallback()
//        getCurrentLocation(LOCATION_REQUEST_TYPE_INITIAL)
    }

//    private fun initializeLocationCallback() {
//        fun initializeLocationCallback() {
//            locationCallback = object : LocationCallback() {
//                override fun onLocationResult(locationResult: LocationResult) {
//                    if (locationResult == null) {
//                        return
//                    }
//                    for (location in locationResult.locations) {
//                        mLastLocation = location
//
//                        latitude1 = location.latitude
//                        longitude1 = location.longitude
//                        mLastLocation = location
//                        speed = LocationUpdate.speed
//
//                        if (dropLatLng != null && p_travelstatus!!.trim { it <= ' ' } == "2") if (!checkLocationInRoute()) {
//                            viaLatlng =
//                                LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())
//                            mHandler.sendEmptyMessage(1)
//                        }
//
//                        bearing = location.bearing
//                        bearings = location.bearing
//                        if (map != null) zoom = map.getCameraPosition().zoom
//                        bearing = if (bearing >= 0) bearing + 90 else bearing - 90
//                        try {
//                            val latLng = LatLng(location.latitude, location.longitude)
//                            // marker Animation Function
//                            if (!animLocation) {
//                                listPoint.add(latLng)
//                            } else {
//                                savedpoint.add(latLng)
//                            }
//                            if (listPoint.size > 1) {
//                                if (a_marker != null) {
//                                    a_marker.setVisible(false)
//                                    a_marker.remove()
//                                }
//                                if (!animStarted) {
//                                    if (savedLatLng != null) {
//                                        listPoint.set(0, savedLatLng)
//                                    }
//                                    if (SessionSave.getSession(
//                                            "model_name",
//                                            this@OngoingAct
//                                        ) == "Auto"
//                                    ) {
//                                        c_marker = map.addMarker(
//                                            MarkerOptions().position(listPoint.get(0)).rotation(0f)
//                                                .anchor(0.5f, 0.5f).title(Address)
//                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.auto))
//                                        )
//                                    } else if (SessionSave.getSession(
//                                            "model_name",
//                                            this@OngoingAct
//                                        ) == "Bike"
//                                    ) {
//                                        c_marker = map.addMarker(
//                                            MarkerOptions().position(listPoint.get(0)).rotation(0f)
//                                                .anchor(0.5f, 0.5f).title(Address)
//                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bike))
//                                        )
//                                    } else {
//                                        c_marker = map.addMarker(
//                                            MarkerOptions().position(listPoint.get(0)).rotation(0f)
//                                                .anchor(0.5f, 0.5f).title(Address)
//                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.top))
//                                        )
//                                    }
//                                    c_marker.setVisible(true)
//                                    if (speed > 20 && map != null) {
//                                        animStarted = true
//                                        animLocation = true
//                                        if (map != null) {
//                                            val camPos = CameraPosition
//                                                .builder(
//                                                    map.getCameraPosition() // current Camera
//                                                )
//                                                .bearing(bearings)
//                                                .build()
//                                            if (MapWrapperLayout.ismMapIsTouched()) {
//                                                map.animateCamera(
//                                                    CameraUpdateFactory.newCameraPosition(
//                                                        camPos
//                                                    )
//                                                )
//                                            }
//                                        }
//                                        savedLatLng = listPoint.get(listPoint.size - 1)
//                                        animateLine(listPoint, c_marker, bearings)
//                                    } else {
//                                        if (c_marker != null) {
//                                            c_marker.setVisible(false)
//                                            c_marker.remove()
//                                        }
//                                        if (GpsStatus.ischecked == 0) {
//                                            GpsStatus.ischecked = 1
//                                            if (SessionSave.getSession(
//                                                    "model_name",
//                                                    this@OngoingAct
//                                                ) == "Auto"
//                                            ) {
//                                                a_marker = map.addMarker(
//                                                    MarkerOptions().position(latLng).rotation(0f)
//                                                        .anchor(0.5f, 0.5f).title(Address)
//                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.auto))
//                                                )
//                                            } else if (SessionSave.getSession(
//                                                    "model_name",
//                                                    this@OngoingAct
//                                                ) == "Bike"
//                                            ) {
//                                                a_marker = map.addMarker(
//                                                    MarkerOptions().position(latLng).rotation(0f)
//                                                        .anchor(0.5f, 0.5f).title(Address)
//                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bike))
//                                                )
//                                            } else {
//                                                a_marker = map.addMarker(
//                                                    MarkerOptions().position(latLng).rotation(0f)
//                                                        .anchor(0.5f, 0.5f).title(Address)
//                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.top))
//                                                )
//                                            }
//                                            a_marker.setVisible(true)
//                                            if (speed > 20 && map != null) {
//                                                val camPos = CameraPosition
//                                                    .builder(
//                                                        map.getCameraPosition() // current Camera
//                                                    )
//                                                    .bearing(bearings)
//                                                    .build()
//                                                map.animateCamera(
//                                                    CameraUpdateFactory.newCameraPosition(
//                                                        camPos
//                                                    )
//                                                )
//                                            }
//                                        } else {
//                                            if (SessionSave.getSession(
//                                                    "model_name",
//                                                    this@OngoingAct
//                                                ) == "Auto"
//                                            ) {
//                                                a_marker = map.addMarker(
//                                                    MarkerOptions().position(
//                                                        listPoint.get(0)
//                                                    ).rotation(0f).anchor(0.5f, 0.5f).title(Address)
//                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.auto))
//                                                )
//                                            } else if (SessionSave.getSession(
//                                                    "model_name",
//                                                    this@OngoingAct
//                                                ) == "Bike"
//                                            ) {
//                                                a_marker = map.addMarker(
//                                                    MarkerOptions().position(
//                                                        listPoint.get(0)
//                                                    ).rotation(0f).anchor(0.5f, 0.5f).title(Address)
//                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bike))
//                                                )
//                                            } else {
//                                                a_marker = map.addMarker(
//                                                    MarkerOptions().position(latLng).rotation(0f)
//                                                        .anchor(0.5f, 0.5f).title(Address)
//                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.top))
//                                                )
//                                            }
//                                            a_marker.setVisible(true)
//                                            if (speed > 20 && map != null) {
//                                                val camPos = CameraPosition
//                                                    .builder(
//                                                        map.getCameraPosition() // current Camera
//                                                    )
//                                                    .bearing(bearings)
//                                                    .build()
//                                                map.animateCamera(
//                                                    CameraUpdateFactory.newCameraPosition(
//                                                        camPos
//                                                    )
//                                                )
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                            bearing = 0f
//                            bearings = 0f
//                        } catch (ex: java.lang.Exception) {
//                            ex.printStackTrace()
//                        }
//                    }
//                }
//            }
//        }
//    }

    private fun loadTripDetails() {
        if (SessionSave.getSession("trip_id", this@TripInprogressActivity) != "") {
            val j = JSONObject()
            j.put("trip_id", SessionSave.getSession("trip_id", this@TripInprogressActivity))
            val Url = "type=get_trip_detail"
          Tripdetails(Url, j)
            nonactiityobj.startServicefromNonActivity(this@TripInprogressActivity)
        }
    }



    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.clear() // Clear any existing polylines or markers
        if (map != null) {

// Customise the styling of the base map using a JSON object defined
// in a raw resource file.
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this@TripInprogressActivity, R.raw.map_style
                )
            )

            try {
                val resultCode = GoogleApiAvailability.getInstance()
                    .isGooglePlayServicesAvailable(this@TripInprogressActivity)

                    var imagepath: String? = ""
                    if (SessionSave.getSession("p_image", this@TripInprogressActivity) != "") {
                        imagepath = SessionSave.getSession("p_image", this@TripInprogressActivity)
                        Log.i(
                            "Imagepath in session",
                            SessionSave.getSession("p_image", this@TripInprogressActivity)
                        )
                    } else imagepath = SessionSave.getSession("noimage_base", this@TripInprogressActivity)

                    MapsInitializer.initialize(this@TripInprogressActivity)

                    googleMap!!.uiSettings.isZoomControlsEnabled = false
                    googleMap!!.setOnCameraMoveStartedListener(this)
                    googleMap!!.uiSettings.isCompassEnabled = false
                    googleMap!!.uiSettings.isMyLocationButtonEnabled = false
                    googleMap!!.isMyLocationEnabled = true
                    googleMap!!.setPadding(0, 0, 0, 120)
                    googleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL


                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                LocationUpdate.currentLatitude,
                                LocationUpdate.currentLongtitude
                            ), zoom
                        )
                    )


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    override fun haversineResult(success: Boolean?) {

    }

    override fun onCameraMoveStarted(p0: Int) {

    }




    override fun positiveButtonClick(dialog: DialogInterface?, id: Int, s: String?) {
       // TODO("Not yet implemented")
    }

    override fun negativeButtonClick(dialog: DialogInterface?, id: Int, s: String?) {
        //TODO("Not yet implemented")
    }
    inner class Tripdetails(url: String, data: JSONObject): APIResult {


        init {
            APIService_Retrofit_JSON(this@TripInprogressActivity, this, data, false).execute(url)

        }

        override fun getResult(isSuccess: Boolean, result: String?) {
            val json = JSONObject(result)
            println("workload"+" "+"samdinasndansdnasklscla")
            if (json.getInt("status") == 1) {
                val detail = json.getJSONObject("detail")
                if (detail.getString("street_pickup_trip").equals("1")) {
                    startActivity(Intent(this@TripInprogressActivity, StreetPickUpAct::class.java))
                    Toast.makeText(
                        this@TripInprogressActivity,
                        NC.getString(R.string.you_are_in_trip),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                else{

                    p_logid = detail.getString("trip_id")
                    p_name = detail.getString("passenger_name")
                    p_pickloc = detail.getString("current_location")
                    p_droploc = detail.getString("drop_location")
                    p_picklat = detail.getString("pickup_latitude")
                    p_picklng = detail.getString("pickup_longitude")
                    p_droplat = detail.getString("drop_latitude")
                    p_droplng = detail.getString("drop_longitude")
                    p_driverlat = detail.getString("driver_latitute")
                    p_driverlng = detail.getString("driver_longtitute")
                    p_travelstatus = detail.getString("travel_status")
                    p_driverstatus = detail.getString("driver_status")
                    p_notes = detail.getString("notes")
                    p_phone = detail.getString("passenger_phone")
                    p_image = detail.getString("passenger_image")
                    p_taxi_speed = detail.getString("taxi_min_speed")
                    pickup_notes = detail.getString("pickup_notes")
                    dropoff_notes = detail.getString("dropoff_notes")
                    booking_Type = detail.getString("trip_type")
                    model_id = detail.getString("model_id")
                    payment_type_label = detail.getString("payment_type_label")
                    SessionSave.saveSession(
                        "status",
                        detail.getString("driver_status"),
                        this@TripInprogressActivity
                    )
                    SessionSave.saveSession("Metric", detail.getString("metric"), this@TripInprogressActivity)
                    SessionSave.saveSession("p_image", p_image, this@TripInprogressActivity)
                    SessionSave.saveSession("c", p_travelstatus, this@TripInprogressActivity)
                    if (detail.has(CommonData.IS_CORPORATE_BOOKING)) {
                        SessionSave.saveSession(
                            CommonData.IS_CORPORATE_BOOKING,
                            detail.getString("corporate_booking"),
                            this@TripInprogressActivity
                        )
                    }

                    if (json.getJSONObject("detail").has("route_path"))
                        mroute = json.getJSONObject("detail").getString("route_path")

                    if (json.getJSONObject("detail").has("enable_os_waiting_fare")) {
                        if (json.getJSONObject("detail")
                                .getString("enable_os_waiting_fare") == "0"
                        ) {
                            SessionSave.saveSession("isonewaytrip", "no", this@TripInprogressActivity)
                            enable_os_waiting_fare = false
                        } else if (json.getJSONObject("detail")
                                .getString("enable_os_waiting_fare") == "1"
                        ) {
                            SessionSave.saveSession("isonewaytrip", "yes", this@TripInprogressActivity)
                            enable_os_waiting_fare = true
                        }
                    } else {
                        SessionSave.saveSession("isonewaytrip", "no", this@TripInprogressActivity)
                        enable_os_waiting_fare = false
                    }
                    if (stops != null && stops.length() > 0)
                    {
                        stopLists = parseStop(stops.toString())
                    }
//                    else{
//                        stopLists = createPickAndStopView(
//                            p_pickloc,
//                            p_picklat,
//                            p_picklng,
//                            p_droploc,
//                            p_droplat,
//                            p_droplng
//                        )
//                    }




                    loadUi()

                    // Check if the 'detail' object has 'citylimit_data'
                    if (json.has("citylimit_data")) {
                        // Get the 'citylimit_data' array
                        val citylimitDataArray = json.getJSONArray("citylimit_data")

                        // Loop through the array if there are multiple objects or just get the first element
                        for (i in 0 until citylimitDataArray.length()) {
                            // Get each city limit data as a JSONObject
                            val cityLimitData = citylimitDataArray.getJSONObject(i)

                            // Now you can access individual fields
                            val id = cityLimitData.getInt("_id")
                            val cityLimitEnable = cityLimitData.getInt("city_limit_enable")
                            val cityLatitude = cityLimitData.getDouble("city_latitude")
                            val cityLongitude = cityLimitData.getDouble("city_longitude")
                            val cityRadius = cityLimitData.getInt("city_radius")
                            val farePerDistance =
                                cityLimitData.getString("city_limit_fare_per_distance")
                            val fareUpto = cityLimitData.getString("city_limit_fare_upto")
                            SessionSave.saveSession(
                                "city_latitude",
                                cityLatitude.toString(),
                                this@TripInprogressActivity
                            )
                            SessionSave.saveSession(
                                "cityLongitude",
                                cityLongitude.toString(),
                                this@TripInprogressActivity
                            )
                            SessionSave.saveSession(
                                "cityRadius",
                                cityRadius.toString(),
                                this@TripInprogressActivity
                            )
                            // Use the data as needed
                            println("citylimit_data: $cityLatitude")
                            println("citylimit_data: $cityLongitude")
                            println("citylimit_data: $cityRadius")
                            println("Latitude: $cityLatitude, Longitude: $cityLongitude")
                        }
                    }


                    if (p_travelstatus.equals("2", ignoreCase = true)) {

                        //i have arrived
                    //    setStopAdapter()
                    }

                    if ((p_driverstatus.equals("F", ignoreCase = true)
                                || p_driverstatus.equals("B", ignoreCase = true) ||
                                p_driverstatus.equals("A", ignoreCase = true))
                        && !p_travelstatus.equals("5", ignoreCase = true)
                    ) {
                        if (p_travelstatus.equals("3", ignoreCase = true)) {
//                            HeadTitle.setText(NC.getString(R.string.waitingpassenger))
//                            view_line_trip.setVisibility(View.VISIBLE)
                            MainActivity.mMyStatus.onstatus = "Arrivd"
                            // setDelayForCancel();
                        } else if (p_travelstatus.equals("2", ignoreCase = true)) {

                            MainActivity.mMyStatus.onstatus = "Complete"
                        } else if (p_travelstatus.equals("9", ignoreCase = true)) {

                            MainActivity.mMyStatus.onstatus = "On"
                            //                                    if (!booking_Type.equals("0")) {
//                                        SessionSave.saveSession("odameter_status", "1", OngoingAct.this);
//                                        showodometer();
//                                    }
                        } else {

                        }
                        p_pickloc = p_pickloc.trim { it <= ' ' }
                        if (p_pickloc.length > 0 && SessionSave.getSession(
                                "Lang",
                                this@TripInprogressActivity
                            ) == "en"
                        ) {
                            p_pickloc =
                                p_pickloc[0].uppercaseChar().toString() + p_pickloc.substring(1)
                            p_droploc = p_droploc.trim { it <= ' ' }
                        }
                        if (p_droploc.length > 0) {
                            p_droploc =
                                p_droploc[0].uppercaseChar().toString() + p_droploc.substring(1)
                        }
                        if (p_name.length > 0) {
                            p_name = p_name[0].uppercaseChar().toString() + p_name.substring(1)
                        }
                        if (p_taxi_speed != null && p_taxi_speed!!.length > 0) {
                            SessionSave.saveSession("taxi_speed", p_taxi_speed, this@TripInprogressActivity)
                        }
                        if (p_notes.length > 0) {
                            p_notes = p_notes[0].uppercaseChar().toString() + p_notes.substring(1)
                        }
//                        txt_pickup.setText(p_pickloc)
//                        txt_drop.setText(p_droploc)
                        MainActivity.mMyStatus.onpickupLocation = p_pickloc
                        MainActivity.mMyStatus.ondropLocation = p_droploc
                        MainActivity.mMyStatus.passengerOndropLocation = p_droploc
                        MainActivity.mMyStatus.onpickupLatitude = p_picklat
                        MainActivity.mMyStatus.onpickupLongitude = p_picklng
                        MainActivity.mMyStatus.ondriverLatitude = p_driverlat
                        MainActivity.mMyStatus.ondriverLongitude = p_driverlng
                        MainActivity.mMyStatus.onpassengerName = p_name
                        MainActivity.mMyStatus.settripId(p_logid)
                        SessionSave.saveSession("trip_id", p_logid, this@TripInprogressActivity)
                       // MainActivity(p_pickloc)
                        MainActivity.mMyStatus.ondropLatitude = p_droplat
                        MainActivity.mMyStatus.ondropLongitude = p_droplng
                        MainActivity.mMyStatus.setdropLoc(p_droploc)
                        MainActivity.mMyStatus.setpassengerId(p_logid)
                        MainActivity.mMyStatus.setphoneNo(p_phone)
                        MainActivity.mMyStatus.onPassengerImage = p_image
                        MainActivity.mMyStatus.setpassengerNotes(p_notes)
                        MainActivity.mMyStatus.setpassengerphone(p_phone)
                        MystatusData.setPickup_notes(pickup_notes)
                        MystatusData.setDropoff_notes(dropoff_notes)
                      //  init()
                        var imagepath: String? = ""
                        if (SessionSave.getSession("p_image", this@TripInprogressActivity) != "") {
                            imagepath = SessionSave.getSession("p_image", this@TripInprogressActivity)
                            Log.i(
                                "Imagepath in session",
                                SessionSave.getSession("p_image", this@TripInprogressActivity)
                            )
                        } else imagepath = SessionSave.getSession("noimage_base", this@TripInprogressActivity)
                        Picasso.get().load(imagepath)
                            .placeholder(getResources().getDrawable(R.drawable.loadingimage))
                            .error(getResources().getDrawable(R.drawable.noimage)).into(profile_avatar)
                    }


                }
            }


        }
    }

    private fun parseStop(path: String): java.util.ArrayList<StopData> {
        val stopDataArrayList = java.util.ArrayList<StopData>()
        stopListData = ArrayList()
        val gson = Gson()
        val type = object : TypeToken<List<StopData?>?>() {}.type
        val stopList = gson.fromJson<java.util.ArrayList<StopData>>(path, type)
        for (i in stopList.indices) {
            stopListData.add(stopList[i].getLatLng())
            stopDataArrayList.add(stopList[i])
        }
      //  pickUpDropView.setData(stopList, "ONGOING", SessionSave.getSession("Lang", this@OngoingAct))
        return stopDataArrayList
    }

    private fun loadUi() {

        val cityName = getCityNameFromLatLng(this, p_picklat.toDouble(), p_picklng.toDouble())
        origin_name.setText(cityName ?: "")
        origin_address.setText(p_pickloc)
        val destCityName = getCityNameFromLatLng(this, p_droplat.toDouble(), p_droplng.toDouble())
        destination_name.setText(destCityName ?: "")
        destination_address.setText(p_droploc)
        profile_name.setText(p_name)
        payment_method_button.setText(payment_type_label)
        loadStatus()


    }

    private fun loadStatus() {
        if (MainActivity.mMyStatus.onstatus.equals("on", ignoreCase = true)) {

            btn_trip_update.setText("I've Arrived")
            if (MainActivity.mMyStatus.onpickupLatitude.length != 0)
                p_latitude = MainActivity.mMyStatus.onpickupLatitude.toDouble()
            if (MainActivity.mMyStatus.onpickupLongitude.length != 0)
                p_longtitude = MainActivity.mMyStatus.onpickupLongitude.toDouble()
            if (MainActivity.mMyStatus.ondropLatitude.length != 0)
                d_latitude = MainActivity.mMyStatus.ondropLatitude.toDouble()
            if (MainActivity.mMyStatus.ondropLongitude.length != 0)
                d_longtitude = MainActivity.mMyStatus.ondropLongitude.toDouble()
            if (MainActivity.mMyStatus.ondriverLatitude.length != 0)
                driver_latitude = MainActivity.mMyStatus.ondriverLatitude.toDouble()
            if (MainActivity.mMyStatus.ondriverLongitude.length != 0)
                driver_longtitude = MainActivity.mMyStatus.ondriverLongitude.toDouble()
            getPickDropLoc()
          //  navigator_layout.setVisibility(View.VISIBLE)
        }
    }

    fun getCityNameFromLatLng(context: Context, latitude: Double, longitude: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].locality // City name like "Coimbatore"
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun getPickDropLoc() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                getLocationViewLocation()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            withContext(Dispatchers.Main) {
                try {
                    googleMap!!.clear()
                    startLocationUpdates()

                    if (mLastLocation != null) {
                        latitude1 = mLastLocation!!.latitude
                        longitude1 = mLastLocation!!.longitude
                        bearing = mLastLocation!!.bearing
                        currentLatLng = LatLng(latitude1, longitude1)
                    }

                    bearing = if (bearing >= 0) bearing + 90 else bearing - 90
                    googleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
                    bearing = 0f

                    pickUpDropMarker()

                    if (driver_latitude != null && driver_latitude != 0.0 &&
                        driver_longtitude != null && driver_longtitude != 0.0
                    ) {
                        currentLatLng = LatLng(driver_latitude!!, driver_longtitude!!)
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        ROUTE_DRAW_ON_START = true
                        mHandler.sendEmptyMessage(1)
                    }, 5000)

                    if (!address.isNullOrEmpty()) {
                        MainActivity.mMyStatus.setOndropLocation(address)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    var mHandler: Handler = object : Handler() {
        private val countDownTimer: CountDownTimer? = null
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> {}
                1 -> try {
                    if (googleMap != null && (!LOCATION_UPDATE_STOPPED || ROUTE_DRAW_ON_START)) {
                        LOCATION_UPDATE_STOPPED = true
                        ROUTE_DRAW_ON_START = false
                        if (MainActivity.mMyStatus.onstatus.equals(
                                "Complete",
                                ignoreCase = true
                            ) || MainActivity.mMyStatus.onstatus.equals("Arrivd", ignoreCase = true)
                        ) {
                            if (route != null) route!!.removePolyLines()
                            pickUpDropMarker()
                            val pp = ArrayList<LatLng>()
                            pp.add(pickupLatLng!!)
                            pp.add(dropLatLng!!)
                            if (viaLatlng != null) pp.add(viaLatlng)
                            if (pickupLatLng != null && pickupLatLng!!.latitude != 0.0 && pickupLatLng!!.longitude != 0.0) {
                                p_marker = googleMap!!.addMarker(
                                    MarkerOptions().position(
                                        LatLng(
                                            pickupLatLng!!.latitude,
                                            pickupLatLng!!.longitude
                                        )
                                    ).title(NC.getString(R.string.pickuploc))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_green))
                                        .draggable(true)
                                )
                            }
                            if (dropLatLng != null && dropLatLng!!.latitude != 0.0 && dropLatLng!!.longitude != 0.0) {
                                d_marker = googleMap!!.addMarker(
                                    MarkerOptions().position(
                                        LatLng(
                                            dropLatLng!!.latitude,
                                            dropLatLng!!.longitude
                                        )
                                    ).title(NC.getString(R.string.droploc))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_red))
                                        .draggable(true)
                                )
                                //                                    route.setUpPolyLine(map, OngoingAct.this, pp.get(0), pp.get(1));
                                Handler().postDelayed({
                                    if (mroute != null && !mroute!!.isEmpty() && !mroute.equals(
                                            "0",
                                            ignoreCase = true
                                        )
                                    ) route!!.drawRouteFromPolyline(
                                        googleMap,
                                        mroute,
                                        stopListData
                                    ) else route!!.setUpPolyLine(
                                        googleMap, this@TripInprogressActivity,
                                        pp[0],
                                        pp[1], stopListData
                                    )
                                }, 500)
                            }
                        } else if (MainActivity.mMyStatus.onstatus.equals(
                                "On",
                                ignoreCase = true
                            )
                        ) {
                            val pp = ArrayList<LatLng>()
                            pp.add(currentLatLng!!)
                            pp.add(pickupLatLng!!)
                            //                                if (viaLatlng != null)
//                                    pp.add(viaLatlng);
                            if (pp != null) {
                                route!!.setUpPolyLine(googleMap, this@TripInprogressActivity, pp[0], pp[1], pp)
                            }
                        } else {
                            val pp = ArrayList<LatLng>()
                            pp.add(pickupLatLng!!)
                            pp.add(dropLatLng!!)
                            if (viaLatlng != null) pp.add(viaLatlng)
                            try {
                                if (pp != null && googleMap != null) {
                                    Handler().postDelayed({
                                        if (mroute != null && !mroute!!.isEmpty() && !mroute.equals(
                                                "0",
                                                ignoreCase = true
                                            )
                                        ) route!!.drawRouteFromPolyline(
                                            googleMap,
                                            mroute,
                                            stopListData
                                        ) else route!!.setUpPolyLine(
                                            googleMap, this@TripInprogressActivity,
                                            pp[0],
                                            pp[1], stopListData
                                        )
                                    }, 500)

//                                        route.setUpPolyLine(map, OngoingAct.this, pp.get(0), pp.get(1));
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                        Handler().postDelayed({ LOCATION_UPDATE_STOPPED = false }, 50000)
                    }
                } catch (e: java.lang.Exception) {
                    sendEmptyMessage(5)
                    e.printStackTrace()
                }

                2 -> {

                    sendEmptyMessage(1)
                }

                3 -> {

                  //  mProgressdialog.dismiss()
                }

                4 -> countDownTimer!!.cancel()
                5 -> try {
                    Handler().postDelayed({
                        if (mroute != null && !mroute!!.isEmpty() && !mroute.equals(
                                "0",
                                ignoreCase = true
                            )
                        ) route!!.drawRouteFromPolyline(googleMap, mroute, stopListData)
                    }, 500)
                    //                        route.setUpPolyLine(map, OngoingAct.this, pickupLatLng, dropLatLng);
//                        route.drawRoute(map, OngoingAct.this, pickupLatLng, dropLatLng, "en", Color.parseColor("#00BFFF"));
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun pickUpDropMarker() {
        try {
            if (googleMap != null) {
                if (p_latitude != null && p_latitude != 0.0 && p_longtitude != null && p_longtitude != 0.0) {
                    if (p_marker != null) p_marker!!.remove()
                    p_marker = googleMap!!.addMarker(
                        MarkerOptions().position(LatLng(p_latitude!!, p_longtitude!!))
                            .title(NC.getString(R.string.pickuploc))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_green))
                            .draggable(true)
                    )
                    pickupLatLng = LatLng(p_latitude!!, p_longtitude!!)
                }
                if (d_latitude != null && d_latitude != 0.0 && d_longtitude != null && d_longtitude != 0.0) {
                    if (d_marker != null) d_marker!!.remove()
                    val px = getResources().getDimensionPixelSize(R.dimen.map_dot_marker_size)
                    val mDotMarkerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(mDotMarkerBitmap)
                    val shape = getResources().getDrawable(R.drawable.cust_progress)
                    shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight())
                    shape.draw(canvas)
                    d_marker = googleMap!!.addMarker(
                        MarkerOptions().position(LatLng(d_latitude!!, d_longtitude!!))
                            .title(NC.getString(R.string.droploc))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_red))
                            .draggable(true)
                    )
                    dropLatLng = LatLng(d_latitude!!, d_longtitude!!)
                }
            }
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
    }

    private fun getLocationViewLocation() {

            val geocoder = Geocoder(this, Locale.getDefault())
            var addresses: List<Address?>? = null
            try {
                if (mLastLocation != null) {
                    addresses = geocoder.getFromLocation(
                        mLastLocation!!.latitude,
                        mLastLocation!!.longitude,
                        1
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
            if (addresses != null && addresses.size > 0) {
                try {
                    address = address!!.replace("null".toRegex(), "").replace(", ,".toRegex(), "")
                        .replace(", ,".toRegex(), "")
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            } else address = ""

    }
    protected fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this@TripInprogressActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this@TripInprogressActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            dialog1 = Utils.alert_view_dialog(this@TripInprogressActivity,
                "",
                NC.getString(R.string.str_loc),
                NC.getString(R.string.yes),
                NC.getString(R.string.no),
                true,
                { dialog: DialogInterface, i: Int ->
                    ActivityCompat.requestPermissions(
                        this@TripInprogressActivity,
                        arrayOf<String>(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ),
                        MY_PERMISSIONS_REQUEST_GPS
                    )
                    dialog.dismiss()
                },
                { dialog: DialogInterface, i: Int -> dialog.dismiss() },
                ""
            )
        } else {
            fusedLocationClient!!.requestLocationUpdates(
                mLocationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        CommonData.mActivitylist.add(this)
        CommonData.current_act = "OngoingAct"
        CommonData.sContext = this
        CommonData.current_trip_accept = 1
    }




}







