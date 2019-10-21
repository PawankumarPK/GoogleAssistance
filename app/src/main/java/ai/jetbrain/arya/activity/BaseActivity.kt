package ai.jetbrain.arya.activity

import ai.jetbrain.arya.*
import ai.jetbrain.arya.api.RetrofitClient
import ai.jetbrain.arya.fragment.BaseFragment
import ai.jetbrain.arya.fragment.EyeFragment
import ai.jetbrain.arya.fragment.WelcomeFragment
import ai.jetbrain.arya.utils.Helper
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class BaseActivity : AppCompatActivity() {

    private lateinit var mAdminComponentName: ComponentName
    private lateinit var mDevicePolicyManager: DevicePolicyManager
    private val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE)


    private val receiver = FaceDataReceiver()
    private val sbreceiver = BatteryInfoReceiver()

    private val filter = IntentFilter("ai.jetbrain.FaceXY")
    private var selection = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        startKioskMode()

        RetrofitClient.init(Helper.getConfigValue(this, "api_url")!!)
        Intent(this, FaceTrackService::class.java).also { intent ->
            startService(intent)
        }
        Intent(this, DeviceManagerService::class.java).also { intent ->
            startService(intent)
        }
        screenSelection()
    }


    fun muteSound(sound: Boolean) {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, sound)
    }

    private fun startKioskMode() {
        mAdminComponentName = MyDeviceAdminReceiver.getComponentName(this)
        mDevicePolicyManager =
            getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        if (mDevicePolicyManager.isDeviceOwnerApp(packageName)) {
            mDevicePolicyManager.setLockTaskPackages(mAdminComponentName, arrayOf(packageName))
            startLockTask()
            val intentFilter = IntentFilter(Intent.ACTION_MAIN)
            intentFilter.addCategory(Intent.CATEGORY_HOME)
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
            mDevicePolicyManager.addPersistentPreferredActivity(
                mAdminComponentName,
                intentFilter, ComponentName(packageName, BaseActivity::class.java.name)
            )
            mDevicePolicyManager.setKeyguardDisabled(mAdminComponentName, true)
            mDevicePolicyManager.setGlobalSetting(
                mAdminComponentName,
                Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                (BatteryManager.BATTERY_PLUGGED_AC
                        or BatteryManager.BATTERY_PLUGGED_USB
                        or BatteryManager.BATTERY_PLUGGED_WIRELESS).toString()
            )

            window.decorView.systemUiVisibility = flags

            window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                    window.decorView.systemUiVisibility = flags
                } else {
                }
            }

        } else {
            // Please contact your system administrator
            window.decorView.systemUiVisibility = flags
        }
    }

    private fun screenSelection() {
        selection = getScreenSelection()
        if (selection == 1) {
            loadFragment(EyeFragment())
        }
        if (selection == 2) {
            loadFragment(WelcomeFragment())
        } else {
            btnScreen1.setOnClickListener {
                loadFragment(EyeFragment())
                setScreenSelection(1)
            }
            btnScreen2.setOnClickListener {
                loadFragment(WelcomeFragment())
                setScreenSelection(2)
            }
        }
    }

    private fun setScreenSelection(id: Int) {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(getString(R.string.screen_select_preference), id)
            apply()
        }
    }

    private fun getScreenSelection(): Int {
        var pref: Int = -1
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return pref
        if (sharedPref.contains(getString(R.string.screen_select_preference)))
            pref = sharedPref.getInt(getString(R.string.screen_select_preference), -1)
        return pref
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
        unregisterReceiver(sbreceiver)

    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, filter)
        registerReceiver(sbreceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

    }

    private fun loadFragment(fragment: BaseFragment) {
        receiver.setFragment(fragment)

        supportFragmentManager.beginTransaction()
            .replace(R.id.mFrameContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

}
