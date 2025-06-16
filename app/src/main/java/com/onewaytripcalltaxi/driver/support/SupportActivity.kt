package com.onewaytripcalltaxi.driver.support

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import com.onewaytripcalltaxi.driver.MainActivity
import com.onewaytripcalltaxi.driver.R
import com.onewaytripcalltaxi.driver.interfaces.APIResult
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON_NoProgress
import com.onewaytripcalltaxi.driver.utils.SessionSave
import org.json.JSONException
import org.json.JSONObject


class SupportActivity : MainActivity() {


    var back_supports: CardView? = null
    var mobiletxt: AppCompatTextView? = null
    var Emailtxt: AppCompatTextView? = null
    var Email_lay: LinearLayout? = null
    var mobile_lay: LinearLayout? = null
    override fun setLayout(): Int {
        return R.layout.activity_support
    }

    override fun Initialize() {
        back_supports = findViewById(R.id.back_supports)
        mobiletxt = findViewById(R.id.mobiletxt)
        Emailtxt = findViewById(R.id.emailtxt)
        Email_lay = findViewById(R.id.email_lay)
        mobile_lay = findViewById(R.id.mobile_lay)

        Email_lay!!.setOnClickListener {
            try {
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + Emailtxt!!.text.toString()))
                intent.putExtra(Intent.EXTRA_SUBJECT, "")
                intent.putExtra(Intent.EXTRA_TEXT, "")
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {

            }
        }
        mobile_lay!!.setOnClickListener {

            val callIntent = Intent(Intent.ACTION_VIEW)
            callIntent.setData(Uri.parse("tel:${mobiletxt!!.text.toString()}"))
           startActivity(callIntent)

        }
        Emailtxt!!.setText(SessionSave.getSession("email",this@SupportActivity))
        mobiletxt!!.setText(
            SessionSave.getSession("dispatcher_phone_number",this@SupportActivity)
        )


        back_supports!!.setOnClickListener {
            onBackPressed()
        }

        val url = "type=getcoreconfig"
        CoreConfigCall(url, applicationContext)


    }


    inner class CoreConfigCall internal constructor(url: String?, applicationContext: Context) :
        APIResult {
        lateinit var mContext: Context

        init {
            System.out.println("Mobilenumber_result" + " " + "sckpx[xlx;zmv;lm")

            mContext = applicationContext
            APIService_Retrofit_JSON_NoProgress(mContext, this, "", true).execute(url)

        }

        override fun getResult(isSuccess: Boolean, result: String) {

            System.out.println("Mobilenumber_result" + " " + "sckpx[xlx;zmv;lm")
            if (isSuccess) {
                try {
                    val json = JSONObject(result)
                    if (json.getInt("status") == 1) {
                        val jArry = json.getJSONArray("detail")

                        SessionSave.saveSession("email",jArry.getJSONObject(0).getString("admin_email"),this@SupportActivity)
                        SessionSave.saveSession("dispatcher_phone_number", json.getString("dispatcher_phone_number"),this@SupportActivity)
                        Emailtxt!!.setText(jArry.getJSONObject(0).getString("admin_email"))
                        mobiletxt!!.setText(
                            json.getString("dispatcher_phone_number")
                        )


                    }
                } catch (e: JSONException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()

                } catch (e: NullPointerException) {
                    // TODO: handle exception
                    e.printStackTrace()

                } catch (e: Exception) {
                    // TODO: handle exception
                    e.printStackTrace()

                }
            }
        }
    }

}