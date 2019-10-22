package ai.jetbrain.arya

import ai.jetbrain.arya.fragment.BaseFragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class FaceDataReceiver : BroadcastReceiver() {

    var loadedFragment: BaseFragment? = null

    fun setFragment(fragment: BaseFragment) {
        loadedFragment = fragment
    }

    override fun onReceive(context: Context, intent: Intent) {
        val error = intent.getBooleanExtra("error", false)
        var x = -1F
        var y = -1F
        if (!error) {
            x = intent.getFloatExtra("x", -1F)
            y = intent.getFloatExtra("y", -1F)
        }

        if (loadedFragment != null) loadedFragment!!.onFaceUpdate(x, y)
    }
}

