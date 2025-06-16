package com.onewaytripcalltaxi.driver.driverdashboard

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import com.onewaytripcalltaxi.driver.MainActivity

import com.onewaytripcalltaxi.driver.R
import com.onewaytripcalltaxi.driver.tracklocation.TrackLocationActivity
import com.onewaytripcalltaxi.driver.WebviewAct
import com.onewaytripcalltaxi.driver.data.CommonData
import com.onewaytripcalltaxi.driver.earningchart.EarningsAct
import com.onewaytripcalltaxi.driver.interfaces.APIResult
import com.onewaytripcalltaxi.driver.more.MoreActivity
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
import java.util.Calendar

class DriverDashboardActivity : MainActivity() {
    var profile_img: ImageView? = null
    var imagefirsttrip: ImageView? = null
    var imagesecondtrip: ImageView? = null
    var imagethirdtrip: ImageView? = null
    var wishes: TextView? = null
    var name_user: TextView? = null
    var today_earning: TextView? = null
    var completed_trips: TextView? = null
    var first_pickup: TextView? = null
    var first_drop: TextView? = null
    var first_tripamt: TextView? = null
    var second_pickup: TextView? = null
    var second_drop: TextView? = null
    var second_tripamt: TextView? = null
    var third_pickup: TextView? = null
    var third_drop: TextView? = null
    var third_tripamt: TextView? = null
    var no_taxi_assigned: TextView? = null
    var dialog1: Dialog? = null
    var trip_detailslay: LinearLayout? = null

    var first_lay: LinearLayout? = null
    var second_lay: LinearLayout? = null
    var third_lay: LinearLayout? = null

    var earnings_lays: RelativeLayout? = null
    var more_lay: LinearLayout? = null
    var booking: LinearLayout? = null
    var see_history: RelativeLayout? = null
    var earnings: LinearLayout? = null
    var profile: LinearLayout? = null
    var no_taxi_view: LinearLayout? = null
    var ui_data_view: NestedScrollView? = null
    var track_location: LinearLayout? = null
    var nonactiityobj = NonActivity()
    override fun setLayout(): Int {
        return R.layout.activity_driver_dashboard
    }

    override fun Initialize() {

        profile_img = findViewById(R.id.profile_img)
        imagefirsttrip = findViewById(R.id.imagefirsttrip)
        imagesecondtrip = findViewById(R.id.imagesecondtrip)
        imagethirdtrip = findViewById(R.id.imagethirdtrip)
        wishes = findViewById(R.id.wishes)
        name_user = findViewById(R.id.name_user)
        today_earning = findViewById(R.id.today_earning)
        completed_trips = findViewById(R.id.completed_trips)
        first_pickup = findViewById(R.id.first_pickup)
        first_drop = findViewById(R.id.first_drop)
        first_tripamt = findViewById(R.id.first_tripamt)
        second_pickup = findViewById(R.id.second_pickup)
        second_drop = findViewById(R.id.second_drop)
        second_tripamt = findViewById(R.id.second_tripamt)
        third_pickup = findViewById(R.id.third_pickup)
        third_drop = findViewById(R.id.third_drop)
        third_tripamt = findViewById(R.id.third_tripamt)
        trip_detailslay = findViewById(R.id.trip_detailslay)
        first_lay = findViewById(R.id.firstlay)
        second_lay = findViewById(R.id.second_lay)
        third_lay = findViewById(R.id.third_lay)

        earnings_lays = findViewById(R.id.earnings_lays)
        more_lay = findViewById(R.id.more_lay)
        booking = findViewById(R.id.booking)
        see_history = findViewById(R.id.see_history)
        earnings = findViewById(R.id.earnings)
        profile = findViewById(R.id.profile)
        track_location = findViewById(R.id.track_location)
        no_taxi_assigned = findViewById(R.id.no_taxi_assigned)
        no_taxi_view = findViewById(R.id.no_taxi_view)
        ui_data_view = findViewById(R.id.ui_data_view)



        name_user?.text = SessionSave.getSession("Name", this@DriverDashboardActivity)

        val c = Calendar.getInstance()
        val timeOfDay = c[Calendar.HOUR_OF_DAY]
        if (timeOfDay >= 0 && timeOfDay < 12) {
            wishes!!.text = "Good Morning!"
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            wishes!!.text = "Good Afternoon!"
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            wishes!!.text = "Good Evening!"
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            wishes!!.text = "Good Night!"
        }



        more_lay!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@DriverDashboardActivity, MoreActivity::class.java)
            startActivity(intent)
        })
        track_location!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@DriverDashboardActivity, TrackLocationActivity::class.java)
            startActivity(intent)
        })
        earnings_lays!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@DriverDashboardActivity, EarningsAct::class.java)
           // val intent = Intent(this@DriverDashboardActivity, SettlementLists::class.java)
            startActivity(intent)
        })

        callProfileApi()
        callRecentTripDetails()
    }

    private fun callRecentTripDetails() {
        try {
            val j = JSONObject()
            j.put("driver_id", SessionSave.getSession("Id", this@DriverDashboardActivity))
            j.put(
                "driver_type",
                SessionSave.getSession("driver_type", this@DriverDashboardActivity)
            )
            j.put(
                "device_token",
                SessionSave.getSession(CommonData.DEVICE_TOKEN, this@DriverDashboardActivity)
            )
            val pro_url = "type=driver_recent_trip_list"
            GetTripData(pro_url, j)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callProfileApi() {
        val j = JSONObject()
        try {
            j.put("userid", SessionSave.getSession("Id", this@DriverDashboardActivity))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val pro_url = "type=driver_profile"
        GetProfileData(pro_url, j)
    }

    inner class GetTripData(url: String, data: JSONObject) : APIResult {
        init {
            APIService_Retrofit_JSON(this@DriverDashboardActivity, this, data, false).execute(url)
        }

        override fun getResult(isSuccess: Boolean, result: String?) {

            if (isSuccess) {
                val json = JSONObject(result)
                if (json.has("trip_list")) {
                    val details1 = json.getJSONArray("trip_list")
                    println("recent_details 5  $details1")
                }

                if (json.getInt("status") == 1 || json.getInt("status") == -4 || json.getInt("status") == -2 || json.getInt(
                        "status"
                    ) == -3
                ) {

                    loadTripData(json)
                    if (json.has("total_amount")) {
                        today_earning!!.text = SessionSave.getSession(
                            "site_currency",
                            this@DriverDashboardActivity
                        ) + "" + json.getString("total_amount")
                        completed_trips!!.text =
                            json.getString("total_trips") + " " + "Rides"

                        enableDrivertoActiveState()
                    }

                    if (json.getInt("status") == -4) {
                        dialog1 = Utils.alert_view(
                            this@DriverDashboardActivity,
                            "" + NC.getString(R.string.message),
                            "" + json.getString("message"),
                            "" + NC.getString(R.string.ok),
                            "",
                            true,
                            this@DriverDashboardActivity,
                            "3"
                        )
                    } else if (json.getInt("status") == -2) {
                        Systems.out.println("myCode_______" + "in -2 condition")
                        dialog1 = Utils.alert_view(
                            this@DriverDashboardActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),
                            NC.getString(R.string.ok),
                            NC.getString(R.string.cancel),
                            false,
                            this@DriverDashboardActivity,
                            "1"
                        )
                    } else if (json.getInt("status") == -3) {
                        Systems.out.println("myCode_______" + "in -3 condition")
                        dialog1 = Utils.alert_view_dialog(
                            this@DriverDashboardActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),
                            NC.getString(R.string.ok),
                            NC.getString(R.string.cancel),
                            false,
                            { dialogInterface, i -> //
                                dialogInterface.dismiss()
                                val intent1 =
                                    Intent(this@DriverDashboardActivity, WebviewAct::class.java)
                                intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                intent1.putExtra("fromMyStatus", "YES")
                                intent1.putExtra("type", "1")
                                startActivity(intent1)
                                finish()
                            },
                            { dialogInterface, i -> dialogInterface.dismiss() },
                            ""
                        )
                    }
                }
                else if (json.getInt("status") == -1) {
                    enableDrivertoActiveState()
                }

                else if (json.getInt("status") == 10) {
                    trip_detailslay!!.visibility = View.VISIBLE
                    SessionSave.saveSession("driver_type", "D", this@DriverDashboardActivity)
                    SessionSave.saveSession("account_activate", false, this@DriverDashboardActivity)
                    AccountNotActivated(SessionSave.getSession("account_message", this@DriverDashboardActivity))
                } else if (json.getInt("status") == -4) {
                    dialog1 = Utils.alert_view(
                        this@DriverDashboardActivity,
                        "" + NC.getString(R.string.message),
                        "" + json.getString("message"),
                        "" + NC.getString(R.string.ok),
                        "",
                        true,
                        this@DriverDashboardActivity,
                        "3"
                    )
                    trip_detailslay!!.visibility = View.VISIBLE
                } else if (json.getInt("status") == 40) {
                    enableDrivertoActiveState()
                    dialog1 = Utils.alert_view(
                        this@DriverDashboardActivity,
                        "" + NC.getString(R.string.message),
                        "" + json.getString("message"),
                        "" + NC.getString(R.string.ok),
                        "",
                        true,
                        this@DriverDashboardActivity,
                        "3"
                    )
                    trip_detailslay!!.visibility = View.VISIBLE
                }


            }
        }

        private fun loadTripData(json: JSONObject) {
            val details = json.getJSONArray("trip_list")
            if (details.length() > 0) {
                for (i in 0 until details.length()) {
                    if(i == 0)
                    {
                        first_pickup!!.text = details.getJSONObject(i).getString("pickup_location")

                        first_drop!!.text = details.getJSONObject(i).getString("drop_location")
                        first_tripamt!!.text =
                            SessionSave.getSession(
                                "site_currency",
                                this@DriverDashboardActivity
                            ) + "" + details.getJSONObject(i).getString("fare")
                        Picasso.get().load(details.getJSONObject(i).getString("profile_image"))
                            .placeholder(
                                resources.getDrawable(R.drawable.loadingimage)
                            ).error(
                                resources.getDrawable(R.drawable.noimage)
                            ).into(imagefirsttrip)
                    }
                    if(i == 1)
                    {

                        second_pickup!!.text = details.getJSONObject(i).getString("pickup_location")
                            .trim { it <= ' ' }
                        second_drop!!.text = details.getJSONObject(i).getString("drop_location").trim { it <= ' ' }
                        second_tripamt!!.text =
                            SessionSave.getSession(
                                "site_currency",
                                this@DriverDashboardActivity
                            ) + "" + details.getJSONObject(i).getString("fare").trim { it <= ' ' }
                        Picasso.get().load(details.getJSONObject(i).getString("profile_image"))
                            .placeholder(
                                resources.getDrawable(R.drawable.loadingimage)
                            ).error(
                                resources.getDrawable(R.drawable.noimage)
                            ).into(imagesecondtrip)

                    }
                    if(i == 2)
                    {
                        third_pickup!!.text = details.getJSONObject(i).getString("pickup_location")
                            .trim { it <= ' ' }
                        third_drop!!.text = details.getJSONObject(i).getString("drop_location").trim { it <= ' ' }
                        third_tripamt!!.text =
                            SessionSave.getSession(
                                "site_currency",
                                this@DriverDashboardActivity
                            ) + " " + details.getJSONObject(i).getString("fare").trim { it <= ' ' }
                        //
                        //
                        Picasso.get().load(details.getJSONObject(i).getString("profile_image"))
                            .placeholder(resources.getDrawable(R.drawable.loadingimage)).error(
                                resources.getDrawable(R.drawable.noimage)
                            ).into(imagethirdtrip)

                    }
                }

                if (details.length()==1)
                {
                    trip_detailslay!!.visibility = View.VISIBLE
                    first_lay!!.visibility = View.VISIBLE
                    second_lay!!.visibility = View.GONE
                    third_lay!!.visibility = View.GONE

                }else if (details.length()==2)
                {
                    trip_detailslay!!.visibility = View.VISIBLE
                    first_lay!!.visibility = View.VISIBLE
                    second_lay!!.visibility = View.VISIBLE
                    third_lay!!.visibility = View.GONE
                }
                else if (details.length()==3)
                {
                    trip_detailslay!!.visibility = View.VISIBLE
                    first_lay!!.visibility = View.VISIBLE
                    second_lay!!.visibility = View.VISIBLE
                    third_lay!!.visibility = View.VISIBLE
                }
                else{
                    trip_detailslay!!.visibility = View.GONE
                }
            }
            else{
                trip_detailslay!!.visibility = View.GONE
            }
        }

    }

    private fun enableDrivertoActiveState() {
        if (SessionSave.getSession("shift_status", this@DriverDashboardActivity) == "IN") {
            nonactiityobj.startServicefromNonActivity(this@DriverDashboardActivity)
        }
    }
    fun AccountNotActivated(Message: String?) {

        SessionSave.saveSession("account_activate", false, this@DriverDashboardActivity)
        no_taxi_view!!.visibility = View.VISIBLE
        ui_data_view!!.visibility = View.GONE
        val window = window
        no_taxi_assigned!!.text = Message
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.button_accept)
        }
        val i = Intent(this@DriverDashboardActivity, LocationUpdate::class.java)
        stopService(i)
    }
    inner class GetProfileData(url: String, data: JSONObject) : APIResult {

        init {
            APIService_Retrofit_JSON(this@DriverDashboardActivity, this, data, false).execute(url)
        }

        override fun getResult(isSuccess: Boolean, result: String?) {
            if (isSuccess) {
                val json = JSONObject(result)
                if (json.getInt("status") == 1) {
                    val details = json.getJSONObject("detail")
                    val imgPath = details.getString("main_image_path").trim { it <= ' ' }

                    if (imgPath != null && imgPath.length > 0) {
                        Picasso.get().load(imgPath)
                            .placeholder(resources.getDrawable(R.drawable.loadingimage)).error(
                                resources.getDrawable(R.drawable.noimage)
                            ).into(profile_img)
                    }
                    SessionSave.saveSession(
                        "taxi_model",
                        details.getString("taxi_model"),
                        this@DriverDashboardActivity
                    )
                    SessionSave.saveSession(
                        "taxi_no",
                        details.getString("taxi_no"),
                        this@DriverDashboardActivity
                    )
                    SessionSave.saveSession(
                        "taxi_map_from",
                        details.getString("taxi_map_from"),
                        this@DriverDashboardActivity
                    )
                    SessionSave.saveSession(
                        "taxi_map_to",
                        details.getString("taxi_map_to"),
                        this@DriverDashboardActivity
                    )

                    SessionSave.saveSession(
                        "driver_lic_front",
                        details.getString("driver_licence_path"),
                        this@DriverDashboardActivity
                    )
                    SessionSave.saveSession(
                        "driver_lic_back",
                        details.getString("driver_licence_back_side_path"),
                        this@DriverDashboardActivity
                    )
                }
            }
        }
    }

}




