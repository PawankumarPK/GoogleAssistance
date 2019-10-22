package ai.jetbrain.arya

import ai.jetbrain.arya.api.DeviceStats
import ai.jetbrain.arya.api.RetrofitClient
import ai.jetbrain.arya.api.model.StandardModel.DeviceStatsModel
import android.app.IntentService
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Response

class DeviceManagerService : IntentService("DeviceManagerService") {

    fun getDeviceStats() {

        val api = RetrofitClient.apiService
        val call = api.deviceStats()

        call.enqueue(object : retrofit2.Callback<DeviceStatsModel> {
            override fun onFailure(call: Call<DeviceStatsModel>?, t: Throwable?) {
                Handler().postDelayed({
                    getDeviceStats()
                }, 5000)
            }

            override fun onResponse(
                call: Call<DeviceStatsModel>,
                response: Response<DeviceStatsModel>
            ) {
                if (response.isSuccessful) {
                    val deviceStatsModel: DeviceStatsModel = response.body()

                    DeviceStats.ServerConnected = true
                    DeviceStats.Battery = deviceStatsModel.Battery!!
                    Log.d("--->", "${DeviceStats.Battery}")
                    //  Toast.makeText(applicationContext,"Success",Toast.LENGTH_SHORT).show()
                    Handler().postDelayed({
                        getDeviceStats()
                    }, 5000)

                }
            }
        })

        Log.d("DMS--->", "${DeviceStats.Battery}")
    }

    override fun onHandleIntent(intent: Intent?) {
        try {
            getDeviceStats()

        } catch (e: InterruptedException) {
            Toast.makeText(applicationContext, "Thread Exception!!", Toast.LENGTH_SHORT).show()
            //Thread.currentThread().interrupt()
            Handler().postDelayed({
                getDeviceStats()
            }, 5000)
        }
    }

}