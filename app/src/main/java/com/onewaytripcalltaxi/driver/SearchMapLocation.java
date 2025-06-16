package com.onewaytripcalltaxi.driver;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.data.MapWrapperLayout;
import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.locationSearch.LocationSearchActivity;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.utils.CToast;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.FontHelper;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.NetworkStatus;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Systems;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

/**
 * Created by developer on 14/2/18.
 */

public class SearchMapLocation extends MainActivity implements View.OnClickListener, OnMapReadyCallback {
    public static boolean GEOCODE_EXPIRY = false;
    private static TextView TappedLocation;
    public String DropAddress = "";
    public Double droplatitude = 0.0, droplongitude = 0.0;
    public Address_s address;
    private ImageView btn_back;
    private Button Complete_trip;
    private GoogleMap googleMap;
    private MapWrapperLayout mapWrapperLayout;
    private LatLng dropLocation;
    private final String type = "";
    private LinearLayout lay_search;
    private static final String LocationRequestedBy = "P";


    /**
     * Get the google map pixels from xml density independent pixel.
     */
    public static int getPixelsFromDp(final Context context, final float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * Set fetching address
     */
    public static void setfetch_address() {
        TappedLocation.setText(R.string.fetching_address);
    }

    @Override
    public int setLayout() {
        return R.layout.search_map_location;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null && getIntent().getParcelableExtra("dropLocation") != null)
            dropLocation = getIntent().getParcelableExtra("dropLocation");

//        if (getIntent() != null && getIntent().getParcelableExtra("type") != null)
//            type = getIntent().getParcelableExtra("type");

        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
    }

    @Override
    public void Initialize() {
        FontHelper.applyFont(this, findViewById(R.id.select_drop_location));
        Colorchange.ChangeColor(findViewById(R.id.select_drop_location), this);
        btn_back = findViewById(R.id.back_icon);
        Complete_trip = findViewById(R.id.butt_onboard);
        //Complete_trip.setVisibility(View.GONE);
        TappedLocation = findViewById(R.id.tapped_location);
        lay_search = findViewById(R.id.lay_search);
        findViewById(R.id.header_titleTxt).setVisibility(View.GONE);
        Glide.with(this).load(SessionSave.getSession("image_path", this) + "headerLogo_driver.png").apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)).into((ImageView) findViewById(R.id.header_titleTxt));
        btn_back.setOnClickListener(this);
        Complete_trip.setOnClickListener(this);

        lay_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Bundle b = new Bundle();
                    b.putString("type", "P");
                    Intent i = new Intent(SearchMapLocation.this, LocationSearchActivity.class);
                    i.putExtras(b);
                    startActivityForResult(i, CommonData.LocationResult);


              /*  Bundle b = new Bundle();
                Intent i = new Intent(SearchMapLocation.this, LocationSearchActivity.class);
                if (!ispickup) {
                    b.putBoolean("onlyDrop", true);
                    b.putString("type", "D");
                    PlacesDetail drop_obj = new PlacesDetail();
                    drop_obj.setLatitude(latitude);
                    drop_obj.setLongtitute(longitude);
                    drop_obj.setLocation_name(tapped_location.getText().toString().trim().replaceAll("null", "").replaceAll(", ,", "")
                    );
                    i.putExtra("drop_obj", drop_obj);
                } else {
                    b.putString("type", "P");
                    PlacesDetail pickup_obj = new PlacesDetail();
                    pickup_obj.setLatitude(latitude);
                    pickup_obj.setLongtitute(longitude);
                    pickup_obj.setLocation_name(currentlocTxt.getText().toString().trim().replaceAll("null", "").replaceAll(", ,", "")
                    );
                    b.putParcelable("pickup_obj", pickup_obj);

                }
                b.putBoolean("onlyPickup", ispickup);
                i.putExtras(b);
                startActivityForResult(i, CommonData.LocationResult);*/
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_icon:
                finish();
                break;
            case R.id.butt_onboard:
                UpdateLocationData();
                break;
            default:
                break;
        }
    }

    private void UpdateLocationData() {
        if (!TappedLocation.getText().toString().equals("") && droplatitude != 0.0 && droplongitude != 0.0) {
            Bundle conData = new Bundle();
            conData.putString("param_result", TappedLocation.getText().toString());
            conData.putDouble("lat", droplatitude);
            conData.putDouble("lng", droplongitude);
            conData.putString("type", type);
            Intent intent = new Intent();
            intent.putExtras(conData);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        CameraUpdate cameraUpdate = null;
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setZoomControlsEnabled(false);
        MapsInitializer.initialize(SearchMapLocation.this);
        mapWrapperLayout = findViewById(R.id.map_relative_layout);
        mapWrapperLayout.init(googleMap, getPixelsFromDp(SearchMapLocation.this, 39 + 20));
        mapWrapperLayout.setVisibility(View.VISIBLE);

        if (dropLocation != null && dropLocation.latitude != 0.0) {
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(dropLocation.latitude, dropLocation.longitude), 16f);
            if (address != null)
                address.cancel(true);
            address = new Address_s(SearchMapLocation.this, new LatLng(dropLocation.latitude, dropLocation.longitude));
            address.execute();

        } else if (!SessionSave.getSession(CommonData.SOS_LAST_LAT, SearchMapLocation.this).equals("")) {
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(
                    Double.parseDouble(SessionSave.getSession(CommonData.SOS_LAST_LAT, SearchMapLocation.this))
                    , Double.parseDouble(SessionSave.getSession(CommonData.SOS_LAST_LNG, SearchMapLocation.this))), 16f);
            if (address != null)
                address.cancel(true);
            address = new Address_s(SearchMapLocation.this, new LatLng(Double.parseDouble(SessionSave.getSession(CommonData.SOS_LAST_LAT, SearchMapLocation.this)), Double.parseDouble(SessionSave.getSession(CommonData.SOS_LAST_LNG, SearchMapLocation.this))));
            address.execute();
        }
        if (cameraUpdate != null)
            googleMap.moveCamera(cameraUpdate);

        droplatitude = googleMap.getCameraPosition().target.latitude;
        droplongitude = googleMap.getCameraPosition().target.longitude;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                enableListener();
            }
        }, 1000);


        // googleMap.setOnMapClickListener(this);
    }


    public void enableListener() {
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.e("camera", "==camera idle==" + googleMap.getCameraPosition().target);
                droplatitude = googleMap.getCameraPosition().target.latitude;
                droplongitude = googleMap.getCameraPosition().target.longitude;
                if (address != null)
                    address.cancel(true);
                address = new Address_s(SearchMapLocation.this, new LatLng(droplatitude, droplongitude));
                address.execute();

            }
        });

        googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {
                setfetch_address();
            }


        });
    }


//    @Override
//    public void onMapClick(LatLng latLng) {
//        if (googleMap != null) {
//            googleMap.clear();
//            googleMap.addMarker(new MarkerOptions()
//                    .position(latLng)
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//            Complete_trip.setVisibility(View.VISIBLE);
//            if (address != null)
//                address.cancel(true);
//            address = new Address_s(SearchMapLocation.this, new LatLng(latLng.latitude, latLng.longitude));
//            address.execute();
//        }
//    }

    /**
     * Address parsing from given lat and long
     */
    public class Address_s extends AsyncTask<String, String, String> {
        public Context mContext;
        LatLng mPosition;
        String Address = "";
        Geocoder geocoder;
        List<android.location.Address> addresses = null;
        private final double latitude;
        private final double longitude;

        public Address_s(Context context, LatLng position) {
            showLoading(SearchMapLocation.this);
            mContext = context;
            mPosition = position;
            latitude = mPosition.latitude;
            longitude = mPosition.longitude;
            geocoder = new Geocoder(context, Locale.getDefault());
        }


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            SessionSave.saveSession("notes", "", mContext);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                Systems.out.println("address size11ssss:" + latitude + "%$#" + longitude);

                if (Geocoder.isPresent()) {
                    Systems.out.println("address size:" + latitude + "%$#" + longitude);
                    addresses = geocoder.getFromLocation(latitude, longitude, 3);
                    Systems.out.println("address size:" + addresses.size());
                    if (addresses.size() == 0) {
                        new convertLatLngtoAddressApi(mContext, latitude, longitude);
                    } else {
                        for (int i = 0; i < addresses.size(); i++) {
                            Address += addresses.get(0).getAddressLine(i) + ", ";
                        }
                        Systems.out.println("____________aaa" + Address);
                        if (Address.length() > 0) {
                            Address = Address.substring(0, Address.length() - 2);
                            try {
                                Address = Address.replaceAll("null", "").replaceAll(", ,", "").replaceAll(", ,", "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    if (NetworkStatus.isOnline(mContext))
                        new convertLatLngtoAddressApi(mContext, latitude, longitude);
                    else {
                        SearchMapLocation.setfetch_address();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                if (NetworkStatus.isOnline(mContext))
                    new convertLatLngtoAddressApi(mContext, latitude, longitude);
                else {
                    SearchMapLocation.setfetch_address();
                }
            }
            return Address;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (Address.length() != 0 && SearchMapLocation.this != null) {
                try {
                    Address = Address.replaceAll("null", "").replaceAll(", ,", "").replaceAll(", ,", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cancelLoading();
                TappedLocation.setText(Address);
                DropAddress = Address;
                droplatitude = latitude;
                droplongitude = longitude;
            } else {
                new convertLatLngtoAddressApi(mContext, latitude, longitude);

            }
            result = null;

        }

        /**
         * Set location and response parsing
         */
        public void setLocation(String inputJson) {

            try {
                cancelLoading();
                JSONObject object = new JSONObject(inputJson);
                JSONArray array = object.getJSONArray("results");
                if (array.length() > 0) {
                    object = array.getJSONObject(0);
                    Systems.out.println("address size11:" + latitude + "%$#" + longitude + object.getString("formatted_address"));
                    TappedLocation.setText(Address);
                    DropAddress = Address;
                    droplatitude = latitude;
                    droplongitude = longitude;
                } else
                    DropAddress = "Tap your current Location on Map";
                droplongitude = 0.0;
                droplatitude = 0.0;
                GEOCODE_EXPIRY = true;
                CToast.ShowToast(mContext, NC.getString(R.string.c_tryagain));

            } catch (Exception ex) {
                ex.printStackTrace();
                cancelLoading();
                DropAddress = NC.getString(R.string.tap_drop);
                droplongitude = 0.0;
                droplatitude = 0.0;
                GEOCODE_EXPIRY = true;
                if (mContext != null)
                    if (!NetworkStatus.isOnline(mContext))
                        CToast.ShowToast(mContext, NC.getString(R.string.check_internet));
                    else
                        CToast.ShowToast(mContext, NC.getString(R.string.c_tryagain));
                SearchMapLocation.setfetch_address();
            }
        }

        /**
         * Convert lat and lon to address
         */
        public class convertLatLngtoAddressApi implements APIResult {
            public convertLatLngtoAddressApi(Context c, double lati, double longi) {

                if (GEOCODE_EXPIRY) {
                    String googleMapUrl = SessionSave.getSession("base_url", SearchMapLocation.this) + "?type=google_geocoding";
                    try {
                        JSONObject j = new JSONObject();
                        j.put("origin", lati + "," + longi);
                        j.put("type", "2");
                        new APIService_Retrofit_JSON(c, this, j, false, googleMapUrl, false).execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    String googleMapUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lati + "," + longi + "&sensor=false";
                    new APIService_Retrofit_JSON(c, this, null, true, googleMapUrl, true).execute();
                }
            }

            @Override
            public void getResult(boolean isSuccess, String result) {
                if (result != null)
                    setLocation(result);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String result = "", Address;
        Double obtainedlatitude, obtainedlongitude;
        try {
            double lat = 0.0, lng = 0.0;
            try {
                // if (OnmywayActivity.this != null)
                //  startLocationUpdates();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (data != null) {
                Bundle res = data.getExtras();
                result = res.getString("param_result");
                lat = res.getDouble("lat");
                lng = res.getDouble("lng");
            }
            if (LocationRequestedBy.equals("P") && result != null && !result.trim().equals("")) {
                TappedLocation.setText(result);
                droplatitude = lat;
                droplongitude = lng;

                LatLng latLng = new LatLng(droplatitude, droplongitude);
                if (googleMap != null)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
