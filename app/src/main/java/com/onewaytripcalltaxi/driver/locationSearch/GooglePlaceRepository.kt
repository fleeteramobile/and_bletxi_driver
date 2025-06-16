package com.onewaytripcalltaxi.driver.locationSearch

import android.content.Context
import android.widget.Filter
import android.widget.Filterable
import com.onewaytripcalltaxi.driver.R
import com.onewaytripcalltaxi.driver.data.CommonData
import com.onewaytripcalltaxi.driver.utils.SessionSave
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.cos


class GooglePlaceRepository(val mContext: Context, val listener: PlaceSearchList) : OnLocationSearched, Filterable {
    private var mResultList: ArrayList<PlacesDetail>? = null
    private var mBounds: RectangularBounds? = null

    private var placesClient: PlacesClient
    private val token: AutocompleteSessionToken

    init {
        if (SessionSave.getSession(CommonData.SOS_LAST_LAT, mContext) != "") {
            try {
                val lat = java.lang.Double.parseDouble(SessionSave.getSession(CommonData.SOS_LAST_LAT, mContext))
                val lon = java.lang.Double.parseDouble(SessionSave.getSession(CommonData.SOS_LAST_LNG, mContext))
                mBounds = RectangularBounds.newInstance(
                        getBoundingBox(lat, lon, 50000).southwest,
                        getBoundingBox(lat, lon, 50000).northeast)
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }

        } else
            mBounds = null

        if (!Places.isInitialized())
         //   Places.initialize(mContext, SessionSave.getSession(CommonData.GOOGLE_KEY, mContext))
            Places.initialize(mContext,mContext.resources.getString(R.string.googleID))
        placesClient = Places.createClient(mContext)
        token = AutocompleteSessionToken.newInstance()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint != null) {
                    mResultList = getAutocomplete(constraint)
                    if (mResultList != null) {
                        results.values = mResultList
                        results.count = mResultList?.size ?: 0

                    }
                }
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults?) {

            }
        }
    }

    override fun onLocationSearched(queryString: String) {
        this.filter.filter(queryString)
    }

    override fun onItemClicked(placesDetail: PlacesDetail) {
        if (placesDetail.getPlaceType() == 1) {

            listener.setPlaceDetail(PlacesDetail().apply {
                location_name = placesDetail.getLocation_name()
                label_name = placesDetail.getLabel_name()
                latitude = placesDetail.getLatitude()
                longtitute = placesDetail.getLongtitute()
                placeId = placesDetail.getPlaceId()
            })

        } else {
            val placeId = placesDetail.placeId.toString()
            val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            val crequest = FetchPlaceRequest.builder(placeId, placeFields).build()
            placesClient.fetchPlace(crequest).addOnSuccessListener {
                if (it != null) {
                    val places = it.place
                    places.latLng?.let { latLng ->
                        listener.setPlaceDetail(PlacesDetail().apply {
                            location_name = places.address.toString()
                            label_name = places.name.toString()
                            latitude = latLng.latitude
                            longtitute = latLng.longitude
                            this.placeId = placeId
                        })
                    }
                } else {
                    //CToast.ShowToast(mContext, NC.getString(R.string.server_error))
                }
            }.addOnFailureListener {
                it.printStackTrace()
                //CToast.ShowToast(mContext, NC.getString(R.string.server_error))
            }
        }
    }

    private fun getAutocomplete(constraint: CharSequence): ArrayList<PlacesDetail> {
        val resultList = ArrayList<PlacesDetail>()
        val request = FindAutocompletePredictionsRequest.builder().let {
            it.setLocationBias(mBounds)
            it.setSessionToken(token)
            it.setQuery(constraint.toString())

            if (SessionSave.getSession("country_iso_code", mContext) != "") {
                it.setCountry(SessionSave.getSession("country_iso_code", mContext))
            }
            it.build()
        }

        val task = placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            for (prediction in response.autocompletePredictions) {
                resultList.add(PlacesDetail().apply {
                    setLabel_name(prediction.getPrimaryText(null).toString())
                    setLocation_name(prediction.getSecondaryText(null).toString())
                    setPlaceId(prediction.placeId)
                    setPlaceType(0)
                })
            }

            if (resultList.count() > 0)
                listener.setPlaceList(resultList)
            else
                listener.setPlaceList(null)

        }.addOnFailureListener { exception ->
            exception.printStackTrace()
            resultList.clear()
            listener.setPlaceList(null)
        }
        try {
            Tasks.await(task, 5, TimeUnit.SECONDS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resultList
    }

    private fun getBoundingBox(pLatitude: Double, pLongitude: Double, pDistanceInMeters: Int): LatLngBounds {

        val boundingBox = DoubleArray(4)

        val latRadian = Math.toRadians(pLatitude)

        val degLatKm = 110.574235
        val degLongKm = 110.572833 * cos(latRadian)
        val deltaLat = pDistanceInMeters.toDouble() / 1000.0 / degLatKm
        val deltaLong = pDistanceInMeters.toDouble() / 1000.0 /
                degLongKm

        val minLat = pLatitude - deltaLat
        val minLong = pLongitude - deltaLong
        val maxLat = pLatitude + deltaLat
        val maxLong = pLongitude + deltaLong

        boundingBox[0] = minLat
        boundingBox[1] = minLong
        boundingBox[2] = maxLat
        boundingBox[3] = maxLong
        return LatLngBounds(LatLng(minLat, minLong), LatLng(maxLat, maxLong))
    }

}