package com.onewaytripcalltaxi.driver.profile

import android.app.Dialog
import android.content.Intent
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.onewaytripcalltaxi.driver.MainActivity
import com.onewaytripcalltaxi.driver.MeAct
import com.onewaytripcalltaxi.driver.R
import com.onewaytripcalltaxi.driver.UserLoginAct
import com.onewaytripcalltaxi.driver.data.CommonData
import com.onewaytripcalltaxi.driver.interfaces.APIResult
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON
import com.onewaytripcalltaxi.driver.service.LocationUpdate
import com.onewaytripcalltaxi.driver.utils.NC
import com.onewaytripcalltaxi.driver.utils.SessionSave
import com.onewaytripcalltaxi.driver.utils.Utils
import com.onewaytripcalltaxi.driver.utils.loadImage
import org.json.JSONException
import org.json.JSONObject


class ProfileActivity : MainActivity() {
    lateinit var profile_img : ImageView
    lateinit var user_name : AppCompatTextView
    lateinit var mobile_number : AppCompatTextView
    lateinit var email_id : AppCompatTextView
    lateinit var logout : CardView
    lateinit var back_trip_details : CardView
    lateinit var edit_profile : LinearLayout
    var dialog1: Dialog? = null
    lateinit var mDialog: Dialog

    lateinit var mDialogLoader: Dialog
    override fun setLayout(): Int {
       return R.layout.activity_profile
    }

    override fun Initialize() {
        profile_img = findViewById(R.id.profile_img)
        user_name = findViewById(R.id.user_name)
        mobile_number = findViewById(R.id.mobile_number)
        email_id = findViewById(R.id.email_id)
        logout = findViewById(R.id.logout)
        back_trip_details = findViewById(R.id.back_trip_details)
        edit_profile = findViewById(R.id.edit_profile)


        logout.setOnClickListener {
          logout()
        }
        edit_profile.setOnClickListener {
            val mIntent = Intent(this, MeAct::class.java)
            startActivity(mIntent)
        }
        back_trip_details.setOnClickListener {
            onBackPressed()
        }
    }

    private fun logout() {
        val view = View.inflate(this, R.layout.custom_msg_popup_yn, null)
         mDialog = Dialog(this!!, R.style.dialogwinddow_trans)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.show()
        val window: Window? = mDialog.getWindow()
        window!!.setLayout(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT)

        //        final TextView t = mDialog.findViewById(R.id.f_textview);
//        t.setText(NC.getResources().getString(R.string.email));
        val mail = mDialog.findViewById<AppCompatTextView>(R.id.msg_txt)

        mail.setText(NC.getString(R.string.confirmlogout))

        val yesBtn = mDialog.findViewById<LinearLayout>(R.id.yesbtn)
        val noBtn = mDialog.findViewById<LinearLayout>(R.id.nobtn)

        val txtyes = mDialog.findViewById<AppCompatTextView>(R.id.txtyes)
        val txtno = mDialog.findViewById<AppCompatTextView>(R.id.txtno)
        //  val Cancel = mDialog.findViewById<TextView>(R.id.cancelbtn)
        txtyes.setText(NC.getString(R.string.m_logout))
        txtno.setText(NC.getString(R.string.cancel))

        yesBtn.setOnClickListener(object : View.OnClickListener {

            override fun onClick(arg0: View) {

                try {
                    val j = JSONObject()
                    j.put("driver_id", SessionSave.getSession("Id", context))
                    j.put("shiftupdate_id", SessionSave.getSession("Shiftupdate_Id", context))
                    val url = "type=user_logout"
                    LogoutApi(url, j)
                    mDialog.dismiss()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }



            }
        })

        noBtn.setOnClickListener(object : View.OnClickListener {

            override fun onClick(arg0: View) {


                mDialog.dismiss()

            }
        })
    }


    inner class LogoutApi(url: String?, data: JSONObject) : APIResult {
        init {

                APIService_Retrofit_JSON(applicationContext, this, data, false).execute(url)

        }

        override fun getResult(isSuccess: Boolean, result: String) {
            if (isSuccess) {
                try {
                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {
                        mDialog.dismiss()
                        val locationService = Intent(
                            this@ProfileActivity,
                            LocationUpdate::class.java
                        )
                        stopService(Intent(locationService))
                        clearsession(this@ProfileActivity)
//                        dialog1 = Utils.alert_view(
//                            this@ProfileActivity,
//                            NC.getString(R.string.message),
//                            json.getString("message"),
//                            NC.getString(R.string.ok),
//                            "",
//                            true,
//                            this@ProfileActivity,
//                            "7"
//                        )
//                        dialog1.dismiss()
                        val length = CommonData.mActivitylist.size
                        if (length != 0) {
                            for (jv in 0 until length) {
                                CommonData.mActivitylist[jv].finish()
                            }
                        }
                        val intent = Intent(
                            this@ProfileActivity,
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
                                this@ProfileActivity
                            )
                            dialog1 = Utils.alert_view(
                                this@ProfileActivity,
                                NC.getString(R.string.message),
                                json.getString("message"),
                                NC.getString(R.string.ok),
                                "",
                                true,
                                this@ProfileActivity,
                                "3"
                            )
                        }
                    } else {
                        dialog1 = Utils.alert_view(
                            this@ProfileActivity,
                            NC.getString(R.string.message),
                            json.getString("message"),
                            NC.getString(R.string.ok),
                            "",
                            true,
                            this@ProfileActivity,
                            "2"
                        )
                    }
                } catch (e: JSONException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
            } else {
                // runOnUiThread(() -> ShowToast(MainActivity.this, NC.getString(R.string.server_error)));
            }
        }
    }


    override fun onResume() {
        super.onResume()


        //Getting Driver Profile
        showDialog()
        try {
            val j = JSONObject()
            j.put("userid", SessionSave.getSession("Id", this@ProfileActivity))
            val pro_url = "type=driver_profile"
            GetProfileData(pro_url, j)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    inner class GetProfileData(url: String?, data: JSONObject?) : APIResult {
        init {
            try {

                    APIService_Retrofit_JSON(applicationContext, this, data, false).execute(url)

            } catch (e: Exception) {
                // TODO: handle exception
                e.printStackTrace()
            }
        }

        override fun getResult(isSuccess: Boolean, result: String) {

            try {
                if (isSuccess) {
                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {
                        closeDialog()
                        val details = json.getJSONObject("detail")
                        profile_img.loadImage(details.getString("main_image_path"))
                        user_name.setText(details.getString("name"))
                        mobile_number.setText(details.getString("phone"))
                        email_id.setText(details.getString("email") )
                    }
                    else{
                        closeDialog()
                    }

                } else {
                    // CToast.ShowToast(MeAct.this, NC.getString(R.string.server_error));
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }


    fun showDialog() {
        try {

                val view = View.inflate(this, R.layout.progress_bar, null)
            mDialogLoader = Dialog(this, R.style.dialogwinddow_trans)
            mDialogLoader.setContentView(view)
            mDialogLoader.setCancelable(false)
            mDialogLoader.show()
                val iv: ImageView = mDialogLoader.findViewById<ImageView>(R.id.giff)
                val imageViewTarget = DrawableImageViewTarget(iv)
                Glide.with(this)
                    .load(R.raw.loading_anim)
                    .into<DrawableImageViewTarget>(imageViewTarget)

        } catch (e: java.lang.Exception) {
        }
    }

    fun closeDialog() {
        try {
            if (mDialogLoader != null) if (mDialogLoader.isShowing()) mDialogLoader.dismiss()
        } catch (e: java.lang.Exception) {
        }
    }

}