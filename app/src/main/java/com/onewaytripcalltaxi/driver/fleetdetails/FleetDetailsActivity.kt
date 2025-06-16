package com.onewaytripcalltaxi.driver.fleetdetails

import android.widget.EditText
import androidx.cardview.widget.CardView
import com.onewaytripcalltaxi.driver.MainActivity
import com.onewaytripcalltaxi.driver.R
import com.onewaytripcalltaxi.driver.utils.SessionSave

class FleetDetailsActivity : MainActivity() {
 var modelTxt : EditText? = null
    var taxinoTxt : EditText? = null
    var assignfromTxt : EditText? = null
    var taxi_assignto : EditText? = null
    var back_trip_details : CardView? = null

    override fun setLayout(): Int {
      return R.layout.activity_fleet_details
    }

    override fun Initialize() {
        modelTxt = findViewById(R.id.modelTxt)
        taxinoTxt = findViewById(R.id.taxinoTxt)
        assignfromTxt = findViewById(R.id.assignfromTxt)
        taxi_assignto = findViewById(R.id.assigntoTxt)
        back_trip_details = findViewById(R.id.back_trip_details)
        back_trip_details!!.setOnClickListener {
            onBackPressed()
        }

        modelTxt!!.setText(SessionSave.getSession("taxi_model",this@FleetDetailsActivity))
        taxinoTxt!!.setText(SessionSave.getSession("taxi_no",this@FleetDetailsActivity))

        assignfromTxt!!.setText(SessionSave.getSession("taxi_map_from",this@FleetDetailsActivity))

        taxi_assignto!!.setText(SessionSave.getSession("taxi_map_to",this@FleetDetailsActivity))


    }
}