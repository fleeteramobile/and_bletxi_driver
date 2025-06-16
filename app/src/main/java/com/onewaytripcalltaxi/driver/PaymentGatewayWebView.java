package com.onewaytripcalltaxi.driver;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.utils.CToast;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.SessionSave;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentGatewayWebView extends Activity {


    private WebView paymentwebView;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.paymentgatewaywebview);

        paymentwebView = findViewById(R.id.paymentwebView);


        paymentwebView.setWebViewClient(new MyWebViewClient());
        paymentwebView.getSettings().setJavaScriptEnabled(true);

        paymentwebView.loadUrl(SessionSave.getSession("payment_url", PaymentGatewayWebView.this));


    }


    private class MyWebViewClient extends WebViewClient {


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, final String url) {
            Log.e("testing", url);

            if (url.contains("sybertechnology") && url.contains("process")) {

                CheckPaymentStatus();

            } else {
                view.loadUrl(url);
            }

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (url.contains("sybertechnology") && url.contains("process")) {

                CheckPaymentStatus();

            }

        }

        @Override
        public void onLoadResource(WebView view, String url) {

        }

        public void onPageStarted(WebView webView, String url, Bitmap favicon) {
            super.onPageStarted(webView, url, favicon);

        }


    }

    private void CheckPaymentStatus() {
        try {
            JSONObject j = new JSONObject();
            j.put("TRANSACTIONID", SessionSave.getSession("transaction_id", PaymentGatewayWebView.this));
            final String url = "type=check_paymentstautus_syberpay";
            new PaymentCheck(url, j);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();

        }
    }

    private class PaymentCheck implements APIResult {
        private PaymentCheck(final String url, JSONObject data) {

            new APIService_Retrofit_JSON(PaymentGatewayWebView.this, this, data, false).execute(url);

        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            // TODO Auto-generated method stub
            if (isSuccess) {
                try {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
//                        dialog = Utils.alert_view_dialog(PaymentGatewayWebView.this, "Message", "" + json.getString("message"),
//                                "" + NC.getResources().getString(R.string.ok), "", true, (dialog, which) -> {

                        Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_LONG).show();

                        Intent jobintent = new Intent(PaymentGatewayWebView.this, JobdoneAct.class);
                        Bundle bun = new Bundle();
                        bun.putString("message", result);
                        jobintent.putExtras(bun);
                        startActivity(jobintent);


                        //  }, (dialog, which) -> dialog.dismiss(), "");

                    } else {
//                        dialog = Utils.alert_view_dialog(PaymentGatewayWebView.this, "Message", "" + json.getString("message"),
//                                "" + NC.getResources().getString(R.string.ok), "", true, (dialog, which) -> {

                        Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(PaymentGatewayWebView.this, OngoingAct.class);
                        startActivity(intent);
                        finish();

                        //        }, (dialog, which) -> dialog.dismiss(), "");

                    }

                } catch (final JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                PaymentGatewayWebView.this.runOnUiThread(() -> CToast.ShowToast(PaymentGatewayWebView.this, NC.getString(R.string.server_error)));
            }
        }
    }
}
