/*
 * *
 *  * Created by Nethaji on 27/6/20 1:18 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 27/6/20 12:32 PM
 *
 */
package com.onewaytripcalltaxi.driver.splash

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.text.InputFilter
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.onewaytripcalltaxi.driver.BuildConfig
import com.onewaytripcalltaxi.driver.R
import com.onewaytripcalltaxi.driver.utils.NC
import com.onewaytripcalltaxi.driver.utils.SessionSave
import com.onewaytripcalltaxi.driver.utils.Utils

fun Context.getCompatFont(@FontRes fontRes: Int): Typeface =
    ResourcesCompat.getFont(this, fontRes) ?: Typeface.DEFAULT

fun View.getCompatSize(@DimenRes dimenRes: Int): Int =
    resources.getDimension(dimenRes).toInt()

fun View.getCompatColor(@ColorRes colorRes: Int): Int =
    ContextCompat.getColor(this.context, colorRes)

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.toast(msg: Int) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}


fun ImageView.loadCircularImage(image: Any) {
    Glide.with(context)
        .load(
            when (image) {
                is Int -> ContextCompat.getDrawable(context, image)
                is Bitmap -> image
                else -> image
            }
        )
        .circleCrop()
        .into(this)
}


fun ImageView.loadImage(image: Any) {
    Glide.with(context)
        .load(
            when (image) {
                is Int -> ContextCompat.getDrawable(context, image)
                is Bitmap -> image
                else -> image
            }
        )
              .into(this)
}


fun HuaweiDeviceAlert(mContext: Context) {
    Utils.alert_view_dialog(mContext,
        NC.getString(R.string.huawei_title),
        "" + String.format(NC.getString(R.string.huawei_msg)),
        "" + NC.getString(R.string.ok), "", false,
        { dialog, which ->
            dialog.dismiss()
            SessionSave.saveSession("settings_alert", "SETTINGS", mContext)
            EnableHuaweiProtectedApps(mContext)
        }, { dialog, which -> dialog.dismiss() }, ""
    )
}

fun EnableHuaweiProtectedApps(mContext: Context) {

}

 fun VersionCheck(mContext: Context): Boolean {
    try {
        val newVersion = if (SessionSave.getSession(
                "play_store_version",
                mContext
            ) == ""
        ) "0" else SessionSave.getSession("play_store_version", mContext)
        val curVersion = BuildConfig.VERSION_CODE
        System.err.println(
            "New version" + newVersion + "curVersion" + curVersion + "---" + (curVersion < value(
                newVersion
            ))
        )
        return curVersion < newVersion.toInt()
    } catch (e: Exception) {
        // TODO: handle exception
        e.printStackTrace()
    }
    return false
}

 fun value(string: String): Long {
    var string = string
    string = string.trim { it <= ' ' }
    return if (string.contains(".")) {
        val index = string.lastIndexOf(".")
        value(string.substring(0, index)) * 100 + value(string.substring(index + 1))
    } else {
        string.toLong()
    }
}

fun setEditTextMaxLength(length: Int, edt_text: EditText) {
    val FilterArray = arrayOfNulls<InputFilter>(1)
    FilterArray[0] = InputFilter.LengthFilter(length)
    edt_text.filters = FilterArray
}