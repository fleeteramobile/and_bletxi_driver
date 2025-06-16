package com.onewaytripcalltaxi.driver;

import android.content.Intent;
import android.util.Log;

import com.google.android.libraries.places.api.Places;
import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.service.CoreClient;
import com.onewaytripcalltaxi.driver.service.NodeServiceGenerator;
import com.onewaytripcalltaxi.driver.service.ServiceGenerator;
import com.onewaytripcalltaxi.driver.utils.SessionSave;

import androidx.multidex.MultiDexApplication;
//import com.mapbox.mapboxsdk.Mapbox;


/**
 * Created by developer on 15/2/18.
 */


public class MyApplication extends MultiDexApplication {

    private static MyApplication mInstance;


    private CoreClient apiManagerWithBaseUrl, checkCompanyDomainapiManager, googleapiManager, apiManagerWithTimeoutWithEncrypt;
    private CoreClient apiManagerWithTimeoutWithoutEncrypt, nodeApiManagerWithTimeOut;

    private long nodeTimeOut = 0L;


    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
       /* if (BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                            .build());
        }*/
//        if (!SessionSave.getSession(CommonData.MAP_BOX_TOKEN, MyApplication.this).equals(""))
//            Mapbox.getInstance(MyApplication.this, SessionSave.getSession(CommonData.MAP_BOX_TOKEN, MyApplication.this));
//        else
//            Mapbox.getInstance(MyApplication.this, "pk.eyJ1IjoibmFuZGhpbmlzIiwiYSI6ImNqaGl0M3U0aDI5MXczYW8xZGY3bmxod3gifQ.CsQZTI8nf5ZDh8ES3Iu87g");

        mInstance = this;

       /* if (SessionSave.getSession(CommonData.GOOGLE_KEY, this).equals(""))
            SessionSave.saveSession(CommonData.GOOGLE_KEY, getString(R.string.googleID), this);*/

        setPlaceApiKey(getResources().getString(R.string.googleID));

    }

    public void handleUncaughtException(Thread thread, Throwable e) {
        String stackTrace = Log.getStackTraceString(e);
        String message = e.getMessage();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"nagarajan.s@ndot.in"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "App log file");
        intent.putExtra(Intent.EXTRA_TEXT, stackTrace);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        startActivity(intent);
    }

    public void setPlaceApiKey(String apiKey) {
        SessionSave.saveSession(CommonData.GOOGLE_KEY, apiKey, this);
        Places.initialize(this, apiKey);
    }


    public CoreClient getCheckCompanyDomainapiManager(String url) {
        checkCompanyDomainapiManager = ServiceGenerator.getRetrofitEncryptUrl(this, url).create(CoreClient.class);
        return checkCompanyDomainapiManager;
    }

    /**
     * Get the API Manager for calling API with Base url
     *
     * @return com.owayride.retrofit.apiManager instance
     */
    public CoreClient getApiManagerWithEncryptBaseUrl() {
        if (apiManagerWithBaseUrl == null) {
            apiManagerWithBaseUrl = ServiceGenerator.getRetrofitWithEncryptBaseUrl(this).create(CoreClient.class);
        }
        return apiManagerWithBaseUrl;
    }

    public CoreClient getApiManagerWithoutEncryptBaseUrl() {
        if (googleapiManager == null) {
            googleapiManager = ServiceGenerator.getRetrofitWithoutEncryptBaseUrl(this).create(CoreClient.class);
        }
        return googleapiManager;
    }

    public CoreClient getNodeApiManagerWithTimeOut(String base_url, long timeOut) {
        if (nodeTimeOut != timeOut) {
            nodeTimeOut = timeOut;
            nodeApiManagerWithTimeOut = null;
        }
        if (nodeApiManagerWithTimeOut == null) {
            nodeApiManagerWithTimeOut = NodeServiceGenerator.INSTANCE.nodeGetRetrofitWithTimeOut(this, base_url, nodeTimeOut).create(CoreClient.class);
        }
        return nodeApiManagerWithTimeOut;
    }

}