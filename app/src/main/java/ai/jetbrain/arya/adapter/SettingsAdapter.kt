package ai.jetbrain.arya.adapter

import ai.jetbrain.arya.R
import ai.jetbrain.arya.fragment.SettingsFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.settings_progress_item.view.*
import kotlinx.android.synthetic.main.settings_status_item.view.*

class SettingsAdapter(private val list: Map<Int,SettingsFragment.SettingItem>) : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(list[position] ?: error(""))
    }

    override fun getItemViewType(position: Int): Int {
        val item = list[position] ?: error("")
        val type:Int

        when(item.type) {
            SettingsFragment.SettingType.Status ->
                type = R.layout.settings_status_item
            SettingsFragment.SettingType.Progress ->
                type = R.layout.settings_progress_item
        }
        return type
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItems(data: SettingsFragment.SettingItem) {
            when(data.type) {
                SettingsFragment.SettingType.Status -> {
                    itemView.mCheckTextview.text = data.name
                    itemView.mCheckBox.isChecked = data.value != "0"
                }
                SettingsFragment.SettingType.Progress -> {
                    itemView.mProgressTextview.text = data.name
                    itemView.mProgressText.text = data.value
                    Log.d("value---","${data.value}")
                }
            }
        }

    }
}