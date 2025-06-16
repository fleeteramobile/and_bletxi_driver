package com.onewaytripcalltaxi.driver.locationSearch

import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.onewaytripcalltaxi.driver.R
import com.onewaytripcalltaxi.driver.RecyclerItemClickListener
import com.onewaytripcalltaxi.driver.utils.Colorchange
import com.onewaytripcalltaxi.driver.utils.SessionSave
import org.json.JSONArray
import java.util.*


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 *
 */
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

class LocationSearchFragment : Fragment(), OnLocationSearched, PlaceSearchList {
    override fun onItemClicked(placesDetail: PlacesDetail) {
        //No need to handle anything here
    }

    override fun setPlaceList(placeDetailResult: ArrayList<PlacesDetail>?) {
        mAutoCompleteAdapter.submitList(placeDetailResult)
    }

    override fun setPlaceDetail(placeDetail: PlacesDetail) {
        listener?.onPlaceSelected(placeDetail)
    }

    private lateinit var onPlaceSearchedListener: OnLocationSearched
    override fun onLocationSearched(queryString: String) {
        if (queryString.length > 2)
            onPlaceSearchedListener.onLocationSearched(queryString)
        else
            mAutoCompleteAdapter.submitList(favouritesList)
    }

    private var isFourSquare = false
    private var listener: SetPlaceResult? = null
    private lateinit var mAutoCompleteAdapter: PlacesAutoCompleteAdapter
    private var favouritesList: ArrayList<PlacesDetail> = ArrayList()
    private lateinit var rvLocationItems: RecyclerView
    private lateinit var imgPoweredBy:AppCompatImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val mView = inflater.inflate(R.layout.fragment_location_search, container, false)
        Colorchange.ChangeColor(mView as ViewGroup, requireActivity())
        rvLocationItems = mView.findViewById(R.id.rvLocationItems)
        imgPoweredBy = mView.findViewById(R.id.imgPoweredBy)
        return  mView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SetPlaceResult) {
            listener = context
        } else {
            throw RuntimeException("$context must implement SetPlaceResult")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let { context ->
            favouritesList = getFavouritesList(context)
            if (SessionSave.getSession("isFourSquare", context) == "1") {
                isFourSquare = true
            }
            onPlaceSearchedListener = if (isFourSquare) {
                imgPoweredBy.visibility = View.GONE
                FourSquarePlaceRepository(context, this@LocationSearchFragment)
            } else {
                imgPoweredBy.visibility = View.VISIBLE
                GooglePlaceRepository(context, this@LocationSearchFragment)
            }


            mAutoCompleteAdapter = PlacesAutoCompleteAdapter(context)
            mAutoCompleteAdapter.submitList(favouritesList)
            rvLocationItems.adapter = mAutoCompleteAdapter

            rvLocationItems.addOnItemTouchListener(
                    RecyclerItemClickListener(context, RecyclerItemClickListener.OnItemClickListener { views, position ->
                        val view = activity?.currentFocus
                        if (view != null) {
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                            if (imm.isAcceptingText) {
                                val im = context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                                im.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                            }
                        }
                        if (mAutoCompleteAdapter.getItem(position) != null)
                            onPlaceSearchedListener.onItemClicked(mAutoCompleteAdapter.getItem(position))
                    })
            )
        }
    }

    private fun getFavouritesList(context: Context): ArrayList<PlacesDetail> {
        val favouritesList = ArrayList<PlacesDetail>()
        if (!SessionSave.getSession("popular_places", context).isNullOrEmpty()) {
            try {
                val popularPlaces = JSONArray(SessionSave.getSession("popular_places", context))
                for (i in 0 until popularPlaces.length()) {
                    val jo = popularPlaces.getJSONObject(i)
                    favouritesList.add(PlacesDetail().apply {
                        setLabel_name(jo.getString("label_name"))
                        setLatitude(jo.getDouble("latitude"))
                        setLongtitute(jo.getDouble("longtitute"))
                        setLocation_name(jo.getString("location_name"))
                        setAndroid_image_unfocus(jo.getString("android_icon"))
                        setPlaceId("")
                        setPlaceType(1)
                    })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return favouritesList
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
