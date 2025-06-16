package com.onewaytripcalltaxi.driver;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.FontHelper;
import com.onewaytripcalltaxi.driver.utils.NetworkStatus;

/**
 * this class is used to show TermsAndConditions of the application
 *
 * @author ndot
 */
public class TermsAndConditions extends BaseActivity {
    TextView back_text;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.termsandconditions);
        NetworkStatus.appContext = this;
        Initialize();
    }

    /**
     * this method is used for fields declaration
     */
    @SuppressLint("NewApi")
    public void Initialize() {
        Colorchange.ChangeColor((ViewGroup) (((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0)), TermsAndConditions.this);
        CommonData.sContext = this;
        CommonData.mActivitylist.add(this);
        FontHelper.applyFont(this, findViewById(R.id.terms_contain));

        back_text = findViewById(R.id.slideImg);
        back_text.setVisibility(View.VISIBLE);

        back_text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            TextView title = findViewById(R.id.headerTxt);
            title.setText(bundle.getString("name"));
            WebView webview = findViewById(R.id.webview);
            String pish = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/" + FontHelper.FONT_TYPEFACE + "\")}body {font-family: MyFont;font-size: medium;text-align: justify;}</style></head><body>";
            String pas = "</body></html>";
            String myHtmlString = pish + getIntent().getExtras().getString("content") + pas;
            webview.loadDataWithBaseURL("file:///android_asset/", myHtmlString, "text/html", "UTF-8", null);
            WebSettings webSettings = webview.getSettings();
            webSettings.setDefaultFontSize(14);

        }
    }




    @Override
    protected void onDestroy() {
        CommonData.mActivitylist.remove(this);
        super.onDestroy();
    }
}