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
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_welcome_screen.*


class WelcomeFragment : BaseFragment(), AIListener {

    private var aiService: AIService? = null
    val fragment = SettingsFragment()
    var busy = false
    private var fragmentIsVisible = true
    var handler = Handler()
    private var runnable: Runnable? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_welcome_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity = activity as BaseActivity
/*

        if (fragmentIsVisible){
            handler.postDelayed({
                checkInternet()
            }, 5000)
        }else{
            mVoiceResult.text = null
            handler.removeCallbacksAndMessages(null)
        }
*/

        aiConfig()
        mRelativeLayout.setOnClickListener { aiListening() }
        mSettings.setOnClickListener { settingFragment() }

    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        fragmentIsVisible = isVisibleToUser

        if (fragmentIsVisible)
            batteryHandler()
        else
            handler.removeCallbacksAndMessages(null)

    }

    private fun textUpdate(txt: String) {
        if (fragmentIsVisible)
            mVoiceResult.text = txt
        else
            fragmentIsVisible = true
    }

    private fun settingFragment() {
        fragmentManager!!.beginTransaction().replace(R.id.mFrameContainer, SettingsFragment())
            .addToBackStack(null).commit()
    }

    override fun onFaceUpdate(facex: Float, facey: Float) {
        super.onFaceUpdate(facex, facey)
        aiListening()
    }

    override fun updateText(txt: String) {
        super.updateText(txt)
    }

    private fun aiListening() {
        if (!busy)
            busy = true
        aiService!!.startListening()

    }

    private fun aiConfig() {
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
        if (CheckNetwork.isInternetAvailable(baseActivity))
            textUpdate("No speech found")
        else
            textUpdate("No Internet")
    }

    override fun onAudioLevel(level: Float) {

    }

    override fun onListeningStarted() {
        baseActivity.muteSound(true)
        if (CheckNetwork.isInternetAvailable(baseActivity))
            textUpdate("Listening...")
        else
            textUpdate("No Internet")
    }

    override fun onListeningCanceled() {
        textUpdate("Hello I'm ARYA")

    }

    override fun onListeningFinished() {
        baseActivity.muteSound(true)
    }

    @SuppressLint("SetTextI18n")
    private fun getResultInText(response: AIResponse) {
        baseActivity.muteSound(false)
        val resultResponse = response.result
        val parameterString = StringBuilder()
        if (resultResponse.parameters != null && !resultResponse.parameters.isEmpty()) {
            for ((key, value) in resultResponse.parameters) {
                parameterString.append("(").append(key).append(", ")
                    .append(value).append(") ")
            }
        }
        //mVoiceResult.text = result1.resolvedQuery
        val result = resultResponse.fulfillment.speech
        mVoiceResult.text = result
        Speech.setOnCompleteListener {
            busy = false
        }
        Speech.speakOutText(result)

    }

    private fun checkInternet() {
        if (fragmentIsVisible) {
            if (CheckNetwork.isInternetAvailable(baseActivity)) {
                aiConfig()
            } else
                textUpdate("No Internet")
        }
    }

    private fun batteryHandler() {
        runnable = object : Runnable {
            override fun run() {
                checkInternet()
                handler.postDelayed(this, 5000)
            }
        }
//Start
        handler.postDelayed(runnable, 3000)
    }

    override fun onDetach() {
        super.onDetach()
        mVoiceResult.text = null
        handler.removeCallbacksAndMessages(runnable)
    }

}




