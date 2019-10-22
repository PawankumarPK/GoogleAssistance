package ai.jetbrain.arya


import ai.jetbrain.arya.api.DeviceStats
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log

class BatteryInfoReceiver : BroadcastReceiver() {

    override fun onReceive(ctxt: Context, intent: Intent) {
        DeviceStats.ScreenBattery = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
        Log.d("BroadCast--->", "${DeviceStats.ScreenBattery}")

    }
}