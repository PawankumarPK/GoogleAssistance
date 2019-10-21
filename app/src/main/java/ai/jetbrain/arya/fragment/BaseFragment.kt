package ai.jetbrain.arya.fragment

import ai.api.AIConfiguration
import ai.api.AIListener
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIResponse
import ai.jetbrain.arya.R
import ai.jetbrain.arya.SttUtils.ConversionCallback
import ai.jetbrain.arya.SttUtils.Speech
import ai.jetbrain.arya.SttUtils.TranslatorFactory
import ai.jetbrain.arya.activity.BaseActivity
import ai.jetbrain.arya.utils.CheckNetwork
import ai.jetbrain.arya.utils.Helper
import ai.jetbrain.arya.utils.okhttpInterceptor
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject


open class BaseFragment : Fragment(){

    protected lateinit var baseActivity: BaseActivity

    private var currentText = ""

    private var isBusy = false

    protected var speechEnabled = true

   /* private val c = object : ConversionCallback {
        override fun onSuccess(result: String) {
            Log.i("HEARD", result)
            //matchSpeech(result)
            updateText(result)
            //baseActivity.muteSound(false)
           // dialogFlow(result)
            textRetained()


        }

        override fun onCompletion() {

        }

        override fun onErrorOccurred(errorMessage: String) {
            Log.e("ERROR", errorMessage)
            //isBusy = false
            updateText("Sorry, I didn't get that.")
            Speech.speakOutText("Sorry, I didn't get that.")
            //welcome()
        }

    }
*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity = activity as BaseActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSpeechAndText()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Speech.onPermissionResult(requestCode, grantResults[0])
    }

    private fun initSpeechAndText() {
        Speech.initSTT(baseActivity)
        Speech.initTTS(baseActivity)
        Speech.setOnCompleteListener {
            isBusy = false
        }
    }

    protected fun listen() {
        if (!isBusy) {
            isBusy = true
            updateText("Listening...")
           // baseActivity.muteSound(true)
           /* TranslatorFactory.with(TranslatorFactory.TRANSLATORS.SPEECH_TO_TEXT, c)
                .initialize(baseActivity)*/
        }
    }

    private fun welcome() {
        val welcome_start_text = resources.getString(R.string.welcome_start_text)
        val welcome_text_array = resources.getStringArray(R.array.welcome_text)
        val speak_text = welcome_start_text +
                welcome_text_array[Math.floor(Math.random() * welcome_text_array.size).toInt()]
        Speech.speakOutText(speak_text)
    }

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

    open fun onFaceUpdate(facex: Float, facey: Float) {
        if (facex > 0 && facex < 1 && facey > 0 && facey < 1) {
            Log.i("Face", "$facex,$facey")
            if (speechEnabled) listen()
        }
    }

    open fun updateText(txt: String) {
        currentText = txt
    }


}


/* fun dialogFlow(query: String) {
     if (query.isNotEmpty()) {

         val accessToken = Helper.getConfigValue(baseActivity, "dialogflow_conn")

         val client = OkHttpClient.Builder()
             .addInterceptor(okhttpInterceptor())
             .build()

         val request = okhttp3.Request.Builder()
             .url("https://api.dialogflow.com/v1/query?v=20150910&lang=en&query=$query&sessionId=12345")
             .header("Authorization", "Bearer $accessToken")
             .build()

         Thread {
             val response = client.newCall(request).execute()
             val body = response.body().string()
             response.body().close()

             val jsonBody: JSONObject = JSONObject(body)
             val gson = GsonBuilder().setLenient().create()
             //gson.fromJson(body, DFResponseObject::class.java)

             val result: JSONObject = jsonBody["result"] as JSONObject
             val fulfillment: JSONObject = result["fulfillment"] as JSONObject

             if (fulfillment.has("messages")) {
                 val messages: JSONArray = fulfillment["messages"] as JSONArray

                 if (messages.length() > 0) {
                     val message: JSONObject = messages.get(0) as JSONObject

                     if (message.has("speech")) {
                         baseActivity.runOnUiThread { updateText(message["speech"].toString()) }
                         baseActivity.muteSound(false)
                         Speech.speakOutText(message["speech"].toString())
                         Log.e("-->>>", "$message")

                     }
                 }
             } else {
                 //if (result.has("score") && (result["score"] as Double) < 0.1){
                 baseActivity.runOnUiThread { updateText("Sorry, I didn't get that.") }
                 baseActivity.muteSound(false)
                 Speech.speakOutText("Sorry, I didn't get that.")
             }

             Log.i("---->", response.toString())

         }.start()


     }

 }*/



