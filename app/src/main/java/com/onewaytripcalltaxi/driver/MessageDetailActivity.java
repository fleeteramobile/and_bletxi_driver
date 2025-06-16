package com.onewaytripcalltaxi.driver;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.FontHelper;

public class MessageDetailActivity extends MainActivity  {

    private TextView  message_Txt,time_Txt,back_btn;
    @Override
    public int setLayout() {
        return R.layout.inbox_detail_lay;
    }

    @Override
    public void Initialize() {

        CommonData.mActivitylist.add(this);
        CommonData.sContext = this;
        CommonData.current_act = "Inboxdetail";

        Colorchange.ChangeColor((ViewGroup) (((ViewGroup) MessageDetailActivity.this
                .findViewById(android.R.id.content)).getChildAt(0)), MessageDetailActivity.this);

        FontHelper.applyFont(this, findViewById(R.id.slide_lay));
        message_Txt=findViewById(R.id.message_Txt);
        time_Txt=findViewById(R.id.time_Txt);
        back_btn=findViewById(R.id.back_btn);


        Bundle bun = getIntent().getExtras();
        if (bun != null) {
            message_Txt.setText(bun.getString("message"));
            time_Txt.setText(bun.getString("time"));

        }

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
