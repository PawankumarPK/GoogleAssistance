package ai.jetbrain.arya.fragment

import ai.jetbrain.arya.SttUtils.Speech
import ai.jetbrain.arya.activity.BaseActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment


open class BaseFragment : Fragment() {

    protected lateinit var baseActivity: BaseActivity

    protected var speechEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity = activity as BaseActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSpeechAndText()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Speech.onPermissionResult(requestCode, grantResults[0])
    }

    private fun initSpeechAndText() {
        Speech.initTTS(baseActivity)
    }

    /*protected fun listen() {
        if (!isBusy) {
            isBusy = true
            updateText("Listening...")
        }
    }*/
    /*
    private fun welcome() {
        val welcome_start_text = resources.getString(R.string.welcome_start_text)
        val welcome_text_array = resources.getStringArray(R.array.welcome_text)
        val speak_text = welcome_start_text +
                welcome_text_array[Math.floor(Math.random() * welcome_text_array.size).toInt()]
        Speech.speakOutText(speak_text)
    }
    */
    /*
    private fun matchSpeech(speech: String) {

        val queries = resources.getStringArray(R.array.queries)
        val responses = resources.getStringArray(R.array.responses)
        var talkBackText = resources.getString(R.string.error_response)

        queries.forEachIndexed { index, queryList ->
            queryList.split("|").forEach { query ->
                if (speech == query || speech.contains(query)) talkBackText = responses[index]
            }
        }

        updateText(talkBackText)
        Speech.speakOutText(talkBackText)
    }
*/

    open fun onFaceUpdate(facex: Float, facey: Float) {
        if (facex > 0 && facex < 1 && facey > 0 && facey < 1) {
            Log.i("Face", "$facex,$facey")
        }
    }
}



