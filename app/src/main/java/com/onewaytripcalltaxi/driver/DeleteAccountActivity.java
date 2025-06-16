package com.onewaytripcalltaxi.driver;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.onewaytripcalltaxi.driver.utils.NC;

public class DeleteAccountActivity extends  MainActivity {
    TextView deleteacc;
    private TextView HeadTitle;
    private CardView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_delete_account;
    }

    @Override
    public void Initialize() {
//        Colorchange.ChangeColor((ViewGroup) (((ViewGroup) DeleteAccountActivity.this
//                .findViewById(android.R.id.content)).getChildAt(0)), DeleteAccountActivity.this);

        deleteacc= findViewById(R.id.deleteacc);
        HeadTitle = findViewById(R.id.headerTxt);
        btn_back = findViewById(R.id.back_page);
        btn_back.setVisibility(View.VISIBLE);
        HeadTitle.setText(NC.getString(R.string.privacy_settings));
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        deleteacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(DeleteAccountActivity.this, WebviewAct.class);
                in.putExtra("type", "delete");
                startActivity(in);
            }
        });


    }

}
