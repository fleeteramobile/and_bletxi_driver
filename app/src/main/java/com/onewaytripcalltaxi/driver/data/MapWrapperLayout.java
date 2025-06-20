package com.onewaytripcalltaxi.driver.data;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.onewaytripcalltaxi.driver.utils.Systems;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MapWrapperLayout extends RelativeLayout {
    /**
     * Reference to a GoogleMap object
     */
    private GoogleMap map;
    /**
     * Vertical offset in pixels between the bottom edge of our InfoWindow and the marker position (by default it's bottom edge too). It's a good idea to use custom markers and also the InfoWindow frame, because we probably can't rely on the sizes of the default marker and frame.
     */
    private int bottomOffsetPixels;
    /**
     * A currently selected marker
     */
    private Marker marker;
    /**
     * Our custom view which is returned from either the InfoWindowAdapter.getInfoContents or InfoWindowAdapter.getInfoWindow
     */
    private View infoWindow;
    private final Context mContext;
    private final Handler handler = new Handler();
    private static boolean mMapIsTouched = true;
    private Runnable mRunnable;
    private Handler mHandler;

    public MapWrapperLayout(Context context) {
        super(context);
        mContext = context;
    }

    public MapWrapperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public MapWrapperLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    /**
     * Must be called before we can route the touch events
     */
    public void init(GoogleMap map, int bottomOffsetPixels) {
        this.map = map;
        this.bottomOffsetPixels = bottomOffsetPixels;


        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                setmMapIsTouched(true);
            }
        };
    }

    /**
     * Best to be called from either the InfoWindowAdapter.getInfoContents or InfoWindowAdapter.getInfoWindow.
     */
    public void setMarkerWithInfoWindow(Marker marker, View infoWindow) {
        this.marker = marker;
        this.infoWindow = infoWindow;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // ev.addBatch(1000, 100,0, 0, 0,0);
        boolean ret = false;
        // Make sure that the infoWindow is shown and we have all the needed
        // references
        if (marker != null && marker.isInfoWindowShown() && map != null) {
            // Get a marker position on the screen
            Point point = map.getProjection().toScreenLocation(marker.getPosition());
            // Make a copy of the MotionEvent and adjust it's location
            // so it is relative to the infoWindow left top corner
            MotionEvent copyEv = MotionEvent.obtain(ev);
            copyEv.offsetLocation(-point.x + (infoWindow.getWidth() / 2), -point.y + infoWindow.getHeight() + bottomOffsetPixels);
            // Dispatch the adjusted MotionEvent to the infoWindow
            ret = infoWindow.dispatchTouchEvent(copyEv);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setmMapIsTouched(false);
                Systems.out.println("myyyodee" + "googlemap touched");
                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(mRunnable);
                mHandler.postDelayed(mRunnable, 10000);
                break;
        }


        // If the infoWindow consumed the touch event, then just return true.
        // Otherwise pass this event to the super class and return it's result
        return ret || super.dispatchTouchEvent(ev);
    }

    public static boolean ismMapIsTouched() {
        return mMapIsTouched;
    }

    public static void setmMapIsTouched(boolean mMapIsToucheds) {
        mMapIsTouched = mMapIsToucheds;
    }

}