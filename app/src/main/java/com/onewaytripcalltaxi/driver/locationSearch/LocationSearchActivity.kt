package com.onewaytripcalltaxi.driver.locationSearch

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.onewaytripcalltaxi.driver.BaseActivity
import com.onewaytripcalltaxi.driver.R
import com.onewaytripcalltaxi.driver.utils.Colorchange
import com.onewaytripcalltaxi.driver.utils.SessionSave
import kotlinx.android.synthetic.main.activity_location_search.*

class LocationSearchActivity : BaseActivity(), SetPlaceResult {
    private var isFourSquare = false

    private lateinit var locationSearchFragment: LocationSearchFragment
    private lateinit var listener: OnLocationSearched
    private lateinit var edLocation:EditText
    override fun onPlaceSelected(placesDetail: PlacesDetail) {
        setResult(RESULT_OK, Intent().apply {
            putExtras(Bundle().apply {
                putExtra("param_result", placesDetail.location_name)
                putExtra("lat", placesDetail.latitude)
                putExtra("lng", placesDetail.longtitute)
            })
        })
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_search)
        edLocation = findViewById(R.id.edLocation)
        edLocation.hint = "Enter Your Location"
        Colorchange.ChangeColor((this@LocationSearchActivity
                .findViewById(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup, this@LocationSearchActivity)
        locationSearchFragment = LocationSearchFragment()
        listener = locationSearchFragment
        supportFragmentManager.beginTransaction().add(R.id.searchFrag, locationSearchFragment).commitNow()

        isFourSquare = SessionSave.getSession("isFourSquare", this) == "1"
    }

    override fun onResume() {
        super.onResume()
        val textWatcher = object : TextWatcher {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listener.onLocationSearched(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                imgClearSearch.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable) {
                imgClearSearch.visibility = View.VISIBLE
            }
        }
        edLocation.addTextChangedListener(textWatcher)

        imgClearSearch.setOnClickListener {
            edLocation.setText("")
        }

        imgBackButton.setOnClickListener {
            onBackPressed()

        }

    }
}
