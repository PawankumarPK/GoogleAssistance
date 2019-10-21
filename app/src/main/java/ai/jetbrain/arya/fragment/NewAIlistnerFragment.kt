package ai.jetbrain.arya.fragment


import ai.api.AIConfiguration
import ai.api.AIListener
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIResponse
import ai.jetbrain.arya.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_new_ailistner.*

class NewAIlistnerFragment : BaseFragment(), AIListener {

    private var aiService: AIService? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_ailistner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenButton.setOnClickListener { listenButtonOnClick() }
        aiConfig()
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

    private fun listenButtonOnClick() {
        aiService!!.startListening()
    }

    override fun onResult(response: AIResponse) {

        getResultInText(response)

    }

    override fun onError(error: AIError) {
        resultTextView.text = error.toString()
    }

    override fun onAudioLevel(level: Float) {

    }

    override fun onListeningStarted() {

    }

    override fun onListeningCanceled() {

    }

    override fun onListeningFinished() {

    }

    @SuppressLint("SetTextI18n")
    private fun getResultInText(response: AIResponse) {
        val result1 = response.result
        val parameterString = StringBuilder()
        if (result1.parameters != null && !result1.parameters.isEmpty()) {
            for ((key, value) in result1.parameters) {
                parameterString.append("(").append(key).append(", ")
                    .append(value).append(") ")
            }
        }
        resultTextView.text = ("\n Output: " + result1.fulfillment.speech + "\n You: " + result1.resolvedQuery)

    }


}
