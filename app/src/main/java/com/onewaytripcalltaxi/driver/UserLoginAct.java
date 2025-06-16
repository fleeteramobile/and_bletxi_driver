package com.onewaytripcalltaxi.driver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onewaytripcalltaxi.driver.Login.LoginActivity;
import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.interfaces.ClickInterface;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.service.LocationUpdate;
import com.onewaytripcalltaxi.driver.utils.CL;
import com.onewaytripcalltaxi.driver.utils.CToast;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.FontHelper;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.NetworkStatus;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Systems;
import com.onewaytripcalltaxi.driver.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

//import com.taximobility.driver.utils.DeviceUtils;

/**
 * This is the main Login page
 */
public class UserLoginAct extends MainActivity implements ClickInterface, TextView.OnEditorActionListener {
    private static boolean FORCE_LOGIN = false;
    Dialog mDialog;
    private EditText PhoneEdt;
    private EditText PasswordEdt;
    private TextView ForgotTxt;

    //LinearLayout signup_web;
    private LinearLayout DoneBtn;
    private ImageView hidePwd;
    private String phone;
    private String password;
    private SplashAct mSplash;
    private String alert_msg;
    private Bundle alert_bundle = new Bundle();
    private Dialog mDialogs;
    private JSONObject jsonDriver;
    private boolean isReferalSuccess;
    private Dialog dialog1;
    private Button choose_language;
    private Dialog mlangDialog;
    private final int types = 1;
    private final String value="1";
    String token = "";

private TextView sign_up_new;
    // Set the layout to activity.
    @Override
    public int setLayout() {
        setLocale();

        return R.layout.signin;
    }

    /**
     * Initialize the views on layout
     */
    @Override
    public void Initialize() {
        mSplash = new SplashAct();
        alert_bundle = getIntent().getExtras();
        Systems.out.println("values_bundle");
        Colorchange.ChangeColor((ViewGroup) (((ViewGroup) UserLoginAct.this
                .findViewById(android.R.id.content)).getChildAt(0)), UserLoginAct.this);


        if (alert_bundle != null) {
            alert_msg = alert_bundle.getString("alert_message");
        }


        if (alert_msg != null && alert_msg.length() != 0)
            dialog1 = Utils.alert_view(UserLoginAct.this, NC.getResources().getString(R.string.message), alert_msg, NC.getResources().getString(R.string.ok), "", true, UserLoginAct.this, "");


        FontHelper.applyFont(this, findViewById(R.id.signinlayout));
        CommonData.current_act = "SplashAct";

        PhoneEdt = findViewById(R.id.phoneEdt);
        DoneBtn = findViewById(R.id.doneBtn1);

        setSpannableTextView(findViewById(R.id.t_c_web_txt));


        TextView HeadTitle = findViewById(R.id.header_titleTxt);



        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                 token = task.getResult();
                System.out.println("Token_new"+ " "+ token);

                // Log and toast
//                String msg = getString(R.string.msg_token_fmt, token);
//                Log.d(TAG, msg);
//                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
        if (!token.equals(""))
        {
            SessionSave.saveSession(CommonData.DEVICE_TOKEN,token, this);

        }












      //  LinearLayout leftIcontxt = findViewById(R.id.leftIconTxt);

//        TextView leftIcon = findViewById(R.id.leftIcon);
//        leftIcon.setVisibility(View.VISIBLE);
//        leftIcon.setBackgroundResource(R.drawable.back);

     //   HeadTitle.setText("" + NC.getResources().getString(R.string.signin));
       // Glide.with(this).load(SessionSave.getSession("image_path", this) + "signInLogo_driver.png").apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)).into((ImageView) findViewById(R.id.imageview));
        PasswordEdt = findViewById(R.id.passwordEdt);
        PasswordEdt.setOnEditorActionListener(this);
      //  signup_web = findViewById(R.id.signup_web);
       // choose_language=findViewById(R.id.choose_language);
        sign_up_new=findViewById(R.id.sign_up_new);
        ForgotTxt = findViewById(R.id.forgotpswdTxt);
        AtomicInteger c = new AtomicInteger(0);
        String mDeviceid= "";


        if (SessionSave.getSession("sDevice_id",  UserLoginAct.this).equals("")) {
            if (!UUID.randomUUID().toString().equals("")) {
                mDeviceid = UUID.randomUUID().toString();
            } else {
                mDeviceid = CommonData.mDevice_id_constant+ c.incrementAndGet();
            }
            SessionSave.saveSession("sDevice_id", mDeviceid, UserLoginAct.this);
        }

        if (CommonData.mDevice_id.equals("")) {
            if (!UUID.randomUUID().toString().equals("")) {
                mDeviceid = UUID.randomUUID().toString();
            } else {
                mDeviceid = CommonData.mDevice_id_constant+ c.incrementAndGet();
            }
            CommonData.mDevice_id = mDeviceid;
            SessionSave.saveSession("sDevice_id", mDeviceid, UserLoginAct.this);

        }else{

        }






        sign_up_new.setOnClickListener(v -> {

//LoginActivity
           Intent intent = new Intent(UserLoginAct.this, LoginActivity.class);
           // Intent intent = new Intent(UserLoginAct.this, DriverRegisterActStepOne.class);
            startActivity(intent);

            /*Intent web_signup = new Intent(UserLoginAct.this, WebviewAct.class);
            web_signup.putExtra("type", "12");
            startActivityForResult(web_signup, 350);*/

        });



//        PhoneEdt.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                try {
//                    if (charSequence.length() != 0) {
//                        String s = String.valueOf(charSequence.charAt(0));
//
//
//
//                            if (s.equalsIgnoreCase("0")||s.equalsIgnoreCase("2")||s.equalsIgnoreCase("3")||s.equalsIgnoreCase("4")||s.equalsIgnoreCase("5")) {
//                                PhoneEdt.setText("");
//                               // ShowToast(UserLoginAct.this, "Country code already added");
////                                int maxLength = 10;
////                                InputFilter[] FilterArray = new InputFilter[1];
////                                FilterArray[0] = new InputFilter.LengthFilter(maxLength);
////                                PhoneEdt.setFilters(FilterArray);
//                            } else {
//                                int maxLength = 13;
//                                InputFilter[] FilterArray = new InputFilter[1];
//                                FilterArray[0] = new InputFilter.LengthFilter(maxLength);
//                                PhoneEdt.setFilters(FilterArray);
//                            }
//
//
//                    }
//
//
////                    if (s.equalsIgnoreCase("0")) {
////                        MobileNumber.setText("");
////                        ShowToast(LoginActivity.this, "Country code already added");
////                    } else {
////
////                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        /**
         * Forget password action on click
         *
         */
        ForgotTxt.setOnClickListener(new OnClickListener() {
            private Dialog mDialog;

            @Override
            public void onClick(final View v) {
                final View view = View.inflate(UserLoginAct.this, R.layout.forgot_popup, null);
                mDialog = new Dialog(UserLoginAct.this, R.style.NewDialog);
                mDialog.setContentView(view);
                Colorchange.ChangeColor(mDialog.findViewById(R.id.inner_content), UserLoginAct.this);
                FontHelper.applyFont(UserLoginAct.this, mDialog.findViewById(R.id.inner_content));
                mDialog.setCancelable(true);
                mDialog.show();
                final EditText mail = mDialog.findViewById(R.id.forgotmail);
                mail.setInputType(InputType.TYPE_CLASS_NUMBER);
                final Button OK = mDialog.findViewById(R.id.okbtn);
                final Button Cancel = mDialog.findViewById(R.id.cancelbtn);
                OK.setOnClickListener(new OnClickListener() {
                    private String Email;

                    @Override
                    public void onClick(final View v) {
                        try {
                            Email = mail.getText().toString();
                            if (validations(ValidateAction.isValueNULL, UserLoginAct.this, Email)) {
                                JSONObject j = new JSONObject();
                                j.put("phone_no", Email);
                                j.put("user_type", "D");
                                final String url = "type=forgot_password";
                                new ForgotPassword(url, j);
                                mail.setText("");
                                mDialog.dismiss();
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }
                    }
                });
                Cancel.setOnClickListener(v1 -> mDialog.dismiss());
            }
        });
        /**
         * Action performed,done button onclick
         *
         */
        DoneBtn.setOnClickListener(v -> {
            phone = PhoneEdt.getText().toString().trim();
            if (validations(ValidateAction.isValueNULL, UserLoginAct.this, phone))
                if (validations(ValidateAction.isValidPassword, UserLoginAct.this, PasswordEdt.getText().toString().trim())) {
                    SessionSave.saveSession("phone_number", phone, UserLoginAct.this);
                    SessionSave.saveSession("driver_password", PasswordEdt.getText().toString().trim(), UserLoginAct.this);
                    password = convertPassMd5(PasswordEdt.getText().toString().trim());
                    final String url = "type=driver_login";
                    new SignIn(url, FORCE_LOGIN);
                }
        });

//        leftIcon.setVisibility(View.INVISIBLE);
//        leftIcon.setOnClickListener(view -> {
//            final Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        });
//
//        leftIcontxt.setVisibility(View.INVISIBLE);
//        leftIcontxt.setOnClickListener(view -> {
//            final Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        });
    }

    private void setSpannableTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                NC.getString(R.string.terms_condition) + " ");
        spanTxt.append(NC.getString(R.string.terms_condition2));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(CL.getColor(R.color.button_accept));    // you can use custom color
                ds.setUnderlineText(true);
            }

            @Override
            public void onClick(View widget) {
                String url = "&type=dynamic_page&pagename=10&device_type=1";
                new ShowWebpage(url, null, "T");
            }
        }, spanTxt.length() - NC.getString(R.string.terms_condition2).length(), spanTxt.length(), 0);
        spanTxt.append(" " + NC.getString(R.string.and));
        spanTxt.setSpan(new ForegroundColorSpan(CL.getColor(R.color.black)), spanTxt.length() - NC.getString(R.string.and).length(), spanTxt.length(), 0);
        spanTxt.append(" " + NC.getString(R.string.privacy_policy));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(CL.getColor(R.color.button_accept));    // you can use custom color
                ds.setUnderlineText(true);
            }

            @Override
            public void onClick(View widget) {
                String url = "&type=dynamic_page&pagename=11&device_type=1";
                new ShowWebpage(url, null, "P");
            }
        }, spanTxt.length() - NC.getString(R.string.privacy_policy).length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    public void HuaweiDeviceAlert() {

        dialog1 = Utils.alert_view_dialog(UserLoginAct.this,
                NC.getResources().getString(R.string.huawei_title),
                String.format(NC.getResources().getString(R.string.huawei_msg)),
                NC.getResources().getString(R.string.ok), "", false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SessionSave.saveSession("settings_alert", "SETTINGS", UserLoginAct.this);
                        EnableHuaweiProtectedApps();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, "");
    }


    public void vivoDeviceAlert() {
        dialog1 = Utils.alert_view_dialog(UserLoginAct.this,
                NC.getResources().getString(R.string.auto_start),
                String.format(NC.getResources().getString(R.string.auto_start_msg)),
                NC.getResources().getString(R.string.ok), "", false, (dialog, which) -> {
                    dialog.dismiss();
                    SessionSave.saveSession("settings_alert", "SETTINGS", UserLoginAct.this);
                    autostartVivo();
                }, (dialog, which) -> dialog.dismiss(), "");
    }

    public void xiaomiDeviceAlert() {

        dialog1 = Utils.alert_view_dialog(UserLoginAct.this,
                NC.getResources().getString(R.string.auto_start),
                String.format(NC.getResources().getString(R.string.auto_start_msg)),
                NC.getResources().getString(R.string.ok), "", false, (dialog, which) -> {
                    dialog.dismiss();
                    SessionSave.saveSession("settings_alert", "SETTINGS", UserLoginAct.this);
                    try {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.miui.securitycenter",
                                "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, (dialog, which) -> dialog.dismiss(), "");

    }

    public void oppoDeviceAlert() {

        dialog1 = Utils.alert_view_dialog(UserLoginAct.this,
                NC.getResources().getString(R.string.power_saving),
                String.format(NC.getResources().getString(R.string.power_saving_msg)),
                NC.getResources().getString(R.string.ok), "", false, (dialog, which) -> {
                    dialog.dismiss();
                    SessionSave.saveSession("settings_alert", "SETTINGS", UserLoginAct.this);
                    try {
                        Intent intentBatteryUsage = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
                        context.startActivity(intentBatteryUsage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, (dialog, which) -> dialog.dismiss(), "");
    }

    private void EnableHuaweiProtectedApps() {
        try {
            String cmd = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cmd = "am start -n com.huawei.systemmanager/.appcontrol.activity.StartupAppControlActivity";
            } else {
                cmd = "am start -n com.huawei.systemmanager/.optimize.process.ProtectActivity";
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cmd += " --user " + getUserSerial();
            }

            Runtime.getRuntime().exec(cmd);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void autostartVivo() {

        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.iqoo.secure",
                    "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                context.startActivity(intent);
            } catch (Exception ex) {
                try {
                    Intent intent = new Intent();
                    intent.setClassName("com.iqoo.secure",
                            "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager");
                    context.startActivity(intent);
                } catch (Exception exx) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private String getUserSerial() {
//noinspection ResourceType
        @SuppressLint("WrongConstant")
        Object userManager = getSystemService("user");
        if (null == userManager) return "";

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);

            if (userSerial != null) {
                return String.valueOf(userSerial);
            } else {
                return "";
            }
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException ignored) {
        }
        return "";
    }


    @Override
    protected void onResume() {
        super.onResume();


        String reqString = Build.MANUFACTURER;
        if (SessionSave.getSession("settings_alert", UserLoginAct.this).isEmpty()) {
            if (reqString.toLowerCase().contains("huawei")) {
                HuaweiDeviceAlert();
            } else if (reqString.toLowerCase().contains("vivo")) {
                vivoDeviceAlert();
            } else if (reqString.toLowerCase().contains("xiaomi")) {
                xiaomiDeviceAlert();
            } else if (reqString.toLowerCase().contains("oppo")) {
                oppoDeviceAlert();
            }
        }
    }

    @Override
    protected void onStop() {
        Utils.closeDialog(mDialog);
        Utils.closeDialog(mDialogs);
        Utils.closeDialog(alertDialog);
        super.onStop();
    }

    /**
     * Referal post method API call and response parsing.
     */
    private void referalPopup() {

        try {
            final View view = View.inflate(UserLoginAct.this, R.layout.referal_popup, null);
            mDialogs = new Dialog(UserLoginAct.this, R.style.dialogwinddow);
            mDialogs.setContentView(view);
            mDialogs.setCancelable(true);
            if (!mDialogs.isShowing())
                mDialogs.show();
            FontHelper.applyFont(UserLoginAct.this, mDialogs.findViewById(R.id.inner_content));
            mDialogs.findViewById(R.id.f_textview);
            final EditText mail = mDialogs.findViewById(R.id.forgotmail);

            final Button OK = mDialogs.findViewById(R.id.okbtn);
            final Button Cancel = mDialogs.findViewById(R.id.cancelbtn);
            OK.setOnClickListener(new OnClickListener() {
                private String mobilenumber;

                @Override
                public void onClick(final View v) {
                    try {
                        mobilenumber = mail.getText().toString();
                        if (!mobilenumber.trim().equals("")) {
                            JSONObject j = new JSONObject();
                            j.put("driver_id", SessionSave.getSession("Id", UserLoginAct.this));
                            j.put("referral_code", mobilenumber);
                            final String url = "type=check_driver_referral_code";
                            new ReferalCode(url, j);
                            mail.setText("");
                            mDialogs.dismiss();
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            });
            Cancel.setOnClickListener(v -> {
                mDialogs.dismiss();
                pop_up(jsonDriver);
            });
        } catch (Exception e) {
e.printStackTrace();
        }
    }

    /**
     * Alert view for referal code
     */
    public void alert_views(AppCompatActivity m,
                            String title,
                            String message,
                            String success_txt,
                            String failure_txt) {


//changed
        dialog1 = Utils.alert_view_dialog(m, title, message, success_txt,
                failure_txt, true, (dialog, which) -> {

                    pop_up(jsonDriver);

                   //sabari
                  /*  if (isReferalSuccess)
                        pop_up(jsonDriver);
                    else referalPopup();*/
                    dialog.dismiss();
                }, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                }, "");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (dialog1 != null)
            Utils.closeDialog(dialog1);
        super.onDestroy();
    }

    /**
     * This function will redirect to other activities
     */
    public void pop_up(final JSONObject jsonDriverObject) {
        if (!SessionSave.getSession("trip_id", UserLoginAct.this).equals("")) {
            if (SessionSave.getSession("travel_status", UserLoginAct.this).equals("5")) {
                SessionSave.saveSession("status", "A", UserLoginAct.this);
                Intent in = new Intent(UserLoginAct.this, OngoingAct.class);
                startActivity(in);
                LocationUpdate.startLocationService(UserLoginAct.this);
                finish();
                if (mDialog != null)
                    mDialog.dismiss();
            } else if (SessionSave.getSession("travel_status", UserLoginAct.this).equals("2")) {
                SessionSave.saveSession("status", "A", UserLoginAct.this);
                Intent in = new Intent(UserLoginAct.this, OngoingAct.class);
                startActivity(in);
                LocationUpdate.startLocationService(UserLoginAct.this);
                finish();
                if (mDialog != null && UserLoginAct.this != null)
                    mDialog.dismiss();
            } else {
                final Intent i = new Intent(UserLoginAct.this, MyStatus.class);
                SessionSave.saveSession("need_animation", true, UserLoginAct.this);
                SessionSave.saveSession(CommonData.SHIFT_OUT, false, UserLoginAct.this);
                SessionSave.saveSession(CommonData.LOGOUT, false, UserLoginAct.this);
                LocationUpdate.startLocationService(UserLoginAct.this);
                startActivity(i);
                finish();
                overridePendingTransition(0, 0);
                if (mDialog != null)
                    mDialog.dismiss();
            }
        } else {
            final Intent i = new Intent(UserLoginAct.this, MyStatus.class);
            SessionSave.saveSession(CommonData.SHIFT_OUT, false, UserLoginAct.this);
            SessionSave.saveSession(CommonData.LOGOUT, false, UserLoginAct.this);
            SessionSave.saveSession("need_animation", true, UserLoginAct.this);
            LocationUpdate.startLocationService(UserLoginAct.this);
            startActivity(i);
            finish();
            overridePendingTransition(0, 0);
            if (mDialog != null)
                mDialog.dismiss();
        }
    }

    /**
     * Already logged in dialog
     */

    public void loggedInOtherDevice(String msg) {
        try {


            dialog1 = Utils.alert_view_dialog(UserLoginAct.this,
                    NC.getResources().getString(R.string.message),
                    msg,
                    NC.getResources().getString(R.string.ok),
                    NC.getResources().getString(R.string.cancell),
                    false, (dialog, which) -> {
                        dialog.dismiss();
                        if (NetworkStatus.isOnline(UserLoginAct.this)) {
                            dialog.dismiss();
                            FORCE_LOGIN = true;
                            UserLoginAct.this.DoneBtn.performClick();
                        } else {
                            CToast.ShowToast(UserLoginAct.this, NC.getResources().getString(R.string.check_net_connection));
                        }
                    }, (dialog, which) -> dialog.dismiss(), "");

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 350) {
                Systems.out.println("actvty_from_webview");
                if (data != null) {
                    String bookdriver_msg = data.getStringExtra("bok_driver");
                    dialog1 = Utils.alert_view_dialog(UserLoginAct.this, "",
                            bookdriver_msg,
                            NC.getResources().getString(R.string.ok), "",
                            true, (dialog, which) -> dialog.dismiss(), (dialog, which) -> dialog.dismiss(), "");
                }
            }
        }
    }

    @Override
    public void positiveButtonClick(DialogInterface dialog, int id, String s) {
        dialog.dismiss();
    }

    /**
     * Function: To show the success popup dialog and move to dashboard
     */

    @Override
    public void negativeButtonClick(DialogInterface dialog, int id, String s) {
        dialog.dismiss();
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
            phone = PhoneEdt.getText().toString().trim();
            if (validations(ValidateAction.isValueNULL, UserLoginAct.this, phone))
                if (validations(ValidateAction.isValidPassword, UserLoginAct.this, PasswordEdt.getText().toString().trim())) {
                    SessionSave.saveSession("phone_number", phone, UserLoginAct.this);
                    SessionSave.saveSession("driver_password", PasswordEdt.getText().toString().trim(), UserLoginAct.this);
                    password = convertPassMd5(PasswordEdt.getText().toString().trim());
                    final String url = "type=driver_login";
                    new SignIn(url, false);
                }
        }
        return false;
    }

    private void showAlertView(String message) {
        dialog1 = Utils.alert_view_dialog(UserLoginAct.this,
                "",
                message,
                NC.getString(R.string.ok),
                "",
                false, (dialog, which) -> dialog.dismiss(), null, "");
    }

    private class ShowWebpage implements APIResult {
        String type = "T";

        public ShowWebpage(final String string, JSONObject data, String type) {
            // TODO Auto-generated constructor stub
            this.type = type;
            String ss = SessionSave.getSession("base_url", UserLoginAct.this) + "?" + "lang=" + SessionSave.getSession("Lang", UserLoginAct.this) + string;
            Systems.out.println("weburl____" + ss);
            new APIService_Retrofit_JSON(UserLoginAct.this, this, true, ss).execute();
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            // TODO Auto-generated method stub
            try {
                if (isSuccess) {
                    final Intent intent = new Intent(UserLoginAct.this, TermsAndConditions.class);
                    final Bundle bundle = new Bundle();
                    intent.putExtra("content", result);
                    if (type.equals("T"))
                        bundle.putString("name", NC.getString(R.string.terms_condition2));
                    else
                        bundle.putString("name", NC.getString(R.string.privacy_policy));
                    bundle.putBoolean("status", true);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                   // runOnUiThread(() -> ShowToast(UserLoginAct.this, NC.getString(R.string.server_error)));
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }
    }

    /**
     * ForgotPassword API response parsing.
     */
    private class ForgotPassword implements APIResult {
        ForgotPassword(String url, JSONObject data) {
            if (isOnline()) {
                new APIService_Retrofit_JSON(UserLoginAct.this, this, data, false).execute(url);
            } else {
                dialog1 = Utils.alert_view(UserLoginAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, UserLoginAct.this, "");
            }
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            if (isSuccess) {
                try {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1)
                        dialog1 = Utils.alert_view(UserLoginAct.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, UserLoginAct.this, "");
                    else
                        dialog1 = Utils.alert_view(UserLoginAct.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, UserLoginAct.this, "");
                } catch (final JSONException e) {
                    e.printStackTrace();
                }
            } else {
                runOnUiThread(() -> CToast.ShowToast(UserLoginAct.this, NC.getString(R.string.server_error)));
            }
        }
    }

    /**
     * Signin post method API call and response parsing.
     */
    private class SignIn implements APIResult {
        SignIn(final String url, boolean FORCE_LOGIN) {
            try {

                JSONObject j = new JSONObject();
                j.put("phone", phone);
                j.put("password", password);
                j.put("device_id", SessionSave.getSession("sDevice_id", UserLoginAct.this));
                j.put("device_token", SessionSave.getSession(CommonData.DEVICE_TOKEN, UserLoginAct.this));
                j.put("device_type", "1");
                j.put("force_login", FORCE_LOGIN);
            //    j.put("device_info", new JSONObject(new Gson().toJson(DeviceUtils.INSTANCE.getAllInfo(UserLoginAct.this))));
                UserLoginAct.FORCE_LOGIN = false;
                DoneBtn.setEnabled(false);
                PasswordEdt.setOnEditorActionListener(null);
                new APIService_Retrofit_JSON(UserLoginAct.this, this, j, false).execute(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            Runnable runnableServerError = () -> CToast.ShowToast(UserLoginAct.this, NC.getString(R.string.server_error));
            try {
                if (isSuccess) {
                    final JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1 || json.getInt("status") == 10) {
                        final JSONObject obj = json.getJSONObject("detail");
                        final JSONArray ary = obj.getJSONArray("driver_details");
                        final JSONObject detail = ary.getJSONObject(0);
                        SessionSave.saveSession("Email", detail.getString("email"), UserLoginAct.this);
                        SessionSave.saveSession("Id", detail.getString("userid"), UserLoginAct.this);
                        SessionSave.saveSession("Lastname", detail.getString("lastname"), UserLoginAct.this);
                        SessionSave.saveSession("Name", detail.getString("name"), UserLoginAct.this);
                        SessionSave.saveSession("u_name", detail.getString("name"), UserLoginAct.this);
                        SessionSave.saveSession("Bankname", detail.getString("bankname"), UserLoginAct.this);
                        SessionSave.saveSession("Bankaccount_No", detail.getString("bankaccount_no"), UserLoginAct.this);
                        SessionSave.saveSession("Salutation", detail.getString("salutation"), UserLoginAct.this);
                        SessionSave.saveSession("taxi_id", detail.getString("taxi_id"), UserLoginAct.this);
                        SessionSave.saveSession("company_id", detail.getString("company_id"), UserLoginAct.this);
                        SessionSave.saveSession("status", detail.getString("driver_status"), UserLoginAct.this);
                        SessionSave.saveSession("Shiftupdate_Id", detail.getString("shiftupdate_id"), UserLoginAct.this);
                        SessionSave.saveSession("country_code", detail.getString("country_code"), UserLoginAct.this);
                        SessionSave.saveSession("model_id", detail.getString("model_id"), UserLoginAct.this);


                        if (detail.has("driver_type")) {
                            SessionSave.saveSession("account_message", json.getString("message"), UserLoginAct.this);
                            SessionSave.saveSession("driver_type", detail.getString("driver_type"), UserLoginAct.this);
                        } else {
                            SessionSave.saveSession("driver_type", "A", UserLoginAct.this);
                        }
                        if (!detail.getString("shiftupdate_id").equals(""))
                            SessionSave.saveSession("driver_shift", "IN", UserLoginAct.this);
                        SessionSave.saveSession("Picture", detail.getString("profile_picture"), UserLoginAct.this);
                        SessionSave.saveSession("Register", "", UserLoginAct.this);
                        if (!detail.getString("trip_id").equals("")) {
                            SessionSave.saveSession("trip_id", detail.getString("trip_id"), UserLoginAct.this);
                            MainActivity.mMyStatus.settripId(detail.getString("trip_id"));
                            SessionSave.saveSession("status", detail.getString("driver_status"), UserLoginAct.this);
                            SessionSave.saveSession("travel_status", detail.getString("travel_status"), UserLoginAct.this);
                        }

                        if (json.has("user_key")) {
                            String userKey = json.getString(CommonData.USER_KEY);
                            if (!TextUtils.isEmpty(userKey))
                                SessionSave.saveSession(CommonData.USER_KEY, userKey, UserLoginAct.this);
                        }

                        if (json.has("sos_detail"))
                            SessionSave.saveSession("contact_sos_list", json.getString("sos_detail"), UserLoginAct.this);
                        jsonDriver = detail.getJSONObject("driver_statistics");
                        SessionSave.saveSession("driver_statistics", String.valueOf(jsonDriver), UserLoginAct.this);
                        SessionSave.saveSession("Version_Update", "0", UserLoginAct.this);
                        SessionSave.saveSession("shift_status", detail.getJSONObject("driver_statistics").getString("shift_status"), UserLoginAct.this);


                        SessionSave.saveSession("taxi_no", detail.getString("taxi_no"), UserLoginAct.this);
                        SessionSave.saveSession("model_name", detail.getString("model_name"), UserLoginAct.this);

                        String isFirst = detail.getString("driver_first_login");
                        if (isFirst.equals("1")) {
                            //sabari Aug1 2022
                           /* if ((Integer.parseInt(SessionSave.getSession("referal", UserLoginAct.this))) == 1)
                                referalPopup();
                            else*/
                                pop_up(jsonDriver);
                        } else
                            pop_up(jsonDriver);

                    }else if(json.getInt("status") == 100){
                        SessionSave.saveSession("reg_driver_Id", json.getString("driver_id"), UserLoginAct.this);
                        SessionSave.saveSession("company_id",  json.getString("company_id"), UserLoginAct.this);
                        Intent intent = new Intent(UserLoginAct.this, AddFleetAct.class);
                        startActivity(intent);


                    } else if (json.getInt("status") == -5)
                        CToast.ShowToast(UserLoginAct.this, json.getString("message"));
                    else if (json.getInt("status") == 0)
                        loggedInOtherDevice(json.getString("message"));
                    else
                        showAlertView(json.getString("message"));

                    DoneBtn.setEnabled(true);
                } else {
                    runOnUiThread(runnableServerError);
                    DoneBtn.setEnabled(true);
                }

                PasswordEdt.setOnEditorActionListener(UserLoginAct.this);

            } catch (final Exception e) {
                e.printStackTrace();
                DoneBtn.setEnabled(true);
                PasswordEdt.setOnEditorActionListener(UserLoginAct.this);
                runOnUiThread(runnableServerError);
            }
        }
    }

    /**
     * Referal  method API call and response parsing.
     */
    private class ReferalCode implements APIResult {
        ReferalCode(String url, JSONObject data) {
            if (isOnline()) {
                new APIService_Retrofit_JSON(UserLoginAct.this, this, data, false).execute(url);
            } else {
                dialog1 = Utils.alert_view(UserLoginAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, UserLoginAct.this, "");
            }
        }

        @Override
        public void getResult(final boolean isSuccess, final String result) {
            if (isSuccess) {
                try {
                    final JSONObject json = new JSONObject(result);

                    if (json.getInt("status") == 1) {
                        isReferalSuccess = true;

                        alert_views(UserLoginAct.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "");
                    } else {
                        isReferalSuccess = false;
                        alert_views(UserLoginAct.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "");
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                }
            } else {
                runOnUiThread(() -> CToast.ShowToast(UserLoginAct.this, NC.getString(R.string.server_error)));
            }
        }
    }


    private class callString implements APIResult {
        public callString(final String url) {
            String urls = SessionSave.getSession("currentStringUrl", UserLoginAct.this);
            if (urls.equals(""))
                urls = SessionSave.getSession("Lang_English", UserLoginAct.this);
            new APIService_Retrofit_JSON(UserLoginAct.this, this, null, true, urls, true).execute();
        }

        @Override
        public void getResult(boolean isSuccess, String result) {
            if (isSuccess) {
                NC.nfields_byID.clear();
                NC.nfields_byName.clear();
                NC.fields.clear();
                NC.fields_value.clear();
                NC.fields_id.clear();
                setLocale();
                SessionSave.saveSession("wholekey", result, UserLoginAct.this);
                getAndStoreStringValues(result);
                RefreshAct();
            }
        }
    }
    private synchronized void getAndStoreStringValues(String result) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));
            Document doc = dBuilder.parse(is);
            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("*");

            for (int i = 0; i < nList.getLength(); i++) {

                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element2 = (Element) node;
                    NC.nfields_byName.put(element2.getAttribute("name"), element2.getTextContent());
                }
            }
            getValueDetail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void RefreshAct() {


        String temptype = SessionSave.getSession("LANGTemp" + types, UserLoginAct.this);
        SessionSave.saveSession("Lang", SessionSave.getSession("LANGCode" + types, UserLoginAct.this), UserLoginAct.this);

        if (temptype.equals("LTR")) {
            SessionSave.saveSession("Lang_Country", "en_US", UserLoginAct.this);
        } else {
            SessionSave.saveSession("Lang_Country", "ar_EG", UserLoginAct.this);
        }
        Configuration config = new Configuration();
        String langcountry = SessionSave.getSession("Lang_Country", UserLoginAct.this);
        String[] arry = langcountry.split("_");
        String language = SessionSave.getSession("Lang", UserLoginAct.this);

        config.locale = new Locale(language, arry[1]);
        Locale.setDefault(new Locale(language, arry[1]));
        UserLoginAct.this.getBaseContext().getResources().updateConfiguration(config, UserLoginAct.this.getResources().getDisplayMetrics());
        Utils.closeDialog(mlangDialog);
        finish();
        startActivity(getIntent());

       /* Intent intent = new Intent(UserLoginAct.this, UserLoginAct.class);
         showLoading(UserLoginAct.this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
    }

}