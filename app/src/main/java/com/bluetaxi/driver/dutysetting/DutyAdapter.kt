package com.bluetaxi.driver.dutysetting

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bluetaxi.driver.R

class DutyAdapter(
    private val options: List<DutyOption>,
    private val onSwitchToggled: (DutyOption, Boolean) -> Unit
) : RecyclerView.Adapter<DutyAdapter.DutyViewHolder>() {

    inner class DutyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.imgIcon)
        val title: TextView = view.findViewById(R.id.tvTitle)
        val subtitle: TextView = view.findViewById(R.id.tvSubtitle)
        val toggle: Switch = view.findViewById(R.id.switchOption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DutyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_duty_option, parent, false)
        return DutyViewHolder(view)
    }

    override fun onBindViewHolder(holder: DutyViewHolder, position: Int) {
        val option = options[position]

        // Load image from URL
        Glide.with(holder.itemView.context)
            .load(option.modelImage)
            .placeholder(R.drawable.ic_schdule_time)
            .into(holder.icon)

        holder.title.text = option.modelName
        holder.toggle.isChecked = option.isEnabled
        holder.toggle.isEnabled = option.isSwitchEnabled

        if (!option.isSwitchEnabled) {
            holder.icon.setColorFilter(Color.GRAY)
            holder.title.setTextColor(Color.GRAY)
        }

        if (option.isMyVehicle) {
            holder.subtitle.visibility = View.VISIBLE
            holder.subtitle.text = "YOUR VEHICLE"
        } else {
            holder.subtitle.visibility = View.GONE
        }

        holder.toggle.setOnCheckedChangeListener { _, isChecked ->
            onSwitchToggled(option, isChecked)
        }
    }

    override fun getItemCount() = options.size
}

