package com.onewaytripcalltaxi.driver.pdview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.onewaytripcalltaxi.driver.R
import com.onewaytripcalltaxi.driver.utils.CircleOverlayView.dpToPx

class CustomIconView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0) : View(context, attrs, defStyle), CollapseInterface {
    override fun collapsed(collapsed: Boolean) {
        length = if (collapsed && expandedLength > 2)
            2
        else
            expandedLength
        invalidate()
    }

    private var paint: Paint


    private var singleLayHeight: Int
    private var spaceBetweenLayout: Int
    private var pointHeight: Int
    private var canvasHeight: Int
    private var marginTop: Int
    private var linkLineHeight: Int
    private var linkLineWidth: Int
    private var length = 0
    private var expandedLength = 0
    private var includeUnderline = false

    init {
        var underlineHeight = 0
        if (includeUnderline)
            underlineHeight = resources.getDimension(R.dimen.underline_view_height).toInt()

        singleLayHeight = resources.getDimension(R.dimen.stop_lay_height).toInt() + underlineHeight
        spaceBetweenLayout = resources.getDimension(R.dimen.stop_space_lay).toInt()
        pointHeight = resources.getDimension(R.dimen.stop_icon_lay).toInt()
        canvasHeight = (singleLayHeight * length) + (spaceBetweenLayout * (length - 1)) + (underlineHeight * (length - 1))
        marginTop = (singleLayHeight / 2) - (pointHeight / 2)
        linkLineHeight = singleLayHeight + spaceBetweenLayout - pointHeight + underlineHeight
        linkLineWidth = dpToPx(2)
        paint = Paint()


    }


    fun setData(length: Int) {
        this.length = length
        expandedLength = length
        invalidate()

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas?.let {
            val widthoffset = (canvas.width / 2)

            var yOffset = marginTop

            for (i in 1..length) {

                if (i == 1) {
                    paint.color = ContextCompat.getColor(context, R.color.pickup_green)
                    canvas.drawCircle(widthoffset.toFloat(), yOffset.toFloat() + (pointHeight / 2), (pointHeight / 2).toFloat(), paint)

                    yOffset += pointHeight
                    if (i != length) {
                        canvas.drawRect(Rect(widthoffset - (linkLineWidth / 2), yOffset, widthoffset + (linkLineWidth / 2), yOffset + (linkLineHeight / 2)), paint)

                        yOffset += linkLineHeight / 2
                        paint.color = ContextCompat.getColor(context, R.color.pickup_red)
                        canvas.drawRect(Rect(widthoffset - (linkLineWidth / 2), yOffset, widthoffset + (linkLineWidth / 2), yOffset + (linkLineHeight / 2)), paint)
                        yOffset += linkLineHeight / 2
                    }


                } else {


                    if (i == length)
                        canvas.drawRect(Rect(widthoffset - (pointHeight / 2), yOffset, widthoffset + (pointHeight / 2), yOffset + pointHeight), paint)
                    else
                        canvas.drawCircle(widthoffset.toFloat(), yOffset.toFloat() + (pointHeight / 2), (pointHeight / 2).toFloat(), paint)
                    yOffset += pointHeight
                    if (i != length) {
                        canvas.drawRect(Rect(widthoffset - (linkLineWidth / 2), yOffset, widthoffset + (linkLineWidth / 2), yOffset + linkLineHeight), paint)

                        yOffset += linkLineHeight
                    }

                }

            }


        }

    }

}