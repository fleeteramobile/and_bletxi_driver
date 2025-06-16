package com.onewaytripcalltaxi.driver.more

import android.app.Dialog
import android.content.Intent
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.onewaytripcalltaxi.driver.DeleteAccountActivity
import com.onewaytripcalltaxi.driver.InviteFriendAct
import com.onewaytripcalltaxi.driver.MainActivity
import com.onewaytripcalltaxi.driver.R
import com.onewaytripcalltaxi.driver.documentlist.DocumentListActivity
import com.onewaytripcalltaxi.driver.fleetdetails.FleetDetailsActivity
import com.onewaytripcalltaxi.driver.service.ServiceGenerator
import java.net.MalformedURLException
import java.net.URL

class MoreActivity : MainActivity() {
    var documents_lay : ConstraintLayout? = null
    var fleet_details_lay : ConstraintLayout? = null
    var invite_lay : ConstraintLayout? = null
    var privacy_lay : ConstraintLayout? = null
    var logout_lay : ConstraintLayout? = null
    var back_trip_details_more : CardView? = null
    var domain_name_setting : TextView? = null

    var dialog1: Dialog? = null





    override fun setLayout(): Int {
        return R.layout.activity_more
    }

    override fun Initialize() {
        documents_lay = findViewById(R.id.documents_lay)
        fleet_details_lay = findViewById(R.id.fleet_details_lay)
        invite_lay = findViewById(R.id.invite_lay)
        privacy_lay = findViewById(R.id.privacy_lay)
        logout_lay = findViewById(R.id.logout_lay)
        domain_name_setting = findViewById(R.id.domain_name_setting)
        back_trip_details_more = findViewById(R.id.back_trip_details_more)
        back_trip_details_more?.setOnClickListener {
            onBackPressed()
        }
        documents_lay?.setOnClickListener {
            startActivity(Intent(this@MoreActivity, DocumentListActivity::class.java))
        }

        fleet_details_lay?.setOnClickListener {
            startActivity(Intent(this@MoreActivity, FleetDetailsActivity::class.java))
        }

        invite_lay?.setOnClickListener {
            startActivity(Intent(this@MoreActivity, InviteFriendAct::class.java))
        }

        privacy_lay?.setOnClickListener {
            val deleteAcc = Intent(this@MoreActivity, DeleteAccountActivity::class.java)
            startActivity(deleteAcc)
//            val `in` = Intent(
//                this@MoreActivity ,
//                WebviewAct::class.java
//            )
//            `in`.putExtra("type", "delete")
//            startActivity(`in`)
        }
        logout_lay?.setOnClickListener {
            logout(this@MoreActivity)
        }
        var host = ""
        try {
            val urls = URL(ServiceGenerator.API_BASE_URL)
            host = urls.host
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        domain_name_setting!!.setText(host)
    }




}