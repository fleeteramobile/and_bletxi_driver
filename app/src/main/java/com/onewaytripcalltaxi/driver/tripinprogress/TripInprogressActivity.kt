package com.onewaytripcalltaxi.driver.tripinprogress

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.onewaytripcalltaxi.driver.FarecalcAct
import com.onewaytripcalltaxi.driver.MainActivity
import com.onewaytripcalltaxi.driver.OngoingAct.getOdometer
import com.onewaytripcalltaxi.driver.R
import com.onewaytripcalltaxi.driver.StreetPickUpAct
import com.onewaytripcalltaxi.driver.TripHistoryAct
import com.onewaytripcalltaxi.driver.data.CommonData
import com.onewaytripcalltaxi.driver.data.MystatusData
import com.onewaytripcalltaxi.driver.interfaces.APIResult
import com.onewaytripcalltaxi.driver.interfaces.LocalDistanceInterface
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON
import com.onewaytripcalltaxi.driver.service.LocationUpdate
import com.onewaytripcalltaxi.driver.service.NonActivity
import com.onewaytripcalltaxi.driver.utils.NC
import com.onewaytripcalltaxi.driver.utils.SessionSave
import com.onewaytripcalltaxi.driver.utils.Systems
import com.onewaytripcalltaxi.driver.utils.Utils
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.util.Locale

class TripInprogressActivity : AppCompatActivity(), OnMapReadyCallback, LocalDistanceInterface,
    OnCameraMoveStartedListener {

    private val nonactiityobj = NonActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_trip_inprogress)

        loadTripDetails()

    }

    private fun loadTripDetails() {
        if (SessionSave.getSession("trip_id", this@TripInprogressActivity) != "") {
            val j = JSONObject()
            j.put("trip_id", SessionSave.getSession("trip_id", this@TripInprogressActivity))
            val Url = "type=get_trip_detail"
            Tripdetails(Url, j)
            nonactiityobj.startServicefromNonActivity(this@TripInprogressActivity)
        }
    }

    override fun onMapReady(p0: GoogleMap) {

    }

    override fun haversineResult(success: Boolean?) {

    }

    override fun onCameraMoveStarted(p0: Int) {

    }


    private class Tripdetails(url: String?, data: JSONObject?) : APIResult {
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
        private var p_taxi_speed: String? = ""
        private var pickup_notes = ""
        private var dropoff_notes = ""

        init {
            try {
                if (isOnline()) {
                    butt_onboard.setEnabled(false)
                    APIService_Retrofit_JSON(this@OngoingAct, this, data, false).execute(url)
                } else {
                    dialog1 = Utils.alert_view(
                        this@OngoingAct,
                        NC.getString(R.string.message),
                        NC.getString(R.string.check_net_connection),
                        NC.getString(R.string.ok),
                        "",
                        true,
                        this@OngoingAct,
                        "4"
                    )
                }
            } catch (e: Exception) {
                butt_onboard.setEnabled(true)
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            butt_onboard.setEnabled(true)
            try {
                if (isSuccess) {
                    tripInfo.setVisibility(View.VISIBLE)
                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {
                        val detail = json.getJSONObject("detail")
                        if (detail.getString("street_pickup_trip").trim { it <= ' ' } == "1") {
                            startActivity(Intent(this@OngoingAct, StreetPickUpAct::class.java))
                            Toast.makeText(
                                this@OngoingAct,
                                NC.getString(R.string.you_are_in_trip),
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            speedTxt.setText(
                                String.format(
                                    Locale.UK,
                                    "%.2f",
                                    LocationUpdate.speed
                                ) + metricss.lowercase(
                                    Locale.getDefault()
                                )
                            )
                            waitingTimeTxt.setText(
                                String.format(
                                    Locale.UK, CommonData.getDateForWaitingTime(
                                        SessionSave.getWaitingTime(this@OngoingAct)
                                    )
                                )
                            )
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
                            SessionSave.saveSession(
                                "status",
                                detail.getString("driver_status"),
                                this@OngoingAct
                            )
                            SessionSave.saveSession(
                                "Metric",
                                detail.getString("metric"),
                                this@OngoingAct
                            )
                            SessionSave.saveSession("p_image", p_image, this@OngoingAct)
                            SessionSave.saveSession("c", p_travelstatus, this@OngoingAct)
                            if (detail.has(CommonData.IS_CORPORATE_BOOKING)) {
                                SessionSave.saveSession(
                                    CommonData.IS_CORPORATE_BOOKING,
                                    detail.getString("corporate_booking"),
                                    this@OngoingAct
                                )
                            }
                            if (json.getJSONObject("detail").getString("approx_fare")
                                    .equals("0", ignoreCase = true)
                            ) {
                                estimatelay.setVisibility(View.GONE)
                            } else {
                                estimatelay.setVisibility(View.GONE)
                                estimateTxt.setText(
                                    NC.getString(R.string.Estimated) + " : " + SessionSave.getSession(
                                        "site_currency",
                                        this@OngoingAct
                                    ) + " " + json.getJSONObject("detail").getString("approx_fare")
                                )
                            }
                            if (json.getJSONObject("detail").has("route_path")) mroute =
                                json.getJSONObject("detail").getString("route_path")
                            if (json.getJSONObject("detail").has("stops")) stops =
                                json.getJSONObject("detail").getJSONArray("stops")
                            if (stops != null && stops.length() > 0) stopLists =
                                parseStop(stops.toString()) else stopLists = createPickAndStopView(
                                p_pickloc,
                                p_picklat,
                                p_picklng,
                                p_droploc,
                                p_droplat,
                                p_droplng
                            )
                            if (json.getJSONObject("detail").has("manual_waiting_time")) {
                                SessionSave.saveSession(
                                    CommonData.WAITING_TIME_MANUAL,
                                    json.getJSONObject("detail")
                                        .getString("manual_waiting_time") == "1", this@OngoingAct
                                )
                            }
                            if (SessionSave.getSession(
                                    CommonData.WAITING_TIME_MANUAL,
                                    this@OngoingAct,
                                    false
                                )
                            ) {
                                ssWaitingTime_img.setVisibility(View.VISIBLE)
                            } else {
                                ssWaitingTime_img.setVisibility(View.GONE)
                            }
                            if (json.getJSONObject("detail").has("enable_os_waiting_fare")) {
                                if (json.getJSONObject("detail")
                                        .getString("enable_os_waiting_fare") == "0"
                                ) {
                                    SessionSave.saveSession("isonewaytrip", "no", this@OngoingAct)
                                    enable_os_waiting_fare = false
                                } else if (json.getJSONObject("detail")
                                        .getString("enable_os_waiting_fare") == "1"
                                ) {
                                    SessionSave.saveSession("isonewaytrip", "yes", this@OngoingAct)
                                    enable_os_waiting_fare = true
                                }
                            } else {
                                SessionSave.saveSession("isonewaytrip", "no", this@OngoingAct)
                                enable_os_waiting_fare = false
                            }
                            if (json.getJSONObject("detail").has("is_on_my_way_trip")) {
                                if (json.getJSONObject("detail")
                                        .getString("is_on_my_way_trip") == "0"
                                ) trip_types.setText(
                                    NC.getString(R.string.triptype) + " : " + NC.getString(
                                        R.string.trip_normal
                                    )
                                ) else if (json.getJSONObject("detail")
                                        .getString("is_on_my_way_trip") == "1"
                                ) trip_types.setText(
                                    NC.getString(R.string.triptype) + " : " + NC.getString(
                                        R.string.trip_onmyway
                                    )
                                )
                            }
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
                                        this@OngoingAct
                                    )
                                    SessionSave.saveSession(
                                        "cityLongitude",
                                        cityLongitude.toString(),
                                        this@OngoingAct
                                    )
                                    SessionSave.saveSession(
                                        "cityRadius",
                                        cityRadius.toString(),
                                        this@OngoingAct
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
                                setStopAdapter()
                            }
                            Systems.out.println(
                                "statusss" + p_driverstatus + "__" + p_travelstatus + "___" + SessionSave.getSession(
                                    CommonData.IS_CORPORATE_BOOKING, this@OngoingAct
                                )
                            )
                            if ((p_driverstatus.equals("F", ignoreCase = true)
                                        || p_driverstatus.equals("B", ignoreCase = true) ||
                                        p_driverstatus.equals("A", ignoreCase = true))
                                && !p_travelstatus.equals("5", ignoreCase = true)
                            ) {
                                if (p_travelstatus.equals("3", ignoreCase = true)) {
                                    HeadTitle.setText(NC.getString(R.string.waitingpassenger))
                                    view_line_trip.setVisibility(View.VISIBLE)
                                    MainActivity.mMyStatus.onstatus = "Arrivd"
                                    // setDelayForCancel();
                                } else if (p_travelstatus.equals("2", ignoreCase = true)) {
                                    card_view_pickup.setCardElevation(0f)
                                    card_view_pickup.setUseCompatPadding(false)
                                    card_view_pickup.setRadius(0f)
                                    view_line_trip.setVisibility(View.VISIBLE)
                                    HeadTitle.setText(NC.getString(R.string.tripprogress))
                                    tripinprogress_lay.setVisibility(View.GONE)
                                    pickup_drop_lay.setVisibility(View.GONE)
                                    contact_lay.setVisibility(View.GONE)
                                    cancellay.setVisibility(View.VISIBLE)
                                    contact_txt.setVisibility(View.VISIBLE)
                                    phonelay.setVisibility(View.GONE)

                                    //  tripDetails_lay.setBackgroundColor(getResources().getColor(R.color.white));
                                    trip_view.setVisibility(View.VISIBLE)
                                    MainActivity.mMyStatus.onstatus = "Complete"
                                } else if (p_travelstatus.equals("9", ignoreCase = true)) {
                                    view_line_trip.setVisibility(View.VISIBLE)
                                    HeadTitle.setText(NC.getString(R.string.tripdetails))
                                    MainActivity.mMyStatus.onstatus = "On"
                                    //                                    if (!booking_Type.equals("0")) {
//                                        SessionSave.saveSession("odameter_status", "1", OngoingAct.this);
//                                        showodometer();
//                                    }
                                } else {
                                    HeadTitle.setText(NC.getString(R.string.tripdetails))
                                }
                                p_pickloc = p_pickloc.trim { it <= ' ' }
                                if (p_pickloc.length > 0 && SessionSave.getSession(
                                        "Lang",
                                        this@OngoingAct
                                    ) == "en"
                                ) {
                                    p_pickloc = p_pickloc[0].uppercaseChar()
                                        .toString() + p_pickloc.substring(1)
                                    p_droploc = p_droploc.trim { it <= ' ' }
                                }
                                if (p_droploc.length > 0) {
                                    p_droploc = p_droploc[0].uppercaseChar()
                                        .toString() + p_droploc.substring(1)
                                }
                                if (p_name.length > 0) {
                                    p_name =
                                        p_name[0].uppercaseChar().toString() + p_name.substring(1)
                                }
                                if (p_taxi_speed != null && p_taxi_speed!!.length > 0) {
                                    SessionSave.saveSession(
                                        "taxi_speed",
                                        p_taxi_speed,
                                        this@OngoingAct
                                    )
                                }
                                if (p_notes.length > 0) {
                                    p_notes =
                                        p_notes[0].uppercaseChar().toString() + p_notes.substring(1)
                                }
                                txt_pickup.setText(p_pickloc)
                                txt_drop.setText(p_droploc)
                                MainActivity.mMyStatus.onpickupLocation = p_pickloc
                                MainActivity.mMyStatus.ondropLocation = p_droploc
                                MainActivity.mMyStatus.passengerOndropLocation = p_droploc
                                MainActivity.mMyStatus.onpickupLatitude = p_picklat
                                MainActivity.mMyStatus.onpickupLongitude = p_picklng
                                MainActivity.mMyStatus.ondriverLatitude = p_driverlat
                                MainActivity.mMyStatus.ondriverLongitude = p_driverlng
                                MainActivity.mMyStatus.onpassengerName = p_name
                                MainActivity.mMyStatus.settripId(p_logid)
                                SessionSave.saveSession("trip_id", p_logid, this@OngoingAct)
                                MainActivity.mMyStatus.setpickupLoc(p_pickloc)
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
                                init()
                                var imagepath: String? = ""
                                if (SessionSave.getSession("p_image", this@OngoingAct) != "") {
                                    imagepath = SessionSave.getSession("p_image", this@OngoingAct)
                                    Log.i(
                                        "Imagepath in session",
                                        SessionSave.getSession("p_image", this@OngoingAct)
                                    )
                                } else imagepath =
                                    SessionSave.getSession("noimage_base", this@OngoingAct)
                                Picasso.get().load(imagepath)
                                    .placeholder(getResources().getDrawable(R.drawable.loadingimage))
                                    .error(getResources().getDrawable(R.drawable.noimage))
                                    .into(proimg)
                            } else if (p_driverstatus.equals(
                                    "A",
                                    ignoreCase = true
                                ) && p_travelstatus.equals("5", ignoreCase = true)
                            ) {
                                if (SessionSave.getSession(
                                        CommonData.IS_CORPORATE_BOOKING,
                                        this@OngoingAct
                                    ) == "1"
                                ) {
//                                    setFareCalculatorScreen(result);
                                    CompleteSuccessClick()
                                } else {
                                    val i = Intent(
                                        this@OngoingAct,
                                        FarecalcAct::class.java
                                    )
                                    i.putExtra("from", "pending")
                                    i.putExtra("lat", detail.getString("drop_latitude"))
                                    i.putExtra("lon", detail.getString("drop_longitude"))
                                    i.putExtra("distance", detail.getString("distance"))
                                    i.putExtra("waitingHr", detail.getString("waiting_time"))
                                    i.putExtra("drop_location", detail.getString("drop_location"))
                                    i.putExtra("stopList", detail.getJSONArray("stops").toString())
                                    i.putExtra(
                                        "corporate",
                                        SessionSave.getSession(
                                            CommonData.IS_CORPORATE_BOOKING,
                                            this@OngoingAct
                                        )
                                    )
                                    startActivity(i)
                                    overridePendingTransition(0, 0)
                                    finish()
                                }
                            } else {
                                Systems.out.println("haiiiiiiiTriphistory" + p_driverstatus + "___" + p_travelstatus)
                                ShowToast(this@OngoingAct, NC.getString(R.string.you_are_in_trip))
                                val i = Intent(
                                    this@OngoingAct,
                                    TripHistoryAct::class.java
                                )
                                startActivity(i)
                                finish()
                            }
                            tripInfo.post(Runnable {
                                layoutheight = tripInfo.getHeight() - 20
                                if (map != null) {
                                    map.setPadding(0, layoutheight, 0, 120)
                                }
                            })
                        }
                        nodataTxt.setVisibility(View.GONE)
                    } else {
                        val i = Intent(
                            this@OngoingAct,
                            TripHistoryAct::class.java
                        )
                        startActivity(i)
                        finish()
                    }
                } else {
                    runOnUiThread(Runnable {
                        ShowToast(
                            this@OngoingAct,
                            NC.getString(R.string.server_error)
                        )
                    })
                    val i = Intent(
                        this@OngoingAct,
                        TripHistoryAct::class.java
                    )
                    startActivity(i)
                    finish()
                }
            } catch (e: Exception) {
                // TODO: handle exception
                Systems.out.println("pass---j$e")
                e.printStackTrace()
                val i = Intent(this@OngoingAct, TripHistoryAct::class.java)
                startActivity(i)
                finish()
            } finally {
                if (booking_Type == "2" || booking_Type == "3") {
                    val j = JSONObject()
                    try {
                        j.put("trip_id", SessionSave.getSession("trip_id", this@OngoingAct))
                        val Url = "type=get_odometer"
                        getOdometer(Url, j)
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                }
            }
        }
    }


}

