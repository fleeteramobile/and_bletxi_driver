package com.onewaytripcalltaxi.driver.pdview

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.onewaytripcalltaxi.driver.R
import com.onewaytripcalltaxi.driver.route.StopData
import com.onewaytripcalltaxi.driver.utils.NC


class LocationListView : LinearLayout, CollapseInterface {
    override fun collapsed(collapsed: Boolean) {
        isCollapsed = collapsed
        println("COLLAPSED")
        removeAllViews()
        setData(stopArray, type, lang)
    }

    private var isCollapsed = false

    @JvmOverloads
    constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr)

   /* @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes)*/

    private lateinit var stopArray: ArrayList<StopData>

    var type: String = ""
    var lang: String = ""


    init {
        orientation = VERTICAL
    }

    fun setData(stopArray: ArrayList<StopData>, types: String, language: String) {
        removeAllViews()
        this.stopArray = stopArray
        this.type = types
        this.lang = language
        for (i in 0 until stopArray.size) {
            if (!isCollapsed || (i == 0 || i == stopArray.size - 1)) {
                println("COLLAPSED")
                createStop(stopArray[i])
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        (parent as PickupDropView).forceInvalidate()
    }

    private fun createStop(stopData: StopData) {


        if (lang == "ar" || lang == "fa") {


            val edtStop = AppCompatTextView(context)
            edtStop.isFocusableInTouchMode = false
            edtStop.hint = NC.getString(R.string.search_stop_hint)
            edtStop.id = stopData.id
            val layParams = LayoutParams(LayoutParams.MATCH_PARENT, resources.getDimension(R.dimen.stop_lay_height).toInt())
            layParams.setMargins(0, 0, 0, resources.getDimension(R.dimen.stop_space_lay).toInt())
            edtStop.setPadding(resources.getDimension(R.dimen.stop_space_lay).toInt(), 0, 0, 0)
            edtStop.layoutParams = layParams
            edtStop.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
            edtStop.isSingleLine = true
            edtStop.ellipsize = TextUtils.TruncateAt.MARQUEE
            edtStop.marqueeRepeatLimit = -1
            edtStop.isSelected = true


            edtStop.text = stopData.placeName
            addView(edtStop)
            when {
                type.equals("ONGOING", ignoreCase = true) -> {
                    edtStop.setBackgroundColor(Color.parseColor("#ffffff"))
                    edtStop.setTextColor(Color.parseColor("#3d3d3d"))
                }
                type.equals("SCHEDULE",ignoreCase = true) -> {
                    edtStop.setBackgroundColor(Color.parseColor("#ffffff"))
                    edtStop.setTextColor(Color.parseColor("#3d3d3d"))
                }
                else -> {
                    edtStop.setBackgroundColor(Color.parseColor("#00000000"))
                    edtStop.setTextColor(Color.parseColor("#ffffff"))
                }
            }
        } else {
            val edtStop = AppCompatTextView(context)
            edtStop.isFocusableInTouchMode = false
            edtStop.hint = NC.getString(R.string.search_stop_hint)
            edtStop.id = stopData.id
            val layParams = LayoutParams(LayoutParams.MATCH_PARENT, resources.getDimension(R.dimen.stop_lay_height).toInt())
            layParams.setMargins(0, 0, 0, resources.getDimension(R.dimen.stop_space_lay).toInt())
            edtStop.setPadding(resources.getDimension(R.dimen.stop_space_lay).toInt(), 0, 0, 0)
            edtStop.layoutParams = layParams
            edtStop.gravity = Gravity.CENTER_VERTICAL
            edtStop.isSingleLine = true
            edtStop.ellipsize = TextUtils.TruncateAt.MARQUEE
            edtStop.marqueeRepeatLimit = -1
            edtStop.post {
                edtStop.isSelected = true
            }

            edtStop.text = stopData.placeName
            addView(edtStop)
            when {
                type.equals("ONGOING", ignoreCase = true) -> {
                    edtStop.setBackgroundColor(Color.parseColor("#ffffff"))
                    edtStop.setTextColor(Color.parseColor("#3d3d3d"))
                }
                type.equals("SCHEDULE",ignoreCase = true) -> {
                    edtStop.setBackgroundColor(Color.parseColor("#ffffff"))
                    edtStop.setTextColor(Color.parseColor("#3d3d3d"))
                }
                else -> {
                    edtStop.setBackgroundColor(Color.parseColor("#00000000"))
                    edtStop.setTextColor(Color.parseColor("#ffffff"))
                }
            }
        }

    }

}