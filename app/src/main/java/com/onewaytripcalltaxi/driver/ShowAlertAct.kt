package com.onewaytripcalltaxi.driver

import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.onewaytripcalltaxi.driver.utils.Colorchange
import com.onewaytripcalltaxi.driver.utils.FontHelper
import com.onewaytripcalltaxi.driver.utils.NC
import kotlinx.android.synthetic.main.canceltrip_lay.*

/**
 * This is cancel the trip
 */
class ShowAlertAct : MainActivity() {


    private var messages: String = ""
    /**
     * setting the layout
     */
    override fun setLayout(): Int {
        return R.layout.canceltrip_lay
    }

    /**
     * Initializing the component variables
     */
    override fun Initialize() {

        var moveToPlaystore = false
        val bun = intent.extras
        if (bun != null) {
            messages = bun.getString("message").toString()
            moveToPlaystore = bun.getBoolean("move_to_playstore")
        }

        FontHelper.applyFont(this@ShowAlertAct, findViewById<View>(R.id.canceltrip))

        Colorchange.ChangeColor((this@ShowAlertAct
                .findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup, this@ShowAlertAct)

        message.text = messages
        button1.text = NC.getString(R.string.ok)
        button1.setOnClickListener {
            if (moveToPlaystore) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
                startActivity(intent)
            } else
                finish()
        }

    }


}