package ai.jetbrain.arya.utils

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log


object CheckNetwork {

    private val TAG = CheckNetwork::class.java.simpleName

    fun isInternetAvailable(context: Context): Boolean {
        val info =
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo

        if (info == null) {
            Log.d(TAG, "no internet connection")
            return false
        } else {
            if (info.isConnected) {
                Log.d(TAG, " internet connection available...")
                return true
            } else {
                Log.d(TAG, " internet connection")
                return true
            }

        }
    }
}