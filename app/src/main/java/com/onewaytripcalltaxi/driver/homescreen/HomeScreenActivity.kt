package com.onewaytripcalltaxi.driver.homescreen

import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.onewaytripcalltaxi.driver.MainActivity
import com.onewaytripcalltaxi.driver.OngoingAct
import com.onewaytripcalltaxi.driver.R
import com.onewaytripcalltaxi.driver.UserLoginAct
import com.onewaytripcalltaxi.driver.WebviewAct
import com.onewaytripcalltaxi.driver.data.CommonData
import com.onewaytripcalltaxi.driver.interfaces.APIResult
import com.onewaytripcalltaxi.driver.interfaces.ClickInterface
import com.onewaytripcalltaxi.driver.profile.ProfileActivity
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON_NoProgress
import com.onewaytripcalltaxi.driver.service.FirebaseService
import com.onewaytripcalltaxi.driver.service.LocationUpdate
import com.onewaytripcalltaxi.driver.service.NonActivity
import com.onewaytripcalltaxi.driver.triplist.CommonTripHistory
import com.onewaytripcalltaxi.driver.tripnotification.TripNotificationActivity
import com.onewaytripcalltaxi.driver.utils.CToast
import com.onewaytripcalltaxi.driver.utils.ListViewEX
import com.onewaytripcalltaxi.driver.utils.NC
import com.onewaytripcalltaxi.driver.utils.NetworkStatus.isOnline
import com.onewaytripcalltaxi.driver.utils.SessionSave
import com.onewaytripcalltaxi.driver.utils.Systems
import com.onewaytripcalltaxi.driver.utils.Utils
import org.json.JSONException
import org.json.JSONObject

class HomeScreenActivity : AppCompatActivity(), ClickInterface, NavigationView.OnNavigationItemSelectedListener {
    var alert_bundle: Bundle? = null
    var alert_msg: String? = null
    var alert_trip_id: String? = null
    private var scheduleAlert = false
    var dialog1: Dialog? = null
    private var scheduleTripId = ""
    var nonactiityobj = NonActivity()
    var recentListMessage = ""
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        navigationView.setNavigationItemSelectedListener(this)

        // You might want to use a Toolbar instead of just an ImageView for the menu icon
        // If you have a Toolbar, set it as the support action bar:
        // setSupportActionBar(findViewById(R.id.your_toolbar_id))
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) // Define these strings in strings.xml
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val profileImageView = findViewById<ShapeableImageView>(R.id.profileImageView)
        val earningCard = findViewById<CardView>(R.id.earningCard)
        val menuIcon = findViewById<ImageView>(R.id.menuIcon)

        menuIcon.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }


        earningCard.setOnClickListener {
            startActivity(Intent(this, TripNotificationActivity::class.java))
        }

        Glide.with(this)
            .load("https://fastly.picsum.photos/id/292/200/300.jpg?hmac=zm-TXplXe70N7LGm2HWu9iOPXoBtQvwyhAF2CSj0ccs") // Your drawable resource
            .into(profileImageView)

        try {
            alert_bundle = intent.extras
            if (alert_bundle != null) {
                alert_msg = alert_bundle!!.getString("alert_message")
                alert_trip_id = alert_bundle!!.getString("alert_trip_id")
                val alertSchedule = alert_bundle!!.getString("alert_schedule")
                if (alertSchedule != null && alertSchedule == "1") {
                    scheduleAlert = true
                }
            }
            if (scheduleAlert) {
                if (alert_msg != null && alert_msg!!.length != 0) {
                    bookLaterNotificationAlert(alert_msg!!)
                }
            } else {
                if (alert_msg != null && alert_msg!!.length != 0) dialog1 = Utils.alert_view(
                    this@HomeScreenActivity,
                    NC.getString(R.string.message),
                    alert_msg,
                    NC.getString(R.string.ok),
                    "",
                    true,
                    this@HomeScreenActivity,
                    ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        if (SessionSave.getSession("driver_type", this@HomeScreenActivity)
                .equals("D", ignoreCase = true)
        ) {
            AccountNotActivated(SessionSave.getSession("account_message", this@HomeScreenActivity))
        } else {
            SessionSave.saveSession("account_activate", true, this@HomeScreenActivity)
            // no_taxi_view.setVisibility(View.GONE)
            val window = window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = getResources().getColor(R.color.header_text)
            }
            Systems.out.println("nan---nOTyET Activated")
        }
    }

    fun AccountNotActivated(Message: String?) {


        //   no_taxi_view.setVisibility(View.VISIBLE)
        val window = window
        //  no_taxi_assign.setText(Message)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getResources().getColor(R.color.button_accept)
        }
        val i = Intent(this@HomeScreenActivity, LocationUpdate::class.java)
        stopService(i)
    }





    private fun bookLaterNotificationAlert(bookLaterDetails: String) {
        val bookLaterView = View.inflate(this@HomeScreenActivity, R.layout.booklater_alert, null)
        val bookLaterDialog = Dialog(this@HomeScreenActivity, R.style.dialogwinddow)
        bookLaterDialog.setContentView(bookLaterView)
        bookLaterDialog.setCancelable(false)
        bookLaterDialog.show()
        val listViewEX = bookLaterView.findViewById<ListViewEX>(R.id.testLay)
        listViewEX.setData(
            getStopArray(bookLaterDetails),
            "SCHEDULE",
            SessionSave.getSession("Lang", this@HomeScreenActivity)
        )
        bookLaterView.findViewById<View>(R.id.btnAccept).setOnClickListener { view: View? ->
            bookLaterDialog.dismiss()
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(FirebaseService.BOOKLATER_NOTIFICATION_ID)
            try {
                val j = JSONObject()
                j.put("trip_id", scheduleTripId)
                j.put(
                    "driver_id",
                    SessionSave.getSession("Id", this@HomeScreenActivity)
                )
                val scheduleTripUrl = "type=schedule_accept_trip"
                ScheduleTrip(scheduleTripUrl, j)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        bookLaterView.findViewById<View>(R.id.btnDecline).setOnClickListener { view: View? ->
            bookLaterDialog.dismiss()
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(FirebaseService.BOOKLATER_NOTIFICATION_ID)
            try {
                val j = JSONObject()
                j.put("pass_logid", scheduleTripId)
                j.put(
                    "driver_id",
                    SessionSave.getSession("Id", this@HomeScreenActivity)
                )
                j.put(
                    "taxi_id",
                    SessionSave.getSession("taxi_id", this@HomeScreenActivity)
                )
                j.put(
                    "company_id",
                    SessionSave.getSession(
                        "company_id",
                        this@HomeScreenActivity
                    )
                )
                j.put("driver_reply", "C")
                j.put("field", "")
                j.put("flag", "1")
                if (MainActivity.mMyStatus.onstatus
                        .equals("Arrivd", ignoreCase = true)
                ) j.put("driver_arrived", 1) else j.put("driver_arrived", 0)
                j.put("schedule", "1")
                val canceltrip_url = "type=driver_reply"
                CancelTrip(canceltrip_url, j)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getStopArray(alertMsg: String): ArrayList<HashMap<String, String>> {
        val arrayList = ArrayList<HashMap<String, String>>()
        try {
            val jsonObject = JSONObject(alertMsg)
            if (jsonObject.has("info")) {
                val infoJsonObject = jsonObject.getJSONObject("info")
                val iter = infoJsonObject.keys()
                while (iter.hasNext()) {
                    val h2 = HashMap<String, String>()
                    val key = iter.next()
                    try {
                        val value = infoJsonObject[key]
                        h2["KEY"] = key
                        h2["VALUE"] = value.toString()
                        scheduleTripId = infoJsonObject.getString("trip_id")
                        arrayList.add(h2)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return arrayList
    }


    inner class ScheduleTrip internal constructor(url: String?, data: JSONObject?) :
        APIResult {
        init {
            try {
                if (isOnline(this@HomeScreenActivity)) {
                    APIService_Retrofit_JSON(
                        this@HomeScreenActivity,
                        this,
                        data,
                        false
                    ).execute(url)
                } else {
                    Utils.alert_view(
                        this@HomeScreenActivity,
                        NC.getString(R.string.message),
                        NC.getString(R.string.check_net_connection),
                        NC.getString(R.string.ok),
                        "",
                        true,
                        this@HomeScreenActivity,
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
                        CToast.ShowToast(this@HomeScreenActivity, json.getString("message"))
                    } else {
                        CToast.ShowToast(this@HomeScreenActivity, json.getString("message"))
                    }
                    if (SessionSave.getSession("trip_id", this@HomeScreenActivity) != "") {
                        startActivity(Intent(this@HomeScreenActivity, OngoingAct::class.java))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                // CToast.ShowToast(MyStatus.this, NC.getString(R.string.server_error));
            }
        }
    }


    inner class CancelTrip internal constructor(url: String?, data: JSONObject?) :
        APIResult {
        init {
            try {
                if (isOnline(this@HomeScreenActivity)) {
                    APIService_Retrofit_JSON(
                        this@HomeScreenActivity,
                        this,
                        data,
                        false
                    ).execute(url)
                } else {
                    Utils.alert_view(
                        this@HomeScreenActivity,
                        NC.getString(R.string.message),
                        NC.getString(R.string.check_net_connection),
                        NC.getString(R.string.ok),
                        "",
                        true,
                        this@HomeScreenActivity,
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
                        CToast.ShowToast(this@HomeScreenActivity, json.getString("message"))
                    } else {
                        CToast.ShowToast(this@HomeScreenActivity, json.getString("message"))
                    }
                    if (SessionSave.getSession("trip_id", this@HomeScreenActivity) != "") {
                        startActivity(Intent(this@HomeScreenActivity, OngoingAct::class.java))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                //CToast.ShowToast(MyStatus.this, NC.getString(R.string.server_error));
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            if (this@HomeScreenActivity != null /*&& MyStatus.this.getCurrentFocus() != null*/) {
                try {
                    val j = JSONObject()
                    j.put("driver_id", SessionSave.getSession("Id", this@HomeScreenActivity))
                    j.put(
                        "driver_type",
                        SessionSave.getSession("driver_type", this@HomeScreenActivity)
                    )
                    j.put(
                        "device_token",
                        SessionSave.getSession(CommonData.DEVICE_TOKEN, this@HomeScreenActivity)
                    )
                    val pro_url = "type=driver_recent_trip_list"
                    if (SessionSave.getSession("Id", this@HomeScreenActivity) != "")
                        GetTripData(
                            pro_url,
                            j
                        )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }, 500)
    }


    fun enableDrivertoActiveState() {
        SessionSave.saveSession("driver_type", "A", this@HomeScreenActivity)
        if (SessionSave.getSession("shift_status", this@HomeScreenActivity) == "IN") {
            nonactiityobj.startServicefromNonActivity(this@HomeScreenActivity)
        }
        SessionSave.saveSession("account_activate", true, this@HomeScreenActivity)
        //  no_taxi_view.setVisibility(View.GONE)
        val window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = getResources().getColor(R.color.header_text)
        }
    }


    inner class GetTripData(url: String?, data: JSONObject?) : APIResult {
        init {
            try {
                if (isOnline(this@HomeScreenActivity)) {
                    APIService_Retrofit_JSON_NoProgress(
                        this@HomeScreenActivity,
                        this,
                        data,
                        false
                    ).execute(
                        url
                    )
                } else {
                    Log.d("No Internet Connection", "No Internet")
                }
            } catch (e: java.lang.Exception) {
                // TODO: handle exception
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            try {
                if (isSuccess) {
                    val json = JSONObject(result)
                    if (json.has("trip_list")) {
                        val details1 = json.getJSONArray("trip_list")
                    }
                    if (json.has("total_amount")) {



                        enableDrivertoActiveState()
                    }



                    if (json.getInt("status") == 1 || json.getInt("status") == -4 || json.getInt("status") == -2 || json.getInt(
                            "status"
                        ) == -3
                    ) {

                        if (json.getInt("status") == 1) {
                        }
                        if (json.getInt("status") == -4) {
                            dialog1 = Utils.alert_view(
                                this@HomeScreenActivity,
                                NC.getString(R.string.message),
                                json.getString("message"),
                                NC.getString(R.string.ok),
                                "",
                                true,
                                this@HomeScreenActivity,
                                "3"
                            )
                        } else if (json.getInt("status") == -2) {
                            dialog1 = Utils.alert_view(
                                this@HomeScreenActivity,
                                NC.getString(R.string.message),
                                json.getString("message"),
                                NC.getString(R.string.ok),
                                NC.getString(R.string.cancel),
                                false,
                                this@HomeScreenActivity,
                                "1"
                            )
                        } else if (json.getInt("status") == -3) {
                            Systems.out.println("myCode_______" + "in -3 condition")
                            dialog1 = Utils.alert_view_dialog(
                                this@HomeScreenActivity,
                                NC.getString(R.string.message),
                                json.getString("message"),
                                NC.getString(R.string.ok),
                                NC.getString(R.string.cancel),
                                false,
                                { dialogInterface, i -> //                                    Utils.closeDialog();
                                    dialogInterface.dismiss()
                                    val intent1 = Intent(
                                        this@HomeScreenActivity,
                                        WebviewAct::class.java
                                    )
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent1.putExtra("fromMyStatus", "YES")
                                    intent1.putExtra("type", "1")
                                    startActivity(intent1)
                                    finish()
                                },
                                { dialogInterface, i -> dialogInterface.dismiss() },
                                ""
                            )
                        }

                    } else if (json.getInt("status") == 10) {
                        SessionSave.saveSession("driver_type", "D", this@HomeScreenActivity)
                        SessionSave.saveSession("account_activate", false, this@HomeScreenActivity)
                        AccountNotActivated(
                            SessionSave.getSession(
                                "account_message",
                                this@HomeScreenActivity
                            )
                        )
                    } else if (json.getInt("status") == -4)
                    {
                        dialog1 = Utils.alert_view(
                            this@HomeScreenActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),
                            NC.getString(R.string.ok),
                            "",
                            true,
                            this@HomeScreenActivity,
                            "3"
                        )
                    } else if (json.getInt("status") == 40)
                    {
                        enableDrivertoActiveState()
                        dialog1 = Utils.alert_view(
                            this@HomeScreenActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),
                            NC.getString(R.string.ok),
                            "",
                            true,
                            this@HomeScreenActivity,
                            "3"
                        )

                    } else if (json.getInt("status") == 41)
                    {
                        dialog1 = Utils.alert_view(
                            this@HomeScreenActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),
                            NC.getString(R.string.ok),
                            "",
                            true,
                            this@HomeScreenActivity,
                            "3"
                        )
                        recentListMessage = json.getString("message")

                        val window: Window = getWindow()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                            window.statusBarColor = getResources().getColor(R.color.button_accept)
                        }
                        if (SessionSave.getSession(
                                "shift_status",
                                this@HomeScreenActivity
                            ) == "IN"
                        ) {
                            nonactiityobj.startServicefromNonActivity(this@HomeScreenActivity)
                        }


                    } else if (json.getInt("status") == -1) {



                        enableDrivertoActiveState()
                    } else {

                    }
                } else {

                    runOnUiThread(Runnable {
                        //  CToast.ShowToast(MyStatus.this, NC.getString(R.string.server_error));
                    })
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    override fun positiveButtonClick(dialog: DialogInterface?, id: Int, s: String?) {
        when (s) {


            "5" -> {
                dialog!!.dismiss()
                try {
                    val js = JSONObject()
                    js.put("driver_id", SessionSave.getSession("Id", this@HomeScreenActivity))
                    js.put(
                        "shiftupdate_id",
                        SessionSave.getSession("Shiftupdate_Id", this@HomeScreenActivity)
                    )
                    val urls = "type=user_logout"
                    Logout(urls, js)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }


        }
    }

    override fun negativeButtonClick(dialog: DialogInterface?, id: Int, s: String?) {

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                return true // Indicate that the item click was handled
            }
            R.id.nav_profile -> { // Assuming you add this to your menu XML
                // Handle profile menu item click
                val intent = Intent(
                    this@HomeScreenActivity,
                    ProfileActivity::class.java
                )
                startActivity(intent)
            }
            R.id.nav_trips -> { // Assuming you add this to your menu XML
                // Handle profile menu item click
                val intent = Intent(
                    this@HomeScreenActivity,
                    CommonTripHistory::class.java
                )
                startActivity(intent)
            }

            R.id.nav_logout -> { // Assuming you add this to your menu XML
                // Handle profile menu item click
                logout(this@HomeScreenActivity)
            }
            // Add more cases for other menu items
        }
        drawerLayout.closeDrawer(GravityCompat.START) // Close the drawer after an item is selected
        return true

    }

    fun logout(context: Context?) {
        dialog1 = Utils.alert_view(
            this@HomeScreenActivity,
            NC.getString(R.string.message),
            NC.getString(R.string.confirmlogout),
            NC.getString(R.string.m_logout),
            NC.getString(R.string.cancel),
            true,
            this@HomeScreenActivity,
            "5"
        )
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    inner class Logout(url: String?, data: JSONObject) : APIResult {
        init {
            Systems.out.println(url)
            Systems.out.println(data.toString())
            if (isOnline(this@HomeScreenActivity)) {
                APIService_Retrofit_JSON(this@HomeScreenActivity, this, data, false).execute(url)
            } else {
                dialog1 = Utils.alert_view(
                    this@HomeScreenActivity,
                    NC.getString(R.string.message),
                    NC.getString(R.string.please_check_internet),
                    NC.getString(R.string.ok),
                    "",
                    true,
                    this@HomeScreenActivity,
                    "2"
                )
            }
        }

        override fun getResult(isSuccess: Boolean, result: String) {
            if (isSuccess) {
                try {
                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {
                        val locationServiceIntent = Intent(this@HomeScreenActivity, LocationUpdate::class.java)
                        stopService(locationServiceIntent)

                        MainActivity.clearsession(this@HomeScreenActivity)
                        //   dialog1 = Utils.alert_view(MainActivity.this, NC.getResources().getString(R.string.message),json.getString("message"), NC.getResources().getString(R.string.ok), "", true, MainActivity.this, "7");
                        val length = CommonData.mActivitylist.size
                        if (length != 0) {
                            for (jv in 0 until length) {
                                CommonData.mActivitylist[jv].finish()
                            }
                        }
                        val intent = Intent(
                            this@HomeScreenActivity,
                            UserLoginAct::class.java
                        )
                        startActivity(intent)
                        finish()
                        //                        dialog1 = Utils.alert_view_dialog(MainActivity.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", false, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        }, (dialog, which) -> dialog.dismiss(), "");
                    } else if (json.getInt("status") == -4) {
                        if (json.has("trip_id")) {
                            SessionSave.saveSession(
                                "trip_id",
                                json.getString("trip_id"),
                                this@HomeScreenActivity
                            )
                            dialog1 = Utils.alert_view(
                                this@HomeScreenActivity,
                                NC.getString(R.string.message),
                                json.getString("message"),
                                NC.getString(R.string.ok),
                                "",
                                true,
                                this@HomeScreenActivity,
                                "3"
                            )
                        }
                    } else {
                        dialog1 = Utils.alert_view(
                            this@HomeScreenActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),
                            NC.getString(R.string.ok),
                            "",
                            true,
                            this@HomeScreenActivity,
                            "2"
                        )
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                // runOnUiThread(() -> ShowToast(MainActivity.this, NC.getString(R.string.server_error)));
            }
        }
    }


}


