package ai.jetbrain.arya.fragment


import ai.api.AIConfiguration
import ai.api.AIListener
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIResponse
import ai.jetbrain.arya.SttUtils.Speech
import ai.jetbrain.arya.activity.BaseActivity
import ai.jetbrain.arya.api.DeviceStats
import ai.jetbrain.arya.utils.CheckNetwork
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_battery_chg.*
import kotlinx.android.synthetic.main.fragment_welcome_screen.*
import kotlinx.android.synthetic.main.fragment_welcome_screen.mRelativeLayout
import java.lang.Exception


class WelcomeFragment : BaseFragment(), AIListener {

    private var aiService: AIService? = null
    private var fragmentIsVisible = true
    private var handler = Handler()
    private var runnable: Runnable? = null
    private val REQUEST_AUDIO_PERMISSION_REQ_ID = 1

    private var isBusy = false
    private var isSpeaking = false
    private var isListening = false

    private val defaultText = "Hello, I'm ARYA.\n\nCan I help you?"
    private val defaultText2 = "Welcome to Signature Hospital.\n\nCan I help you?"
    private val defaultSpeechErrorText = "Sorry, I didn't get that."
    private val defaultErrorText = "Sorry. I'm having trouble connecting to the Internet."

    private var lastActivityStamp = System.currentTimeMillis().toInt()

    private val settingsFragment = SettingsFragment()

    private fun initSTT(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var check = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
            if (check == PackageManager.PERMISSION_GRANTED) {

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO))
                    Toast.makeText(activity, "App required access to audio", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_AUDIO_PERMISSION_REQ_ID
                );
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(ai.jetbrain.arya.R.layout.fragment_welcome_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity = activity as BaseActivity
        initSTT(baseActivity)
        setSpeechAIConfig()
        mRelativeLayout.setOnClickListener {
            onFaceUpdate(0.5F, 0.5F)
        }
        //mSettings.setOnClickListener { showSettingsFragment() }
        Speech.setOnCompleteListener { onSpeechComplete() }
        initBatteryAnimation()

        Thread {
            while (true) {

                var stamp = System.currentTimeMillis().toInt()
                if ((stamp - lastActivityStamp) > 5000) {
                    if (!isBusy && !isSpeaking && !isListening) {
                        baseActivity.runOnUiThread {
                            textUpdate(defaultText)
                        }
                    }
                }
                if ((stamp - lastActivityStamp) > 10000) {
                    if (isListening) {
                        baseActivity.runOnUiThread {
                            aiService!!.cancel()
                        }
                    }
                }

                baseActivity.runOnUiThread {

                    if (chargingFragment.isVisible && !DeviceStats.ChargingState)
                        chargingFragment.visibility = View.GONE
                    else if (!chargingFragment.isVisible && DeviceStats.ChargingState)
                        chargingFragment.visibility = View.VISIBLE

                    mBatteryWatt.text = DeviceStats.Battery.toString() + "%"
                }


                Thread.sleep(1000)
            }
        }.start()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        fragmentIsVisible = isVisibleToUser
    }

    private fun textUpdate(txt: String) {
        mVoiceResult.text = txt
    }


    override fun onFaceUpdate(facex: Float, facey: Float) {
        super.onFaceUpdate(facex, facey)
        if (facex > 0 && facex < 1 && facey > 0 && facey < 1) {
            if (!isBusy) {
                if (CheckNetwork.isInternetAvailable(baseActivity)) {
                    isBusy = true
                    if ((System.currentTimeMillis().toInt() - lastActivityStamp) > 5000) {
                        welcome()
                    } else {
                        listen()
                    }
                } else onNetworkError()
            }
        }
    }

    private fun onNetworkError() {
        Speech.speakOutText(defaultErrorText)
        textUpdate(defaultErrorText)
    }

    private fun welcome() {
        lastActivityStamp = System.currentTimeMillis().toInt()
        if (!isSpeaking) {
            isSpeaking = true
            baseActivity.muteSound(false)
            var txt = defaultText
            if (Math.random() > 0.5) txt = defaultText2
            textUpdate(txt)
            Speech.speakOutText(txt)
        }
    }

    private fun listen() {
        lastActivityStamp = System.currentTimeMillis().toInt()
        if (!isListening) {
            isListening = true
            aiService!!.startListening()
            textUpdate("Listening...")
        }
    }

    private fun setSpeechAIConfig() {
        val config = ai.api.android.AIConfiguration(
            "f3bec00f4fac4c8fa9e606c2f97a73ef",
            AIConfiguration.SupportedLanguages.English,
            ai.api.android.AIConfiguration.RecognitionEngine.System
        )
        aiService = AIService.getService(baseActivity, config)
        aiService!!.setListener(this)
    }

    override fun onAudioLevel(level: Float) {

    }

    override fun onListeningStarted() {
        lastActivityStamp = System.currentTimeMillis().toInt()
        baseActivity.muteSound(true)
        listen()
    }

    override fun onListeningFinished() {
        lastActivityStamp = System.currentTimeMillis().toInt()
        baseActivity.muteSound(true)
        isListening = false
    }

    override fun onListeningCanceled() {
        lastActivityStamp = System.currentTimeMillis().toInt()
        isListening = false
        isBusy = false
        textUpdate(defaultText)
    }

    override fun onResult(response: AIResponse) {
        lastActivityStamp = System.currentTimeMillis().toInt()
        baseActivity.muteSound(false)
        try {
            val result = response.result.fulfillment.speech
            textUpdate(result)
            Speech.speakOutText(result)
        }catch (e : Exception){
            Toast.makeText(baseActivity, "Response Exception!!", Toast.LENGTH_SHORT).show()
            textUpdate("I'm having trouble to connect internet")
        }
    }

    override fun onError(error: AIError) {
        lastActivityStamp = System.currentTimeMillis().toInt()
        isListening = false
        isBusy = false
    }

    private fun onSpeechComplete() {
        lastActivityStamp = System.currentTimeMillis().toInt()
        isSpeaking = false
        isBusy = false
        baseActivity.runOnUiThread { onFaceUpdate(0.5F, 0.5F) }
    }

    override fun onDetach() {
        super.onDetach()
        mVoiceResult.text = null
        handler.removeCallbacksAndMessages(runnable)
    }


    fun initBatteryAnimation() {
        val animation = AlphaAnimation(1f, 0f)
        animation.duration = 1000
        animation.interpolator = LinearInterpolator() as Interpolator?
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.REVERSE
        mFlash.startAnimation(animation)
    }

}




