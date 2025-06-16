package com.onewaytripcalltaxi.driver.permission;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onewaytripcalltaxi.driver.MainActivity;
import com.onewaytripcalltaxi.driver.R;
import com.onewaytripcalltaxi.driver.utils.FontHelper;


public class StoreAndSecureActivity extends MainActivity implements View.OnClickListener {

    private TextView collect_information_desc_tv, information_usage_desc_tv, protect_information_desc_tv, know_information_desc_tv;
    private Boolean isCollectInformation = false, isInformationUsage = false, isProtectInformation = false, isKnowInformation = false;
    private ImageView collect_information_iv, information_usage_iv, protect_information_iv, know_information_iv, back_btn;
    private TextView tvProtectYourData,headerTxt,tvUseData,tvPrivacyPolicy,tvCollectData,tvUseItFor,tvRights;


    @Override
    public int setLayout() {
        return R.layout.activity_store_and_secure;
    }

    @Override
    public void Initialize() {
        back_btn = findViewById(R.id.back_btn);
        collect_information_iv = findViewById(R.id.collect_information_iv);
        information_usage_iv = findViewById(R.id.information_usage_iv);
        protect_information_iv = findViewById(R.id.protect_information_iv);
        know_information_iv = findViewById(R.id.know_information_iv);

        collect_information_desc_tv = findViewById(R.id.collect_information_desc_tv);
        information_usage_desc_tv = findViewById(R.id.information_usage_desc_tv);
        protect_information_desc_tv = findViewById(R.id.protect_information_desc_tv);
        know_information_desc_tv = findViewById(R.id.know_information_desc_tv);
        FontHelper.applyFont(this, findViewById(R.id.storeAndSecureParentLay));

        headerTxt=findViewById(R.id.headerTxt);
        tvUseData=findViewById(R.id.tvUseData);
        tvPrivacyPolicy=findViewById(R.id.tvPrivacyPolicy);
        tvCollectData=findViewById(R.id.tvCollectData);
        tvUseItFor=findViewById(R.id.tvUseItFor);
        tvProtectYourData=findViewById(R.id.tvProtectYourData);
        tvRights=findViewById(R.id.tvRights);
//        headerTxt.setTypeface(MyApplication.getInstance().getTypeFace(0));
//        tvUseData.setTypeface(MyApplication.getInstance().getTypeFace(0));
//        tvPrivacyPolicy.setTypeface(MyApplication.getInstance().getTypeFace(0));
//        tvCollectData.setTypeface(MyApplication.getInstance().getTypeFace(0));
//        tvUseItFor.setTypeface(MyApplication.getInstance().getTypeFace(0));
//        tvProtectYourData.setTypeface(MyApplication.getInstance().getTypeFace(0));
//        tvRights.setTypeface(MyApplication.getInstance().getTypeFace(0));

        collect_information_iv.setOnClickListener(this);
        information_usage_iv.setOnClickListener(this);
        protect_information_iv.setOnClickListener(this);
        know_information_iv.setOnClickListener(this);



        back_btn.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.collect_information_iv:{
                if(isCollectInformation) {
                    collect_information_desc_tv.setMaxLines(2);
                    collect_information_iv.setImageDrawable(getResources().getDrawable(R.drawable.plus_icon));
                    isCollectInformation = false;
                }
                else {
                    collect_information_desc_tv.setMaxLines(Integer.MAX_VALUE);
                    collect_information_iv.setImageDrawable(getResources().getDrawable(R.drawable.minus_icon));
                    isCollectInformation = true;
                }
                break;
            }

            case R.id.information_usage_iv:{
                if(isInformationUsage) {
                    information_usage_desc_tv.setMaxLines(2);
                    information_usage_iv.setImageDrawable(getResources().getDrawable(R.drawable.plus_icon));
                    isInformationUsage = false;
                }
                else {
                    information_usage_desc_tv.setMaxLines(Integer.MAX_VALUE);
                    information_usage_iv.setImageDrawable(getResources().getDrawable(R.drawable.minus_icon));
                    isInformationUsage = true;
                }
                break;
            }

            case R.id.protect_information_iv:{
                if(isProtectInformation) {
                    protect_information_desc_tv.setMaxLines(2);
                    protect_information_iv.setImageDrawable(getResources().getDrawable(R.drawable.plus_icon));
                    isProtectInformation = false;
                }
                else {
                    protect_information_desc_tv.setMaxLines(Integer.MAX_VALUE);
                    protect_information_iv.setImageDrawable(getResources().getDrawable(R.drawable.minus_icon));
                    isProtectInformation = true;
                }
                break;
            }

            case R.id.know_information_iv:{
                if(isKnowInformation) {
                    know_information_desc_tv.setMaxLines(2);
                    know_information_iv.setImageDrawable(getResources().getDrawable(R.drawable.plus_icon));
                    isKnowInformation = false;
                }
                else {
                    know_information_desc_tv.setMaxLines(Integer.MAX_VALUE);
                    know_information_iv.setImageDrawable(getResources().getDrawable(R.drawable.minus_icon));
                    isKnowInformation = true;
                }
                break;
            }

        }
    }

}
