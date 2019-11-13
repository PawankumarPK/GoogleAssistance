package ai.jetbrain.arya.fragment


import ai.api.AIConfiguration
import ai.api.AIListener
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIResponse
import ai.jetbrain.arya.R
import ai.jetbrain.arya.SttUtils.Speech
import ai.jetbrain.arya.activity.BaseActivity
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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_welcome_screen.*


class WelcomeFragment : BaseFragment(), AIListener {

    private var aiService: AIService? = null
    private val fragment = SettingsFragment()
    private var fragmentIsVisible = true
    private var handler = Handler()
    private var runnable: Runnable? = null
    private val REQUEST_AUDIO_PERMISSION_REQ_ID = 1

    private var isBusy = false
    private  var isListening = false

    private val defaultText = "Hello, I'm ARYA"
    private val defaultErrorText = "Sorry. I'm having some trouble."

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
        return inflater.inflate(R.layout.fragment_welcome_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity = activity as BaseActivity
        initSTT(baseActivity)
        setSpeechAIConfig()
        mRelativeLayout.setOnClickListener { listen() }
        mSettings.setOnClickListener { showSettingsFragment() }
        Speech.setOnCompleteListener {
            isBusy = false
            watchForResetText()
        }
    }

    private fun watchForResetText() {
        baseActivity.runOnUiThread {
            Handler().postDelayed({
                if (!isBusy) textUpdate(defaultText)
            }, 5000)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        fragmentIsVisible = isVisibleToUser
        /*
        if (fragmentIsVisible)
            internetHandler()
        else
            handler.removeCallbacksAndMessages(null)
        */
    }

    private fun textUpdate(txt: String) {
        mVoiceResult.text = txt
    }

    private fun showSettingsFragment() {
        fragmentManager!!.beginTransaction().replace(R.id.mFrameContainer, fragment)
            .addToBackStack(null).commit()
    }

    override fun onFaceUpdate(facex: Float, facey: Float) {
        super.onFaceUpdate(facex, facey)
        if (facex > 0 && facex < 1 && facey > 0 && facey < 1) {
            if(CheckNetwork.isInternetAvailable(baseActivity)) {
                listen()
            }
            else {
                textUpdate("I'm having trouble connecting to internet.")
            }
        }
    }

    private fun listen() {
        if (!isBusy) {
            isBusy = true
            isListening = true
            aiService!!.startListening()
            Handler().postDelayed({
                if(isListening) aiService!!.cancel();
            },10000)
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

    override fun onResult(response: AIResponse) {
        getResultInText(response)
    }

    override fun onError(error: AIError) {
        textUpdate(defaultErrorText)
        isBusy = false
        watchForResetText()
    }

    override fun onAudioLevel(level: Float) {

    }

    override fun onListeningStarted() {
        baseActivity.muteSound(true)
        listen()
    }

    override fun onListeningCanceled() {
        isBusy = false
        isListening = false
        textUpdate(defaultText)
    }

    override fun onListeningFinished() {
        baseActivity.muteSound(true)
        isListening = false
    }

    private fun getResultInText(response: AIResponse) {
        baseActivity.muteSound(false)
        val resultResponse = response.result
        val result = resultResponse.fulfillment.speech
        mVoiceResult.text = result
        Speech.speakOutText(result)
        textUpdate(result)
    }

    override fun onDetach() {
        super.onDetach()
        mVoiceResult.text = null
        handler.removeCallbacksAndMessages(runnable)
    }

}




