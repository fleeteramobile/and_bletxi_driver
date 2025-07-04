package com.bluetaxi.driver

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class ZigzagDividerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, android.R.color.darker_gray) // Default color
        style = Paint.Style.FILL // Fill the zigzag area
    }
    private val path = Path()

    // You can make these customizable via XML attributes (see step 3)
    private var zigzagHeight = 10f // Height of each zig/zag peak
    private var zigzagWidth = 20f // Width of one full zig-zag cycle (peak to peak)

    init {
        // Optional: Get custom attributes from XML if needed
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZigzagDividerView, defStyleAttr, 0)
        zigzagHeight = typedArray.getDimension(R.styleable.ZigzagDividerView_zigzagHeight, zigzagHeight).toFloat()
        zigzagWidth = typedArray.getDimension(R.styleable.ZigzagDividerView_zigzagWidth, zigzagWidth).toFloat()
        paint.color = typedArray.getColor(R.styleable.ZigzagDividerView_zigzagColor, paint.color)
        typedArray.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateZigzagPath(w, h)
    }

    private fun updateZigzagPath(width: Int, height: Int) {
        path.reset()

        // Start from the bottom-left
        path.moveTo(0f, height.toFloat())

        var currentX = 0f
        val numSegments = (width / zigzagWidth).toInt() * 2 // Two segments per full zigzag width

        for (i in 0..numSegments) {
            val nextX = currentX + (zigzagWidth / 2f) // Move half a zigzag width

            val yOffset = if (i % 2 == 0) {
                // Even segment: peak goes up (towards top of view)
                height - zigzagHeight
            } else {
                // Odd segment: peak goes down (towards bottom of view)
                height.toFloat()
            }

            // Draw a line to the next point
            path.lineTo(nextX, yOffset)
            currentX = nextX
        }

        // Ensure the path extends to the right edge and then closes
        path.lineTo(width.toFloat(), height.toFloat())
        path.close() // Closes the path back to the starting point
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }
}