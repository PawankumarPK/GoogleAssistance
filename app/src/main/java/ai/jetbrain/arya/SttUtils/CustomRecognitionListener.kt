package ai.jetbrain.arya.SttUtils

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log

class CustomRecognitionListener(val speechRec:SpeechRecognization, val conversionCB: ConversionCallback) : RecognitionListener {

    private val TAG = SpeechRecognization::class.java.name

    override fun onReadyForSpeech(params: Bundle) {
        Log.d(TAG, "onReadyForSpeech")
    }

    override fun onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {
        //Log.d(TAG, "onRmsChanged")
    }

    override fun onBufferReceived(buffer: ByteArray) {
        Log.d(TAG, "onBufferReceived")
    }

    override fun onEndOfSpeech() {
        Log.d(TAG, "onEndofSpeech")
    }

    override fun onError(error: Int) {
        Log.e(TAG, "error $error " + speechRec.getErrorText(error))
        conversionCB.onErrorOccurred(speechRec.getErrorText(error))
    }

    override fun onResults(results: Bundle) {
        var translateResults = String()
        val data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        for (result in data!!) {
            translateResults = result
        }
        conversionCB.onSuccess(translateResults)
    }

    override fun onPartialResults(partialResults: Bundle) {
        //val words = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        // words!!.joinToString { "|" }
        Log.d(TAG, "onPartialResults")
    }

    override fun onEvent(eventType: Int, params: Bundle) {
        Log.d(TAG, "onEvent $eventType")
    }
}