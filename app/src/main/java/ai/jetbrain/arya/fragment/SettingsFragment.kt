package ai.jetbrain.arya.fragment


import ai.jetbrain.arya.R
import ai.jetbrain.arya.adapter.SettingsAdapter
import ai.jetbrain.arya.api.DeviceStats
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment() {


    private var loop = false
    private val handler = Handler()
    private var runnable: Runnable? = null
    private var name = ""

    enum class SettingType {
        Status,
        Progress
    }

    class SettingItem {
        var name: String = ""
        var type: SettingType
        var value: String = "0"

        constructor(name: String, type: SettingType = SettingType.Status) {
            this.name = name
            this.type = type
        }
    }

    private var itemList: Map<Int, SettingItem> = mapOf<Int, SettingItem>(
        0 to SettingItem("Server"),
        1 to SettingItem("Core"),
        2 to SettingItem("Database"),
        3 to SettingItem("Arduino1"),
        4 to SettingItem("Arduino2"),
        5 to SettingItem("Lidar"),
        6 to SettingItem("Battery", SettingType.Progress),
        7 to SettingItem("Screen", SettingType.Progress),
        8 to SettingItem("Sensors", SettingType.Progress)
    )

    private var adapter: SettingsAdapter = SettingsAdapter(itemList)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        loop = isVisibleToUser
        if (isVisibleToUser) {
            updateSettings()
            //activity!!.runOnUiThread(Runnable { adapter.notifyDataSetChanged() })
        }
    }

    fun updateSettings() {

        (itemList[0] ?: error("")).value = if (DeviceStats.ServerConnected) "1" else "0"
        (itemList[6] ?: error("")).value = "%.2fV".format(DeviceStats.Battery)
        (itemList[7] ?: error("")).value = DeviceStats.ScreenBattery.toString() + "%"
        Log.d("updateSetting", "${DeviceStats.ScreenBattery}")

        if (activity != null)
            activity!!.runOnUiThread { adapter.notifyDataSetChanged() }

        if (loop) Handler().postDelayed({
            updateSettings()
        }, 3000)
    }

    private fun checkBatteryState() {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = baseActivity.registerReceiver(null, filter)
        val chargeState = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

        when (chargeState) {
            BatteryManager.BATTERY_STATUS_CHARGING, BatteryManager.BATTERY_STATUS_FULL -> {
                /**State = "charging" */
                mBatterCharging.visibility = View.VISIBLE
                mBatterAlert.visibility = View.INVISIBLE
            }
            else -> {
                /** State = "not charging" */
                mBatterCharging.visibility = View.INVISIBLE
                mBatterAlert.visibility = View.VISIBLE
            }
        }
    }

    private fun batteryHandler() {
        runnable = object : Runnable {
            override fun run() {
                checkBatteryState()
                handler.postDelayed(this, 1000)
            }
        }
//Start
        handler.postDelayed(runnable!!, 0)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateSettings()

        batteryHandler()

        mRecyclerView.layoutManager = GridLayoutManager(context, 3)
        mRecyclerView.adapter = adapter
        settingsBackBtn.setOnClickListener {
            updateSettings()
            fragmentManager!!.beginTransaction().replace(R.id.mFrameContainer, WelcomeFragment())
                .addToBackStack(null).commit()
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!)

    }

    override fun onDetach() {
        super.onDetach()
        handler.removeCallbacks(runnable!!)
    }
}

