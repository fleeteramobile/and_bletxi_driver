package com.bluetaxi.driver.service

import android.content.Context
import com.bluetaxi.driver.MyApplication
import com.bluetaxi.driver.data.CommonData
import com.bluetaxi.driver.data.apiData.ApiRequestData
import com.bluetaxi.driver.data.apiData.CompanyDomainResponse
import com.bluetaxi.driver.utils.SessionSave
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckCompanyDomain {

    fun callCheckCompanyDomain(context: Context) {
        val baseUrl = SessionSave.getSession(CommonData.DOMAIN_URL, context)
        val client = MyApplication.getInstance().getCheckCompanyDomainapiManager(baseUrl)
        val request = ApiRequestData.BaseUrl()
        request.company_domain = SessionSave.getSession(CommonData.ACCESS_KEY, context)
        request.company_main_domain = SessionSave.getSession(CommonData.COMPANY_DOMAIN, context)
        request.device_type = "1"
        val response = client.callData(ServiceGenerator.COMPANY_KEY, request)

        response.enqueue(object : Callback<CompanyDomainResponse> {

            override fun onResponse(call: Call<CompanyDomainResponse>, response: Response<CompanyDomainResponse>) {
                try {
                    if (response.isSuccessful) {
                        val cr = response.body()
                        if (cr!!.auth_key != "" && cr.auth_key != null) {
                            SessionSave.saveSession(CommonData.AUTH_KEY, cr.auth_key, context)
                        } /*else {
//                            CToast.ShowToast(context, NC.getString(R.string.server_error))
                        }*/
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<CompanyDomainResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })

    }
}