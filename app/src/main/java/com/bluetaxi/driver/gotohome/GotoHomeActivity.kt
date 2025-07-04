package com.bluetaxi.driver.gotohome

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.bluetaxi.driver.R
import com.bluetaxi.driver.data.CommonData
import com.bluetaxi.driver.interfaces.APIResult
import com.bluetaxi.driver.service.APIService_Retrofit_JSON
import com.bluetaxi.driver.utils.SessionSave
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Locale

class GotoHomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var confirmLocationButton: Button

    private var allAddresses: List<AddressData> = emptyList()
    private var autocompleteJob: Job? = null
    private var selectedLatLng: LatLng? = null
    var mytrip : SwitchCompat? = null
    var checked = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goto_home)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val apiKey = SessionSave.getSession(CommonData.GOOGLE_KEY, this)
        if (!Places.isInitialized()) Places.initialize(applicationContext, apiKey)
        placesClient = Places.createClient(this)
        mytrip = findViewById(R.id.mytrip)

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView)
        confirmLocationButton = findViewById(R.id.confirmLocationButton)

        supportFragmentManager.findFragmentById(R.id.map)
            ?.let { it as SupportMapFragment }
            ?.getMapAsync(this)

        confirmLocationButton.setOnClickListener {
            selectedLatLng?.let {
                val lat = it.latitude
                val lng = it.longitude
                val address = autoCompleteTextView.text.toString()

                SessionSave.saveSession("go_home_lat", lat.toString(), this)
                SessionSave.saveSession("go_home_lang", lng.toString(), this)

                Toast.makeText(this, "Confirmed:\n$address\nLat: $lat\nLng: $lng", Toast.LENGTH_LONG).show()
            } ?: Toast.makeText(this, "Location not selected", Toast.LENGTH_SHORT).show()
        }

        CoroutineScope(Dispatchers.IO).launch {
            allAddresses = ExcelReader(this@GotoHomeActivity).readAddressesFromExcel("addresses.xlsx")
            withContext(Dispatchers.Main) { setupAutoComplete() }
        }

        mytrip!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
            {
                checked = 1
                RequestingCheckBox()

            }
            else{

                checked = 0
                RequestingCheckBox()


            }

        }


    }

    override fun onResume() {
        super.onResume()
        showGoHomeStatus()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnCameraIdleListener {
            val center = map.cameraPosition.target
            val address = getAddressFromLatLng(this, center)
            selectedLatLng = center
            autoCompleteTextView.setText(address, false)
            confirmLocationButton.visibility = View.VISIBLE
        }

        val savedLat = SessionSave.getSession("go_home_lat", this)?.toDoubleOrNull()
        val savedLng = SessionSave.getSession("go_home_lang", this)?.toDoubleOrNull()

        if (savedLat != null && savedLng != null) {
            val savedLatLng = LatLng(savedLat, savedLng)
            moveMapCamera(savedLatLng)
        } else {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                moveMapCamera(currentLatLng)
            } ?: Toast.makeText(this, "Couldn't get current location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveMapCamera(latLng: LatLng) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    private fun setupAutoComplete() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            allAddresses.map { it.address }
        )
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(autoCompleteTextView.windowToken, 0)

            val selectedText = parent.getItemAtPosition(position) as String
            val matched = allAddresses.find { it.address == selectedText }

            if (matched != null) {
                val latLng = LatLng(matched.latitude, matched.longitude)
                selectedLatLng = latLng
                moveMapCamera(latLng)
            } else {
                findPlaceDetailsFromText(selectedText)
            }
        }

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    autocompleteJob?.cancel()
                    autocompleteJob = CoroutineScope(Dispatchers.Main).launch {
                        performAutocomplete(query)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun performAutocomplete(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setCountries("in")
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val suggestions = response.autocompletePredictions.map { it.getFullText(null).toString() }
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
                autoCompleteTextView.setAdapter(adapter)
                adapter.notifyDataSetChanged()
            }
    }

    private fun findPlaceDetailsFromText(address: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(address)
            .setCountries("in")
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                if (response.autocompletePredictions.isNotEmpty()) {
                    val placeId = response.autocompletePredictions.first().placeId
                    val fields = listOf(Place.Field.LAT_LNG, Place.Field.ADDRESS)
                    val fetchRequest = FetchPlaceRequest.builder(placeId, fields).build()

                    placesClient.fetchPlace(fetchRequest)
                        .addOnSuccessListener {
                            val latLng = it.place.latLng
                            latLng?.let {
                                selectedLatLng = it
                                moveMapCamera(it)
                            }
                        }
                }
            }
    }

    private fun getAddressFromLatLng(context: Context, latLng: LatLng): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                addressList[0].getAddressLine(0) ?: "Unknown Location"
            } else {
                "Unknown Location"
            }
        } catch (e: Exception) {
            "Unknown Location"
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        }
    }

    inner class RequestingCheckBox : APIResult {

        init {
            val j = JSONObject()
            j.put("driver_id", SessionSave.getSession("Id", this@GotoHomeActivity))
            j.put("go_home_enable", checked)
            j.put("go_home_lat", selectedLatLng!!.latitude.toString())
            j.put("go_home_lng", selectedLatLng!!.longitude.toString())

            val requestingCheckBox = "type=add_driver_go_home_info"

            APIService_Retrofit_JSON(this@GotoHomeActivity, this, j, false).execute(
                requestingCheckBox
            )
        }

        @SuppressLint("MissingPermission")
        override fun getResult(isSuccess: Boolean, result: String?) {
            if (isSuccess) {
                val mJSONObject = JSONObject(result)
                Toast.makeText(this@GotoHomeActivity,mJSONObject.getString("message"),Toast.LENGTH_LONG).show()

                if (mJSONObject.getInt("status") == 1) {



                    }



            }
        }
    }

    inner class showGoHomeStatus : APIResult {

        init {
            val j = JSONObject()
            j.put("driver_id", SessionSave.getSession("Id", this@GotoHomeActivity))


            val requestingCheckBox = "type=get_driver_go_home_info"

            APIService_Retrofit_JSON(this@GotoHomeActivity, this, j, false).execute(
                requestingCheckBox
            )
        }

        @SuppressLint("MissingPermission")
        override fun getResult(isSuccess: Boolean, result: String?) {
            if (isSuccess) {
                val mJSONObject = JSONObject(result)
                Toast.makeText(this@GotoHomeActivity,mJSONObject.getString("message"),Toast.LENGTH_LONG).show()

                if (mJSONObject.getInt("status") == 1) {

                    val go_home_enable = mJSONObject.getInt("go_home_enable")
                    if (go_home_enable==1)
                    {
                        mytrip!!.isChecked = true
                    }
                    else{
                        mytrip!!.isChecked = false

                    }
                }
            }
        }
    }
}


