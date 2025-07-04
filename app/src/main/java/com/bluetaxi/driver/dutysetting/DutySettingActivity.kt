package com.bluetaxi.driver.dutysetting

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bluetaxi.driver.R
import com.bluetaxi.driver.interfaces.APIResult
import com.bluetaxi.driver.service.APIService_Retrofit_JSON
import com.bluetaxi.driver.utils.SessionSave
import org.json.JSONArray
import org.json.JSONObject

class DutySettingActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvApply: TextView
    private lateinit var adapter: DutyAdapter
    private val selectedModelIds = mutableListOf<Int>()
    private val initiallyEnabledModelIds = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_duty_setting)
        recyclerView = findViewById(R.id.recyclerViewDuty)
        tvApply = findViewById(R.id.tvApply)
        recyclerView.layoutManager = LinearLayoutManager(this)
        loadDutySettings()


        tvApply.setOnClickListener {
            sendApplyApi()

        }




    }

    inner class sendApplyApi : APIResult {

        init {
            val j = JSONObject()
            j.put("driver_id", SessionSave.getSession("Id", this@DutySettingActivity))
            val jsonArray = JSONArray()

// Combine both lists: selectedModelIds + initiallyEnabledModelIds
            val combinedSet = (selectedModelIds + initiallyEnabledModelIds).toSet()

            combinedSet.forEach {
                jsonArray.put(it)
            }


            j.put("accept_higher_end_model", jsonArray)


            val requestingCheckBox = "type=add_driver_duty_setting"

            APIService_Retrofit_JSON(this@DutySettingActivity, this, j, false).execute(
                requestingCheckBox
            )
        }

        @SuppressLint("MissingPermission")
        override fun getResult(isSuccess: Boolean, result: String?) {
            if (isSuccess) {
                val mJSONObject = JSONObject(result)
                Toast.makeText(this@DutySettingActivity, mJSONObject.getString("message"), Toast.LENGTH_LONG).show()


            }
        }

    }

    inner class loadDutySettings : APIResult {

        init {
            val j = JSONObject()
            j.put("driver_id", SessionSave.getSession("Id", this@DutySettingActivity))
            j.put("model_id", SessionSave.getSession("model_id", this@DutySettingActivity))


            val requestingCheckBox = "type=get_duty_setting_model"

            APIService_Retrofit_JSON(this@DutySettingActivity, this, j, false).execute(
                requestingCheckBox
            )
        }

        @SuppressLint("MissingPermission")
        override fun getResult(isSuccess: Boolean, result: String?) {
            if (isSuccess) {
                val mJSONObject = JSONObject(result)
                Toast.makeText(this@DutySettingActivity, mJSONObject.getString("message"), Toast.LENGTH_LONG).show()

                val dataArray = mJSONObject.getJSONArray("data")
                val dutyOptions = mutableListOf<DutyOption>()

                for (i in 0 until dataArray.length()) {
                    val obj = dataArray.getJSONObject(i)
                    val modelId = obj.getInt("model_id")
                    val acceptDuty = obj.getInt("accept_duty") == 1

                    if (acceptDuty && !initiallyEnabledModelIds.contains(modelId)) {
                        initiallyEnabledModelIds.add(modelId)
                    }

                    val option = DutyOption(
                        modelId = modelId,
                        modelName = obj.getString("model_name"),
                        isMyVehicle = obj.getInt("is_my_vehicle_model") == 1,
                        modelImage = obj.getString("model_image"),
                        isEnabled = acceptDuty,
                        isSwitchEnabled = true
                    )
                    dutyOptions.add(option)
                }

                adapter = DutyAdapter(dutyOptions) { item, isChecked ->
                    item.isEnabled = isChecked
                    if (isChecked) {
                        if (!selectedModelIds.contains(item.modelId)) {
                            selectedModelIds.add(item.modelId)
                        }
                    } else {
                        selectedModelIds.remove(item.modelId)
                    }
                    // send update to server if needed
                }

                recyclerView.adapter = adapter
            }
        }

    }
}