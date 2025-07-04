//package com.taximobility.driver;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.IntentFilter;
//import android.net.ConnectivityManager;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.text.Html;
//import android.text.InputFilter;
//import android.text.InputType;
//import android.text.SpannableString;
//import android.text.TextUtils;
//import android.text.style.UnderlineSpan;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.view.inputmethod.EditorInfo;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.taximobility.driver.interfaces.APIResult;
//import com.taximobility.driver.service.APIService_Retrofit_JSON;
//import com.taximobility.driver.service.ServiceGenerator;
//import com.taximobility.driver.utils.CL;
//import com.taximobility.driver.utils.CToast;
//import com.taximobility.driver.utils.Colorchange;
//import com.taximobility.driver.utils.FontHelper;
//import com.taximobility.driver.utils.NC;
//import com.taximobility.driver.utils.NetworkStatus;
//import com.taximobility.driver.utils.SessionSave;
//import com.taximobility.driver.utils.Systems;
//import com.taximobility.driver.utils.Utils;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.target.DrawableImageViewTarget;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.Locale;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * Created by developer on 15/11/17.
// */
//
//public class WalletActivity extends BaseActivity implements View.OnClickListener {
//
//
//    private LinearLayout SlideImg;
//
//    private LinearLayout Donelay;
//    private TextView BackBtn;
//    private TextView leftIcon;
//    private TextView HeadTitle;
//    private TextView walletbalTxt;
//    private Button addmoneyBut;
//    private EditText addmoneyEdt;
//    private TextView monoption1;
//    private TextView monoption2;
//    private TextView monoption3;
//    private TextView procodeTxt;
//    private String promoCode = "";
//    private Dialog mDialog;
//    private int addMoney = 0;
//    private Double walletAmount;
//    private String wallet_amount_range = "";
//    private String wallet_amount1 = "";
//    private String wallet_amount2;
//    private String wallet_amount3;
//    private int range1;
//    private int range2;
//    private Dialog alertmDialog;
//    private Dialog mshowDialog;
//    private LinearLayout loading;
//    private NetworkStatus networkStatus;
//
//    private Dialog dialog1;
//
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        networkStatus = new NetworkStatus();
//        registerReceiver(networkStatus, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//        setContentView(R.layout.walletlay);
//        NetworkStatus.appContext = this;
//    }
//
//    public void findViewById(View v) {
//        FontHelper.applyFont(WalletActivity.this, v.findViewById(R.id.rootContain));
//        HeadTitle = v.findViewById(R.id.header_titleTxt);
//        HeadTitle.setText(NC.getResources().getString(R.string.wallet));
//        leftIcon = v.findViewById(R.id.leftIcon);
//        leftIcon.setVisibility(View.GONE);
//        BackBtn = v.findViewById(R.id.back_text);
//        BackBtn.setVisibility(View.VISIBLE);
//
//        Glide.with(this).load(SessionSave.getSession("image_path", WalletActivity.this) + "walletPageIcon.png").into((ImageView) v.findViewById(R.id.walletPageIcon));
//        Initialize(v);
//    }
//
//    public void Initialize(View v) {
//        // TODO Auto-generated method stub
//
//        Colorchange.ChangeColor((ViewGroup) v, WalletActivity.this);
//
//        SlideImg = v.findViewById(R.id.leftIconTxt);
//
//        Donelay = v.findViewById(R.id.rightlay);
//        Donelay.setVisibility(View.INVISIBLE);
//        ((TextView) v.findViewById(R.id.cur_sym)).setText(SessionSave.getSession("Currency", WalletActivity.this));
//
//        walletbalTxt = v.findViewById(R.id.walletbalTxt);
//        loading = v.findViewById(R.id.loading);
//        ImageView iv = v.findViewById(R.id.giff);
//        DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(iv);
//        Glide.with(WalletActivity.this)
//                .load(R.raw.loading_anim)
//                .into(imageViewTarget);
//
//        addmoneyBut = v.findViewById(R.id.addmoneyBut);
//        addmoneyEdt = v.findViewById(R.id.addmoneyEdt);
//        monoption1 = v.findViewById(R.id.monoption1);
//        monoption2 = v.findViewById(R.id.monoption2);
//        monoption3 = v.findViewById(R.id.monoption3);
//        procodeTxt = v.findViewById(R.id.procodeTxt);
//
//        setOnclickListener();
//
//        addmoneyEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
//                    addmoneyBut.performClick();
//                }
//                return false;
//            }
//        });
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        CheckWallet();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//
//    private void setOnclickListener() {
//
//        BackBtn.setOnClickListener(this);
//        addmoneyBut.setOnClickListener(this);
//        monoption1.setOnClickListener(this);
//        monoption2.setOnClickListener(this);
//        monoption3.setOnClickListener(this);
//        procodeTxt.setOnClickListener(this);
//
//        SpannableString content = new SpannableString(NC.getString(R.string.have_promocode).trim());
//        content.setSpan(new UnderlineSpan(), 0, NC.getString(R.string.have_promocode).trim().length(), 0);
//        procodeTxt.setText(Html.fromHtml("<p><u>" + (NC.getString(R.string.have_promocode).trim()) + "<p><u>"));
//    }
//
//    /**
//     * Calls api for wallet amount enqury
//     */
//    private void CheckWallet() {
//
//        try {
//            JSONObject j = new JSONObject();
//            j.put("passenger_id", SessionSave.getSession("Id", WalletActivity.this));
//            String url = "type=passenger_wallet";
//            new WalletBal(url, j);
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * This class used to check wallet balance
//     * <p>
//     * This class used to check wallet balance
//     * <p>
//     *
//     * @author developer
//     */
//    private class WalletBal implements APIResult {
//        private WalletBal(final String url, JSONObject data) {
//
//            new APIService_Retrofit_JSON(WalletActivity.this, this, data, false).execute(url);
//        }
//
//        @Override
//        public void getResult(final boolean isSuccess, final String result) {
//            // TODO Auto-generated method stub
//            if (isSuccess) {
//                try {
//
//                    loading.setVisibility(View.GONE);
//                    addmoneyBut.setVisibility(View.VISIBLE);
//                    final JSONObject json = new JSONObject(result);
//                    if (json.getInt("status") == 1) {
//                        walletAmount = Double.parseDouble(json.getString("wallet_amount"));
//                        wallet_amount_range = json.getJSONObject("amount_details").getString("wallet_amount_range");
//                        wallet_amount1 = json.getJSONObject("amount_details").getString("wallet_amount1");
//                        wallet_amount2 = json.getJSONObject("amount_details").getString("wallet_amount2");
//                        wallet_amount3 = json.getJSONObject("amount_details").getString("wallet_amount3");
//                        String[] rangeary = wallet_amount_range.split("-");
//
//                        if (rangeary.length > 1) {
//                            range1 = Integer.parseInt(rangeary[0]);
//                            range2 = Integer.parseInt(rangeary[1]);
//                        }
//                        addmoneyEdt.setHint("" + NC.getResources().getString(R.string.amount_between) + " " + SessionSave.getSession("Currency", WalletActivity.this) + range1 + "-" + SessionSave.getSession("Currency", WalletActivity.this) + range2);
//                        monoption1.setText("" + SessionSave.getSession("Currency", WalletActivity.this) + wallet_amount1);
//                        monoption2.setText("" + SessionSave.getSession("Currency", WalletActivity.this) + wallet_amount2);
//                        monoption3.setText("" + SessionSave.getSession("Currency", WalletActivity.this) + wallet_amount3);
//                        walletbalTxt.setText("" + SessionSave.getSession("Currency", WalletActivity.this) + String.format(Locale.UK, "%.2f", walletAmount));
//                    } else {
//                        walletbalTxt.setText("" + SessionSave.getSession("Currency", WalletActivity.this) + "0.00");
//                    }
//                } catch (final JSONException e) {
//                    e.printStackTrace();
//                }
//            } else {
//
//                dialog1 = Utils.alert_view_dialog(WalletActivity.this, "Message", "" + result, "" + NC.getResources().getString(R.string.ok), "", true, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }, "");
//            }
//        }
//    }
//
//    /**
//     * This class used to check valid promo code
//     * <p>
//     * This class used to check valid promo code
//     * <p>
//     *
//     * @author developer
//     */
//    private class CheckPromoCode implements APIResult {
//        private CheckPromoCode(final String url, JSONObject data) {
//            new APIService_Retrofit_JSON(WalletActivity.this, this, data, false, (ServiceGenerator.API_BASE_URL + ServiceGenerator.COMPANY_KEY + "/?" + "lang=" + SessionSave.getSession("Lang", WalletActivity.this) + "&" + url), false).execute();
//        }
//
//        @Override
//        public void getResult(final boolean isSuccess, final String result) {
//            // TODO Auto-generated method stub
//            if (isSuccess) {
//                try {
//                    final JSONObject json = new JSONObject(result);
//                    if (json.getInt("status") == 1) {
//                        dialog1 = Utils.alert_view_dialog(WalletActivity.this, "Message", "" + json.getString("message"), "" + NC.getResources().getString(R.string.ok), "", true, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }, "");
//                    } else {
//                        WalletActivity.this.runOnUiThread(new Runnable() {
//                            public void run() {
//                                try {
//                                    CToast.ShowToast(WalletActivity.this, json.getString("message"));
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                        promoCode = "";
//                    }
//                } catch (final JSONException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                    promoCode = "";
//                }
//            } else {
//                promoCode = "";
//                WalletActivity.this.runOnUiThread(new Runnable() {
//                    public void run() {
//                        CToast.ShowToast(WalletActivity.this, NC.getString(R.string.server_error));
//                    }
//                });
//            }
//        }
//    }
//
//    public void alert_view(Context mContext, String title, String message, String success_txt, String failure_txt) {
//        try {
//            final View view = View.inflate(mContext, R.layout.alert_view, null);
//            alertmDialog = new Dialog(mContext, R.style.dialogwinddow);
//            alertmDialog.setContentView(view);
//            alertmDialog.setCancelable(true);
//            FontHelper.applyFont(mContext, alertmDialog.findViewById(R.id.alert_id));
//            Colorchange.ChangeColor(alertmDialog.findViewById(R.id.alert_id), WalletActivity.this);
//            alertmDialog.show();
//            final TextView title_text = alertmDialog.findViewById(R.id.title_text);
//            final TextView message_text = alertmDialog.findViewById(R.id.message_text);
//            final Button button_success = alertmDialog.findViewById(R.id.button_success);
//            final Button button_failure = alertmDialog.findViewById(R.id.button_failure);
//            button_failure.setVisibility(View.GONE);
//            title_text.setText(title);
//            message_text.setText(message);
//            button_success.setText(success_txt);
//            button_success.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View v) {
//                    // TODO Auto-generated method stub
//                    alertmDialog.dismiss();
//                }
//            });
//            button_failure.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View v) {
//                    // TODO Auto-generated method stub
//                    alertmDialog.dismiss();
//                }
//            });
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * this method is used to show the promo code dialog and execute api
//     */
//
//    private void ShowPromoDilaog() {
//
//        try {
//
//            final View view = View.inflate(WalletActivity.this, R.layout.alert_view, null);
//            mDialog = new Dialog(WalletActivity.this, R.style.dialogwinddow);
//            mDialog.setContentView(view);
//            mDialog.setCancelable(true);
//            mDialog.show();
//            Colorchange.ChangeColor(mDialog.findViewById(R.id.alert_id), WalletActivity.this);
//            FontHelper.applyFont(WalletActivity.this, mDialog.findViewById(R.id.alert_id));
//            final TextView titleTxt = mDialog.findViewById(R.id.title_text);
//            final TextView msgTxt = mDialog.findViewById(R.id.message_text);
//            msgTxt.setVisibility(View.GONE);
//            final EditText promocodeEdt = mDialog.findViewById(R.id.forgotmail);
//            final Button OK = mDialog.findViewById(R.id.button_success);
//            final Button Cancel = mDialog.findViewById(R.id.button_failure);
//            Cancel.setVisibility(View.GONE);
//            promocodeEdt.setVisibility(View.VISIBLE);
//            OK.setText("" + NC.getResources().getString(R.string.ok));
//            titleTxt.setText("" + NC.getResources().getString(R.string.reg_promocode));
//            int maxLengthpromoCode = getResources().getInteger(R.integer.promoMaxLength);
//            promocodeEdt.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
//            promocodeEdt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLengthpromoCode)});
//            InputFilter[] editFilters = promocodeEdt.getFilters();
//            InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
//            System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
//            newFilters[editFilters.length] = new InputFilter.AllCaps();
//            promocodeEdt.setFilters(newFilters);
//            promocodeEdt.setHint("" + NC.getResources().getString(R.string.reg_enterprcode));
//            OK.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View v) {
//                    // TODO Auto-generated method stub
//                    try {
//                        promoCode = promocodeEdt.getText().toString();
//                        if (TextUtils.isEmpty(promoCode))
//                            if (!TextUtils.isEmpty(promoCode)) {
//                                mDialog.dismiss();
//                                JSONObject j = new JSONObject();
//                                j.put("passenger_id", SessionSave.getSession("Id", WalletActivity.this));
//                                j.put("promo_code", promoCode);
//                                String url = "type=check_valid_promocode";
//                                new CheckPromoCode(url, j);
//                                WalletActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//                            } else {
//                                CToast.ShowToast(WalletActivity.this, NC.getResources().getString(R.string.reg_enterprcode));
//                            }
//                    } catch (Exception e) {
//                        // TODO: handle exception
//                        e.printStackTrace();
//                    }
//                }
//            });
//            Cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View v) {
//                    // TODO Auto-generated method stub
//                    mDialog.dismiss();
//                }
//            });
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//        }
//    }
//
//    //
//
//
//    /**
//     * This is method for check the mail is valid by the use of regex class.
//     */
//    public boolean validdmail(String string) {
//        // TODO Auto-generated method stub
//        boolean isValid = false;
//        String expression = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@" + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|" + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
//        Pattern pattern = Pattern.compile(expression);
//        Matcher matcher = pattern.matcher(string);
//        if (matcher.matches()) {
//            isValid = true;
//        }
//        return isValid;
//    }
//
//    /**
//     * Slider menu used to move from one activity to another activity.
//     *
//     * @param v
//     */
//    @Override
//    public void onClick(View v) {
//        // TODO Auto-generated method stub
//        switch (v.getId()) {
//            case R.id.back_text:
//                // menu.toggle();
//                break;
//            case R.id.addmoneyBut:
//                if (addmoneyEdt.getText().toString().trim().length() != 0) {
//                    addMoney = Integer.parseInt(addmoneyEdt.getText().toString());
//                    Systems.out.println("range1" + range1);
//                    Systems.out.println("range2" + range2);
//                    if (addMoney < range1 || addMoney > range2)
//                        dialog1 = Utils.alert_view_dialog(WalletActivity.this, "" + NC.getResources().getString(R.string.message), "" + NC.getString(R.string.amount_between) + " " + SessionSave.getSession("Currency", WalletActivity.this) + range1 + "-" + SessionSave.getSession("Currency", WalletActivity.this) + range2, "" + NC.getString(R.string.ok), "", true, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        }, "");
//                    else {
//                    }
//                } else {
//                    dialog1 = Utils.alert_view_dialog(WalletActivity.this, "" + NC.getResources().getString(R.string.message), "" + NC.getResources().getString(R.string.enter_amount), "" + NC.getResources().getString(R.string.ok), "", true, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    }, "");
//                }
//                break;
//            case R.id.monoption1:
//                monoption1.setBackgroundResource(R.drawable.draw_select_bg);
//                monoption2.setBackgroundResource(R.drawable.draw_edittext_bg);
//                monoption3.setBackgroundResource(R.drawable.draw_edittext_bg);
//                monoption1.setTextColor(CL.getColor(R.color.button_accept));
//                monoption2.setTextColor(CL.getColor(R.color.hintcolor));
//                monoption3.setTextColor(CL.getColor(R.color.hintcolor));
//                addmoneyEdt.setText("" + wallet_amount1);
//                break;
//            case R.id.monoption2:
//                monoption1.setBackgroundResource(R.drawable.draw_edittext_bg);
//                monoption2.setBackgroundResource(R.drawable.draw_select_bg);
//                monoption3.setBackgroundResource(R.drawable.draw_edittext_bg);
//                monoption2.setTextColor(CL.getColor(R.color.button_accept));
//                monoption1.setTextColor(CL.getColor(R.color.hintcolor));
//                monoption3.setTextColor(CL.getColor(R.color.hintcolor));
//                addmoneyEdt.setText("" + wallet_amount2);
//                break;
//            case R.id.monoption3:
//                monoption1.setBackgroundResource(R.drawable.draw_edittext_bg);
//                monoption2.setBackgroundResource(R.drawable.draw_edittext_bg);
//                monoption3.setBackgroundResource(R.drawable.draw_select_bg);
//                monoption3.setTextColor(CL.getColor(R.color.button_accept));
//                monoption2.setTextColor(CL.getColor(R.color.hintcolor));
//                monoption1.setTextColor(CL.getColor(R.color.hintcolor));
//                addmoneyEdt.setText("" + wallet_amount3);
//                break;
//            case R.id.procodeTxt:
//                ShowPromoDilaog();
//                break;
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        // TODO Auto-generated method stub
//        try {
//            if (mshowDialog != null && mshowDialog.isShowing()) {
//                mshowDialog.dismiss();
//                mshowDialog = null;
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        unregisterReceiver(networkStatus);
//        if (dialog1 != null)
//            Utils.closeDialog(dialog1);
//        super.onDestroy();
//    }
//}