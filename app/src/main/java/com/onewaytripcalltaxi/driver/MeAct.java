package com.onewaytripcalltaxi.driver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.text.InputType;
import android.util.Base64;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.onewaytripcalltaxi.driver.fleetdetails.FleetDetailsActivity;
import com.onewaytripcalltaxi.driver.utils.FourDigitCardFormatWatcher;
import com.squareup.picasso.Picasso;
import com.onewaytripcalltaxi.driver.data.CommonData;
import com.onewaytripcalltaxi.driver.interfaces.APIResult;
import com.onewaytripcalltaxi.driver.interfaces.ClickInterface;
import com.onewaytripcalltaxi.driver.service.APIService_Retrofit_JSON;
import com.onewaytripcalltaxi.driver.service.LocationUpdate;
import com.onewaytripcalltaxi.driver.utils.CL;
import com.onewaytripcalltaxi.driver.utils.CToast;
import com.onewaytripcalltaxi.driver.utils.Colorchange;
import com.onewaytripcalltaxi.driver.utils.FontHelper;
import com.onewaytripcalltaxi.driver.utils.ImageUtils;
import com.onewaytripcalltaxi.driver.utils.NC;
import com.onewaytripcalltaxi.driver.utils.SessionSave;
import com.onewaytripcalltaxi.driver.utils.Systems;
import com.onewaytripcalltaxi.driver.utils.Utils;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static com.onewaytripcalltaxi.driver.data.CommonData.getDateForCreateImageFile;

/**
 * This class is used to show driver profile info
 */
public class MeAct extends MainActivity implements OnClickListener, ClickInterface {
    private static final int FROM_GALLERY = 108;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 113;
    private static final int ACTIVITY_VIEW_ATTACHMENT = 115;
    private static final int ACTION_IMAGE_CAPTURE = 113;
    public static AppCompatActivity mFlagger;
    public static MeAct profileAct;
    public ImageView fileimg;
    public Dialog tDialog;
    Dialog fileDialog;
    int imageSelect = 0, imageSelected = 0;
    // Class members declarations.
    private TextView  hidePwd, hideconPwd, tvHelpLink;
    private LinearLayout DoneBtn;
    private TextView HeadTitle, btn_back;
    private TextView forgotpswdTxt;
    private EditText emailEdt;
    private EditText mobileEdt;
    private EditText passwordEdt;
    private EditText confirmpswdEdt, nation_id_Edt, cardnoEdt;
    private EditText firstTxt;
    private EditText lastTxt;
    private String phone;
    private String newpwd;
    private String confirmpwd;
    private String bankname;
    private String bankaccNo;
    private String taxi_model = "";
    private String taxi_no = "";
    private String taxi_map_from = "";
    private String taxi_map_to = "", bank_id_change;
    private int driver_rating;
    private ImageView profileImage;
    private String base64 = "", file_base64 = "";
    private TextView slider;
    private TextView btnLogout, btnupload;
    //private ImageLoader imageLoader;
    private Bitmap mBitmap;
    private Uri imageUri;
    private Bitmap downImage;
    private TextView btntaxidetail;
    private RelativeLayout me_layout;
    private LinearLayout emergency_contact;
    private ImageView driverRat;
    private TextView emergency_contact_txt, subscription;
    private int walletamountr = 0;
    private Dialog mcancelDialog;    private Dialog mlangDialog;

    private Dialog mDialog;
    private ScrollView profile_lay_s;
    private RelativeLayout slide_lay;

    private int types = 1;
    private String encodedImage = "";
    private final String destinationFileName = "profileImage";
    private TextView invitefriends_bottom;

    private TextView bt_delete_acc;
    private MaterialTapTargetPrompt mTargetPrompt;
    private FrameLayout layout_loading;
    private LinearLayout layout_bottom;
    private ImageView imgV_license, imgV_license_back;

    private Dialog dialog1;

    private TextView txt_mobile_settings;
CardView back_trip_details;
    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

    /**
     * Set the layout to activity.
     */
    @Override
    public int setLayout() {

        setLocale();
        return R.layout.me_lay2;
    }

    /**
     * Initialize the views on layout
     */
    @Override
    public void Initialize() {

        CommonData.mActivitylist.add(this);
        CommonData.sContext = this;
        CommonData.current_act = "MeAct";

//        Colorchange.ChangeColor((ViewGroup) (((ViewGroup) MeAct.this
//                .findViewById(android.R.id.content)).getChildAt(0)), MeAct.this);

       // FontHelper.applyFont(this, findViewById(R.id.me_layout));
        profileAct = this;
        me_layout = findViewById(R.id.me_layout);
        profile_lay_s = findViewById(R.id.profile_lay_s);
        slide_lay = findViewById(R.id.slide_lay);

        layout_loading = findViewById(R.id.layout_loading);
        layout_bottom = findViewById(R.id.bottom);
        tvHelpLink = findViewById(R.id.tvHelpLink);

        slider = findViewById(R.id.slideImg);
        DoneBtn = findViewById(R.id.donebtn);
        HeadTitle = findViewById(R.id.headerTxt);
        firstTxt = findViewById(R.id.firstTxt);
        lastTxt = findViewById(R.id.lastTxt);
        emailEdt = findViewById(R.id.emailEdt);
       // emailEdt.setEnabled(false);
        mobileEdt = findViewById(R.id.mobileEdt);
        mobileEdt.setEnabled(false);
        btnupload = findViewById(R.id.btnupload);
        passwordEdt = findViewById(R.id.passwordEdt);
        confirmpswdEdt = findViewById(R.id.confirmpswdEdt);
        btn_back = findViewById(R.id.slideImg);
        btn_back.setVisibility(View.GONE);
       // bt_delete_acc = findViewById(R.id.bt_privacysettings);
        ((TextView) findViewById(R.id.language_setting)).setText(NC.getString(R.string.select_language).trim());
        //bankEdt = (EditText) findViewById(R.id.bankEdt);
        // bankaccnoEdt = (EditText) findViewById(R.id.bankaccnoEdt);
        profileImage = findViewById(R.id.profileimage);
        btntaxidetail = findViewById(R.id.btntaxidetail);
        btntaxidetail.setText(NC.getString(R.string.service_type));
        driverRat = findViewById(R.id.driverRat);
        HeadTitle.setText(NC.getResources().getString(R.string.m_me));
      //  DoneBtn.setVisibility(View.GONE);
      //  DoneBtn.setText(NC.getResources().getString(R.string.save));
        txt_mobile_settings = findViewById(R.id.txt_mobile_settings);
        btnLogout = findViewById(R.id.btnlogout);
        btnLogout.setOnClickListener(this);
        btnLogout.setText(NC.getResources().getString(R.string.s_logout));
        final View view = View.inflate(MeAct.this, R.layout.fileupload_popup, null);
        fileDialog = new Dialog(MeAct.this, R.style.dialogwinddow);
        fileDialog.setContentView(view);
        hidePwd = findViewById(R.id.hidePwd);
        hideconPwd = findViewById(R.id.hideconPwd);
        emergency_contact_txt = findViewById(R.id.emergency_contact_txt);
        emergency_contact = findViewById(R.id.emergency_contact);
        nation_id_Edt = findViewById(R.id.nation_id_Edt);
        cardnoEdt = findViewById(R.id.cardnoEdt);
        imgV_license = findViewById(R.id.imgV_license);
        imgV_license_back = findViewById(R.id.imgV_license_back);
        back_trip_details = findViewById(R.id.back_trip_details);
        subscription = findViewById(R.id.subscription);
//        invitefriends_bottom = findViewById(R.id.invitefriends_bottom);

        cardnoEdt.addTextChangedListener(new FourDigitCardFormatWatcher(MeAct.this));


        if (SessionSave.getSession(CommonData.SOS_ENABLED, this, false)) {
            emergency_contact.setVisibility(View.VISIBLE);
        }
        String reqString = Build.MANUFACTURER;
        if (reqString.toLowerCase().contains("huawei") || reqString.toLowerCase().contains("vivo") || reqString.toLowerCase().contains("xiaomi") || reqString.toLowerCase().contains("oppo")) {
            txt_mobile_settings.setVisibility(View.VISIBLE);
        } else {
            txt_mobile_settings.setVisibility(View.GONE);
        }
        txt_mobile_settings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String reqString = Build.MANUFACTURER;
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
        });

        if (!SessionSave.getSession(CommonData.HELP_URL, this).equals("")) {
            if (emergency_contact.getVisibility() == View.GONE)
                tvHelpLink.setGravity(Gravity.CENTER);
            tvHelpLink.setVisibility(View.GONE);
        }
        tvHelpLink.setOnClickListener(v -> {
            tvHelpLink.setClickable(false);
            Intent in = new Intent(MeAct.this, WebviewAct.class);
            in.putExtra("type", CommonData.HELP_URL);
            startActivity(in);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvHelpLink.setClickable(true);
                }
            }, 2000L);
        });
//        bt_delete_acc.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent deleteAcc = new Intent(MeAct.this, DeleteAccountActivity.class);
//                startActivity(deleteAcc);
//            }
//        });
        back_trip_details.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        emergency_contact.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                SessionSave.saveSession("sos_id", SessionSave.getSession("Id", MeAct.this), MeAct.this);
                SessionSave.saveSession("user_type", "d", MeAct.this);

              //  startActivity(new Intent(MeAct.this, SOSActivity.class));
            }
        });


        //Getting Driver Profile
        JSONObject j = new JSONObject();
        try {
            j.put("userid", SessionSave.getSession("Id", MeAct.this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showLoading();
        String pro_url = "type=driver_profile";
        new GetProfileData(pro_url, j);


        subscription.setOnClickListener(v -> {
            Intent in = new Intent(MeAct.this, WebviewAct.class);

            in.putExtra("type", SessionSave.getSession(CommonData.PLAN_TYPE, MeAct.this).equals("1") ? "1" : "2");
            startActivity(in);
        });


        //
        lastTxt.setOnEditorActionListener((v, actionId, event) -> {

            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                passwordEdt.requestFocus();
            }
            return true;
        });

        FontHelper.applyFont(MeAct.this, fileDialog.findViewById(R.id.topid_fileup));
        fileDialog.setCancelable(true);


        setOnclicklistener();

        /* newly add for password hide and show*/

        hidePwd.setOnClickListener(v -> {
            // TODO Auto-generated method stub

            if (hidePwd.getText().toString().equals(NC.getResources().getString(R.string.show))) {
                passwordEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                FontHelper.applyFont(MeAct.this, passwordEdt);
                hidePwd.setText(NC.getResources().getString(R.string.hide));

            } else {
                passwordEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                hidePwd.setText(NC.getResources().getString(R.string.show));
                FontHelper.applyFont(MeAct.this, passwordEdt);

            }
        });
        passwordEdt.setOnFocusChangeListener((v, hasFocus) -> {
            // TODO Auto-generated method stub
            if (hasFocus) {
                hidePwd.setVisibility(View.VISIBLE);


            } else {
                hidePwd.setVisibility(View.GONE);
                passwordEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                FontHelper.applyFont(MeAct.this, passwordEdt);
                if (hidePwd.getText().toString().equals(NC.getResources().getString(R.string.hide))) {
                    FontHelper.applyFont(MeAct.this, passwordEdt);
                    hidePwd.setText(NC.getResources().getString(R.string.show));
                }
            }
        });

        hideconPwd.setOnClickListener(v -> {

            if (hideconPwd.getText().toString().equals(NC.getResources().getString(R.string.show))) {
                confirmpswdEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                hideconPwd.setText(NC.getResources().getString(R.string.hide));
                FontHelper.applyFont(MeAct.this, confirmpswdEdt);
            } else {
                confirmpswdEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                hideconPwd.setText(NC.getResources().getString(R.string.show));
                FontHelper.applyFont(MeAct.this, confirmpswdEdt);

            }
        });
        confirmpswdEdt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                hideconPwd.setVisibility(View.VISIBLE);


            } else {
                hideconPwd.setVisibility(View.GONE);
                confirmpswdEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                FontHelper.applyFont(MeAct.this, confirmpswdEdt);
                if (hideconPwd.getText().toString().equals(NC.getResources().getString(R.string.hide))) {
                    hideconPwd.setText(NC.getResources().getString(R.string.show));
                }
            }
        });

        btn_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(MeAct.this, MyStatus.class);
                startActivity(in);
                finish();


            }
        });

    }


    public void HuaweiDeviceAlert() {

        Utils.alert_view_dialog(MeAct.this,
                NC.getResources().getString(R.string.huawei_title),
                String.format(NC.getResources().getString(R.string.huawei_msg)),
                NC.getResources().getString(R.string.ok), "", false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SessionSave.saveSession("settings_alert", "SETTINGS", MeAct.this);
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
        Utils.alert_view_dialog(MeAct.this,
                NC.getResources().getString(R.string.auto_start),
                String.format(NC.getResources().getString(R.string.auto_start_msg)),
                NC.getResources().getString(R.string.ok), "", false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SessionSave.saveSession("settings_alert", "SETTINGS", MeAct.this);
                        autostartVivo();
                    }
                }, (dialog, which) -> dialog.dismiss(), "");
    }

    public void xiaomiDeviceAlert() {

        Utils.alert_view_dialog(MeAct.this,
                NC.getResources().getString(R.string.auto_start),
                String.format(NC.getResources().getString(R.string.auto_start_msg)),
                NC.getResources().getString(R.string.ok), "", false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SessionSave.saveSession("settings_alert", "SETTINGS", MeAct.this);
                        try {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.miui.securitycenter",
                                    "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, (dialog, which) -> dialog.dismiss(), "");

    }

    public void oppoDeviceAlert() {

        Utils.alert_view_dialog(MeAct.this,
                NC.getResources().getString(R.string.power_saving),
                String.format(NC.getResources().getString(R.string.power_saving_msg)),
                NC.getResources().getString(R.string.ok), "", false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SessionSave.saveSession("settings_alert", "SETTINGS", MeAct.this);
                        try {
                            Intent intentBatteryUsage = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
                            context.startActivity(intentBatteryUsage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    /**
     * Click method used to change the language and update the UI based on selected language.
     */
    public void language_settings(View v) {

        final View view = View.inflate(MeAct.this, R.layout.lang_list, null);
        mlangDialog = new Dialog(MeAct.this, R.style.dialogwinddow);
        mlangDialog.setContentView(view);
        FontHelper.applyFont(MeAct.this, mlangDialog.findViewById(R.id.id_lang));
        Colorchange.ChangeColor(mlangDialog.findViewById(R.id.id_lang), MeAct.this);
        mlangDialog.setCancelable(true);
        mlangDialog.show();
        String[] totalLang = (SessionSave.getSession("lang_json", MeAct.this)).trim().split("____");

        final LinearLayout lay_fav_res1 = mlangDialog.findViewById(R.id.language_list);
        for (int i = 0; i < totalLang.length; i++) {
            // lay_fav_res1.
            TextView tv = new TextView(MeAct.this);
           // tv.setText(SessionSave.getSession("LANG" + i, MeAct.this).replaceAll(".xml", ""));
            if(SessionSave.getSession("LANG" + i, MeAct.this).replaceAll(".xml", "").equalsIgnoreCase("arabic")){
                tv.setText(NC.getString(R.string.arabic));
            }else{
                tv.setText(SessionSave.getSession("LANG" + i, MeAct.this).replaceAll(".xml", ""));
            }
            tv.setTag(i);
            tv.setPadding(40, 40, 40, 40);
            tv.setTextSize(18f);

            // if (SessionSave.getSession("Lang", MeAct.this).equals("ar") || SessionSave.getSession("Lang", MeAct.this).equals("fa"))
                tv.setGravity(Gravity.LEFT);
            tv.setOnClickListener(v1 -> {
                int pos = (int) v1.getTag();
                types = pos;
                String url = SessionSave.getSession(SessionSave.getSession("LANG" + pos, MeAct.this), MeAct.this);
                Systems.out.println("current_url" + url);
                SessionSave.saveSession("currentStringUrl", url, MeAct.this);
                if (!SessionSave.getSession("Lang", MeAct.this).equalsIgnoreCase(SessionSave.getSession("LANGCode" + pos, MeAct.this)))
                    new callString("strings.xml");

            });
            lay_fav_res1.addView(tv);
        }


    }

    @Override
    public void positiveButtonClick(DialogInterface dialog, int id, String s) {
        dialog.dismiss();
    }

    @Override
    public void negativeButtonClick(DialogInterface dialog, int id, String s) {

    }

    /**
     * Storing string files in local hash map
     */
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

    /**
     * Getting string files in local hash map
     */
    synchronized void getValueDetail() {
        Field[] fieldss = R.string.class.getDeclaredFields();
        for (Field field : fieldss) {
            int id = getResources().getIdentifier(field.getName(), "string", MeAct.this.getPackageName());
            if (NC.nfields_byName.containsKey(field.getName())) {
                NC.fields.add(field.getName());
                NC.fields_value.add(NC.getResources().getString(id));
                NC.fields_id.put(field.getName(), id);

            }
            for (Map.Entry<String, String> entry : NC.nfields_byName.entrySet()) {
                String h = entry.getKey();
                NC.nfields_byID.put(NC.fields_id.get(h), NC.nfields_byName.get(h));
                // do stuff
            }


        }
    }

    /**
     * This method used to refresh UI after language selection
     */
    private void RefreshAct() {
        String temptype = SessionSave.getSession("LANGTemp" + types, MeAct.this);
        SessionSave.saveSession("Lang", SessionSave.getSession("LANGCode" + types, MeAct.this), MeAct.this);

        if (temptype.equals("LTR")) {
            SessionSave.saveSession("Lang_Country", "en_US", MeAct.this);
        } else {
            SessionSave.saveSession("Lang_Country", "ar_EG", MeAct.this);
        }
        Configuration config = new Configuration();
        String langcountry = SessionSave.getSession("Lang_Country", MeAct.this);
        String[] arry = langcountry.split("_");
        String language = SessionSave.getSession("Lang", MeAct.this);

        config.locale = new Locale(language, arry[1]);
        Locale.setDefault(new Locale(language, arry[1]));
        MeAct.this.getBaseContext().getResources().updateConfiguration(config, MeAct.this.getResources().getDisplayMetrics());

        Intent intent = new Intent(MeAct.this, MyStatus.class);
        showLoading(MeAct.this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Adding click listener to required views.
     */
    private void setOnclicklistener() {
        slider.setOnClickListener(this);
        DoneBtn.setOnClickListener(this);
        profileImage.setOnClickListener(this);
        btnupload.setOnClickListener(this);
        btntaxidetail.setOnClickListener(this);
        me_layout.setOnClickListener(this);
        /*try {
            PackageInfo info = getPackageManager().getPackageInfo("com.Taximobility.driver", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
*/
    }

    @Override
    public void onBackPressed() {

         super.onBackPressed();


    }

    /**
     * Actions to be performed Onclick.
     */
    @Override
    public void onClick(View v) {

        try {
            // If logout view clicked the following process runs.
            if (v == btnLogout) {
                logout(MeAct.this);
            }
            // If back view clicked the following process runs.
            if (v == slider) {
                if (profileImage != null) {
                    finish();
                }
            }
            // If profile image view clicked the following process run.
            else if (v == profileImage) {

                try {
                    if (ActivityCompat.checkSelfPermission(MeAct.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(MeAct.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        dialog1 = Utils.alert_view_dialog(MeAct.this, "", NC.getResources().getString(R.string.str_media), NC.getResources().getString(R.string.yes), "", true, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                ActivityCompat.requestPermissions(MeAct.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_CAMERA);
                                dialog.dismiss();
                            }
                        }, (dialogInterface, i) -> dialogInterface.dismiss(), "");
                    } else
                        getCamera();
                } catch (Exception e) {

                    // TODO: handle exception
                }
            }
            // If done view to update the driver basic details.
            else if (v == DoneBtn) {
                phone = mobileEdt.getText().toString().trim();
                newpwd = passwordEdt.getText().toString().trim();
                confirmpwd = confirmpswdEdt.getText().toString().trim();
                bankname = "";
                bankaccNo = "";
                Drawable drawable = profileImage.getDrawable();
                Bitmap bitmap = ImageUtils.drawableToBitmap(drawable);
                Drawable d = profileImage.getDrawable();
                Bitmap bit = ImageUtils.drawableToBitmap(d);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bit.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                String url = "type=edit_driver_profile_v1";
                new EditProfile(url);

//                if (newpwd.length() > 0) {
//                    if (validations(ValidateAction.isValidPassword, MeAct.this, newpwd)) {
//                        if (!newpwd.equals(confirmpwd)) {
//                            dialog1 = Utils.alert_view(MeAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.pwd_same), NC.getResources().getString(R.string.ok), "", true, MeAct.this, "");
//                        } else {
//                            if (cardnoEdt.getText().toString().length() >= 1) {
//                                if (cardnoEdt.getText().toString().length() < 19 || cardnoEdt.getText().toString().length() == 20 || cardnoEdt.getText().toString().length() == 21 || cardnoEdt.getText().toString().length() == 22) {
//                                    CToast.ShowToast(MeAct.this, NC.getResources().getString(R.string.valid_bankid));
//                                } else {
//                                    if (!cardnoEdt.getText().toString().equalsIgnoreCase(SessionSave.getSession("bank_id", MeAct.this))) {
//                                        password_dialog();
//                                    } else if (nation_id_Edt.getText().toString().length() >= 1) {
//
//                                        if (nation_id_Edt.getText().toString().length() != 11) {
//                                            CToast.ShowToast(MeAct.this, NC.getResources().getString(R.string.valid_nation_id));
//                                        } else {
//                                            if (validations(ValidateAction.isValueNULL, MeAct.this, phone)) {
//
//                                            }
//                                        }
//
//
//                                    } else if (validations(ValidateAction.isValueNULL, MeAct.this, phone)) {
//                                        Drawable d = profileImage.getDrawable();
//                                        Bitmap bit = ImageUtils.drawableToBitmap(d);
//                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                                        bit.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                                        byte[] byteArray = byteArrayOutputStream.toByteArray();
//                                        base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                                        String url = "type=edit_driver_profile_v1";
//                                        new EditProfile(url);
//                                    }
//                                }
//                            } else {
//
//                                if (cardnoEdt.getText().toString().length() >= 1) {
//                                    if (cardnoEdt.getText().toString().length() < 19 || cardnoEdt.getText().toString().length() == 20 || cardnoEdt.getText().toString().length() == 21 || cardnoEdt.getText().toString().length() == 22) {
//                                        CToast.ShowToast(MeAct.this, NC.getResources().getString(R.string.valid_bankid));
//                                    } else {
//                                        if (!cardnoEdt.getText().toString().equalsIgnoreCase(SessionSave.getSession("bank_id", MeAct.this))) {
//                                            password_dialog();
//                                        } else if (nation_id_Edt.getText().toString().length() >= 1) {
//                                            if (nation_id_Edt.getText().toString().length() != 11) {
//                                                CToast.ShowToast(MeAct.this, NC.getResources().getString(R.string.valid_nation_id));
//                                            } else {
//                                                if (validations(ValidateAction.isValueNULL, MeAct.this, phone)) {
//                                                    Drawable d = profileImage.getDrawable();
//                                                    Bitmap bit = ImageUtils.drawableToBitmap(d);
//                                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                                                    bit.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                                                    byte[] byteArray = byteArrayOutputStream.toByteArray();
//                                                    base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                                                    String url = "type=edit_driver_profile_v1";
//                                                    new EditProfile(url);
//                                                }
//                                            }
//                                        } else if (validations(ValidateAction.isValueNULL, MeAct.this, phone)) {
//                                            Drawable d = profileImage.getDrawable();
//                                            Bitmap bit = ImageUtils.drawableToBitmap(d);
//                                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                                            bit.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                                            byte[] byteArray = byteArrayOutputStream.toByteArray();
//                                            base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                                            String url = "type=edit_driver_profile_v1";
//                                            new EditProfile(url);
//                                        }
//                                    }
//                                } else {
//                                    if (!cardnoEdt.getText().toString().equalsIgnoreCase(SessionSave.getSession("bank_id", MeAct.this))) {
//                                        password_dialog();
//                                    } else if (nation_id_Edt.getText().toString().length() >= 1) {
//                                        if (nation_id_Edt.getText().toString().length() != 11) {
//                                            CToast.ShowToast(MeAct.this, NC.getResources().getString(R.string.valid_nation_id));
//                                        } else {
//                                            if (validations(ValidateAction.isValueNULL, MeAct.this, phone)) {
//                                                Drawable d = profileImage.getDrawable();
//                                                Bitmap bit = ImageUtils.drawableToBitmap(d);
//                                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                                                bit.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                                                byte[] byteArray = byteArrayOutputStream.toByteArray();
//                                                base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                                                String url = "type=edit_driver_profile_v1";
//                                                new EditProfile(url);
//                                            }
//                                        }
//                                    } else if (validations(ValidateAction.isValueNULL, MeAct.this, phone)) {
//                                        Drawable d = profileImage.getDrawable();
//                                        Bitmap bit = ImageUtils.drawableToBitmap(d);
//                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                                        bit.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                                        byte[] byteArray = byteArrayOutputStream.toByteArray();
//                                        base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                                        String url = "type=edit_driver_profile_v1";
//                                        new EditProfile(url);
//                                    }
//                                }
//
//
//                            }
//
//
//                        }
//                    }
//                } else
//                {
//
//                    if (cardnoEdt.getText().toString().length() >= 1) {
//                        if (cardnoEdt.getText().toString().length() < 19 || cardnoEdt.getText().toString().length() == 20 || cardnoEdt.getText().toString().length() == 21 || cardnoEdt.getText().toString().length() == 22) {
//                            CToast.ShowToast(MeAct.this, NC.getResources().getString(R.string.valid_bankid));
//                        } else {
//                            if (!cardnoEdt.getText().toString().equalsIgnoreCase(SessionSave.getSession("bank_id", MeAct.this))) {
//                                password_dialog();
//                            } else if (nation_id_Edt.getText().toString().length() >= 1) {
//                                if (nation_id_Edt.getText().toString().length() != 11) {
//                                    CToast.ShowToast(MeAct.this, NC.getResources().getString(R.string.valid_nation_id));
//                                } else {
//                                    if (validations(ValidateAction.isValueNULL, MeAct.this, phone)) {
//                                        Drawable d = profileImage.getDrawable();
//                                        Bitmap bit = ImageUtils.drawableToBitmap(d);
//                                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                                        bit.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                                        byte[] byteArray = byteArrayOutputStream.toByteArray();
//                                        base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                                        String url = "type=edit_driver_profile_v1";
//                                        new EditProfile(url);
//                                    }
//                                }
//                            } else if (validations(ValidateAction.isValueNULL, MeAct.this, phone)) {
//                                Drawable d = profileImage.getDrawable();
//                                Bitmap bit = ImageUtils.drawableToBitmap(d);
//                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                                bit.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                                byte[] byteArray = byteArrayOutputStream.toByteArray();
//                                base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                                String url = "type=edit_driver_profile_v1";
//                                Log.i("profile picture", base64);
//                                new EditProfile(url);
//                            }
//                        }
//                    } else {
//                        if (!cardnoEdt.getText().toString().equalsIgnoreCase(SessionSave.getSession("bank_id", MeAct.this))) {
//                            password_dialog();
//                        } else if (nation_id_Edt.getText().toString().length() >= 1) {
//                            if (nation_id_Edt.getText().toString().length() != 11) {
//                                CToast.ShowToast(MeAct.this, NC.getResources().getString(R.string.valid_nation_id));
//                            } else {
//                                if (validations(ValidateAction.isValueNULL, MeAct.this, phone)) {
//                                    Drawable d = profileImage.getDrawable();
//                                    Bitmap bit = ImageUtils.drawableToBitmap(d);
//                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                                    bit.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                                    byte[] byteArray = byteArrayOutputStream.toByteArray();
//                                    base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                                    String url = "type=edit_driver_profile_v1";
//                                    new EditProfile(url);
//                                }
//                            }
//                        } else if (validations(ValidateAction.isValueNULL, MeAct.this, phone)) {
//                            Drawable d = profileImage.getDrawable();
//                            Bitmap bit = ImageUtils.drawableToBitmap(d);
//                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                            bit.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                            byte[] byteArray = byteArrayOutputStream.toByteArray();
//                            base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                            String url = "type=edit_driver_profile_v1";
//                            Log.i("profile picture", base64);
//                            new EditProfile(url);
//                        }
//                    }
//
//                }
            } else if (v == forgotpswdTxt) {
                Intent intent = new Intent(MeAct.this, ChangepassAct.class);
                startActivity(intent);
            }
            // If file upload view to update the driver document details.
            else if (v == btnupload) {
                final TextView btnchoose = fileDialog.findViewById(R.id.btnchoose);
                final TextView btnsubmit = fileDialog.findViewById(R.id.btnsubmit);
                fileimg = fileDialog.findViewById(R.id.fileimg);
                fileimg.setImageResource(R.drawable.no_file);
                fileDialog.show();
                btnchoose.setOnClickListener(v1 -> {
                    imageSelect = 0;
                    getCamera();
                });
                btnsubmit.setOnClickListener(v12 -> {
                    try {
                        if (imageSelected == 1) {
                            Drawable d = fileimg.getDrawable();
                            Bitmap bit = ImageUtils.drawableToBitmap(d);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bit.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            file_base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            String url = "type=driver_document_upload";
                            new FileUpload(url);
                        } else {
                            dialog1 = Utils.alert_view(MeAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.please_choose), NC.getResources().getString(R.string.ok), "", true, MeAct.this, "");
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        dialog1 = Utils.alert_view(MeAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.image_failed), NC.getResources().getString(R.string.ok), "", true, MeAct.this, "");
                    }
                });
                fileDialog.setOnDismissListener(dialog -> {
                    fileimg.setImageResource(R.drawable.no_file);
                });
            } else if (v == btntaxidetail) {
              //  showtaxiDetails();

                //St
                Intent intent = new Intent(MeAct.this, FleetDetailsActivity.class);
                startActivity(intent);
            } else if (v == me_layout) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(me_layout.getWindowToken(), 0);
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private void password_dialog() {
        final View view = View.inflate(MeAct.this, R.layout.password_popup, null);
        mDialog = new Dialog(MeAct.this, R.style.NewDialog);
        mDialog.setContentView(view);
        Colorchange.ChangeColor(mDialog.findViewById(R.id.inner_content), MeAct.this);
        FontHelper.applyFont(MeAct.this, mDialog.findViewById(R.id.inner_content));
        mDialog.setCancelable(true);
        mDialog.show();
        final EditText mail = mDialog.findViewById(R.id.forgotmail);
        final Button OK = mDialog.findViewById(R.id.okbtn);
        final Button Cancel = mDialog.findViewById(R.id.cancelbtn);
        OK.setOnClickListener(new OnClickListener() {
            private String Email;

            @Override
            public void onClick(final View v) {
                try {
                    Email = mail.getText().toString();
                    if (validations(ValidateAction.isValueNULL, MeAct.this, Email)) {
                        JSONObject j = new JSONObject();
                        j.put("driver_id", SessionSave.getSession("Id", MeAct.this));
                        j.put("password", Email);
                        final String url = "type=bank_card_id_validate_password";
                        new PasswordCheck(url, Email);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                getCamera();

            }  // permission denied, boo! Disable the
            // functionality that depends on this permission.

        }
    }

    @Override
    protected void onStop() {
        Utils.closeDialog(fileDialog);
        Utils.closeDialog(tDialog);
        Utils.closeDialog(mDialog);
        Utils.closeDialog(mlangDialog);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mTargetPrompt != null) {
            mTargetPrompt.dismiss();
        }
        if (dialog1 != null)
            Utils.closeDialog(dialog1);
        super.onDestroy();
    }

    public void showLoading() {
        profile_lay_s.setVisibility(View.GONE);
        slide_lay.setVisibility(View.GONE);
        layout_loading.setVisibility(View.VISIBLE);
        layout_bottom.setVisibility(View.GONE);
    }

    public void cancelLoading() {
        profile_lay_s.setVisibility(View.VISIBLE);
        slide_lay.setVisibility(View.VISIBLE);
        layout_loading.setVisibility(View.GONE);
        layout_bottom.setVisibility(View.VISIBLE);
        showPrompt();
    }

    /**
     * Getting path
     */
    private String getRealPathFromURI(final String contentURI) {

        final Uri contentUri = Uri.parse(contentURI);
        final Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null)
            return contentUri.getPath();
        else {
            cursor.moveToFirst();
            final int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void getCamera() {

        dialog1 = Utils.alert_view_dialog(this, NC.getResources().getString(R.string.profile_image), NC.getResources().getString(R.string.choose_an_image), NC.getResources().getString(R.string.camera), NC.getResources().getString(R.string.gallery), true, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            imageUri = FileProvider.getUriForFile(MeAct.this,
                                    MeAct.this.getPackageName().concat(".files_root"),
                                    photoFile);
                        } else {
                            imageUri = Uri.fromFile(photoFile);
                        }

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(takePictureIntent, 1);
                    }
                }


            }
        }, (dialog, i) -> {
            final Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(intent, 0);
            dialog.cancel();
        }, "");
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = getDateForCreateImageFile();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            // ResultActivity.startWithUri(SampleActivity.this, resultUri);
            Systems.out.println("Hellow" + resultUri);
            new ImageCompressionAsyncTask().execute(resultUri.toString());
        } else {
            // Toast.makeText(SampleActivity.this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(final int requestcode, final int resultcode, final Intent data) {
        try {
            if (requestcode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            } else if (resultcode == RESULT_OK) {
                switch (requestcode) {
                    case 0:
                        try {
                            UCrop uCrop = UCrop.of(Uri.fromFile(new File(getRealPathFromURI(data.getDataString()))), Uri.fromFile(new File(MeAct.this.getCacheDir(), destinationFileName)))
                                    .useSourceImageAspectRatio().withAspectRatio(1, 1)
                                    .withMaxResultSize(400, 400);
                            UCrop.Options options = new UCrop.Options();
                            options.setToolbarColor(ContextCompat.getColor(MeAct.this, R.color.appbg));
                            options.setStatusBarColor(ContextCompat.getColor(MeAct.this, R.color.header_text));
                            options.setToolbarWidgetColor(ContextCompat.getColor(MeAct.this, R.color.header_text));
                            options.setMaxBitmapSize(1000000000);
                            uCrop.withOptions(options);
                            uCrop.start(MeAct.this);
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        try {
                            UCrop.of(imageUri, Uri.fromFile(new File(MeAct.this.getCacheDir(), destinationFileName)))
                                    .withAspectRatio(1, 1)
                                    .withMaxResultSize(2000, 2000)
                                    .start(MeAct.this);
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method used to call logout API.
     */
    public void showtaxiDetails() {

        try {
            final View view = View.inflate(MeAct.this, R.layout.taxidetail_lay, null);
            if (tDialog != null && tDialog.isShowing())
                tDialog.cancel();
            tDialog = new Dialog(MeAct.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

            tDialog.setContentView(view);
            tDialog.setCancelable(true);
            tDialog.show();
            Colorchange.ChangeColor(tDialog.findViewById(R.id.alert_id), MeAct.this);
            FontHelper.applyFont(MeAct.this, tDialog.findViewById(R.id.alert_id));
            final EditText modelTxt = tDialog.findViewById(R.id.modelTxt);
            final EditText taxinoTxt = tDialog.findViewById(R.id.taxinoTxt);
            final EditText assignfromTxt = tDialog.findViewById(R.id.assignfromTxt);
            final EditText assigntoTxt = tDialog.findViewById(R.id.assigntoTxt);
            modelTxt.setText(taxi_model);
            taxinoTxt.setText(taxi_no);
            assignfromTxt.setText(taxi_map_from);
            assigntoTxt.setText(taxi_map_to);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPrompt() {
        if (!SessionSave.getSession(CommonData.SHOW_PROFILE_TOOLTIP, MeAct.this, false)) {
            mTargetPrompt = new MaterialTapTargetPrompt.Builder(MeAct.this)
                    .setTarget(findViewById(R.id.tool_tip))
                    .setFocalRadius(0f)
                    .setAnimationInterpolator(new FastOutSlowInInterpolator())
                    .setBackgroundColour(CL.getResources().getColor(R.color.tooltip_background))
                    .setPrimaryText(NC.getString(R.string.wallet_moved_tooltip))
                    .setSecondaryText(NC.getString(R.string.ok))
                    .setPrimaryTextColour(CL.getResources().getColor(R.color.white))
                    .setSecondaryTextColour(CL.getResources().getColor(R.color.pastbookingcashtext))
                    .setFocalPadding(R.dimen.sp_20)
                    .show();

            SessionSave.saveSession(CommonData.SHOW_PROFILE_TOOLTIP, true, MeAct.this);
        }
    }

    /**
     * Call string method used to call string file from back end
     */
    private class callString implements APIResult {
        public callString(final String url) {
            String urls = SessionSave.getSession("currentStringUrl", MeAct.this);
            if (urls.equals(""))
                urls = SessionSave.getSession("Lang_English", MeAct.this);
            new APIService_Retrofit_JSON(MeAct.this, this, null, true, urls, true).execute();
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
                SessionSave.saveSession("wholekey", result, MeAct.this);
                getAndStoreStringValues(result);
                RefreshAct();
            }
        }
    }


    private class PasswordCheck implements APIResult {
        String msg = "";

        public PasswordCheck(String url, String pwd) {

            try {
                JSONObject j = new JSONObject();
                j.put("driver_id", SessionSave.getSession("Id", MeAct.this));
                j.put("password", pwd);
                if (isOnline()) {
                    new APIService_Retrofit_JSON(MeAct.this, this, j, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(MeAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, MeAct.this, "");
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, final String result) {

            try {
                if (isSuccess) {
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        //     dialog1 = Utils.alert_view(MeAct.this, "" + NC.getResources().getString(R.string.message), "" + msg, "" + NC.getResources().getString(R.string.ok), "", true, MeAct.this, "");

                        if (validations(ValidateAction.isValueNULL, MeAct.this, phone)) {
                            Drawable d = profileImage.getDrawable();
                            Bitmap bit = ImageUtils.drawableToBitmap(d);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bit.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            String url = "type=edit_driver_profile_v1";
                            new EditProfile(url);
                        }

                    } else {
                        msg = json.getString("message");
                        dialog1 = Utils.alert_view(MeAct.this, NC.getResources().getString(R.string.message), msg, NC.getResources().getString(R.string.ok), "", true, MeAct.this, "");
                    }
                } else {
                    // runOnUiThread(() -> CToast.ShowToast(MeAct.this, NC.getString(R.string.server_error)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Used to call the edit profile Api(post method) and parse the response
     */
    private class EditProfile implements APIResult {
        String msg = "";

        public EditProfile(String url) {

            try {
                JSONObject j = new JSONObject();
                j.put("driver_id", SessionSave.getSession("Id", MeAct.this));
                j.put("salutation", SessionSave.getSession("Salutation", MeAct.this));
                j.put("email", emailEdt.getText().toString());
                j.put("phone", phone);
                j.put("firstname", firstTxt.getText().toString());
                j.put("lastname", lastTxt.getText().toString());
                j.put("password", confirmpwd);
                j.put("bankname", bankname);
                j.put("bankaccount_no", bankaccNo);
                j.put("bank_id", cardnoEdt.getText().toString());
                j.put("national_id", nation_id_Edt.getText().toString());
                j.put("profile_picture", encodedImage);
                if (isOnline()) {
                    new APIService_Retrofit_JSON(MeAct.this, this, j, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(MeAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, MeAct.this, "");
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, final String result) {

            try {
                if (isSuccess) {
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        SessionSave.saveSession("Bankname", bankname, MeAct.this);
                        SessionSave.saveSession("Bankaccount_No", bankaccNo, MeAct.this);
                        SessionSave.saveSession("Org_Password", confirmpwd, MeAct.this);
                        SessionSave.saveSession("Phone", phone, MeAct.this);
                        SessionSave.saveSession("Name", firstTxt.getText().toString(), MeAct.this);
                        SessionSave.saveSession("Lastname", lastTxt.getText().toString(), MeAct.this);
                        SessionSave.saveSession("Email", emailEdt.getText().toString(), MeAct.this);
                        SessionSave.saveSession("driver_wallet_amount", json.getJSONObject("detail").getString("driver_wallet_amount"), MeAct.this);
                        SessionSave.saveSession("driver_wallet_pending_amount", json.getJSONObject("detail").getString("driver_wallet_pending_amount"), MeAct.this);
                        SessionSave.saveSession("trip_amount", json.getJSONObject("detail").getString("trip_amount"), MeAct.this);
                        SessionSave.saveSession("trip_pending_amount", json.getJSONObject("detail").getString("trip_pending_amount"), MeAct.this);
                        Drawable drawable = profileImage.getDrawable();
                        downImage = ImageUtils.drawableToBitmap(drawable);
                        msg = json.getString("message");
                        confirmpswdEdt.setText("");
                        passwordEdt.setText("");
                        dialog1 = Utils.alert_view(MeAct.this, NC.getResources().getString(R.string.message), msg, NC.getResources().getString(R.string.ok), "", true, MeAct.this, "");
                    } else {
                        msg = json.getString("message");
                        dialog1 = Utils.alert_view(MeAct.this, NC.getResources().getString(R.string.message), msg, NC.getResources().getString(R.string.ok), "", true, MeAct.this, "");
                    }
                } else {
                    // runOnUiThread(() -> CToast.ShowToast(MeAct.this, NC.getString(R.string.server_error)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @API call(get method) to get the driver profile data and parsing the response
     */
    private class GetProfileData implements APIResult {
        public GetProfileData(String url, JSONObject data) {

            try {
                if (isOnline()) {
                    new APIService_Retrofit_JSON(MeAct.this, this, data, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(MeAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, MeAct.this, "");
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, final String result) {
            cancelLoading();
            try {
                if (isSuccess) {
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        JSONObject details = json.getJSONObject("detail");

                        if (details.has(CommonData.SKIP_DRIVER_EMAIL))
                            SessionSave.saveSession(CommonData.SKIP_DRIVER_EMAIL, details.getString(CommonData.SKIP_DRIVER_EMAIL).equals("1"), MeAct.this);
                        else
                            SessionSave.saveSession(CommonData.SKIP_DRIVER_EMAIL, false, MeAct.this);

                        if (SessionSave.getSession(CommonData.SKIP_DRIVER_EMAIL, MeAct.this, false))
                            emailEdt.setHint(NC.getString(R.string.email_optional));
                        else
                            emailEdt.setHint(NC.getString(R.string.email));

                        firstTxt.setText(Html.fromHtml(details.getString("name")));
                        lastTxt.setText(Html.fromHtml(details.getString("lastname")));
                        emailEdt.setText(details.getString("email"));
                        mobileEdt.setText(details.getString("phone"));
                        nation_id_Edt.setText(details.getString("national_id"));
                        cardnoEdt.setText(details.getString("bank_id"));
                        bank_id_change = details.getString("bank_id_change");
                        SessionSave.saveSession("bank_id", details.getString("bank_id"), MeAct.this);
                        SessionSave.saveSession(CommonData.PLAN_TYPE, details.getString("commission_subscription"), MeAct.this);
                        SessionSave.saveSession("driver_wallet_amount", details.getString("driver_wallet_amount"), MeAct.this);
                        SessionSave.saveSession("driver_wallet_pending_amount", details.getString("driver_wallet_pending_amount"), MeAct.this);
                        SessionSave.saveSession("trip_amount", details.getString("trip_amount"), MeAct.this);
                        SessionSave.saveSession("trip_pending_amount", details.getString("trip_pending_amount"), MeAct.this);
                        SessionSave.saveSession("referal_amount", "300", MeAct.this);
                        SessionSave.saveSession("total_amount", details.getString("total_amount"), MeAct.this);
                        if (details.has("driver_referral_settings"))
                            SessionSave.saveSession("referal", details.getString("driver_referral_settings"), getApplicationContext());


                        if (bank_id_change.equalsIgnoreCase("0")) {
                            cardnoEdt.setEnabled(true);
                            cardnoEdt.setFocusable(true);
                        } else {
                            cardnoEdt.setEnabled(false);
                            cardnoEdt.setFocusable(false);
                        }

                        walletamountr = details.getInt("driver_wallet_amount");

                        String imgPath = details.getString("main_image_path").trim();
                        taxi_model = details.getString("taxi_model");

                        taxi_no = details.getString("taxi_no");
                        taxi_map_from = details.getString("taxi_map_from");
                        taxi_map_to = details.getString("taxi_map_to");
                        driver_rating = details.getInt("driver_rating");
                        driverRat.setImageResource(0);
                        if (driver_rating == 0)
                            driverRat.setImageResource(R.drawable.star6);
                        else if (driver_rating == 1)
                            driverRat.setImageResource(R.drawable.star1);
                        else if (driver_rating == 2)
                            driverRat.setImageResource(R.drawable.star2);
                        else if (driver_rating == 3)
                            driverRat.setImageResource(R.drawable.star3);
                        else if (driver_rating == 4)
                            driverRat.setImageResource(R.drawable.star4);
                        else if (driver_rating == 5)
                            driverRat.setImageResource(R.drawable.star5);
                        if (imgPath != null && imgPath.length() > 0) {
                            Picasso.get().load(imgPath).placeholder(getResources().getDrawable(R.drawable.loadingimage)).error(getResources().getDrawable(R.drawable.noimage)).into(profileImage);
                        }


                        if (details.getString("driver_licence_path") != null && details.getString("driver_licence_path").length() > 0) {
                            Picasso.get().load(details.getString("driver_licence_path")).placeholder(getResources().getDrawable(R.drawable.loadingimage)).error(getResources().getDrawable(R.drawable.loadingimage)).into(imgV_license);

                        }


                        if (details.getString("driver_licence_back_side_path") != null && details.getString("driver_licence_back_side_path").length() > 0) {
                            Picasso.get().load(details.getString("driver_licence_back_side_path")).placeholder(getResources().getDrawable(R.drawable.loadingimage)).error(getResources().getDrawable(R.drawable.loadingimage)).into(imgV_license_back);

                        }


                        Drawable drawable = profileImage.getDrawable();
                        downImage = ImageUtils.drawableToBitmap(drawable);
                        SessionSave.saveSession("Bankname", details.getString("bankname"), MeAct.this);
                        SessionSave.saveSession("Bankaccount_No", details.getString("bankaccount_no"), MeAct.this);
                        SessionSave.saveSession("Org_Password", confirmpwd, MeAct.this);
                        SessionSave.saveSession("Phone", details.getString("phone"), MeAct.this);
                        SessionSave.saveSession("Name", details.getString("name"), MeAct.this);
                        SessionSave.saveSession("Lastname", details.getString("lastname"), MeAct.this);
                        SessionSave.saveSession("Email", details.getString("email"), MeAct.this);
                        if (!details.getString("plan_expiration_message").equals("")) {
                            dialog1 = Utils.alert_view(MeAct.this, NC.getResources().getString(R.string.message), details.getString("plan_expiration_message"), NC.getResources().getString(R.string.ok), "", true, MeAct.this, "");
                        }
                    } else if (json.getInt("status") == -4 || json.getInt("status") == -1) {
                        Intent locationService = new Intent(MeAct.this, LocationUpdate.class);
                        stopService(new Intent(locationService));
                        clearsession(MeAct.this);
                        dialog1 = Utils.alert_view_dialog(MeAct.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", false, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int length = CommonData.mActivitylist.size();
                                if (length != 0) {
                                    for (int i = 0; i < length; i++) {
                                        CommonData.mActivitylist.get(i).finish();
                                    }
                                }
                                dialog.dismiss();
                                Intent intent = new Intent(MeAct.this, UserLoginAct.class);
                                startActivity(intent);
                                finish();
                            }
                        }, (dialog, which) -> dialog.dismiss(), "");
                    } else if (json.getInt("status") == -2) {
                        dialog1 = Utils.alert_view(MeAct.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, MeAct.this, "");
                    } else if (json.getInt("status") == -3) {
                        dialog1 = Utils.alert_view(MeAct.this, NC.getResources().getString(R.string.message), json.getString("message"), NC.getResources().getString(R.string.ok), "", true, MeAct.this, "");
                    } else {
                        CToast.ShowToast(MeAct.this, json.getString("message"));
                    }
                    profile_lay_s.setVisibility(View.VISIBLE);
                    slide_lay.setVisibility(View.VISIBLE);
                    me_layout.setVisibility(View.VISIBLE);

//                    if (SessionSave.getSession(CommonData.PLAN_TYPE, MeAct.this).equals("1")) {
//                        if (SessionSave.getSession("referal", MeAct.this).equalsIgnoreCase("1"))
//                            invitefriends_bottom.setVisibility(View.VISIBLE);
//                        else {
//                            invitefriends_bottom.setVisibility(View.GONE);
//                            findViewById(R.id.inviteView).setVisibility(View.GONE);
//                        }
//                        subscription.setVisibility(View.GONE);
//                    } else {
//                        Systems.out.println("nan----222");
//                        if (SessionSave.getSession("referal", MeAct.this).equalsIgnoreCase("1"))
//                            invitefriends_bottom.setVisibility(View.VISIBLE);
//                        else
//                            invitefriends_bottom.setVisibility(View.GONE);
//
//                        subscription.setVisibility(View.GONE);
//                    }
                } else {
                    // CToast.ShowToast(MeAct.this, NC.getString(R.string.server_error));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ImageCompressionAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private Dialog mDialog;
        private String result;
        private int orientation;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            final View view = View.inflate(MeAct.this, R.layout.progress_bar, null);
            mDialog = new Dialog(MeAct.this, R.style.NewDialog);
            mDialog.setContentView(view);
            mDialog.setCancelable(false);
            mDialog.show();

            ImageView iv = mDialog.findViewById(R.id.giff);
            DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
            Glide.with(MeAct.this)
                    .load(R.raw.loading_anim)
                    .into(imageViewTarget);
        }

        @Override
        protected Bitmap doInBackground(final String... params) {
            try {
                result = getRealPathFromURI(params[0]);
                final File file = new File(result);
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                mBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                final ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                final byte[] image = stream.toByteArray();
                encodedImage = Base64.encodeToString(image, Base64.DEFAULT);
            } catch (final Exception e) {
                // TODO: handle exception
                runOnUiThread(() -> {
                    CToast.ShowToast(MeAct.this, NC.getResources().getString(R.string.image_failed));
                });
            }
            return mBitmap;
        }

        @Override
        protected void onPostExecute(final Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                if (MeAct.this != null && mDialog.isShowing())
                    mDialog.dismiss();

                profileImage.setBackgroundResource(0);
                if (result != null)
                    profileImage.setImageBitmap(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @API call(method) to update the driver document file details and parsing the response
     */
    private class FileUpload implements APIResult {
        String msg = "";

        public FileUpload(String url) {

            try {
                if (isOnline()) {
                    JSONObject j = new JSONObject();
                    j.put("driver_id", SessionSave.getSession("Id", MeAct.this));
                    j.put("driver_document", file_base64);
                    j.put("device_type", "1");
                    new APIService_Retrofit_JSON(MeAct.this, this, j, false).execute(url);
                } else {
                    dialog1 = Utils.alert_view(MeAct.this, NC.getResources().getString(R.string.message), NC.getResources().getString(R.string.check_net_connection), NC.getResources().getString(R.string.ok), "", true, MeAct.this, "");
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        @Override
        public void getResult(boolean isSuccess, final String result) {

            try {
                if (isSuccess) {
                    JSONObject json = new JSONObject(result);
                    if (json.getInt("status") == 1) {
                        msg = json.getString("message");
                        fileDialog.dismiss();
                        file_base64 = "";
                        dialog1 = Utils.alert_view(MeAct.this, NC.getResources().getString(R.string.message), msg, NC.getResources().getString(R.string.ok), "", true, MeAct.this, "");
                    } else {
                        msg = json.getString("message");
                        fileDialog.dismiss();
                        file_base64 = "";
                        dialog1 = Utils.alert_view(MeAct.this, NC.getResources().getString(R.string.message), msg, NC.getResources().getString(R.string.ok), "", true, MeAct.this, "");
                    }
                } else {
                    fileDialog.dismiss();
                    // runOnUiThread(() -> CToast.ShowToast(MeAct.this, NC.getString(R.string.server_error)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                fileDialog.dismiss();
            }
        }
    }
}