package ai.jetbrain.arya.activitys

import ai.jetbrain.arya.R
import ai.jetbrain.arya.api.RetrofitClient
import ai.jetbrain.arya.fragments.HomeFragment
import ai.jetbrain.arya.utils.Helper
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("InlinedApi")
class MainActivity : AppCompatActivity() {

    val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = flags

        setContentView(R.layout.activity_main)

        RetrofitClient.init(Helper.getConfigValue(this,"api_url")!!)

        loadFragment()
    }

    private fun loadFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mFrameContainer, HomeFragment())
            .addToBackStack(null)
            .commit()
    }
}
