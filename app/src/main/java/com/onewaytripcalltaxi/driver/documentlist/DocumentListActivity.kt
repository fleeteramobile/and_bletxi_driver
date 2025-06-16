package com.onewaytripcalltaxi.driver.documentlist


import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.onewaytripcalltaxi.driver.MainActivity
import com.onewaytripcalltaxi.driver.R
import com.onewaytripcalltaxi.driver.utils.SessionSave
import com.squareup.picasso.Picasso


class DocumentListActivity : MainActivity() {
var driver_front_image_lay : ConstraintLayout? = null
    var driver_back_image_lay : ConstraintLayout? = null
    var back_documents : CardView? = null
    override fun setLayout(): Int {
        return R.layout.activity_document_list
    }

    override fun Initialize() {
        driver_back_image_lay = findViewById(R.id.driver_back_image_lay)
        driver_front_image_lay = findViewById(R.id.driver_front_image_lay)
        back_documents = findViewById(R.id.back_documents)
        driver_back_image_lay!!.setOnClickListener {
            showImage(SessionSave.getSession("driver_lic_back",this@DocumentListActivity))
        }
        driver_front_image_lay!!.setOnClickListener {
            showImage(SessionSave.getSession("driver_lic_front",this@DocumentListActivity))
        }
        back_documents!!.setOnClickListener {
           onBackPressed()
        }
    }

    fun showImage(imgPath: String) {
        val builder = AlertDialog.Builder(this,R.style.CustomAlertDialog)
            .create()
        val view = layoutInflater.inflate(R.layout.imageviewlay,null)
        val  imageview = view.findViewById<ImageView>(R.id.dialog_imageview)
        builder.setView(view)
        Picasso.get().load(imgPath).placeholder(resources.getDrawable(R.drawable.loadingimage))
            .error(
                resources.getDrawable(R.drawable.noimage)
            ).into(imageview)

        imageview.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }
}