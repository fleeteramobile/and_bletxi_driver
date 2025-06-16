package com.onewaytripcalltaxi.driver

import android.app.Dialog
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.onewaytripcalltaxi.driver.adapter.PendingHistoryAdapter
import com.onewaytripcalltaxi.driver.data.apiData.ApiRequestData
import com.onewaytripcalltaxi.driver.data.apiData.SettlementPaymentData
import com.onewaytripcalltaxi.driver.data.apiData.SettlementReqData
import com.onewaytripcalltaxi.driver.service.RetrofitCallbackClass
import com.onewaytripcalltaxi.driver.utils.Colorchange
import com.onewaytripcalltaxi.driver.utils.DatePicker_CardExpiry
import com.onewaytripcalltaxi.driver.utils.NC
import com.onewaytripcalltaxi.driver.utils.SessionSave
import kotlinx.android.synthetic.main.include_headler.*
import kotlinx.android.synthetic.main.settlement_lay.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SettlementDetail : BaseActivity(), DatePicker_CardExpiry.DialogInterface {

    private var editNameDialog: DatePicker_CardExpiry? = null

    private var mMonth: Int = 0

    private var mDay: Int = 0

    private var mYear: Int = 0

    private var datePick: Boolean = false

    private var fromDate: String = ""

    private var toDate: String = ""

    private var datePic: String = ""

    private val args = Bundle()

    var infoObj: SettlementReqData.Info? = null



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.settlement_lay)
        Colorchange.ChangeColor((this@SettlementDetail
                .findViewById(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup, this@SettlementDetail)

        args.putString("KEY", "0")

        leftIcon.visibility = View.VISIBLE


        header_titleTxt.text = NC.getString(R.string.settlement_request)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.UK)

        toDate = dateFormat.format(Date().time)

        val calendar = Calendar.getInstance()

        calendar.add(Calendar.DAY_OF_YEAR, -7)

        fromDate = dateFormat.format(calendar.time)

        from_txt.text = fromDate

        to_txt.text = toDate

        callRequestApi("", "")

    }

    override fun onResume() {

        super.onResume()

        txt_header_amount.text = NC.getString(R.string.amount) + "(" + SessionSave.getSession("site_currency", this@SettlementDetail).trim() + ")"

        back_trip_details.setOnClickListener {

            finish()

        }

        from_txt_lay.setOnClickListener {

            datePick = true

            from_txt_lay.isClickable = false

            val fm = supportFragmentManager

            editNameDialog = DatePicker_CardExpiry()

            args.putString("selected_date", from_txt.text.toString())

            editNameDialog?.arguments = args

            editNameDialog?.show(fm, "fragment_edit_name")

        }

        to_txt_lay.setOnClickListener {

            datePick = false

            to_txt_lay.isClickable = false

            val fm = supportFragmentManager

            editNameDialog = DatePicker_CardExpiry()

            args.putString("selected_date", to_txt.text.toString())

            editNameDialog?.arguments = args

            editNameDialog?.show(fm, "fragment_edit_name")

        }

        btn_go.setOnClickListener {

            val dateStr = from_txt.text.toString()

            val dateStr2 = to_txt.text.toString()

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.UK)

            val startDateValue = sdf.parse(dateStr)

            val endDateValue = sdf.parse(dateStr2)

            val diff = endDateValue.time - startDateValue.time

            val numberOfDays: Int = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()

            println("Days: $numberOfDays")

            if (numberOfDays > 31) {

                val builder = AlertDialog.Builder(this@SettlementDetail)

                builder.setTitle("")

                builder.setMessage(NC.getString(R.string.days_limit_exceed))

                builder.setPositiveButton(NC.getString(R.string.t_ok)) { dialog, which ->

                    dialog.dismiss()

                }

                val dialogs: AlertDialog = builder.create()

                dialogs.setOnShowListener {

                    if (dialogs != null) {

                        dialogs.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(this@SettlementDetail.resources.getColor(R.color.button_accept))

                        dialogs.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this@SettlementDetail.resources.getColor(R.color.black))

                    }

                }

                dialogs.show()

            } else {

                callRequestApi(dateStr, dateStr2)

            }

        }

        txt_req_admin.setOnClickListener {

            callPaymentApi()

        }

    }

    private fun callPaymentApi() {

        showLoading()

//        val client = ServiceGenerator(this@SettlementDetail, false).createService(CoreClient::class.java)

        val client = MyApplication.getInstance().apiManagerWithEncryptBaseUrl
        val req = ApiRequestData.PaymentReq()

        req.driver_id = SessionSave.getSession("Id", this@SettlementDetail)

        req.comments = ""

        req.info = infoObj

        println("SettlementDetail_req"+ " "+req )
        val call = client.settlement_paymentCall(req, SessionSave.getSession("Lang", this@SettlementDetail))

        showLoading()

        call.enqueue(RetrofitCallbackClass(this@SettlementDetail, object : Callback<SettlementPaymentData> {

            override fun onResponse(call: Call<SettlementPaymentData>, response: Response<SettlementPaymentData>) {

                cancelLoading()

                if (response.isSuccessful) {

                    val data = response.body()

                    val view = View.inflate(this@SettlementDetail, R.layout.custom_msg_popup, null)
                    val mDialog = Dialog(this@SettlementDetail!!, R.style.dialogwinddow_trans)
                    mDialog.setContentView(view)
                    mDialog.setCancelable(true)
                    mDialog.show()
                    val window: Window? = mDialog.getWindow()
                    window!!.setLayout(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT)

                    //        final TextView t = mDialog.findViewById(R.id.f_textview);
//        t.setText(NC.getResources().getString(R.string.email));
                    val mail = mDialog.findViewById<AppCompatTextView>(R.id.msg_txt)

                    mail.setText(data?.message)

                    val OK = mDialog.findViewById<LinearLayout>(R.id.okbtn)
                    //  val Cancel = mDialog.findViewById<TextView>(R.id.cancelbtn)


                    OK.setOnClickListener(object : View.OnClickListener {

                        override fun onClick(arg0: View) {
                            startActivity(Intent(this@SettlementDetail, SettlementHistoryActivity::class.java))
                            mDialog.dismiss()

                        }
                    })

//                    val builder = AlertDialog.Builder(this@SettlementDetail)
//
//                    builder.setTitle("")
//
//                    builder.setMessage(data?.message)
//
//                    builder.setPositiveButton(NC.getString(R.string.ok)) { dialog, which ->
//
//                        dialog.dismiss()
//
//                        if (data?.status == 1) {
//
//
//
//                        }
//
//                    }
//
//                    builder.setCancelable(false)

//                    val dialogs: AlertDialog = builder.create()
//
//                    dialogs.setOnShowListener {
//
//                        if (dialogs != null) {
//
//                            dialogs.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(this@SettlementDetail.resources.getColor(R.color.button_accept))
//
//                            dialogs.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(this@SettlementDetail.resources.getColor(R.color.black))
//
//                        }
//
//                    }
//
//                    dialogs.show()

                }

            }

            override fun onFailure(call: Call<SettlementPaymentData>, t: Throwable) {

                cancelLoading()

                t.printStackTrace()

            }

        }))

    }

    private fun showLoading() {

        showProgress.visibility = View.VISIBLE

        root_layout.visibility = View.GONE

    }

    private fun cancelLoading() {

        showProgress.visibility = View.GONE

        root_layout.visibility = View.VISIBLE

    }

    private fun callRequestApi(from: String, to: String) {

        showLoading()

//        val client = ServiceGenerator(this@SettlementDetail, false).createService(CoreClient::class.java)
        val client = MyApplication.getInstance().apiManagerWithEncryptBaseUrl
        val req = ApiRequestData.SettlementReq()

        req.driver_id = SessionSave.getSession("Id", this@SettlementDetail)

        req.start_date = from

        req.end_date = to

        val call = client.settlement_reqCall(req, SessionSave.getSession("Lang", this@SettlementDetail))

        call.enqueue(RetrofitCallbackClass(this@SettlementDetail, object : Callback<SettlementReqData> {


            override fun onResponse(call: Call<SettlementReqData>, response: Response<SettlementReqData>) {

                cancelLoading()

                val settlementData = response.body()

                if (response.isSuccessful) {

                    if (settlementData?.status == 1) {

                        //layout_content.visibility = View.VISIBLE

                        txt_nodata.visibility = View.GONE

                        infoObj = settlementData.info

                        txt_req.text = settlementData.details?.hints?.replace("#AMOUNT".toRegex(), SessionSave.getSession("site_currency", this@SettlementDetail) + "" + settlementData.details?.total_amount_driver)

                        if (settlementData.details.start_date != null) {

                            from_txt.text = getDate(settlementData.details.start_date.toLong())

                        } else {

                            from_txt.text = fromDate

                        }

                        if (settlementData.details.end_date != null) {

                            to_txt.text = getDate(settlementData.details.end_date.toLong())

                        } else {

                            to_txt.text = toDate

                        }

                        tv_tax_value.text = SessionSave.getSession("site_currency", this@SettlementDetail) + "" + settlementData.details.tax

                        txt_total_val.text = SessionSave.getSession("site_currency", this@SettlementDetail) + "" + settlementData.details?.total_earning

                        txt_net_earnings_val.text = SessionSave.getSession("site_currency", this@SettlementDetail) + "" + settlementData.details?.wallet_amount

                        txt_cash_val.text = SessionSave.getSession("site_currency", this@SettlementDetail) + "" + settlementData.details?.cash_collected

                        txt_card_val.text = SessionSave.getSession("site_currency", this@SettlementDetail) + "" + settlementData.details?.card_payment
                        println(" settlementData_type "+ " "+ settlementData.details?.settlement_type )
                        txt_admin.text = settlementData.details?.settlement_type +" "+ SessionSave.getSession("site_currency", this@SettlementDetail) + " " + settlementData.details?.total_amount_driver
                     ///   txt_admin.text = "Status : "+"" + settlementData.details?.settlement_type +" "+ SessionSave.getSession("site_currency", this@SettlementDetail) + " " + settlementData.details?.total_amount_driver

                       // txt_admin_value.text = ""

                        txt_req_sent.text = NC.getString(R.string.last_req) + settlementData.details?.last_request_date

                        tv_drivercommission_value.text = SessionSave.getSession("site_currency", this@SettlementDetail) + "" + settlementData.details?.driver_commission_amount

                        val adminCommission: Float? = settlementData.details?.admin_commission_amount?.toFloat()

                        if (adminCommission != null && adminCommission > 0) {

                          //  card_view.visibility = View.VISIBLE

                            admincommission_lay.visibility = View.VISIBLE

                            tv_admincommission_value.text = SessionSave.getSession("site_currency", this@SettlementDetail) + "" + settlementData.details?.admin_commission_amount

                        } else {

                          //  card_view.visibility = View.GONE

                            admincommission_lay.visibility = View.GONE

                        }

                        if (settlementData.details.list != null && settlementData.details.list.size > 0) {

                            menu_header_lay.visibility = View.VISIBLE

                            txt_request_payment.visibility = View.VISIBLE

                            val mAdapter = PendingHistoryAdapter(this@SettlementDetail, settlementData.details?.list)

                            rv_settlement.layoutManager = LinearLayoutManager(this@SettlementDetail, LinearLayout.VERTICAL, false)

                            rv_settlement.adapter = mAdapter

                        } else {

                            menu_header_lay.visibility = View.GONE

                            txt_request_payment.visibility = View.GONE

                        }

                        if (settlementData.details.show_button == 1) {

                            txt_req_admin.visibility = View.VISIBLE

                            txt_req_sent.visibility = View.GONE

                        } else {

                            txt_req_admin.visibility = View.GONE

                            txt_req_sent.visibility = View.GONE

                        }

                    } else {

                       // layout_content.visibility = View.GONE

                        txt_nodata.visibility = View.GONE

                        txt_nodata.text = settlementData?.message

                    }

                } else {

               //     layout_content.visibility = View.GONE

                    txt_nodata.visibility = View.GONE

                    txt_nodata.text = NC.getString(R.string.please_check_internet)

                }

            }

            override fun onFailure(call: Call<SettlementReqData>, t: Throwable) {

                t.printStackTrace()

                cancelLoading()

               // layout_content.visibility = View.GONE

                txt_nodata.visibility = View.GONE

                txt_nodata.text = NC.getString(R.string.please_check_internet)

            }

        }))

    }

    override fun onSuccess(monthOfYear: Int, year: Int, day: Int) {

        mYear = year

        mMonth = monthOfYear + 1

        mDay = 2

        editNameDialog?.dismiss()

        if (datePick) {

            from_txt_lay.isClickable = true

            datePic = StringBuilder().append(mYear).append("-").append(checkDigit(mMonth)).append("-").append(checkDigit(day)).append(" ").toString()

            if (checkDateBefore(to_txt.text.toString(), datePic)) {

                from_txt.text = datePic

            } else {

                from_txt.text = to_txt.text.toString()

            }

        } else {

            to_txt_lay.isClickable = true

            datePic = StringBuilder().append(mYear).append("-").append(checkDigit(mMonth)).append("-").append(checkDigit(day)).append(" ").toString()

            if (checkDateAfter(datePic, from_txt.text.toString())) {

                to_txt.text = datePic

            } else {

                to_txt.text = datePic

                from_txt.text = datePic

            }

        }

    }

    override fun failure(inputText: String?) {

        editNameDialog?.dismiss()

        from_txt_lay.isClickable = true

        to_txt_lay.isClickable = true

    }

    private fun checkDigit(number: Int): String {

        return if (number <= 9) "0$number" else number.toString()

    }

    private fun checkDateBefore(date_End: String, date_Start: String): Boolean {

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.UK)

        val dateEnd = sdf.parse(date_End)

        val dateStart = sdf.parse(date_Start)

        return dateStart.before(dateEnd)

    }

    private fun checkDateAfter(date_End: String, date_Start: String): Boolean {

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.UK)

        val dateEnd = sdf.parse(date_End)

        val dateStart = sdf.parse(date_Start)

        return dateEnd.after(dateStart)

    }

    private fun getDate(timeStamp: Long): String {

        val date = Date(timeStamp * 1000)

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.UK)

        return sdf.format(date.time)

    }

}