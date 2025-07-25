package com.bluetaxi.driver.homescreen



import android.annotation.SuppressLint
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bluetaxi.driver.MainActivity
import com.bluetaxi.driver.OngoingAct
import com.bluetaxi.driver.R
import com.bluetaxi.driver.SettlementHistoryActivity
import com.bluetaxi.driver.StatementofHistory
import com.bluetaxi.driver.UserLoginAct
import com.bluetaxi.driver.WebviewAct
import com.bluetaxi.driver.data.CommonData
import com.bluetaxi.driver.dutysetting.DutySettingActivity
import com.bluetaxi.driver.earningchart.EarningsAct
import com.bluetaxi.driver.gotohome.GotoHomeActivity
import com.bluetaxi.driver.interfaces.APIResult
import com.bluetaxi.driver.interfaces.ClickInterface
import com.bluetaxi.driver.profile.ProfileActivity
import com.bluetaxi.driver.service.APIService_Retrofit_JSON
import com.bluetaxi.driver.service.APIService_Retrofit_JSON_NoProgress
import com.bluetaxi.driver.service.FirebaseService.BOOKLATER_NOTIFICATION_ID
import com.bluetaxi.driver.service.LocationUpdate
import com.bluetaxi.driver.service.NonActivity
import com.bluetaxi.driver.tracklocation.TrackLocationActivity
import com.bluetaxi.driver.tripinprogress.TripInprogressActivity
import com.bluetaxi.driver.triplist.BookingsActivity
import com.bluetaxi.driver.triplist.CommonTripHistory
import com.bluetaxi.driver.triplist.OutstationUpcomingActivity
import com.bluetaxi.driver.triplist.TollRequestActivity
import com.bluetaxi.driver.triplist.pastbooking.PastBookingActivity
import com.bluetaxi.driver.triplist.upcoming.UpcomingTripsActivity
import com.bluetaxi.driver.utils.CToast
import com.bluetaxi.driver.utils.ListViewEX
import com.bluetaxi.driver.utils.NC
import com.bluetaxi.driver.utils.NetworkStatus.isOnline
import com.bluetaxi.driver.utils.SessionSave
import com.bluetaxi.driver.utils.Systems
import com.bluetaxi.driver.utils.Utils
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject


class HomeScreenActivity : AppCompatActivity(), ClickInterface,
    NavigationView.OnNavigationItemSelectedListener {
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
    private lateinit var profileImageView: ShapeableImageView
    private lateinit var scheduleRideButton: Button

    private lateinit var earning: TextView
    private lateinit var driver_name_txt: TextView
    private lateinit var driver_code_txt: TextView
    private lateinit var hour_spend: TextView
    private lateinit var totalRidesCount: TextView
    private lateinit var completedRidesCount: TextView
    private lateinit var cancelledRidesCount: TextView
    private lateinit var walletBalanceButton: Button

    private lateinit var driverProfileImageView: ShapeableImageView
    private lateinit var driverMobileTextView: TextView
    private lateinit var driverNameTextView: TextView
    var mytrip : SwitchCompat? = null
    var checked = "OUT"
    var mPlayer: MediaPlayer? = null
    var bookLaterDetails: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        navigationView.setNavigationItemSelectedListener(this)
        mytrip = findViewById(R.id.mytrip)

        // You might want to use a Toolbar instead of just an ImageView for the menu icon
        // If you have a Toolbar, set it as the support action bar:
        // setSupportActionBar(findViewById(R.id.your_toolbar_id))
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        ) // Define these strings in strings.xml
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

         profileImageView = findViewById(R.id.profileImageView)

        val headerView: View = navigationView.getHeaderView(0) // 0 is usually the index of the first header layout


         driverNameTextView = headerView.findViewById(R.id.driver_name_header)
         driverMobileTextView = headerView.findViewById(R.id.driver_email_header)
          driverProfileImageView = headerView.findViewById(R.id.imageViewDriverProfile)

        earning = findViewById(R.id.earning)
        driver_name_txt = findViewById(R.id.driver_name_home)
        driver_code_txt = findViewById(R.id.driver_code)
        hour_spend = findViewById(R.id.hour_spend)
        totalRidesCount = findViewById(R.id.totalRidesCount)
        completedRidesCount = findViewById(R.id.completedRidesCount)
        cancelledRidesCount = findViewById(R.id.cancelledRidesCount)
         profileImageView = findViewById(R.id.profileImageView)
        walletBalanceButton = findViewById(R.id.walletBalanceButton)
        scheduleRideButton = findViewById(R.id.scheduleRideButton)
        val earningCard = findViewById<CardView>(R.id.earningCard)
        val menuIcon = findViewById<ImageView>(R.id.menuIcon)

        menuIcon.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        scheduleRideButton.setOnClickListener {

        }


        earningCard.setOnClickListener {
            startActivity(Intent(this, TripInprogressActivity::class.java))
        }


        mytrip!!.isChecked = SessionSave.getSession("shift_status",this@HomeScreenActivity).equals("IN")
        mytrip!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                checked = "IN"
                RequestingCheckBox()

            }
            else{

                checked = "OUT"
                RequestingCheckBox()


            }

        }
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
                if (alert_trip_id != null) {
                    println("homeactivity"+" "+"2")
                    println("homeactivity"+" "+"2" + alert_trip_id)
                    // Vibrate when activity is opened


//                    val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
//                    if (vibrator != null && vibrator.hasVibrator()) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            vibrator.vibrate(
//                                VibrationEffect.createOneShot(
//                                    3000,
//                                    VibrationEffect.DEFAULT_AMPLITUDE
//                                )
//                            )
//                        } else {
//                            vibrator.vibrate(3000) // Vibrate for 1 second
//                        }
//                    }
                    tone_play()
                  //  bookLaterDetails = alert_msg
                    bookLaterNotificationAlert()
                }
            } else {
                if (alert_msg != null && alert_msg!!.length != 0) dialog1 = Utils.alert_view(
                    this@HomeScreenActivity,
                    NC.getString(R.string.message),
                    alert_msg,
                    NC.getString(android.R.string.ok),
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


    private fun bookLaterNotificationAlert() {
        println("homeactivity"+" "+"3")


        CoroutineScope(Dispatchers.Main).launch {
            // Code before delay
            delay(1000) // Delay for 2 seconds (2000 milliseconds)
            val js = JSONObject()
            try {
                js.put("trip_id", alert_trip_id)
                val Url = "type=get_trip_detail"
                Tripdetails(Url, js)
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            // Code after delay
            Log.d("Delay", "2 seconds later")
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



    inner class Tripdetails internal constructor(url: String?, data: JSONObject?) :
        APIResult {
        init {
            try {
                println("homeactivity"+" "+"6")

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

        override fun getResult(isSuccess: Boolean, result: String?) {
            if (isSuccess && result != null) {
                try {

                        println("homeactivity"+" "+"4")

                        val json = JSONObject(result)
                        if (json.getInt("status") == 1) {

                            val detail = json.getJSONObject("detail")


                            showBookingNotification(
                                detail.getString("current_location"),
                                detail.getString("drop_location"),
                                detail.getString("pickup_date_time")
                            )
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
    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
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
        }



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

        override fun getResult(isSuccess: Boolean, result: String?) {
            try {
                if (isSuccess) {
                    val json = JSONObject(result)
                    println("profile_pictures"+ " "+json)
                    println("profile_pictures"+ " "+ json.getString("profile_picture"))
                    Glide.with(this@HomeScreenActivity)
                        .load(json.getString("profile_picture")) // Your drawable resource
                        .into(profileImageView)
    Glide.with(this@HomeScreenActivity)
                        .load(json.getString("profile_picture")) // Your drawable resource
                        .into(driverProfileImageView)

                    val message = json.getString("message")
                    val status = json.getInt("status")
                    val driverThresholdSetting = json.getInt("driver_threshold_setting")
                    val driverThresholdAmount = json.getDouble("driver_threshold_amount")
                    val totalAmount = json.getString("total_monthly_amount")
                    val totalTrips = json.getInt("total_trips")
                    val declinedCount = json.getInt("declined_count")
                    val maxDeclinedCount = json.getInt("max_declined_count")
                    val completedTrip = json.getInt("completed_trip")
                    val averageRating = json.getInt("average_rating") // Or getDouble if it can be fractional
                    val totalShiftHrs = json.getString("total_monthly_shift_hrs")
                    val driver_name = json.getString("driver_name")
                    val driver_code = json.getString("driver_code")
                    val driver_email = json.getString("driver_email")
                    val driver_phone = json.getString("driver_phone")
                    val modelId = json.getInt("model_id")
                    val driverWallet = json.getInt("driver_wallet") // Or getDouble if it can be fractional
                    val bookingLimit = json.getInt("booking_limit")


                    val remainingDeclines = maxDeclinedCount - declinedCount
                    cancelledRidesCount.setText(remainingDeclines.toString())

SessionSave.saveSession("model_id",modelId.toString(),this@HomeScreenActivity)


                    driverNameTextView.setText(driver_name)
                    driverMobileTextView.setText(driver_phone)


                    earning.setText(totalAmount.toString())
                    val name = driver_name.toString()
                    val capitalized = name.replaceFirstChar { it.uppercase() }
                    driver_name_txt.setText(capitalized)
                    SessionSave.saveSession("driver_name",capitalized,this@HomeScreenActivity)
                    //driver_code
                    driver_code_txt.setText(driver_code)
                    SessionSave.saveSession("driver_code",driver_code,this@HomeScreenActivity)

                    //driver_name_txt.setText(driver_name.toString())
                    hour_spend.setText(totalShiftHrs.toString())
                    totalRidesCount.setText(totalTrips.toString())
                    completedRidesCount.setText(completedTrip.toString())
                 //   cancelledRidesCount.setText(declinedCount.toString())


                    val walletText = "Wallet Balance\n${driverWallet.toString()}"
                    val spannable = SpannableString(walletText)
                    spannable.setSpan(
                        AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                        walletText.indexOf("\n") + 1,
                        walletText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    walletBalanceButton.setText(spannable)

                   // walletBalanceButton.setText("Wallet Balance ${driverWallet.toString()}")


                    //json parsing add here










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
                    } else if (json.getInt("status") == -4) {
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
                    } else if (json.getInt("status") == 40) {
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

                    } else if (json.getInt("status") == 41) {
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



            R.id.nav_wallet -> { // Assuming you add this to your menu XML
                // Handle profile menu item click
                val intent = Intent(
                    this@HomeScreenActivity,
                    EarningsAct::class.java
                )
                startActivity(intent)
            }


            R.id.nav_tarck -> { // Assuming you add this to your menu XML
                // Handle profile menu item click
                val intent = Intent(
                    this@HomeScreenActivity,
                    TrackLocationActivity::class.java
                )
                startActivity(intent)
            }

            R.id.nav_book -> { // Assuming you add this to your menu XML
                // Handle profile menu item click
                val intent = Intent(
                    this@HomeScreenActivity,
                    BookingsActivity::class.java
                )
                startActivity(intent)
            }






            R.id.nav_statement -> { // Assuming you add this to your menu XML
                // Handle profile menu item click
                val intent = Intent(
                    this@HomeScreenActivity,
                    StatementofHistory::class.java
                )
                startActivity(intent)
            }


            R.id.nav_settlement -> { // Assuming you add this to your menu XML
                // Handle profile menu item click
                val intent = Intent(
                    this@HomeScreenActivity,
                    SettlementHistoryActivity::class.java
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
                        val locationServiceIntent =
                            Intent(this@HomeScreenActivity, LocationUpdate::class.java)
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

    inner class RequestingCheckBox : APIResult {

        init {
            val j = JSONObject()
            j.put("driver_id", SessionSave.getSession("Id", this@HomeScreenActivity))
            j.put("shiftstatus", checked)
            j.put("reason", "")
            Log.e("shiftbefore ", j.toString())
            println("shiftbefore " + " "+  j.toString())
            j.put("update_id", SessionSave.getSession("Shiftupdate_Id", this@HomeScreenActivity))
            val requestingCheckBox = "type=driver_shift_status"

            APIService_Retrofit_JSON(this@HomeScreenActivity, this, j, false).execute(
                requestingCheckBox
            )
        }

        @SuppressLint("MissingPermission")
        override fun getResult(isSuccess: Boolean, result: String?) {
            if (isSuccess) {
                val mJSONObject = JSONObject(result)
                Toast.makeText(this@HomeScreenActivity,mJSONObject.getString("message"), Toast.LENGTH_LONG).show()

                if (mJSONObject.getInt("status") == 1) {




                    if (checked == "IN") {


                        SessionSave.saveSession("shift_status", "IN", this@HomeScreenActivity)
                        SessionSave.saveSession(CommonData.SHIFT_OUT, false, this@HomeScreenActivity)
                        SessionSave.saveSession(
                            "Shiftupdate_Id",
                            mJSONObject.getJSONObject("detail").getString("update_id"),
                            this@HomeScreenActivity
                        )

                        if (!SessionSave.getSession("driver_type", this@HomeScreenActivity)
                                .equals("D", ignoreCase = true)
                        ) {
                            nonactiityobj.startServicefromNonActivity(this@HomeScreenActivity)
                        }


                    }else{
                        SessionSave.saveSession("shift_status", "OUT", this@HomeScreenActivity)
                        SessionSave.saveSession("trip_id", "", this@HomeScreenActivity)
                        SessionSave.setWaitingTime(0L, this@HomeScreenActivity)
                        nonactiityobj.stopServicefromNonActivity(this@HomeScreenActivity)
                    }
                }
                else
                {

                    dialog1 = Utils.alert_view(
                        this@HomeScreenActivity,
                        "" + NC.getString(R.string.message),
                        "" + mJSONObject.getString("message"),
                        "" + NC.getString(R.string.ok),
                        "",
                        true,
                        this@HomeScreenActivity,
                        "6"
                    )
                }
            }
        }
    }


    private fun showBookingNotification(
        currentLocation: String,
        dropLocation: String,
        pickupDateTime: String
    ) {

        val bookLaterView = View.inflate(this@HomeScreenActivity, R.layout.booklater_alert, null)
        val bookLaterDialog = Dialog(this@HomeScreenActivity, R.style.dialogwinddow)
        bookLaterDialog.setContentView(bookLaterView)
        bookLaterDialog.setCancelable(false)
        if (bookLaterDialog.window != null) {
            val params = bookLaterDialog.window!!.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT // Set full width
            params.height = WindowManager.LayoutParams.WRAP_CONTENT // Adjust height as needed
            bookLaterDialog.window!!.attributes = params
            bookLaterDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Optional: Remove background
        }
        bookLaterDialog.show()
//        val listViewEX = bookLaterView.findViewById<ListViewEX>(R.id.testLay)
//        listViewEX.setData(
//            getStopArray(bookLaterDetails!!), "SCHEDULE", SessionSave.getSession(
//                "Lang",
//                this@HomeScreenActivity
//            )
//        )

        val txt_pickup = bookLaterView.findViewById<TextView>(R.id.txt_pickup)
        val txt_drop = bookLaterView.findViewById<TextView>(R.id.txt_drop)
        val txt_pick_time = bookLaterView.findViewById<TextView>(R.id.txt_pick_time)
        txt_pickup.text = currentLocation
        txt_drop.text = dropLocation
        txt_pick_time.text = pickupDateTime
        bookLaterView.findViewById<View>(R.id.btnAccept).setOnClickListener { view: View? ->
            bookLaterDialog.dismiss()
            tone_stop()
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(BOOKLATER_NOTIFICATION_ID)
            try {
                val j = JSONObject()
                j.put("trip_id", alert_trip_id)
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
        bookLaterView.findViewById<View>(R.id.btnDecline)
            .setOnClickListener { view: View? ->
                tone_stop()
                bookLaterDialog.dismiss()
                val notificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(BOOKLATER_NOTIFICATION_ID)
                try {
                    val j = JSONObject()
                    j.put("pass_logid", alert_trip_id)
                    j.put(
                        "driver_id",
                        SessionSave.getSession(
                            "Id",
                            this@HomeScreenActivity
                        )
                    )
                    j.put(
                        "taxi_id",
                        SessionSave.getSession(
                            "taxi_id",
                            this@HomeScreenActivity
                        )
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
                    if (MainActivity.mMyStatus.onstatus.equals("Arrivd", ignoreCase = true)) j.put(
                        "driver_arrived",
                        1
                    )
                    else j.put("driver_arrived", 0)
                    j.put("schedule", "1")
                    val canceltrip_url = "type=driver_reply"
                    CancelTrip(canceltrip_url, j)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
    }
    private fun tone_play() {
        if (mPlayer != null && mPlayer!!.isPlaying()) {
            mPlayer!!.stop() // Stop any currently playing audio
            mPlayer!!.release() // Release the MediaPlayer resources
        }

        try {
            // Initialize MediaPlayer
            mPlayer = MediaPlayer.create(
                this,
                R.raw.tripsounds
            ) // Use the resource ID for the raw file
            mPlayer!!.start() // Start playback

            // Optional: Add a listener to release MediaPlayer when done
            mPlayer!!.setOnCompletionListener { mp ->
                mp.release()
                mPlayer = null // Prevent further operations on the released MediaPlayer
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun tone_stop() {
        if (mPlayer != null && mPlayer!!.isPlaying()) {
            mPlayer!!.stop() // Stop playback
            mPlayer!!.release() // Release resources
            mPlayer = null // Prevent memory leaks
        }
    }

}


